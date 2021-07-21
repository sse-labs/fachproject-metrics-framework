package org.tud.sse.metrics
package singlefileanalysis

import org.opalj.br.analyses.Project
import org.slf4j.{Logger, LoggerFactory}
import input.CliParser.OptionMap

import java.net.URL
import scala.util.Try

/**
 * Base Trait for all SingleFileAnalyses. A SingleFileAnalysis calculates a set of metrics for a single
 * JAR file at a time, without considering additional information.
 *
 * @author Johannes Düsing
 */
trait SingleFileAnalysis {

  /**
   * The logger for this instance
   */
  protected val log: Logger = LoggerFactory.getLogger(this.getClass)

  /**
   * This method is being called by an enclosing SingleFileAnalysisApplication after this analysis
   * is initialized, but before any JAR files are being processed. It can be used to initialize
   * custom data structures.
   */
  def initialize(): Unit = {
    log.info("Analysis initialized")
  }

  /**
   * This method is called by an enclosing SingleFileAnalysisApplication for an individual JAR file.
   * It calculates a set of metric values. As a SingleFileAnalysisApplication may be invoked in
   * batch mode, this method may be called multiple times for different JAR files. However, it
   * should typically be stateless, for state-dependent analyses on multiple JAR files a
   * MultiFileAnalysis should be used.
   *
   * @param project Fully initialized OPAL project representing the JAR file under analysis
   * @param customOptions Custom analysis options taken from the CLI. Can be used to modify behavior
   *                      of the analysis via command-line
   * @return Try object that, if successful, holds a set of metrics values.
   */
  def analyzeProject(project: Project[URL],
                     customOptions: OptionMap): Try[Iterable[JarFileMetricValue]]


}
