package org.tud.sse.metrics
package impl.group5

import analysis.{MetricsResult, MultiFileAnalysis}

import org.opalj.br.analyses.Project
import org.tud.sse.metrics.input.CliParser.OptionMap

import java.io.File
import java.net.URL
import scala.util.Try

class InternalStabilityAnalysis(jarDir: File) extends MultiFileAnalysis[(Double, Double, Double, String)](jarDir) {


  override def produceAnalysisResultForJAR(project: Project[URL], file: File,
                                           lastResult: Option[(Double, Double, Double, String)],
                                           customOptions: OptionMap): Try[(Double, Double, Double, String)] = {
    currentfile = file.toString
    produceAnalysisResultForJAR(project, lastResult, customOptions)
  }

  override def produceAnalysisResultForJAR(project: Project[URL],
                                           lastResult: Option[(Double, Double, Double, String)],
                                           customOptions: OptionMap): Try[(Double, Double, Double, String)] = {


  }


  override def produceMetricValues(): List[MetricsResult] = {

    val MetricsResultBuffer = collection.mutable.ListBuffer[MetricsResult]()





    MetricsResultBuffer.toList
  }


  override def analysisName: String = "Internal Stability"
}
