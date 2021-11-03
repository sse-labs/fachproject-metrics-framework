package org.tud.sse.metrics
package analysis

import input.CliParser.OptionMap

import org.opalj.br.analyses.Project

import java.net.URL
import scala.util.Try

/**
 * Base Trait for all SingleFileAnalyses. A SingleFileAnalysis calculates a set of metrics for a single
 * JAR file at a time, without considering additional information.
 *
 * @author Johannes Düsing
 */
trait SingleFileAnalysis extends NamedAnalysis {

  /**
   * This method is called by an enclosing SingleFileAnalysisApplication for an individual JAR file.
   * It calculates a set of metric values. As a SingleFileAnalysisApplication may be invoked in
   * batch mode, this method may be called multiple times for different JAR files. However, it
   * should typically be stateless, for state-dependent analyses on multiple JAR files a
   * MultiFileAnalysis should be used.
   *
   * @param project       Fully initialized OPAL project representing the JAR file under analysis
   * @param customOptions Custom analysis options taken from the CLI. Can be used to modify behavior
   *                      of the analysis via command-line
   * @return Try object that, if successful, holds a set of metrics values.
   */
  def analyzeProject(project: Project[URL],
                     customOptions: OptionMap): Try[Iterable[MetricValue]]


}
