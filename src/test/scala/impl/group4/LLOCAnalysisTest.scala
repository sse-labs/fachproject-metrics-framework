package org.tud.sse.metrics
package impl.group4

import testutils.AnalysisTestUtils

import org.scalatest.{FreeSpec, Matchers}

import java.io.File

class LLOCAnalysisTest extends FreeSpec with Matchers{

  val fileToTest1 = new File(getClass.getResource("/group4/maven-jar-sample-1.0-SNAPSHOT.jar").getPath)
  val fileToTest2 = new File(getClass.getResource("/demo/utils-3.5-beta76.jar").getPath)

  val analysisToTest = new LLOCAnalysis()

  val appConfig = new ApplicationConfiguration(inputFilePath = "", treatFilesAsLibrary = true, outFileOption = None,
    opalLoggingEnabled = false, batchModeEnabled = Some(false), excludedAnalysesNames = List(), includedAnalysesNames = List(),
    excludeJreClasses = true, additionalClassesDir = None, loadAdditionalClassesAsInterface = false)

  val result1 = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest1, appConfig, Map.empty[Symbol, Any])
  val result2 = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest2, appConfig, Map.empty[Symbol, Any])

  val metricResult1 = result1.head
  val metricResult2 = result2.head

  "The LLOCAnalysisTest must calculate valid results for the test Project" in {
    assert(result1.size == 1)
    assert(metricResult1.success)
    assert(metricResult1.metricValues.nonEmpty)
    assert(result2.size == 1)
    assert(metricResult2.success)
    assert(metricResult2.metricValues.nonEmpty)
  }

  "The LLOCAnalysisTest must calculate correct results for" -{

    "test project methods" in {
      //method with comments and empty lines within the body
      assert(metricResult1.metricValues.exists(value => value.entityIdent.equals("void com.group4.sample.LOCphyAnalysisTest.method2()") && value.metricValue == 3.0))
      //method with comments and empty lines in the beginning/end of the source code
      assert(metricResult1.metricValues.exists(value => value.entityIdent.equals("void com.group4.sample.LOCphyAnalysisTest.method1()") && value.metricValue == 7.0))
      //default constructor
      assert(metricResult1.metricValues.exists(value => value.entityIdent.equals("void com.group4.sample.LOCphyAnalysisTest.<init>()") && value.metricValue == 0.0))
    }

    "renjin.utils methods" in {
      assert(metricResult2.metricValues.exists(value => value.entityIdent.equals("void org.renjin.utils.Tables$LogicalConverter.<init>(org.renjin.utils.Tables$1)") && value.metricValue == 0.0))
      assert(metricResult2.metricValues.exists(value => value.entityIdent.equals("void org.renjin.utils.WriteTable.write(org.renjin.eval.Context,org.renjin.sexp.ListVector,org.renjin.sexp.SEXP,int,int,org.renjin.sexp.Vector,java.lang.String,java.lang.String,java.lang.String,java.lang.String,org.renjin.sexp.SEXP,org.renjin.sexp.SEXP)") && value.metricValue == 27.0))
      assert(metricResult2.metricValues.exists(value => value.entityIdent.equals("org.renjin.sexp.IntArrayVector$Builder org.renjin.utils.Tables$IntConverter.newBuilder(int)") && value.metricValue == 1.0))
      assert(metricResult2.metricValues.exists(value => value.entityIdent.equals("void org.renjin.utils.StringPrinter.print(int)") && value.metricValue == 8.0))
    }

  }


}