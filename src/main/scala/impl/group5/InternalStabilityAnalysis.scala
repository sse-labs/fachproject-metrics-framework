package org.tud.sse.metrics
package impl.group5

import analysis.{MetricValue, MetricsResult, MultiFileAnalysis}

import org.opalj.br.analyses.Project
import org.tud.sse.metrics.input.CliParser.OptionMap

import java.io.File
import java.net.URL
import scala.collection.mutable.ListBuffer
import scala.util.Try

class InternalStabilityAnalysis(jarDir: File) extends MultiFileAnalysis[(Double, Double, Double, String)](jarDir) {

  var currentfile: String = ""
  var previousfile: String = ""

  override def produceAnalysisResultForJAR(project: Project[URL], file: File,
                                           lastResult: Option[(Double, Double, Double, String)],
                                           customOptions: OptionMap): Try[(Double, Double, Double, String)] = {
    currentfile = file.toString
    produceAnalysisResultForJAR(project, lastResult, customOptions)
  }

  override def produceAnalysisResultForJAR(project: Project[URL], lastResult: Option[(Double, Double, Double, String)], customOptions: OptionMap): Try[(Double, Double, Double, String)] = {

    var entity_ident: String = "Difference between: " + previousfile + " and " + currentfile
    Try(1.0, 2,3, entity_ident)
  }


  override def produceMetricValues(): List[MetricsResult] = {

    val MetricsResultBuffer = collection.mutable.ListBuffer[MetricsResult]()





    MetricsResultBuffer.toList
  }


  override def analysisName: String = "Internal Stability"
}
