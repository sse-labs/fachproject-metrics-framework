package org.tud.sse.metrics
package impl

import analysis.MultiFileAnalysis
import application.MultiFileAnalysisApplication

import java.io.File

object ExternalStabilityApplication extends MultiFileAnalysisApplication {
  override protected def buildAnalyses(jarDirectory: File): Seq[MultiFileAnalysis[_]] = Seq(
    new ExternalStabilityAnalysis(jarDirectory),
   // new AverageNumberOfMethodsAnalysis(jarDirectory)
  )
}
