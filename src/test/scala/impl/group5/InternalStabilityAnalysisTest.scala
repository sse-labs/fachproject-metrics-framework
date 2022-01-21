package org.tud.sse.metrics
package impl.group5

import org.scalatest.{FlatSpec, Matchers}
import org.tud.sse.metrics.testutils.AnalysisTestUtils

import java.io.File

class InternalStabilityAnalysisTest extends FlatSpec with Matchers{

  val filesToTest = new File(getClass.getResource("/group1/commons-collections").getPath)
  val appConfig = new ApplicationConfiguration(inputFilePath = "", treatFilesAsLibrary = true, outFileOption = None,
    opalLoggingEnabled = false, batchModeEnabled = Some(false), excludedAnalysesNames = List(), includedAnalysesNames = List(),
    excludeJreClasses = true, additionalClassesDir = None, loadAdditionalClassesAsInterface = false)

  val analysisToTest = new InternalStabilityAnalysis(filesToTest)
  val result  = AnalysisTestUtils.runMultiFileAnalysis(_ => analysisToTest,filesToTest,appConfig,Map.empty[Symbol, Any])
  println("Bye")

}
