package org.tud.sse.metrics
package impl.group4

import analysis.{MethodAnalysis, MetricValue}
import input.CliParser.OptionMap

import org.opalj.br.Method
import org.opalj.br.analyses.Project

import java.net.URL
import scala.util.Try


/**
 * Lines of code simply counts the lines of source code (including line break characters and comments) of a method.
 */

class LOCphyAnalysis extends MethodAnalysis{
  /**
   * This method is called to analyse each method individually.
   * It calculates the value of the LOCphy Metric by subtracting the line numbers of the last and
   * first elements of the line-number-table .
   *
   * @param m             is a method object in a class.
   * @param project       Fully initialize OPAL project representing the JAR file under analysis.
   * @param customOptions Custom analysis options taken from the CLI. Can be used to modify behavior
   *                      of the analysis via command-line.
   * @return Try[Iterable[MetricValue]]  object holding the intermediate result, if successful.
   */
  override def analyzeMethod(m: Method, project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try {

    val table =  m.body.get.lineNumberTable.get.lineNumbers

    if (project.isProjectType(m.classFile.thisType) && m.body.isDefined && m.body.get.lineNumberTable.isDefined) {
      List(MetricValue(m.fullyQualifiedSignature, "method.locphy",
       table.last.lineNumber - table.head.lineNumber))
    }
    else List.empty}
  /**
   * The name for this analysis implementation. Will be used to include and exclude analyses via CLI.
   */
  override def analysisName: String = "locphy"
}