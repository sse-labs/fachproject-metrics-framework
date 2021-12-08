package org.tud.sse.metrics
package impl.group3

import analysis.{MetricValue, SingleFileAnalysis}
import input.CliParser.OptionMap

import org.opalj.br.analyses.Project

import java.net.URL
import scala.util.Try

class TCCAnalysis extends SingleFileAnalysis {

  override def analyzeProject(project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try {
    // calculate the metric
    log.info("please calculate the metric here")
    val metricsResult = 100

    List(MetricValue("file", this.analysisName, metricsResult))
  }

  override def analysisName: String = "metric.tcc"
}