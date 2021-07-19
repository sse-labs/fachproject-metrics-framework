package org.tud.sse.metrics
package impl

import multifileanalysis.MultiFileAnalysis

import org.opalj.br.analyses.Project
import input.CliParser.OptionMap

import java.io.File
import java.net.URL
import scala.util.Try

/**
 * Example implementation of a metrics analysis that processes a batch of JAR files
 * and calculates the average number of methods. The type of intermediate results
 * is INT, as it is the number of methods per JAR file.
 *
 * @param directory Directory containing all JAR files
 * @param customOptions Custom analysis options that have been passed via CLI
 */
class AverageNumberOfMethodsAnalysis(directory: File, customOptions: OptionMap)
  extends MultiFileAnalysis[Int](directory, customOptions) {

  /**
   * Produces intermediate results for each JAR file. For this example, the intermediate result
   * is the number of methods for the JAR file.
   * @param project Initialized OPAL project of the JAR file
   * @param lastResult Result of the last JAR file (not required here)
   * @return Number of methods
   */
  override def produceAnalysisResultForJAR(project: Project[URL],
                                           lastResult: Option[Int]): Try[Int] = {
    Try(project.methodsCount)
  }

  /**
   * Produces the metric results for this analysis, based on the intermediate results created
   * for each JAR file earlier. In this case, it calculates the average of all method counts.
   * @return
   */
  override def produceMetricValues(): List[JarFileMetricsResult] = {
    val averageMethods =
      analysisResultsPerFile.values.map(_.get).sum.toDouble / analysisResultsPerFile.size.toDouble

    val metricList = List(JarFileMetricValue("methods.average", averageMethods))
    List(JarFileMetricsResult(directory, success = true, metricList))
  }
}

/**
 * Example implementation for an MultiFileAnalysis Application that uses the
 * AverageNumberOfMethodsAnalysis implementation above to analyze a set of
 * JAR files
 */
object AverageMethodsAnalysisApp extends MultiFileAnalysisApplication[Int] {

  override protected def buildAnalysis(directory: File,
                                       analysisOptions: OptionMap): MultiFileAnalysis[Int] =
    new AverageNumberOfMethodsAnalysis(directory, analysisOptions)

}
