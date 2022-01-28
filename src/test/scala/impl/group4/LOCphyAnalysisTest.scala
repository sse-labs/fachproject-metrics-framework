package org.tud.sse.metrics
package impl.group4

import testutils.AnalysisTestUtils

import org.scalatest.{FreeSpec, Matchers}

import java.io.File

class LOCphyAnalysisTest extends FreeSpec with Matchers{

  val fileToTest1 = new File(getClass.getResource("/group4/maven-jar-sample-1.0-SNAPSHOT.jar").getPath)
  val fileToTest2 = new File(getClass.getResource("/demo/utils-3.5-beta76.jar").getPath)

  val analysisToTest = new LOCphyAnalysis()

  val appConfig = new ApplicationConfiguration(inputFilePath = "", treatFilesAsLibrary = true, outFileOption = None,
    opalLoggingEnabled = false, batchModeEnabled = Some(false), excludedAnalysesNames = List(), includedAnalysesNames = List(),
    excludeJreClasses = true, additionalClassesDir = None, loadAdditionalClassesAsInterface = false)

  val result1 = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest1, appConfig, Map.empty[Symbol, Any])
  val result2 = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest2, appConfig, Map.empty[Symbol, Any])

  val metricResult1 = result1.head
  val metricResult2 = result2.head

  "The LOCphyAnalysisTest must calculate valid results for the test Project" in {
    assert(result1.size == 1)
    assert(metricResult1.success)
    assert(metricResult1.metricValues.nonEmpty)
    assert(result2.size == 1)
    assert(metricResult2.success)
    assert(metricResult2.metricValues.nonEmpty)
  }

  "The LOCphyAnalysisTest must calculate correct results for" -{

    "test project methods" in {
      //method with comments and empty lines within the body
      assert(metricResult1.metricValues.exists(value => value.entityIdent.equals("void com.group4.sample.LOCphyAnalysisTest.method2()") && value.metricValue == 5.0))
      //method with comments and empty lines in the beginning/end of the source code
      assert(metricResult1.metricValues.exists(value => value.entityIdent.equals("void com.group4.sample.LOCphyAnalysisTest.method1()") && value.metricValue == 8.0))
      //default constructor
      assert(metricResult1.metricValues.exists(value => value.entityIdent.equals("void com.group4.sample.LOCphyAnalysisTest.<init>()") && value.metricValue == 0.0))
    }

    "renjin.utils methods" in {
      assert(metricResult2.metricValues.exists(value => value.entityIdent.equals("void org.renjin.utils.Tables$LogicalConverter.<init>(org.renjin.utils.Tables$1)") && value.metricValue == 0.0))
      assert(metricResult2.metricValues.exists(value => value.entityIdent.equals("void org.renjin.utils.WriteTable.<init>()") && value.metricValue == 0.0))
      assert(metricResult2.metricValues.exists(value => value.entityIdent.equals("void org.renjin.utils.StringPrinter.print(int)") && value.metricValue == 12.0))
      assert(metricResult2.metricValues.exists(value => value.entityIdent.equals("org.renjin.sexp.Vector org.renjin.utils.Tables$Converter.build(org.renjin.sexp.StringVector,java.util.Set)") && value.metricValue == 7.0))
    }

  }


}
