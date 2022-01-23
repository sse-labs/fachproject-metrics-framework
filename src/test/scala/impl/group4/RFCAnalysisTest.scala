package org.tud.sse.metrics
package impl.group4

import testutils.AnalysisTestUtils

import org.scalatest.{FreeSpec, Matchers}

import java.io.File

class RFCAnalysisTest extends FreeSpec with Matchers{

  val fileToTest1 = new File(getClass.getResource("/group4/maven-jar-sample-1.0-SNAPSHOT.jar").getPath)
  val fileToTest2 = new File(getClass.getResource("/demo/utils-3.5-beta76.jar").getPath)
  val analysisToTest = new RFCAnalysis()

  val appConfig = new ApplicationConfiguration(inputFilePath = "", treatFilesAsLibrary = true, outFileOption = None,
    opalLoggingEnabled = false, batchModeEnabled = Some(false), excludedAnalysesNames = List(), includedAnalysesNames = List(),
    excludeJreClasses = true, additionalClassesDir = None, loadAdditionalClassesAsInterface = false)

  val result1 = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest1, appConfig, Map.empty[Symbol, Any])
  val result2 = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest2, appConfig, Map.empty[Symbol, Any])


  val metricResult1 = result1.head
  val metricResult2 = result2.head


  "The RFCAnalysisTest must calculate valid results for the test Project" in {
    assert(result1.size == 1)
    assert(metricResult1.success)
    assert(metricResult1.metricValues.nonEmpty)
    assert(result2.size == 1)
    assert(metricResult2.success)
    assert(metricResult2.metricValues.nonEmpty)
  }

  "The RFCAnalysisTest must calculate correct results for" -{

    "test project classes" in {
      //class with private methods only
      assert(metricResult1.metricValues.exists(value => value.entityIdent.equals("com/group4/sample/RFCAnalysisTest1") && value.metricValue == 2.0))
      //class with public/private methods
      assert(metricResult1.metricValues.exists(value => value.entityIdent.equals("com/group4/sample/RFCAnalysisTest2") && value.metricValue == 8.0))
      //class without methods
      assert(metricResult1.metricValues.exists(value => value.entityIdent.equals("com/group4/sample/RFCAnalysisTest3") && value.metricValue == 2.0))

    }

    "renjin.utils classes" in {
      assert(metricResult2.metricValues.exists(value => value.entityIdent.equals("org/renjin/utils/Interactive") && value.metricValue == 6.0))
      assert(metricResult2.metricValues.exists(value => value.entityIdent.equals("org/renjin/utils/WriteTable") && value.metricValue == 25.0))
      assert(metricResult2.metricValues.exists(value => value.entityIdent.equals("org/renjin/utils/Utils") && value.metricValue == 11.0))
    }
  }
}