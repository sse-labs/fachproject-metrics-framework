package org.tud.sse.metrics
package impl.group2

import java.io.File

import org.scalatest.{FlatSpec, Matchers}
import testutils.AnalysisTestUtils


/*
 * Tests for metric "Classes Referenced" (CREF) implemented in impl.group2.ClassesReferencedAnalysis
 */
class ClassesReferencedAnalysisTest extends FlatSpec with Matchers {

  "The ClassesReferencedAnalysis" must "calculate valid results for single JARs" in {

    val filesToTest = List(
      new File(getClass.getResource("/group2/cref.jar").getPath),
      new File(getClass.getResource("/demo/utils-3.5-beta76.jar").getPath)
    )
    val analysisToTest = new ClassesReferencedAnalysis()

    val appConfig = new ApplicationConfiguration(inputFilePath = "", treatFilesAsLibrary = true, outFileOption = None,
      opalLoggingEnabled = false, batchModeEnabled = Some(false), excludedAnalysesNames = List(), includedAnalysesNames = List(),
      excludeJreClasses = true, additionalClassesDir = None, loadAdditionalClassesAsInterface = false)

    /*
     * Test 1: test against some example methods
     *  demo/cref.jar
     */
    var result = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, filesToTest.head, appConfig, Map.empty[Symbol, Any])
    assert(result.size == 1)

    var metricResult = result.head
    assert(metricResult.success)
    assert(metricResult.metricValues.nonEmpty)

    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("void C.noOp()") && value.metricValue == 0))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("int C.incInt(int)") && value.metricValue == 0))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("C C.myself()") && value.metricValue == 0))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("void C.emptyBody(int,A,double,B)") && value.metricValue == 2))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("int C.staticFieldAccess()") && value.metricValue == 1))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("void C.staticFieldAccess2()") && value.metricValue == 2))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("int C.getField(B)") && value.metricValue == 1))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("void C.setField(B)") && value.metricValue == 1))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("void C.setField2(B)") && value.metricValue == 2))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("B C.bFactory()") && value.metricValue == 1))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("B C.getAB()") && value.metricValue == 1))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("boolean C.compare(B)") && value.metricValue == 2))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("java.lang.String C.aOrB()") && value.metricValue == 3))


    /*
     * Test 2: test against some methods from renjin.utils
     *  demo/utils-3.5-beta76.jar, source: https://github.com/bedatadriven/renjin
     */
    result = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, filesToTest.tail.head, appConfig, Map.empty[Symbol, Any])
    assert(result.size == 1)

    metricResult = result.head
    assert(metricResult.success)
    assert(metricResult.metricValues.nonEmpty)

    // method: DoublePrinter.<init>, references: 6 (Object, PrintWriter, DoubleVector, String, DecimalFormatSymbols, DecimalFormat)
    assert(metricResult.metricValues.exists(value => value.entityIdent
      .equals("void org.renjin.utils.DoublePrinter.<init>(java.io.PrintWriter,org.renjin.sexp.DoubleVector,java.lang.String,java.lang.String)")
      && value.metricValue == 6))
    // method: DoublePrinter.print, references: 3 (DoubleVector, PrintWriter, DecimalFormat)
    assert(metricResult.metricValues.exists(value => value.entityIdent
      .equals("void org.renjin.utils.DoublePrinter.print(int)")
      && value.metricValue == 3))
    // method: FactorPrinter.<init>, references: 4 (Object, PrintWriter, IntVector, String)
    assert(metricResult.metricValues.exists(value => value.entityIdent
      .equals("void org.renjin.utils.FactorPrinter.<init>(java.io.PrintWriter,org.renjin.sexp.IntVector,boolean,java.lang.String)")
      && value.metricValue == 4))
    // method: FactorPrinter.print, references: 2 (IntVector, PrintWriter)
    assert(metricResult.metricValues.exists(value => value.entityIdent
      .equals("void org.renjin.utils.FactorPrinter.print(int)")
      && value.metricValue ==  2))
    // method: ColumnPrinter.print, references: 0
    assert(metricResult.metricValues.exists(value => value.entityIdent
      .equals("void org.renjin.utils.ColumnPrinter.print(int)")
      && value.metricValue ==  0))
    // method.Interactive.menu, references: 4 (Context, StringVector, Session, SessionController)
    assert(metricResult.metricValues.exists(value => value.entityIdent
      .equals("int org.renjin.utils.Interactive.menu(org.renjin.eval.Context,org.renjin.sexp.StringVector)")
      && value.metricValue ==  4))
    // method.IntPrinter.<init>, references: 4 (Object, PrintWriter, IntVector, String)
    assert(metricResult.metricValues.exists(value => value.entityIdent
      .equals("void org.renjin.utils.IntPrinter.<init>(java.io.PrintWriter,org.renjin.sexp.IntVector,java.lang.String)")
      && value.metricValue ==  4))

    /*
     * Test 3: test with cli option "--project-cref-only"
     */
    result = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, filesToTest.head, appConfig, Map(Symbol("project-cref-only") -> true))
    assert(result.size == 1)

    metricResult = result.head
    assert(metricResult.success)
    assert(metricResult.metricValues.nonEmpty)

    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("void C.noOp()") && value.metricValue == 0))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("int C.incInt(int)") && value.metricValue == 0))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("void C.setField2(B)") && value.metricValue == 2))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("boolean C.compare(B)") && value.metricValue == 1))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("java.lang.String C.aOrB()") && value.metricValue == 2))

    /*
     * Test 4: renjin.utils and --project-cref-only
     */
    result = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, filesToTest.tail.head, appConfig, Map(Symbol("project-cref-only") -> true))
    assert(result.size == 1)

    metricResult = result.head
    assert(metricResult.success)
    assert(metricResult.metricValues.nonEmpty)

    // method: DoublePrinter.<init>, references: 6 (Object, PrintWriter, DoubleVector, String, DecimalFormatSymbols, DecimalFormat)
    assert(metricResult.metricValues.exists(value => value.entityIdent
      .equals("void org.renjin.utils.DoublePrinter.<init>(java.io.PrintWriter,org.renjin.sexp.DoubleVector,java.lang.String,java.lang.String)")
      && value.metricValue == 0))
    // method: FactorPrinter.<init>, references: 4 (Object, PrintWriter, IntVector, String)
    assert(metricResult.metricValues.exists(value => value.entityIdent
      .equals("void org.renjin.utils.FactorPrinter.<init>(java.io.PrintWriter,org.renjin.sexp.IntVector,boolean,java.lang.String)")
      && value.metricValue == 0))
    // method: FactorPrinter.print, references: 2 (IntVector, PrintWriter)
    assert(metricResult.metricValues.exists(value => value.entityIdent
      .equals("void org.renjin.utils.FactorPrinter.print(int)")
      && value.metricValue == 0))
    // method: ColumnPrinter.print, references: 0
    assert(metricResult.metricValues.exists(value => value.entityIdent
      .equals("void org.renjin.utils.ColumnPrinter.print(int)")
      && value.metricValue == 0))
  }
}
