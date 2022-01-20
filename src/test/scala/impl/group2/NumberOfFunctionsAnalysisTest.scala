package org.tud.sse.metrics
package impl.group2

import java.io.File

import org.scalatest.{FlatSpec, Matchers}
import testutils.AnalysisTestUtils


/*
 * Tests for metric "NumberOfFunctions" implemented in impl.group2.NumberOfFunctionsAnalysis
 *
 * testing with classes in demo/utils-3.5-beta76.jar
 * (source: https://github.com/bedatadriven/renjin)
 */
class NumberOfFunctionsAnalysisTest extends FlatSpec with Matchers {

  "The NumberOfFunctionsAnalysis" must "calculate valid results for single JARs" in {

    val fileToTest = new File(getClass.getResource("/demo/utils-3.5-beta76.jar").getPath)
    val analysisToTest = new NumberOfFunctionsAnalysis()

    val appConfig = new ApplicationConfiguration(inputFilePath = "", treatFilesAsLibrary = true, outFileOption = None,
      opalLoggingEnabled = false, batchModeEnabled = Some(false), excludedAnalysesNames = List(), includedAnalysesNames = List(),
      excludeJreClasses = true, additionalClassesDir = None, loadAdditionalClassesAsInterface = false)

    val result = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest, appConfig, Map.empty[Symbol, Any])
    assert(result.size == 1)

    val metricResult = result.head
    assert(metricResult.success)
    assert(metricResult.metricValues.nonEmpty)

    // class: DoublePrinter, methods: 2
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("org/renjin/utils/DoublePrinter") && value.metricValue == 2))
    // class: FactorPrinter, methods: 3
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("org/renjin/utils/FactorPrinter") && value.metricValue == 3))
    // class: Interactive, methods: 2 (1 explicit + default constructor)
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("org/renjin/utils/Interactive") && value.metricValue == 2))
    // class: IntPrinter, methods: 2
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("org/renjin/utils/IntPrinter") && value.metricValue == 2))
    // class: LogicalPrinter, methods: 2
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("org/renjin/utils/LogicalPrinter") && value.metricValue == 2))
    // class: Tables, methods: 9 (6 explicit + default constructor + 2 synthetic)
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("org/renjin/utils/Tables") && value.metricValue == 9))
    // class: Utils, methods: 2 (1 explicit + default constructor)
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("org/renjin/utils/Utils") && value.metricValue == 2))
    // class: WriteTable, methods: 3 (2 explicit + default constructor
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("org/renjin/utils/WriteTable") && value.metricValue == 3))
  }
}
