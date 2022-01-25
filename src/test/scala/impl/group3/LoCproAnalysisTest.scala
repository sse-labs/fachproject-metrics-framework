package org.tud.sse.metrics
package impl.group3

import impl.group3.LOCproAnalysis
import testutils.AnalysisTestUtils

import org.scalatest.{FlatSpec, Matchers}

import java.io.File

class LOCproAnalysisTest extends FlatSpec with Matchers{

  /** tested the metric Locpro with 3 files, 2 of our own class file */

  "The LOCproAnalysis" must "calculate valid results for single JARs" in {

    val fileToTest = new File(getClass.getResource("/group3/empty.jar").getPath) // is same as testVoid only no last method
    val analysisToTest = new LOCproAnalysis()

    val appConfig = new ApplicationConfiguration(inputFilePath = "", treatFilesAsLibrary = true, outFileOption = None,
      opalLoggingEnabled = false, batchModeEnabled = Some(false), excludedAnalysesNames = List(), includedAnalysesNames = List(),
      excludeJreClasses = true, additionalClassesDir = None, loadAdditionalClassesAsInterface = false)

    val result = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest, appConfig, Map.empty[Symbol, Any])

    assert(result.size == 1)

    val metricResult = result.head

    assert(metricResult.success)
    assert(metricResult.metricValues.nonEmpty)
    assert(metricResult.metricValues.exists(value => value.metricValue == 10.0))


    // 2te file to test
    val fileToTest1 = new File(getClass.getResource("/group3/HelloWorld.jar").getPath)
    val analysisToTest1 = new LOCproAnalysis()


    val result1 = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest1, fileToTest1, appConfig, Map.empty[Symbol, Any])

    assert(result1.size == 1)

    val metricResult1 = result1.head

    assert(metricResult1.success)
    assert(metricResult1.metricValues.nonEmpty)

    assert(metricResult1.metricValues.exists(value => value.metricValue == 22.0))


    // 3rd file to test
    val fileToTest2 = new File(getClass.getResource("/demo/utils-3.5-beta76.jar").getPath)
    val analysisToTest2 = new LOCproAnalysis()


    val result2 = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest2, fileToTest2, appConfig, Map.empty[Symbol, Any])

    assert(result2.size == 1)

    val metricResult2 = result2.head

    assert(metricResult2.success)
    assert(metricResult2.metricValues.nonEmpty)

    assert(metricResult2.metricValues.exists(value => value.metricValue == 269.0))

    println(metricResult2.metricValues)



    val fileToTest3 = new File(getClass.getResource("/group3/testVoid.jar").getPath)
    val analysisToTest3 = new LOCproAnalysis()


    val result3 = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest3, fileToTest3, appConfig, Map.empty[Symbol, Any])

    assert(result3.size == 1)

    val metricResult3 = result3.head

    assert(metricResult3.success)
    assert(metricResult3.metricValues.nonEmpty)

    assert(metricResult3.metricValues.exists(value => value.metricValue == 13.0))





  }




}



