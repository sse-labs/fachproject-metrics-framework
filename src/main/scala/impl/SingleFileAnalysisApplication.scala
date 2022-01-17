package org.tud.sse.metrics
package impl

import analysis.SingleFileAnalysis
import application.SingleFileAnalysisApplication


object SingleFileAnalysisApplication extends SingleFileAnalysisApplication {

  override protected val registeredAnalyses: Seq[SingleFileAnalysis] = Seq(
  )
}
