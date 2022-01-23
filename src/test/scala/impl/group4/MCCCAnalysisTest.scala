package org.tud.sse.metrics
package impl.group4

import testutils.AnalysisTestUtils

import org.scalatest.{FreeSpec, Matchers}

import java.io.File

    class MCCCAnalysisTest extends FreeSpec with Matchers{

      val fileToTest1 = new File(getClass.getResource("/group4/maven-jar-sample-1.0-SNAPSHOT.jar").getPath)
      val fileToTest2 = new File(getClass.getResource("/demo/utils-3.5-beta76.jar").getPath)

      val analysisToTest = new MCCCAnalysis()

      val appConfig = new ApplicationConfiguration(inputFilePath = "", treatFilesAsLibrary = true, outFileOption = None,
        opalLoggingEnabled = false, batchModeEnabled = Some(false), excludedAnalysesNames = List(), includedAnalysesNames = List(),
        excludeJreClasses = true, additionalClassesDir = None, loadAdditionalClassesAsInterface = false)

      val result1 = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest1, appConfig, Map.empty[Symbol, Any])
      val result2 = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest2, appConfig, Map.empty[Symbol, Any])

      val metricResult1 = result1.head
      val metricResult2 = result2.head

      "The MCCCAnalysisTest must calculate valid results for the test Project" in {
        assert(result1.size == 1)
        assert(metricResult1.success)
        assert(metricResult1.metricValues.nonEmpty)
        assert(result2.size == 1)
        assert(metricResult2.success)
        assert(metricResult2.metricValues.nonEmpty)
      }

      "The MCCCAnalysisTest must calculate correct results for" -{

        "test project methods" in {
          //simple method
          assert(metricResult1.metricValues.exists(value => value.entityIdent.equals("void com.group4.sample.LOCphyAnalysisTest.main(java.lang.String[])") && value.metricValue == 1.0))
          //method with if/else
          assert(metricResult1.metricValues.exists(value => value.entityIdent.equals("void com.group4.sample.MCCCAnalysisTest.method1()") && value.metricValue == 2.0))
          //method with switch
          assert(metricResult1.metricValues.exists(value => value.entityIdent.equals("void com.group4.sample.MCCCAnalysisTest.method2()") && value.metricValue == 4.0))
          //method with while/if/else combination
          assert(metricResult1.metricValues.exists(value => value.entityIdent.equals("void com.group4.sample.MCCCAnalysisTest.method3(int)") && value.metricValue == 5.0))
        }

        "renjin.utils methods" in {
          assert(metricResult2.metricValues.exists(value => value.entityIdent.equals("org.renjin.sexp.StringVector org.renjin.utils.Tables.readtablehead(org.renjin.eval.Context,org.renjin.sexp.SEXP,double,java.lang.String,boolean,java.lang.String,java.lang.String,boolean)") && value.metricValue == 5.0))
          assert(metricResult2.metricValues.exists(value => value.entityIdent.equals("org.renjin.sexp.Vector org.renjin.utils.Tables$Converter.build(org.renjin.sexp.StringVector,java.util.Set)") && value.metricValue == 3.0))
          assert(metricResult2.metricValues.exists(value => value.entityIdent.equals("void org.renjin.utils.Tables$Converter.<init>()") && value.metricValue == 1.0))
          assert(metricResult2.metricValues.exists(value => value.entityIdent.equals("void org.renjin.utils.FactorPrinter.print(int)") && value.metricValue == 3.0))
        }


      }
    }