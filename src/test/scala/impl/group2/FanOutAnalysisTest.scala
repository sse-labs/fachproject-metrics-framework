package org.tud.sse.metrics
package impl.group2

import java.io.File

import org.scalatest.{FlatSpec, Matchers}
import testutils.AnalysisTestUtils


/*
 * Tests for metric "FanOut" implemented in impl.group2.FanOutAnalysis
 */
class FanOutAnalysisTest extends FlatSpec with Matchers {

  "The FanOutAnalysis" must "calculate valid results for single JARs" in {

    val filesToTest = List(
      new File(getClass.getResource("/group2/fanout.jar").getPath),
      new File(getClass.getResource("/demo/utils-3.5-beta76.jar").getPath)
    )
    val analysisToTest = new FanOutAnalysis()

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

    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("void B.zero()") && value.metricValue == 0))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("void B.zero_a(Person)") && value.metricValue == 0))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("boolean B.zero_b(Person)") && value.metricValue == 0))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("java.lang.String B.one(Person)") && value.metricValue == 1))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("void B.one_a(Person)") && value.metricValue == 1))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("void B.three(Person)") && value.metricValue == 3))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("void B.eight(Person)") && value.metricValue == 8))

    /*
     * Test 2: various methods from renjin.utils
     */
    result = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, filesToTest.tail.head, appConfig, Map.empty[Symbol, Any])
    assert(result.size == 1)

    metricResult = result.head
    assert(metricResult.success)
    assert(metricResult.metricValues.nonEmpty)

    assert(metricResult.metricValues.exists(value => value.entityIdent
      .equals("void org.renjin.utils.DoublePrinter.print(int)")
      && value.metricValue == 10))

    assert(metricResult.metricValues.exists(value => value.entityIdent
      .equals("void org.renjin.utils.DoublePrinter.<init>(java.io.PrintWriter,org.renjin.sexp.DoubleVector,java.lang.String,java.lang.String)")
      && value.metricValue == 19)) // 18 + implicit call to Object.init

    assert(metricResult.metricValues.exists(value => value.entityIdent
      .equals("int org.renjin.utils.Interactive.menu(org.renjin.eval.Context,org.renjin.sexp.StringVector)")
      && value.metricValue ==  4))

    assert(metricResult.metricValues.exists(value => value.entityIdent
      .equals("void org.renjin.utils.ColumnPrinter.print(int)")
      && value.metricValue ==  0))

    assert(metricResult.metricValues.exists(value => value.entityIdent
      .equals("void org.renjin.utils.FactorPrinter.print(int)")
      && value.metricValue ==  8))

  }
}
