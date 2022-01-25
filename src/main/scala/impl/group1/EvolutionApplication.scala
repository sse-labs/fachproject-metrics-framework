package org.tud.sse.metrics
package impl.group1

import java.io.File

import org.tud.sse.metrics.analysis.MultiFileAnalysis
import org.tud.sse.metrics.application.MultiFileAnalysisApplication

object EvolutionApplication extends MultiFileAnalysisApplication {
  /**
   * Method that builds the sequence of analyses available in this application. This method will only be
   * executed once the input validation has succeeded.
   *
   * @param jarDirectory File object representing the directory that is being analyzed
   * @return List of MultiFileAnalysis objects that can be selected for execution in this application
   */
  override protected def buildAnalyses(jarDirectory: File): Seq[MultiFileAnalysis[_]] = Seq(
    new EvolutionAnalysis(jarDirectory)
  )

}
