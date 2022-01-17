package org.tud.sse.metrics
package impl.group4

import testutils.AnalysisTestUtils

import org.scalatest.{FreeSpec, Matchers}

import java.io.File

class DACAnalysisTest extends FreeSpec with Matchers{

  val fileToTest = new File(getClass.getResource("/group4/maven-jar-sample-1.0-SNAPSHOT.jar").getPath)
  val analysisToTest = new DACAnalysis()

  val appConfig = new ApplicationConfiguration(inputFilePath = "", treatFilesAsLibrary = true, outFileOption = None,
    opalLoggingEnabled = false, batchModeEnabled = Some(false), excludedAnalysesNames = List(), includedAnalysesNames = List(),
    excludeJreClasses = true, additionalClassesDir = None, loadAdditionalClassesAsInterface = false)

  val result = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest, appConfig, Map.empty[Symbol, Any])

  val metricResult = result.head

  "The DACAnalysisTest must calculate valid results for the test Project" in {
    assert(result.size == 1)
    assert(metricResult.success)
    assert(metricResult.metricValues.nonEmpty)
  }

  "The DACAnalysisTest must calculate correct results for" -{

    "classes with no attributes" in {
      assert(metricResult.metricValues.exists(value => value.entityIdent.equals("com/group4/sample/LOCphyAnalysisTest") && value.metricValue == 0.0))
    }


    "classes with primitive/reference type attributes" in {
      assert(metricResult.metricValues.exists(value => value.entityIdent.equals("com/group4/sample/DACAnalysisTest3") && value.metricValue == 5.0))
    }

    "classes with primitive type attributes only" in {
      assert(metricResult.metricValues.exists(value => value.entityIdent.equals("com/group4/sample/DACAnalysisTest2") && value.metricValue == 0.0))
    }

    "classes with reference type attributes only" in {

      assert(metricResult.metricValues.exists(value => value.entityIdent.equals("com/group4/sample/DACAnalysisTest3") && value.metricValue == 5.0))
    }

    "classes with primitive type attributes only" in {
      assert(metricResult.metricValues.exists(value => value.entityIdent.equals("com/group4/sample/DACAnalysisTest2") && value.metricValue == 0.0))
    }

    "classes with reference type attributes only" in {

      assert(metricResult.metricValues.exists(value => value.entityIdent.equals("com/group4/sample/DACAnalysisTest1") && value.metricValue == 5.0))
    }
  }
}
