package org.tud.sse.metrics
package impl.demo

import analysis.SingleFileAnalysis
import application.SingleFileAnalysisApplication
import impl.group3.LCCAnalysis
import impl.group3.CBOAnalysis


object DefaultSingleFileAnalysisApplication extends SingleFileAnalysisApplication {

  override protected val registeredAnalyses: Seq[SingleFileAnalysis] = Seq(
//    new NumberOfMethodsAnalysis(),
//    new MethodSizeAnalysis(),
      new LCCAnalysis(),
      new CBOAnalysis()
  )
}
