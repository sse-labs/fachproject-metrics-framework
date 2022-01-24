package org.tud.sse.metrics
package impl.group4

import testutils.AnalysisTestUtils

import org.scalatest.{FreeSpec, Matchers}

import java.io.File

    class MCCCAnalysisTest extends FreeSpec with Matchers{

      val fileToTest = new File(getClass.getResource("/group4/maven-jar-sample-1.0-SNAPSHOT.jar").getPath)
      val analysisToTest = new MCCCAnalysis()

      val appConfig = new ApplicationConfiguration(inputFilePath = "", treatFilesAsLibrary = true, outFileOption = None,
        opalLoggingEnabled = false, batchModeEnabled = Some(false), excludedAnalysesNames = List(), includedAnalysesNames = List(),
        excludeJreClasses = true, additionalClassesDir = None, loadAdditionalClassesAsInterface = false)

      val result = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest, appConfig, Map.empty[Symbol, Any])

      val metricResult = result.head

      "The MCCCAnalysisTest must calculate valid results for the test Project" in {
        assert(result.size == 1)
        assert(metricResult.success)
        assert(metricResult.metricValues.nonEmpty)
      }

      "The MCCCAnalysisTest must calculate correct results for" -{

        "simple methods" in {
          assert(metricResult.metricValues.exists(value => value.entityIdent.equals("void com.group4.sample.LOCphyAnalysisTest.main(java.lang.String[])") && value.metricValue == 1.0))
        }

        "methods with if/else" in {
          assert(metricResult.metricValues.exists(value => value.entityIdent.equals("void com.group4.sample.MCCCAnalysisTest.method1()") && value.metricValue == 2.0))
        }

        "methods with switch" in {
          assert(metricResult.metricValues.exists(value => value.entityIdent.equals("void com.group4.sample.MCCCAnalysisTest.method2()") && value.metricValue == 4.0))
        }

        "methods with while/if/else combinations" in {
          assert(metricResult.metricValues.exists(value => value.entityIdent.equals("void com.group4.sample.MCCCAnalysisTest.method3(int)") && value.metricValue == 5.0))
        }

      }
    }