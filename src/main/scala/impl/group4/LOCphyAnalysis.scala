package org.tud.sse.metrics
package impl.group4

import analysis.{MethodAnalysis, MetricValue}
import input.CliParser.OptionMap

import org.opalj.br.Method
import org.opalj.br.analyses.Project

import java.net.URL
import scala.util.Try

class LOCphyAnalysis extends MethodAnalysis{
  /**
   * Calculates the value of the LOCphy Metric by subtracting the line numbers of the last and first elements of the line-number-table .
   */
  override def analyzeMethod(m: Method, project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try {
    if (project.isProjectType(m.classFile.thisType) && m.body.isDefined && m.body.get.lineNumberTable.isDefined) {
      List(MetricValue(m.fullyQualifiedSignature, "method.locphy", m.body.get.lineNumberTable.get.lineNumbers.last.lineNumber - m.body.get.lineNumberTable.get.lineNumbers.head.lineNumber))
    }
    else List.empty}
  /**
   * The name for this analysis implementation. Will be used to include and exclude analyses via CLI.
   */
  override def analysisName: String = "locphy"
}