package org.tud.sse.metrics
package impl.group3

import testutils.AnalysisTestUtils

import org.scalatest.{FlatSpec, Matchers}

import java.io.File

class CBOAnalysisTest extends FlatSpec with Matchers{

  "The CBOAnalysis" must "calculate valid results for single JARs" in {
    /**
     * tested with default jar file, and our own file(converted from source code). result is a list of tuple which refers to diffent classfile
     */
    val fileToTest = new File(getClass.getResource("/demo/utils-3.5-beta76.jar").getPath)

    val analysisToTest = new CBOAnalysis()

    val appConfig = new ApplicationConfiguration(inputFilePath = "", treatFilesAsLibrary = true, outFileOption = None,
      opalLoggingEnabled = false, batchModeEnabled = Some(false), excludedAnalysesNames = List(), includedAnalysesNames = List(),
      excludeJreClasses = true, additionalClassesDir = None, loadAdditionalClassesAsInterface = false)

    val result = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest, appConfig, Map.empty[Symbol, Any])

    assert(result.size == 1)

    val metricResult = result.head

    assert(metricResult.success)
    assert(metricResult.metricValues.nonEmpty)
    assert(metricResult.metricValues.exists(value => value.metricValue == 0.0))
    assert(metricResult.metricValues.exists(value => value.metricValue == 1.0))
    println(metricResult.metricValues)



    val fileToTest1 = new File(getClass.getResource("/group3/Animal.jar").getPath)

    val analysisToTest1 = new CBOAnalysis()


    val result1 = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest1, fileToTest1, appConfig, Map.empty[Symbol, Any])

    assert(result1.size == 1)

    val metricResult1 = result1.head

    assert(metricResult1.success)
    assert(metricResult1.metricValues.nonEmpty)

    assert(metricResult1.metricValues.exists(value => value.metricValue == 2.0))
    assert(metricResult1.metricValues.exists(value => value.metricValue == 0.0))
    println(metricResult1.metricValues)

  }

}
