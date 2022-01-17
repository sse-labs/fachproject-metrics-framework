package org.tud.sse.metrics
package impl.group2

import java.io.File

import org.scalatest.{FlatSpec, Matchers}
import testutils.AnalysisTestUtils


/*
 * Tests for metric "Average Fan Out" (AVGFanOut) implemented in impl.group2.AVGFanOutAnalysis
 */
class AVGFanOutAnalysisTest extends FlatSpec with Matchers {

  "The AVGFanOutAnalysis" must "calculate valid results for single JARs" in {

    val filesToTest = List(
      new File(getClass.getResource("/group2/fanout.jar").getPath),
      new File(getClass.getResource("/demo/utils-3.5-beta76.jar").getPath)
    )
    val analysisToTest = new AVGFanOutAnalysis()

    val appConfig = new ApplicationConfiguration(inputFilePath = "", treatFilesAsLibrary = true, outFileOption = None,
      opalLoggingEnabled = false, batchModeEnabled = Some(false), excludedAnalysesNames = List(), includedAnalysesNames = List(),
      excludeJreClasses = true, additionalClassesDir = None, loadAdditionalClassesAsInterface = false)

    /*
     * Test 1: methods from class B in fanout.jar
     */
    var result  = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, filesToTest.head, appConfig, Map.empty[Symbol, Any])
    assert(result.size == 1)

    var metricResult = result.head
    assert(metricResult.success)
    assert(metricResult.metricValues.nonEmpty)

    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("B") && value.metricValue == 1))

    /*
     * Test 2: classes from renjin.utils
     */
    result = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, filesToTest.tail.head, appConfig, Map.empty[Symbol, Any])
    assert(result.size == 1)

    metricResult = result.head
    assert(metricResult.success)
    assert(metricResult.metricValues.nonEmpty)

    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("org/renjin/utils/DoublePrinter") && value.metricValue == 14))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("org/renjin/utils/Utils") && value.metricValue == 7)) // class has implicit default constructor
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("org/renjin/utils/ColumnPrinter") && value.metricValue == 0))

  }
}
