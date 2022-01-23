package org.tud.sse.metrics
package testLCC

import impl.group3.LCCAnalysis
import testutils.AnalysisTestUtils

import org.scalatest.{FlatSpec, Matchers}

import java.io.File


class LCCAnalysisTest extends FlatSpec with Matchers {

  /** test file helloWorld:  directly connected pairs=3, indirect:0, result = (3+0/2)/6=0.5 */
  val fileToTest = new File(getClass.getResource("/demo/HelloWorld.jar").getPath)
  val analysisToTest = new LCCAnalysis()

  val appConfig = new ApplicationConfiguration(inputFilePath = "", treatFilesAsLibrary = true, outFileOption = None,
    opalLoggingEnabled = false, batchModeEnabled = Some(false), excludedAnalysesNames = List(), includedAnalysesNames = List(),
    excludeJreClasses = true, additionalClassesDir = None, loadAdditionalClassesAsInterface = false)


  val result = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest, appConfig, Map.empty[Symbol, Any])
  private val metricResult = result.head

  assert(metricResult.success)
  assert(metricResult.metricValues.nonEmpty)
  assert(metricResult.metricValues.exists(value => value.metricName.equals("metric.lcc")))

  assert(metricResult.metricValues.exists(value => value.entityIdent.equals("helloWorld") && value.metricValue == 0.5 ) )

  /** test file hello1:  directly connected pairs=3, indirect:0, result = (5+2/2)/6=1.0 */
  val fileToTest1 = new File(getClass.getResource("/demo/Hello1.jar").getPath)
  val analysisToTest1 = new LCCAnalysis()
  val result1 = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest1, fileToTest1, appConfig, Map.empty[Symbol, Any])
  private val metricResult1 = result1.head

  assert(metricResult1.success)
  assert(metricResult1.metricValues.nonEmpty)
  assert(metricResult1.metricValues.exists(value => value.metricName.equals("metric.lcc")))

  assert(metricResult1.metricValues.exists(value => value.entityIdent.equals("hello1") && value.metricValue == 1.0))



  val fileToTest2 = new File(getClass.getResource("/demo/utils-3.5-beta76.jar").getPath)
  val analysisToTest2 = new LCCAnalysis()
  val result2 = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest2, fileToTest2, appConfig, Map.empty[Symbol, Any])
  private val metricResult2 = result2.head

  assert(metricResult2.success)
  assert(metricResult2.metricValues.nonEmpty)

  // util file has many different classes with different values, can not identify exact value of each
  // reference test: println(metricResult2.metricValues)

  val fileToTest3 = new File(getClass.getResource("/demo/empty.jar").getPath)
  val analysisToTest3 = new LCCAnalysis()
  val result3 = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest3, fileToTest3, appConfig, Map.empty[Symbol, Any])
  private val metricResult3 = result2.head

  assert(metricResult3.success)
  assert(metricResult3.metricValues.nonEmpty)

}



