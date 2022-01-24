package org.tud.sse.metrics
package impl.group2

import java.io.File

import org.scalatest.{FlatSpec, Matchers}
import testutils.AnalysisTestUtils


/*
 * Tests for metric "FanIn" implemented in impl.group2.FanInAnalysis
 */
class FanInAnalysisTest extends FlatSpec with Matchers {

  "The FanInAnalysis" must "calculate valid results for single JARs" in {

    val filesToTest = List(
      new File(getClass.getResource("/group2/fanin.jar").getPath),
      new File(getClass.getResource("/demo/utils-3.5-beta76.jar").getPath)
    )
    val analysisToTest = new FanInAnalysis()

    val appConfig = new ApplicationConfiguration(inputFilePath = "", treatFilesAsLibrary = true, outFileOption = None,
      opalLoggingEnabled = false, batchModeEnabled = Some(false), excludedAnalysesNames = List(), includedAnalysesNames = List(),
      excludeJreClasses = true, additionalClassesDir = None, loadAdditionalClassesAsInterface = false)

    /*
     * Test 1: methods from classes A and B in fanin.jar (java source in src/test/resources/group2/fanin)
     */
    var result  = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, filesToTest.head, appConfig, Map.empty[Symbol, Any])
    assert(result.size == 1)

    var metricResult = result.head
    assert(metricResult.success)
    assert(metricResult.metricValues.nonEmpty)

    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("void A.<init>()") && value.metricValue == 6))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("int A.prvMethod()") && value.metricValue == 0))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("void A.fieldWrite()") && value.metricValue == 0))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("void A.unused1()") && value.metricValue == 0))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("void A.unused2(int)") && value.metricValue == 1))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("java.lang.String A.unused3()") && value.metricValue == 1))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("int A.unused4()") && value.metricValue == 1))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("int A.unused5(int)") && value.metricValue == 2))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("void A.unused6(int,int)") && value.metricValue == 2))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("boolean A.unused7(int,int,int)") && value.metricValue == 4))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("void A.doNothing()") && value.metricValue == 3))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("void A.doNothing_()") && value.metricValue == 1))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("void A.recursion(int)") && value.metricValue == 2))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("void A.endless()") && value.metricValue == 2))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("int A.staticWork(int,java.lang.String)") && value.metricValue == 4))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("void A.staticWork(B)") && value.metricValue == 3))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("int A.calc(int)") && value.metricValue == 4))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("boolean A.chunk(boolean)") && value.metricValue == 2))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("int A.foo(B)") && value.metricValue == 3))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("int A.lots(B,int,int)") && value.metricValue == 9))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("void B.<init>()") && value.metricValue == 1))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("void B.b1()") && value.metricValue == 0))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("void B.b2()") && value.metricValue == 0))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("void B.b3()") && value.metricValue == 0))

    /*
     * Test 2: various methods from renjin.utils
     *  file: demo/utils-3.5-beta76.jar, source: https://github.com/bedatadriven/renjin
     */
    result = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, filesToTest.tail.head, appConfig, Map.empty[Symbol, Any])
    assert(result.size == 1)

    metricResult = result.head
    assert(metricResult.success)
    assert(metricResult.metricValues.nonEmpty)

    // class WriteTable
    // default constructor
    assert(metricResult.metricValues.exists(value => value.entityIdent
      .equals("void org.renjin.utils.WriteTable.<init>()")
      && value.metricValue == 0))
    // method write: 12 parameters, 1 field read
    assert(metricResult.metricValues.exists(value => value.entityIdent
      .equals("void org.renjin.utils.WriteTable.write(org.renjin.eval.Context,org.renjin.sexp.ListVector,org.renjin.sexp.SEXP,int,int,org.renjin.sexp.Vector,java.lang.String,java.lang.String,java.lang.String,java.lang.String,org.renjin.sexp.SEXP,org.renjin.sexp.SEXP)")
      && value.metricValue == 13))
    // method isColumnQuoted: 2 parameters, 2 field reads
    assert(metricResult.metricValues.exists(value => value.entityIdent
      .equals("boolean org.renjin.utils.WriteTable.isColumnQuoted(org.renjin.sexp.SEXP,int)")
      && value.metricValue == 4))

    // class Utils
    // method findPackageRoot: 2 parameters
    assert(metricResult.metricValues.exists(value => value.entityIdent
      .equals("org.renjin.sexp.SEXP org.renjin.utils.Utils.findPackageRoot(org.renjin.eval.Context,java.lang.String)")
      && value.metricValue == 2))

    // class FactorPrinter
    // constructor: 4 parameters, call from class WriterTable
    assert(metricResult.metricValues.exists(value => value.entityIdent
      .equals("void org.renjin.utils.FactorPrinter.<init>(java.io.PrintWriter,org.renjin.sexp.IntVector,boolean,java.lang.String)")
      && value.metricValue == 5))
    // method formatLevels: 2 parameters, 2 field reads
    assert(metricResult.metricValues.exists(value => value.entityIdent
      .equals("java.lang.String[] org.renjin.utils.FactorPrinter.formatLevels(org.renjin.sexp.IntVector,boolean)")
      && value.metricValue == 4))
    // method print: 1 parameter, 4 reads
    assert(metricResult.metricValues.exists(value => value.entityIdent
      .equals("void org.renjin.utils.FactorPrinter.print(int)")
      && value.metricValue == 5))
  }
}
