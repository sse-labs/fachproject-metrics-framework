package org.tud.sse.metrics
package impl.group2

import java.net.URL
import scala.util.Try
import scala.collection.mutable

import org.opalj.br.analyses.Project
import org.opalj.br.instructions.{MethodInvocationInstruction,FieldReadAccess}

import input.CliParser.OptionMap
import analysis.{SingleFileAnalysis,MetricValue}


/*
 * Implementation of metric FanIn (aka CountInput)
 *
 * The metric counts the number of "inputs" a method has.
 * The inputs of a method are:
 *  - the number of methods in the project that call the method,
 *  - the number of non-local variables the method reads, and
 *  - the number of parameters the method takes.
 *
 *
 * Implementation note:
 * To calculate the FanIn of a single method, every other method in the project needs to be considered.
 * Therefor SingleFileAnalysis (instead of MethodAnalysis) is used to calculate the metric for every
 * method in one go. The hope is, that this keeps the time complexity (in the number of methods) linear.
 *
 */
class FanInAnalysis extends SingleFileAnalysis {

  override def analysisName: String = "methods.FanIn"

  override def analyzeProject(project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try {
    // all project methods
    val projectMethods = project.allMethods.filter(m => project.isProjectType(m.classFile.thisType))
    // the number of calls made to a method
    val call_counts = mutable.Map[String,Int]()
    // the number of non-local reads of a method
    val field_reads = mutable.Map[String,Int]()

    projectMethods.foreach(method => {
      method.body match {
        case Some(code) =>
          // get the set of methods this method can invoke
          code.instructions.collect { case invc: MethodInvocationInstruction => fqs(invc) }.toSet
            // and update the call counts of these methods
            .foreach(m => call_counts.update(m, call_counts.getOrElse(m, 0) + 1))

          // get number of unique field reads
          field_reads.update(method.fullyQualifiedSignature,
            code.instructions.collect { case read: FieldReadAccess => read.toString() }.toSet.size
          )

        case None => // empty method, nothing to do
      }
    })

    // build the list of metric values
    for (method <- projectMethods) yield {
      val name = method.fullyQualifiedSignature
      MetricValue(name, analysisName,
        // sum up the actual metric value
        method.parameterTypes.size + call_counts.getOrElse(name, 0) + field_reads.getOrElse(name, 0)
      )
    }
  }

  /*
   * get the "fully qualified signature" for a method from an invocation instruction
   * this needs to return the same as Method.fullyQualifiedSignature()
   */
  private def fqs(invc: MethodInvocationInstruction): String =
    invc.methodDescriptor.toJava(s"${invc.declaringClass.asObjectType.toJava}.${invc.name}")
}
