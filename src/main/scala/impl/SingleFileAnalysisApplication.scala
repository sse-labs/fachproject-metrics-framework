package org.tud.sse.metrics
package impl

import analysis.SingleFileAnalysis
import application.SingleFileAnalysisApplication
import impl.group5.{NumberOfLoopsAnalysis, NumberOfVariablesDeclaredAnalysis, WeightedMethodsPerClassAnalysis, vrefAnalysis}

import org.tud.sse.metrics.impl.group1.{AverageMaximumNestingAnalysis, DepthOfInheritanceTreeAnalysis, MaximumNestingAnalysis, NumberOfChildrenAnalysis}
import impl.group2.{AVGFanInAnalysis, AVGFanOutAnalysis, ClassesReferencedAnalysis, FanInAnalysis, LackOfCohesionInMethodsAnalysis, NumberOfFunctionsAnalysis}

import org.tud.sse.metrics.impl.group3.{CBOAnalysis, LCCAnalysis, TCCAnalysis}
import org.tud.sse.metrics.impl.group4.{LOCphyAnalysis, MCCCAnalysis}

object SingleFileAnalysisApplication extends SingleFileAnalysisApplication {

  override protected val registeredAnalyses: Seq[SingleFileAnalysis] = Seq(
    new NumberOfLoopsAnalysis(),
    new NumberOfVariablesDeclaredAnalysis(),
    new vrefAnalysis(),
    new WeightedMethodsPerClassAnalysis(),
    new LOCphyAnalysis(),
    new MCCCAnalysis(),
    new TCCAnalysis(),
    new LCCAnalysis(),
    new CBOAnalysis(),
    new NumberOfChildrenAnalysis(),
    new MaximumNestingAnalysis(),
    new DepthOfInheritanceTreeAnalysis(),
    new AverageMaximumNestingAnalysis(),
    new NumberOfFunctionsAnalysis(),
    new FanInAnalysis(),
    new LackOfCohesionInMethodsAnalysis(),
    new ClassesReferencedAnalysis(),
    new AVGFanInAnalysis(),
    new AVGFanOutAnalysis(),


  )
}
