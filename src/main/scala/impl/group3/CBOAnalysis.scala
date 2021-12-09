package org.tud.sse.metrics
package impl.group3


import analysis.{MetricValue, SingleFileAnalysis}
import input.CliParser.OptionMap

import org.opalj.br.analyses.Project

import java.net.URL
import scala.util.Try

class CBOAnalysis extends SingleFileAnalysis{

   def analyzeProject(project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try {
    //calculate the metric
     log.info("Please implement the CBO Metric here")
      List.empty
  }

  /**
   * The name for this analysis implementation. Will be used to include and exclude analyses via CLI.
   */
  override def analysisName: String = "metric.cbo"
}
