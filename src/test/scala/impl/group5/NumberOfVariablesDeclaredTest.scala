package org.tud.sse.metrics
package impl.group5

import testutils.AnalysisTestUtils

import org.scalatest.{FlatSpec, Matchers}

import java.io.File

class NumberOfVariablesDeclaredTest extends FlatSpec with Matchers{

  "The MethodSizeAnalysis" must "calculate valid results for single JARs" in {

    val fileToTest = new File(getClass.getResource("/demo/utils-3.5-beta76.jar").getPath)
    val analysisToTest = new NumberOfVariablesDeclaredAnalysis()

    val appConfig = new ApplicationConfiguration(inputFilePath = "", treatFilesAsLibrary = true, outFileOption = None,
      opalLoggingEnabled = false, batchModeEnabled = Some(false), excludedAnalysesNames = List(), includedAnalysesNames = List(),
      excludeJreClasses = true, additionalClassesDir = None, loadAdditionalClassesAsInterface = false)

    val result = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest, appConfig, Map.empty[Symbol, Any])

    assert(result.size == 1)

    val metricResult = result

    println("result " + metricResult)

  }

}
