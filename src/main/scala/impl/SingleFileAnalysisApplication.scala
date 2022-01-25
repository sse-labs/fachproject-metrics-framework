package org.tud.sse.metrics
package impl

import analysis.SingleFileAnalysis
import application.SingleFileAnalysisApplication
import impl.group1.{AverageMaximumNestingAnalysis, DepthOfInheritanceTreeAnalysis, MaximumNestingAnalysis, NumberOfChildrenAnalysis}
import impl.group2.{AVGFanInAnalysis, AVGFanOutAnalysis, ClassesReferencedAnalysis, LackOfCohesionInMethodsAnalysis, NumberOfFunctionsAnalysis}
import impl.group3.{CBOAnalysis, LCCAnalysis, LOCproAnalysis, TCCAnalysis}
import impl.group4.{DACAnalysis, LOCphyAnalysis, MCCCAnalysis, RFCAnalysis}
import impl.group5.{NumberOfLoopsAnalysis, NumberOfVariablesDeclaredAnalysis, WeightedMethodsPerClassAnalysis, vrefAnalysis}


object SingleFileAnalysisApplication extends SingleFileAnalysisApplication {

  override protected val registeredAnalyses: Seq[SingleFileAnalysis] = Seq(

    //Gruppe 1
    new AverageMaximumNestingAnalysis(),
    new DepthOfInheritanceTreeAnalysis(),
    new MaximumNestingAnalysis(),
    new NumberOfChildrenAnalysis(),

    //Gruppe 2
    new AVGFanInAnalysis(),
    new AVGFanOutAnalysis(),
    new ClassesReferencedAnalysis(),
    new LackOfCohesionInMethodsAnalysis(),
    new NumberOfFunctionsAnalysis(),

    //Gruppe 3
    new CBOAnalysis(),
    new LCCAnalysis(),
    new LOCproAnalysis(),
    new TCCAnalysis(),

    // Gruppe 4
    new LOCphyAnalysis(),
    new MCCCAnalysis(),
    new DACAnalysis(),
    new RFCAnalysis(),

    //Gruppe 5
    new NumberOfLoopsAnalysis(),
    new NumberOfVariablesDeclaredAnalysis(),
    new WeightedMethodsPerClassAnalysis(),
    new vrefAnalysis()
  )
}
