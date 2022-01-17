package org.tud.sse.metrics
package impl.group4

import testutils.AnalysisTestUtils

import org.scalatest.{FreeSpec, Matchers}

import java.io.File

class RFCAnalysisTest extends FreeSpec with Matchers{

  val fileToTest = new File(getClass.getResource("/group4/maven-jar-sample-1.0-SNAPSHOT.jar").getPath)
  val analysisToTest = new RFCAnalysis()

  val appConfig = new ApplicationConfiguration(inputFilePath = "", treatFilesAsLibrary = true, outFileOption = None,
    opalLoggingEnabled = false, batchModeEnabled = Some(false), excludedAnalysesNames = List(), includedAnalysesNames = List(),
    excludeJreClasses = true, additionalClassesDir = None, loadAdditionalClassesAsInterface = false)

  val result = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest, appConfig, Map.empty[Symbol, Any])

  val metricResult = result.head

  "The RFCAnalysisTest must calculate valid results for the test Project" in {
    assert(result.size == 1)
    assert(metricResult.success)
    assert(metricResult.metricValues.nonEmpty)
  }

  "The RFCAnalysisTest must calculate correct results for" -{

    "classes with private methods only" in {
      assert(metricResult.metricValues.exists(value => value.entityIdent.equals("com/group4/sample/RFCAnalysisTest1") && value.metricValue == 6.0))
    }

    "classes with public/private methods" in {
      assert(metricResult.metricValues.exists(value => value.entityIdent.equals("com/group4/sample/RFCAnalysisTest2") && value.metricValue == 12.0))
    }

    "classes without methods" in {
      assert(metricResult.metricValues.exists(value => value.entityIdent.equals("com/group4/sample/RFCAnalysisTest3") && value.metricValue == 2.0))
    }

  }
}