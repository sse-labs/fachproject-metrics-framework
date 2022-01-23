package org.tud.sse.metrics
package impl.group4

import testutils.AnalysisTestUtils

import org.scalatest.{FreeSpec, Matchers}

import java.io.File

class DACAnalysisTest extends FreeSpec with Matchers{

  val fileToTest1 = new File(getClass.getResource("/group4/maven-jar-sample-1.0-SNAPSHOT.jar").getPath)
  val fileToTest2 = new File(getClass.getResource("/demo/utils-3.5-beta76.jar").getPath)

  val analysisToTest = new DACAnalysis()

  val appConfig = new ApplicationConfiguration(inputFilePath = "", treatFilesAsLibrary = true, outFileOption = None,
    opalLoggingEnabled = false, batchModeEnabled = Some(false), excludedAnalysesNames = List(), includedAnalysesNames = List(),
    excludeJreClasses = true, additionalClassesDir = None, loadAdditionalClassesAsInterface = false)

  val result1 = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest1, appConfig, Map.empty[Symbol, Any])
  val result2 = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest2, appConfig, Map.empty[Symbol, Any])


  val metricResult1 = result1.head
  val metricResult2 = result2.head

  "The DACAnalysisTest must calculate valid results for the test Project" in {
    assert(result1.size == 1)
    assert(metricResult1.success)
    assert(metricResult1.metricValues.nonEmpty)
    assert(result2.size == 1)
    assert(metricResult2.success)
    assert(metricResult2.metricValues.nonEmpty)
  }

  "The DACAnalysisTest must calculate correct results for" -{

    "test project classes" in {
      //class with no attributes
      assert(metricResult1.metricValues.exists(value => value.entityIdent.equals("com/group4/sample/LOCphyAnalysisTest") && value.metricValue == 2.0))
      //class with primitive/reference type attributes
      assert(metricResult1.metricValues.exists(value => value.entityIdent.equals("com/group4/sample/DACAnalysisTest3") && value.metricValue == 5.0))
      //class with primitive type attributes only
      assert(metricResult1.metricValues.exists(value => value.entityIdent.equals("com/group4/sample/DACAnalysisTest2") && value.metricValue == 1.0))
      //class with reference type attributes only
        assert(metricResult1.metricValues.exists(value => value.entityIdent.equals("com/group4/sample/DACAnalysisTest1") && value.metricValue == 5.0))
    }

    "classes from renjin.utils" in {
      assert(metricResult2.metricValues.exists(value => value.entityIdent.equals("org/renjin/utils/Interactive") && value.metricValue == 3.0))
      assert(metricResult2.metricValues.exists(value => value.entityIdent.equals("org/renjin/utils/DoublePrinter") && value.metricValue == 6.0))
      assert(metricResult2.metricValues.exists(value => value.entityIdent.equals("org/renjin/utils/LogicalPrinter") && value.metricValue == 4.0))
      assert(metricResult2.metricValues.exists(value => value.entityIdent.equals("org/renjin/utils/Tables$1") && value.metricValue == 0.0))
    }


  }
}
