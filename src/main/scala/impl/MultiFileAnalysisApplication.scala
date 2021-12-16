package org.tud.sse.metrics
package impl

import analysis.MultiFileAnalysis
import application.MultiFileAnalysisApplication

import java.io.File

object MultiFileAnalysisApplication extends MultiFileAnalysisApplication {
  override protected def buildAnalyses(jarDirectory: File): Seq[MultiFileAnalysis[_]] = Seq(

  )
}
