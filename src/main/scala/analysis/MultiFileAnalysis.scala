package org.tud.sse.metrics
package analysis

import input.CliParser.OptionMap

import org.opalj.br.analyses.Project

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
 * @tparam T Type of the intermediate results produced for each JAR file in the folder
 *
 * @author Johannes Düsing
 */
abstract class MultiFileAnalysis[T](directory: File) extends NamedAnalysis {

  /**
   * Map that contains the intermediate results for each JAR file. Technically the map contains
   * values of type Try[T], as the calculation of intermediate results may fail. This map is
   * being filled automatically by an enclosing MultiFileAnalysisApplication.
   */
  protected val analysisResultsPerFile: mutable.Map[File, Try[T]] = mutable.HashMap[File, Try[T]]()

  /**
   * Result of the last file analysis performed by this instance
   */
  private var lastResult: Option[T] = None

  /**
   * This method is called by an enclosing MultiFileAnalysisApplication for each JAR file individually.
   * It calculates the intermediate results of type Try[T], keeps track of the last file results and updates
   * the analysisResultsPerFile map automatically.
   *
   * @param project Fully initialized OPAL project representing the JAR file under analysis
   * @param file File that is currently being analyzed
   * @param customOptions Custom analysis options taken from the CLI. Can be used to modify behavior
   *                      of the analysis via command-line
   * @return Result of the analysis execution
   */
  final def analyzeNext(project:Project[URL], file: File, customOptions: OptionMap): Try[T] = {

    val result = produceAnalysisResultForJAR(project, file, lastResult, customOptions)

    analysisResultsPerFile.put(file, result)

    lastResult = result.toOption

    result
  }

  /**
   * This method is called to execute the analysis for each JAR file individually.
   * It calculates the intermediate results of type Try[T], which will be stored in the
   * analysisResultsPerFile map automatically by the enclosing analyzeNext call.
   *
   * @param project Fully initialized OPAL project representing the JAR file under analysis
   * @param lastResult Option that contains the intermediate result for the previous JAR file, if
   *                   available. This makes differential analyses easier to implement. This argument
   *                   may be None if either this is the first JAR file or the last calculation failed.
   * @param customOptions Custom analysis options taken from the CLI. Can be used to modify behavior
   *                      of the analysis via command-line
   * @return Try[T] object holding the intermediate result, if successful
   */
  protected def produceAnalysisResultForJAR(project: Project[URL], lastResult: Option[T],
                                            customOptions: OptionMap): Try[T]


  /**
   * This method is called to execute the analysis for each JAR file individually.
   * It calculates the intermediate results of type Try[T], which will be stored in the
   * analysisResultsPerFile map automatically by the enclosing analyzeNext call.
   *
   * @param project Fully initialized OPAL project representing the JAR file under analysis
   * @param file The file object for which the OPAL project has been generated
   * @param lastResult Option that contains the intermediate result for the previous JAR file, if
   *                   available. This makes differential analyses easier to implement. This argument
   *                   may be None if either this is the first JAR file or the last calculation failed.
   * @param customOptions Custom analysis options taken from the CLI. Can be used to modify behavior
   *                      of the analysis via command-line
   * @return Try[T] object holding the intermediate result, if successful
   */
  protected def produceAnalysisResultForJAR(project: Project[URL], file: File,
                                            lastResult: Option[T], customOptions: OptionMap): Try[T] = {
    produceAnalysisResultForJAR(project, lastResult, customOptions)
  }

  /**
   * This method is called after all individual intermediate results have been calculated. It may
   * consume those intermediate results and produce a list of JAR file metrics, which can either
   * concern each JAR file individually, or the batch of analyzed files as a whole.
   *
   * @return List of JarFileMetricsResults
   */
  def produceMetricValues(): List[MetricsResult]

}
