package org.tud.sse.metrics
package impl

import analysis.SingleFileAnalysis
import application.SingleFileAnalysisApplication

import impl.group5.NumberOfLoopsAnalysis

object SingleFileAnalysisApplication extends SingleFileAnalysisApplication {

  override protected val registeredAnalyses: Seq[SingleFileAnalysis] = Seq(
    new NumberOfLoopsAnalysis()
  )
}
