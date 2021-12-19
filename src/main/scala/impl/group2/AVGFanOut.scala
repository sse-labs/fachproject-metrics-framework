package org.tud.sse.metrics
package impl.group2

import analysis.{MetricValue, MetricsResult, MultiFileAnalysis}

import org.opalj.br.analyses.Project
import org.tud.sse.metrics.input.CliParser.OptionMap

import java.io.File
import java.net.URL
import scala.util.Try

/**
 * Implementation of the avg FanOut metrics analysis that processes a batch of JAR files
 * and calculates the average number of called subprograms plus the global variables set.
 * The type of intermediate results is INT
 *
 * @param directory Directory containing all JAR files
 */

class AVGFanOut(directory: File) extends MultiFileAnalysis[Int](directory){

  /**
   * The name for this analysis implementation. Will be used to include and exclude analyses via CLI.
   */
  override def analysisName: String = "fanOut.average"

  /**
   * This method is called to execute the analysis for each JAR file individually.
   * It calculates the intermediate results of type Try[T], which will be stored in the
   * analysisResultsPerFile map automatically by the enclosing analyzeNext call.
   *
   * @param project       Fully initialized OPAL project representing the JAR file under analysis
   * @param lastResult    Option that contains the intermediate result for the previous JAR file, if
   *                      available. This makes differential analyses easier to implement. This argument
   *                      may be None if either this is the first JAR file or the last calculation failed.
   * @param customOptions Custom analysis options taken from the CLI. Can be used to modify behavior
   *                      of the analysis via command-line
   * @return number of methode
   */
  override protected def produceAnalysisResultForJAR(project: Project[URL],
                                                     lastResult: Option[Int],
                                                     customOptions: OptionMap): Try[Int] ={
    Try(project.methodsCount)
  }

  /**
   * This method is called after all individual intermediate results have been calculated. It may
   * consume those intermediate results and produce a list of JAR file metrics, which can either
   * concern each JAR file individually, or the batch of analyzed files as a whole.
   *
   * @return List of JarFileMetricsResults
   */
  override def produceMetricValues(): List[MetricsResult] = {
    val averageFanOut = analysisResultsPerFile.values.map(_.get).sum
    val averageNewFanOut = averageFanOut.toDouble / analysisResultsPerFile.size.toDouble
    val metricList = List(MetricValue("file","fanOut.average", averageFanOut ))
    List(MetricsResult(analysisName, directory, success = true, metricList))
  }


}
