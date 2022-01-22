package org.tud.sse.metrics
package impl.group1

import analysis.SingleFileAnalysis
import application.SingleFileAnalysisApplication

object AverageMaximumNestingAnalysisApplication extends SingleFileAnalysisApplication {

  override protected val registeredAnalyses: Seq[SingleFileAnalysis] = Seq(
    new AverageMaximumNestingAnalysis(),
  )
}
