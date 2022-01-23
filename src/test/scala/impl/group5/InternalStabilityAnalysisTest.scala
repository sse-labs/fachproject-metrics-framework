package org.tud.sse.metrics
package impl.group5

import org.scalatest.{FlatSpec, Matchers}
import org.tud.sse.metrics.testutils.AnalysisTestUtils

import java.io.File

class InternalStabilityAnalysisTest extends FlatSpec with Matchers{


  val appConfig = new ApplicationConfiguration(inputFilePath = "", treatFilesAsLibrary = true, outFileOption = None,
    opalLoggingEnabled = false, batchModeEnabled = Some(false), excludedAnalysesNames = List(), includedAnalysesNames = List(),
    excludeJreClasses = true, additionalClassesDir = None, loadAdditionalClassesAsInterface = false)
  val filesToTestjars = new File(getClass.getResource("/group5/testJars").getPath)
  val analysisToTestjars = new InternalStabilityAnalysis(filesToTestjars)
  val resultjars  = AnalysisTestUtils.runMultiFileAnalysis(_ => analysisToTestjars,filesToTestjars,appConfig,Map.empty[Symbol, Any])




  val filesToTest = new File(getClass.getResource("/group1/commons-collections").getPath)
  val analysisToTest = new InternalStabilityAnalysis(filesToTest)
  val result  = AnalysisTestUtils.runMultiFileAnalysis(_ => analysisToTest,filesToTest,appConfig,Map.empty[Symbol, Any])



  val filesToTestGson = new File(getClass.getResource("/group5/gson").getPath)
  val analysisToTestGson = new InternalStabilityAnalysis(filesToTestGson)
  val resultGson  = AnalysisTestUtils.runMultiFileAnalysis(_ => analysisToTestGson,filesToTestGson,appConfig,Map.empty[Symbol, Any])




  println("Bye")

}
