// Copyright (C) 2018 The Delphi Team.
// See the LICENCE file distributed with this work for additional
// information regarding copyright ownership.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at

// http://www.apache.org/licenses/LICENSE-2.0

// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package org.tud.sse.metrics
package opal

import com.typesafe.config.ConfigValueFactory
import org.opalj.br.analyses.{InconsistentProjectException, Project}
import org.opalj.br.reader.Java16LibraryFramework
import org.opalj.br.{BaseConfig, ClassFile, DeclaredMethod}
import org.opalj.bytecode.JRELibraryFolder
import org.opalj.log.{LogContext, OPALLogger, StandardLogContext}
import org.slf4j.{Logger, LoggerFactory}

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, DataInputStream, File, FileInputStream, InputStream}
import java.net.URL
import java.util.jar.JarInputStream
import java.util.zip.ZipFile
import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Try}

object OPALProjectHelper {

  type ClassList = List[(ClassFile, URL)]

  private val LOAD_JRE_IMPLEMENTATION = true

  private val projectLogCtx: LogContext = {
    val ctx = new StandardLogContext()
    OPALLogger.register(ctx, OPALLogAdapter)
    ctx
  }

  private val log: Logger = LoggerFactory.getLogger(this.getClass)
  private val fullClassFileReader = Project.JavaClassFileReader(projectLogCtx, BaseConfig)
  private val interfaceClassFileReader = Java16LibraryFramework

  lazy val jreClasses: ClassList = {

    getContainerFilesRecursive(JRELibraryFolder)
      .filter(f => !f.getName.equals("jfxswt.jar")) // Do not load SWT classes, they depend on eclipse implementations
      .map(f => {
        readClassesFromContainerFile(f, LOAD_JRE_IMPLEMENTATION)
      })
      .filter(readTry => readTry match {
        case Failure(ex) =>
          log.error("Failed to load JRE library file: " + ex.getMessage)
          false
        case _ => true
      })
      .flatMap(_.get)
  }

  private def isClassContainerFile(file: File) = file.getName.toLowerCase.endsWith(".jar") || file.getName.toLowerCase.endsWith(".jmod")

  private def getContainerFilesRecursive(directory: File): List[File] = {
    val directChildJars = directory.listFiles.filter(f => f.isFile && isClassContainerFile(f)).toList
    directChildJars ++ directory.listFiles.filter(_.isDirectory).flatMap(getContainerFilesRecursive).toList
  }

  def isThirdPartyMethod(project: Project[URL],method: DeclaredMethod): Boolean = {
    !project.allProjectClassFiles.map(_.thisType).contains(method.declaringClassType) &&
      !jreClasses.map(_._1.thisType).contains(method.declaringClassType)
  }

  def isThirdPartyClassFile(project: Project[URL], cf: ClassFile): Boolean = {
    !project.allProjectClassFiles.contains(cf) && !jreClasses.map(_._1).contains(cf)
  }

  def buildOPALProject(projectClasses: ClassList, thirdPartyClasses: ClassList, asLibrary: Boolean, excludeJRE: Boolean): Project[URL] = {

    val config = if(asLibrary) BaseConfig.withValue("org.opalj.br.analyses.cg.InitialEntryPointsKey.analysis",
      ConfigValueFactory.fromAnyRef("org.opalj.br.analyses.cg.LibraryEntryPointsFinder")) else BaseConfig

    val inconsistentExceptionHandler = (_: LogContext, error: InconsistentProjectException) => log.info("Inconsistent Project: " + error.message)

    val libClasses = if(excludeJRE) thirdPartyClasses else thirdPartyClasses ++ jreClasses

    Project(projectClasses, libClasses, libraryClassFilesAreInterfacesOnly = false, Traversable.empty, inconsistentExceptionHandler)(config, OPALLogAdapter)
  }

  def readClassesFromFileStructure(file: File, loadImplementation: Boolean): Try[ClassList] = Try {

    if(file.isFile && isClassContainerFile(file)){
      readClassesFromContainerFile(file, loadImplementation).get
    } else if(file.isDirectory){
      getContainerFilesRecursive(file)
        .map(f => readClassesFromContainerFile(f, loadImplementation))
        .filter(readTry => readTry match {
          case Failure(ex) =>
            log.error("Failed to load additional classes: " + ex.getMessage)
            false
          case _ => true
        })
        .flatMap(_.get)
    } else {
      log.error("Failed to load additional classes, no valid class container file found at: " + file.getPath)
      throw new IllegalStateException("Invalid input file for loading additional classes")
    }
  }

  def readClassesFromContainerFile(file: File, loadImplementation: Boolean): Try[ClassList] = {
    if(file.getName.toLowerCase.endsWith(".jar")){
      readClassesFromJarStream(new FileInputStream(file), file.toURI.toURL, loadImplementation)
    } else if(file.getName.toLowerCase.endsWith(".jmod")) {
      readClassesFromJmodFile(file, loadImplementation)
    } else {
      Failure(new IllegalStateException("Not a class container file: " + file.getName))
    }
  }

  def readClassesFromJarStream(jarStream: InputStream, source:URL, loadImplementation: Boolean = true): Try[ClassList] = Try {

    val entries = new ListBuffer[(ClassFile, URL)]()
    val jarInputStream = new JarInputStream(jarStream)

    var currentEntry = jarInputStream.getNextJarEntry

    val reader = if(loadImplementation) this.fullClassFileReader else this.interfaceClassFileReader

    while(currentEntry != null){
      val entryName = currentEntry.getName.toLowerCase

      if (entryName.endsWith(".class")){
        reader
          .ClassFile(getEntryByteStream(jarInputStream))
          .map(cf => (cf, source))
          .foreach(t => entries.append(t))
      }

      currentEntry = jarInputStream.getNextJarEntry
    }

    entries.toList
  }

  def readClassesFromJmodFile(jmod: File, loadImplementation: Boolean): Try[ClassList] = Try {
    val entries = new ListBuffer[(ClassFile, URL)]()

    val zipFile = new ZipFile(jmod)
    val source = jmod.toURI.toURL
    val entryEnum = zipFile.entries()

    val reader = if(loadImplementation) this.fullClassFileReader else this.interfaceClassFileReader

    while(entryEnum.hasMoreElements){
      val currentEntry = entryEnum.nextElement()
      val entryName = currentEntry.getName.toLowerCase

      if (entryName.endsWith(".class")){
        val is = zipFile.getInputStream(currentEntry)

        reader
          .ClassFile(getEntryByteStream(is))
          .map(cf => (cf, source))
          .foreach(t => entries.append(t))
      }
    }

    entries.toList
  }

  private def getEntryByteStream(in: InputStream): DataInputStream = {
    val entryBytes = {
      val baos = new ByteArrayOutputStream()
      val buffer = new Array[Byte](32 * 1024)

      Stream.continually(in.read(buffer)).takeWhile(_ > 0).foreach { bytesRead =>
        baos.write(buffer, 0, bytesRead)
        baos.flush()
      }

      baos.toByteArray
    }

    new DataInputStream(new ByteArrayInputStream(entryBytes))
  }
}
