package org.tud.sse.metrics
package impl.group2

import java.net.URL
import scala.util.Try

import org.opalj.br.ClassFile
import org.opalj.br.analyses.Project

import input.CliParser.OptionMap
import analysis.{ClassFileAnalysis, MetricValue}


/*
 * Implementation of metric "Number of Functions"
 *
 * The metric counts the number of methods a class implements
 * (inherited methods and default constructors included).
 *
 */
class NumberOfFunctionsAnalysis extends ClassFileAnalysis {

  override def analysisName: String = "class.NumberOfFunctions"

  override def analyzeClassFile(classFile: ClassFile, project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try {
    List(MetricValue(classFile.thisType.fqn, this.analysisName, classFile.methods.size))
  }
}
