package org.tud.sse.metrics
package impl.group2

import java.net.URL
import scala.util.Try
import scala.collection.mutable

import org.opalj.br.{Method, ReferenceType}
import org.opalj.br.analyses.Project
import org.opalj.br.instructions.{NEW,MethodInvocationInstruction,FieldAccess}

import analysis.{MethodAnalysis, MetricValue}
import input.CliParser.OptionMap

/*
 * Implementation of metric "Classes Referenced" (CREF)
 *
 * For a given method the metric CREF returns the number of classes
 * the method makes reference to (i.e. classes used by a method).
 *
 * An object is counted as a reference if the method "acts on it", i.e.
 *  - instance creation (new)
 *  - method invocation
 *  - field access (read/write).
 *  - return type and parameters (even if not used)
 *
 * Multiple references to the same class are only counted once.
 *
 * References to the class the examined method belongs to are not counted.
 *
 */
class ClassesReferencedAnalysis extends MethodAnalysis {

  override def analysisName: String = "methods.CRef"

  override def analyzeMethod(method: Method, project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try {
    if (project.isProjectType(method.classFile.thisType)) {
      val crefs = mutable.Set[ReferenceType]()

      /* include return type (if not primitive) */
      if (method.returnType.isReferenceType)
        crefs.add(method.returnType.asReferenceType)

      /* include types from parameter list */
      method.parameterTypes
        .filter(pType => pType.isReferenceType)
        .foreach(pType => crefs.add(pType.asReferenceType))

      /* include types from method body */
      method.body match {
        case None =>
        case Some(code) => code.instructions.foreach {
          case _new: NEW => crefs.add(_new.objectType.asReferenceType)
          case invocation: MethodInvocationInstruction => crefs.add(invocation.declaringClass.asReferenceType)
          case fieldAccess: FieldAccess => crefs.add(fieldAccess.declaringClass.asReferenceType)
          case _ =>
        }
      }

      /* remove class the method belongs to */
      crefs.remove(method.classFile.thisType)

      /* return result */
      List(MetricValue(method.fullyQualifiedSignature, this.analysisName, crefs.size))
    } else {
      List.empty
    }
  }
}
