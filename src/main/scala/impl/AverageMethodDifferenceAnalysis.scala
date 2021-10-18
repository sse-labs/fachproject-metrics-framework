package org.tud.sse.metrics
package impl

import analysis.{JarFileMetricValue, JarFileMetricsResult, MultiFileAnalysis}
import input.CliParser.OptionMap

import org.opalj.br.analyses.Project

import java.io.File
import java.net.URL
import scala.util.Try

class AverageMethodDifferenceAnalysis(jarDir: File) extends MultiFileAnalysis[Long](jarDir){

  override def produceAnalysisResultForJAR(project: Project[URL],
                                           lastResult: Option[Long],
                                           customOptions: OptionMap): Try[Long] = {
    Try(project.methodsCount - lastResult.getOrElse(0L))
  }

  override def produceMetricValues(): List[JarFileMetricsResult] = {

    val allNewMethodCounts = analysisResultsPerFile.values.map(_.get).sum
    val averageNewMethodCount = allNewMethodCounts.toDouble / analysisResultsPerFile.size
    val averageNewMethodMetric = JarFileMetricValue("newmethodcount.average", averageNewMethodCount)

    List(JarFileMetricsResult(analysisName, jarDir, success = true, List(averageNewMethodMetric)))
  }

  override def analysisName: String = "method-difference.avg"
}
