package org.tud.sse.metrics
package multifileanalysis

import org.opalj.br.analyses.Project
import org.slf4j.{Logger, LoggerFactory}
import input.CliParser.OptionMap

import java.io.File
import java.net.URL
import scala.collection.mutable
import scala.util.Try

/**
 * Abstract base class for all MultiFileAnalyses. A MultiFileAnalysis calculates a set of metrics on
 * not just a single JAR file, but a set of JAR files contained in a single directory. This is done
 * by calculating custom intermediate results (of type T) for each JAR individually, before aggregating
 * those intermediate results into one or more metric values.
 *
 * @param directory Directory containing all JAR files that are being analyzed
 * @param customOptions Custom analysis options taken from the CLI. Can be used to modify behavior
 *                      of the analysis via command-line
 * @tparam T Type of the intermediate results produced for each JAR file in the folder
 *
 * @author Johannes Düsing
 */
abstract class MultiFileAnalysis[T](directory: File, customOptions: OptionMap) {

  /**
   * The logger for this instance
   */
  protected val log: Logger = LoggerFactory.getLogger(this.getClass)

  /**
   * Map that contains the intermediate results for each JAR file. Technically the map contains
   * values of type Try[T], as the calculation of intermediate results may fail. This map is
   * being filled automatically by an enclosing MultiFileAnalysisApplication.
   */
  val analysisResultsPerFile: mutable.Map[File, Try[T]] = mutable.HashMap[File, Try[T]]()

  /**
   * This method is being called by an enclosing MultiFileAnalysisApplication after this analysis
   * is initialized, but before any JAR files are being processed. It can be used to initialize
   * custom data structures.
   */
  def initialize(): Unit = {
    log.info("Analysis initialized")
  }

  /**
   * This method is called by an enclosing MultiFileAnalysisApplication for each JAR file individually.
   * It calculates the intermediate results of type Try[T], which will be stored in the
   * analysisResultsPerFile map automatically by an enclosing MultiFileAnalysisApplication.
   *
   * @param project Fully initialized OPAL project representing the JAR file under analysis
   * @param lastResult Option that contains the intermediate result for the previous JAR file, if
   *                   available. This makes differential analyses easier to implement. This argument
   *                   may be None if either this is the first JAR file or the last calculation failed.
   * @return Try[T] object holding the intermediate result, if successful
   */
  def produceAnalysisResultForJAR(project: Project[URL], lastResult: Option[T]): Try[T]

  /**
   * This method is called after all individual intermediate results have been calculated. It may
   * consume those intermediate results and produce a list of JAR file metrics, which can either
   * concern each JAR file individually, or the batch of analyzed files as a whole.
   *
   * @return List of JarFileMetricsResults
   */
  def produceMetricValues(): List[JarFileMetricsResult]

}
