package org.tud.sse.metrics
package impl

import analysis.MultiFileAnalysis
import application.MultiFileAnalysisApplication

import impl.group1.{EvolutionAnalysis, ExternalStabilityAnalysis}
import impl.group5.InternalStabilityAnalysis

import java.io.File

object MultiFileAnalysisApplication extends MultiFileAnalysisApplication {
  override protected def buildAnalyses(jarDirectory: File): Seq[MultiFileAnalysis[_]] = Seq(
    //Gruppe 1
    new EvolutionAnalysis(jarDirectory),
    new ExternalStabilityAnalysis(jarDirectory),

    //Gruppe 5
    new InternalStabilityAnalysis(jarDirectory)
  )
}
