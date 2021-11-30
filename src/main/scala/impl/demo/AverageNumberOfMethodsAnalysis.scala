package org.tud.sse.metrics
package impl.demo

import analysis.{MetricValue, MetricsResult, MultiFileAnalysis}
import input.CliParser.OptionMap

import org.opalj.br.analyses.Project

import java.io.File
import java.net.URL
import scala.util.Try

/**
 * Example implementation of a metrics analysis that processes a batch of JAR files
 * and calculates the average number of methods. The type of intermediate results
 * is INT, as it is the number of methods per JAR file.
 *
 * @param directory Directory containing all JAR files
 */
class AverageNumberOfMethodsAnalysis(directory: File)
  extends MultiFileAnalysis[Int](directory) {

  /**
   * Produces intermediate results for each JAR file. For this example, the intermediate result
   * is the number of methods for the JAR file.
   * @param project Initialized OPAL project of the JAR file
   * @param lastResult Result of the last JAR file (not required here)
   * @return Number of methods
   */
  override def produceAnalysisResultForJAR(project: Project[URL],
                                           lastResult: Option[Int],
                                           customOptions: OptionMap): Try[Int] = {
    Try(project.methodsCount)
  }

  /**
   * Produces the metric results for this analysis, based on the intermediate results created
   * for each JAR file earlier. In this case, it calculates the average of all method counts.
   * @return
   */
  override def produceMetricValues(): List[MetricsResult] = {
    val averageMethods =
      analysisResultsPerFile.values.map(_.get).sum.toDouble / analysisResultsPerFile.size.toDouble

    val metricList = List(MetricValue("file", "methods.average", averageMethods))
    List(MetricsResult(analysisName, directory, success = true, metricList))
  }

  override def analysisName: String = "methods.average"
}