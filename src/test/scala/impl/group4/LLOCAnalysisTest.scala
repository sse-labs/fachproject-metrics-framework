package org.tud.sse.metrics
package impl.group4

import testutils.AnalysisTestUtils

import org.scalatest.{FreeSpec, Matchers}

import java.io.File

class LLOCAnalysisTest extends FreeSpec with Matchers{

  val fileToTest = new File(getClass.getResource("/group4/maven-jar-sample-1.0-SNAPSHOT.jar").getPath)
  val analysisToTest = new LLOCAnalysis()

  val appConfig = new ApplicationConfiguration(inputFilePath = "", treatFilesAsLibrary = true, outFileOption = None,
    opalLoggingEnabled = false, batchModeEnabled = Some(false), excludedAnalysesNames = List(), includedAnalysesNames = List(),
    excludeJreClasses = true, additionalClassesDir = None, loadAdditionalClassesAsInterface = false)

  val result = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest, appConfig, Map.empty[Symbol, Any])

  val metricResult = result.head

  "The LLOCAnalysisTest must calculate valid results for the test Project" in {
    assert(result.size == 1)
    assert(metricResult.success)
    assert(metricResult.metricValues.nonEmpty)
  }

  "The LLOCAnalysisTest must calculate correct results for" -{

    "methods with comments and empty lines within the body" in {
      assert(metricResult.metricValues.exists(value => value.entityIdent.equals("void com.group4.sample.LOCphyAnalysisTest.method2()") && value.metricValue == 3.0))
    }

    "methods with comments and empty lines in the beginning/end of the source code" in {
      assert(metricResult.metricValues.exists(value => value.entityIdent.equals("void com.group4.sample.LOCphyAnalysisTest.method1()") && value.metricValue == 7.0))
    }

    "default constructors" in {
      assert(metricResult.metricValues.exists(value => value.entityIdent.equals("void com.group4.sample.LOCphyAnalysisTest.<init>()") && value.metricValue == 0.0))
    }

    "void/non void type methods" in {
      assert(metricResult.metricValues.exists(value => value.entityIdent.equals("void com.group4.sample.LOCphyAnalysisTest.main(java.lang.String[])") && value.metricValue == 3.0))
      assert(metricResult.metricValues.exists(value => value.entityIdent.equals("java.lang.String com.group4.sample.LOCphyAnalysisTest.method3()") && value.metricValue == 1.0))
    }
  }


}