package org.tud.sse.metrics
package impl.group1

import testutils.AnalysisTestUtils

import org.scalatest.{FlatSpec, Matchers}

import java.io.File

class MaximumNestingAnalysisTest extends FlatSpec with Matchers{

  "The MaximumNestingAnalysis" must "calculate valid results for single JARs" in {

    val fileToTest = new File(getClass.getResource("/group1/MaximumNestingCompanion-1.0-SNAPSHOT.jar").getPath)
    val analysisToTest = new MaximumNestingAnalysis()

    val appConfig = new ApplicationConfiguration(inputFilePath = "", treatFilesAsLibrary = true, outFileOption = None,
      opalLoggingEnabled = false, batchModeEnabled = Some(false), excludedAnalysesNames = List(), includedAnalysesNames = List(),
      excludeJreClasses = true, additionalClassesDir = None, loadAdditionalClassesAsInterface = false)

    val result = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest, appConfig, Map.empty[Symbol, Any])

    assert(result.nonEmpty)

    val metricResult = result.head

    assert(metricResult.success)
    assert(metricResult.metricValues.nonEmpty)
    assert(metricResult.metricValues.exists(value => value.entityIdent.contains("class") && value.metricValue == 6))
    assert(metricResult.metricValues.exists(value => value.entityIdent.contains("void start.<init>()") && value.metricValue == 0))
    assert(metricResult.metricValues.exists(value => value.entityIdent.contains("method: int start.test7(int,int)") && value.metricValue == 6))
    assert(metricResult.metricValues.exists(value => value.entityIdent.contains("method: int start.test6(int,int,int)") && value.metricValue == 2))
    assert(metricResult.metricValues.exists(value => value.entityIdent.contains("method: int start.test5(int,int)") && value.metricValue == 6))
    assert(metricResult.metricValues.exists(value => value.entityIdent.contains("method: int start.test4(int,int)") && value.metricValue == 3))
    assert(metricResult.metricValues.exists(value => value.entityIdent.contains("method: int start.test3(int,int)") && value.metricValue == 2))
    assert(metricResult.metricValues.exists(value => value.entityIdent.contains("method: int start.test2(int,int,int)") && value.metricValue == 3))
    assert(metricResult.metricValues.exists(value => value.entityIdent.contains("method: int start.test1(int,int,int)") && value.metricValue == 2))
    assert(metricResult.metricValues.exists(value => value.entityIdent.contains("method: int start.test8()") && value.metricValue == 3))
    assert(metricResult.metricValues.exists(value => value.entityIdent.contains("method: int start.test9(int,int)") && value.metricValue == 2))
    assert(metricResult.metricValues.exists(value => value.entityIdent.contains("method: int start.test10(int,int)") && value.metricValue == 2))


  }

}
