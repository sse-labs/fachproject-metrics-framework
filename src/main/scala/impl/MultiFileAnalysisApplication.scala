package org.tud.sse.metrics
package impl

import analysis.MultiFileAnalysis
import application.MultiFileAnalysisApplication
import impl.group1
import impl.group1.{EvolutionAnalysis, ExternalStabilityAnalysis}

import org.tud.sse.metrics.impl.group5.InternalStabilityAnalysis

import java.io.File

object MultiFileAnalysisApplication extends MultiFileAnalysisApplication {
  override protected def buildAnalyses(jarDirectory: File): Seq[MultiFileAnalysis[_]] = Seq(
    new InternalStabilityAnalysis(jarDirectory),
    new EvolutionAnalysis(jarDirectory),
    new ExternalStabilityAnalysis(jarDirectory)

  )
}
