package org.tud.sse.metrics
package impl
import singlefileanalysis.SingleFileAnalysis

object DefaultSingleFileAnalysisApplication extends SingleFileAnalysisApplication {

  override protected def registeredAnalyses(): Seq[SingleFileAnalysis] = Seq(
    new NumberOfMethodsAnalysis(),
    new CgTestImpl()
  )
}
