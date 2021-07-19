package org.tud.sse.metrics


import opal.{ClassStreamReader, OPALLogAdapter}

import org.opalj.br.analyses.Project
import org.slf4j.{Logger, LoggerFactory}
import SingleFileAnalysisCliParser.batchModeSymbol
import input.CliParser
import input.CliParser.OptionMap

import java.io.{File, FilenameFilter}
import java.net.URL
import java.nio.file.{Files, Paths}
import scala.util.{Failure, Success, Try}

class SingleFileAnalysisCliParser
  extends CliParser("Usage: MetricsBasedAnalysis [--batch-mode] [--out-file file] filename"){

  override val additionalOptions: List[String] = List()
  override val additionalSwitches: List[String] = List(batchModeSymbol.name)

}

object SingleFileAnalysisCliParser {
  val batchModeSymbol: Symbol = Symbol("batch-mode")

  def apply() = new SingleFileAnalysisCliParser()
}


trait SingleFileMetricsAnalysisApplication {

  protected val log: Logger = LoggerFactory.getLogger(this.getClass)

  def doSingleFileAnalysis(project: Project[URL], customOptions: OptionMap): Try[Iterable[JarFileMetricValue]]

  private def initializeAnalysis(appOptions: OptionMap, analysisOptions: OptionMap): Unit = {
    val inputFilePath = appOptions(CliParser.inFileSymbol).toString
    val batchModeEnabled = appOptions.contains(SingleFileAnalysisCliParser.batchModeSymbol)
    val isLibraryFile = appOptions.contains(CliParser.isLibrarySymbol)
    val outFileOption = appOptions.get(CliParser.outFileSymbol)
    val opalLoggingEnabled = appOptions.contains(CliParser.enableOpalLoggingSymbol)


    log.info("Running analysis with parameters:")
    log.info(s"\t- Input: $inputFilePath")
    log.info(s"\t- Batch Mode Enabled: $batchModeEnabled")
    log.info(s"\t- Output File: ${outFileOption.getOrElse("None")}")
    log.info(s"\t- Treat JAR as Library: $isLibraryFile")
    log.info(s"\t- OPAL Logging Enabled: $opalLoggingEnabled")


    OPALLogAdapter.setOpalLoggingEnabled(opalLoggingEnabled)

    if(!Files.exists(Paths.get(inputFilePath))){
      log.error("Input file does not exist")
    } else if(batchModeEnabled && !Files.isDirectory(Paths.get(inputFilePath))){
      log.error("Batch mode enabled but input file is no directory")
    } else if(!batchModeEnabled && Files.isDirectory(Paths.get(inputFilePath))){
      log.error("No batch mode enabled but input file is directory")
    } else {
      if(!batchModeEnabled){
        val file = new File(inputFilePath)
        log.debug(s"Initializing OPAL project for input file ${file.getName} ...")
        val opalProject = ClassStreamReader.createProject(file.toURI.toURL, isLibraryFile)
        log.info(s"Done initializing OPAL project.")

        val result = doSingleFileAnalysis(opalProject, analysisOptions) match {
          case Success(values) =>
            log.info(s"Got metrics for JAR ${file.getName}: ")
            values.foreach(v => log.info(s"\t-${v.metricName}: ${v.value}"))
            JarFileMetricsResult(file, success = true, values)
          case Failure(ex) =>
            log.error(s"Unexpected failure while processing JAR file", ex)
            JarFileMetricsResult.analysisFailed(file)
        }

        log.info(s"Done processing JAR file ${file.getName}")

      } else {
        val metricResults = handleBatch(new File(inputFilePath), analysisOptions, isLibraryFile)

        metricResults.foreach{ res =>
          log.info(s"Results for ${res.jarFile.getName}:")
          res.metricValues.foreach{ v =>
            log.info(s"\t-${v.metricName}: ${v.value}")
          }
        }
      }
    }

  }

  private def handleBatch(inputDirectory: File, customOptions: OptionMap, loadAsLibrary: Boolean): Iterable[JarFileMetricsResult] = {

    log.info(s"Batch mode: Processing all JAR files in ${inputDirectory.getPath}...")
    var count = 0

    val results = inputDirectory
      .listFiles(new FilenameFilter {
        override def accept(dir: File, name: String): Boolean = name.toLowerCase.endsWith(".jar")
      })
      .map{ file =>
        count += 1
        val project = ClassStreamReader.createProject(file.toURI.toURL, loadAsLibrary)

        log.debug(s"Successfully initialized OPAL project for ${file.getName}")

        doSingleFileAnalysis(project, customOptions) match {
          case Success(values) =>
            JarFileMetricsResult(file, success = true, values)
          case Failure(ex) =>
            log.error(s"Unexpected failure while processing JAR file", ex)
            JarFileMetricsResult.analysisFailed(file)
        }
      }
      .toList

    log.info(s"Batch mode: Done processing $count JAR files in ${inputDirectory.getName}")

    results
  }

  final def main(arguments: Array[String]): Unit = {
    val parser: CliParser = SingleFileAnalysisCliParser()
    parser.parseArguments(arguments.toList) match {
      case Success((appOptions, _))
        if !appOptions.contains(CliParser.inFileSymbol) =>
        log.error("Missing required parameter infile")
      case Success((appOptions, analysisOptions)) =>
        initializeAnalysis(appOptions, analysisOptions)
      case Failure(ex) =>
        log.error(s"Error parsing options: ${ex.getMessage}")
    }

  }


}
