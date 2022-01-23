package org.tud.sse.metrics
package impl

import analysis.MultiFileAnalysis
import application.MultiFileAnalysisApplication

import org.tud.sse.metrics.impl.group5.InternalStabilityAnalysis

import java.io.File

object MultiFileAnalysisApplication extends MultiFileAnalysisApplication {
  override protected def buildAnalyses(jarDirectory: File): Seq[MultiFileAnalysis[_]] = Seq(
    new InternalStabilityAnalysis(jarDirectory)
  )
}
