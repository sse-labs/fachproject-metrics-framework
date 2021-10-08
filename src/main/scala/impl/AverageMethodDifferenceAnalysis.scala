package org.tud.sse.metrics
package impl

import multifileanalysis.MultiFileAnalysis

import org.opalj.br.analyses.Project
import org.tud.sse.metrics.input.CliParser.OptionMap

import java.io.File
import java.net.URL
import scala.util.Try

class AverageMethodDifferenceAnalysis(jarDir: File,
                                      customOptions: OptionMap)
  extends MultiFileAnalysis[Int](jarDir, customOptions){

  override def produceAnalysisResultForJAR(project: Project[URL],
                                           lastResult: Option[Int]): Try[Int] = {
    Try(project.methodsCount - lastResult.getOrElse(0))
  }

  override def produceMetricValues(): List[JarFileMetricsResult] = {

    val allNewMethodCounts = analysisResultsPerFile.values.map(_.get).sum
    val averageNewMethodCount = allNewMethodCounts.toDouble / analysisResultsPerFile.size
    val averageNewMethodMetric = JarFileMetricValue("newmethodcount.average", averageNewMethodCount)

    List(JarFileMetricsResult(analysisName, jarDir, success = true, List(averageNewMethodMetric)))
  }

  override def analysisName: String = "method-difference.avg"
}

object AverageMethodsDifferenceAnalysisApp extends MultiFileAnalysisApplication[Int] {

  override protected def buildAnalysis(directory: File,
                                       analysisOptions: OptionMap): MultiFileAnalysis[Int] =
    new AverageMethodDifferenceAnalysis(directory, analysisOptions)

}
