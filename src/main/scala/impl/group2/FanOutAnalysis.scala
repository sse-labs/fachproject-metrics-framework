package org.tud.sse.metrics
package impl.group2

import java.net.URL
import scala.util.Try

import org.opalj.br.Method
import org.opalj.br.analyses.Project
import org.opalj.br.instructions.{InvocationInstruction,FieldWriteAccess}

import input.CliParser.OptionMap
import analysis.{MethodAnalysis, MetricValue}


/*
 * Implementation of metric FanOut (aka CountOutput)
 *
 * The metric counts the number of "outputs" a method can make.
 * An output is:
 *  - a method call,
 *  - a parameter set when calling a method, and
 *  - a non-local variable that is set
 *
 */
class FanOutAnalysis extends MethodAnalysis {

  override def analysisName: String = "methods.fanout"

  override def analyzeMethod(method: Method, project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try {
    if (project.isProjectType(method.classFile.thisType)) {
      List(MetricValue(method.fullyQualifiedSignature, this.analysisName, FanOut.calc(method)))
    } else {
      List.empty
    }
  }
}

object FanOut {
  /* calculate the FanOut of the given method */
  def calc(method: Method): Int = {
    if (method.body.nonEmpty) {
      method.body.get.instructions.map {
        case invocation: InvocationInstruction => 1 + invocation.methodDescriptor.parameterTypes.size // one output for method call plus size of parameter list
        case write: FieldWriteAccess => 1 // writing a field: one output
        case _ => 0 // other instructions do not increase the metric
      }.sum
    } else { 0 } // no code, no fanout
  }
}
