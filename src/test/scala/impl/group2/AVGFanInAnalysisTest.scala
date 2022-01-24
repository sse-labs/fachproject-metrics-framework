package org.tud.sse.metrics
package impl.group2

import java.io.File

import org.scalatest.{FlatSpec, Matchers}
import testutils.AnalysisTestUtils


/*
 * Tests for metric "AVGFanIn" implemented in impl.group2.AVGFanInAnalysis
 */
class AVGFanInAnalysisTest extends FlatSpec with Matchers {

  "The AVGFanInAnalysis" must "calculate valid results for single JARs" in {

    val filesToTest = List(
      new File(getClass.getResource("/group2/fanin.jar").getPath),
      new File(getClass.getResource("/demo/utils-3.5-beta76.jar").getPath)
    )
    val analysisToTest = new AVGFanInAnalysis()

    val appConfig = new ApplicationConfiguration(inputFilePath = "", treatFilesAsLibrary = true, outFileOption = None,
      opalLoggingEnabled = false, batchModeEnabled = Some(false), excludedAnalysesNames = List(), includedAnalysesNames = List(),
      excludeJreClasses = true, additionalClassesDir = None, loadAdditionalClassesAsInterface = false)

    /*
     * Test 1: classes in fanin.jar (sources in src/test/resources/group2/fanin)
     */
    var result = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, filesToTest.head, appConfig, Map.empty[Symbol, Any])
    assert(result.size == 1)

    var metricResult = result.head
    assert(metricResult.success)
    assert(metricResult.metricValues.nonEmpty)

    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("A") && value.metricValue == 2.5))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("B") && value.metricValue == 0.2))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("Empty") && value.metricValue == 0))

    /*
     * Test 2: classes from renjin.utils
     *  file: demo/utils-3.5-beta76.jar, source: https://github.com/bedatadriven/renjin
     */
    result = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, filesToTest.tail.head, appConfig, Map.empty[Symbol, Any])
    assert(result.size == 1)

    metricResult = result.head
    assert(metricResult.success)
    assert(metricResult.metricValues.nonEmpty)

    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("org/renjin/utils/WriteTable") && value.metricValue == 5.67))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("org/renjin/utils/Utils") && value.metricValue == 1))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("org/renjin/utils/FactorPrinter") && value.metricValue == 4.67))
  }
}
