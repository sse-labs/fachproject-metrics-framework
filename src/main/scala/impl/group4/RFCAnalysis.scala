package org.tud.sse.metrics
package impl.group4

import analysis.{ClassFileAnalysis, MetricValue}
import input.CliParser.OptionMap

import org.opalj.br.ClassFile
import org.opalj.br.analyses.Project

import java.net.URL
import scala.util.Try

/**
 * RFCAnalysis implements the Response For a Class (RFC) metric
 */
class RFCAnalysis extends ClassFileAnalysis {

  private val countPublicMethodsOnlySymbol: Symbol = Symbol("count-public-only")

  /**
   * Calculates the value of the RFC Metric by adding the number of methods in the class to the number of method invocations in each method
   *
   * @param classFile     is a class in the project.
   * @param project       Fully initialize OPAL project representing the JAR file under analysis.
   * @param customOptions Custom analysis options taken from the CLI. Can be used to modify behavior
   *                      of the analysis via command-line.
   * @return Try[Iterable[MetricValue]]  object holding the intermediate result, if successful.
   */
  override def analyzeClassFile(classFile: ClassFile, project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try {
    val onlyCountPublicMethods = customOptions.contains(countPublicMethodsOnlySymbol)
    var rfc = 0
    classFile.methods.foreach(m=> {
      if((!onlyCountPublicMethods & m.isPrivate)||(m.isPublic) ){
        rfc=rfc+1
      }
      if(m.body.isDefined) m.body.get.instructions.foreach(i => {
        if(i!=null) if (i.isMethodInvocationInstruction) rfc = rfc + 1
      })
    })

    List(MetricValue(classFile.fqn, "class.rfc", rfc))
  }
  /**
   * The name for this analysis implementation.
   */
  override def analysisName: String = "rfc"
}
