package org.tud.sse.metrics
package testTCC

import org.scalatest.{FlatSpec, Matchers}
import impl.group3.TCCAnalysis
import testutils.AnalysisTestUtils

import java.io.File

class TCCAnalysisTest extends FlatSpec with Matchers{
  /** give path of the file being tested */
  val fileToTest = new File(getClass.getResource("/demo/HelloWorld.jar").getPath)
  val analysisToTest = new TCCAnalysis()

  val appConfig = new ApplicationConfiguration(inputFilePath = "", treatFilesAsLibrary = true, outFileOption = None,
    opalLoggingEnabled = false, batchModeEnabled = Some(false), excludedAnalysesNames = List(), includedAnalysesNames = List(),
    excludeJreClasses = true, additionalClassesDir = None, loadAdditionalClassesAsInterface = false)


  val result = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest, appConfig, Map.empty[Symbol, Any])
  private val metricResult = result.head

  /** test: if the test is successed, if the metric value isn't empty, and name, entity, and the final value-> in this case the
   * number of directly connected methos, if it's the right number of related project */
  assert(metricResult.success)
  assert(metricResult.metricValues.nonEmpty)
  assert(metricResult.metricValues.exists(value => value.metricName.equals("metric.tcc")))

  assert(metricResult.metricValues.exists(value => value.entityIdent.equals("helloWorld") && value.metricValue == 0.5 ) )

  /** use 2nd file to test */

  val fileToTest1 = new File(getClass.getResource("/demo/Hello1.jar").getPath)
  val analysisToTest1 = new TCCAnalysis()


  val result1 = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest1, fileToTest1, appConfig, Map.empty[Symbol, Any])
  private val metricResult1 = result1.head

  assert(metricResult1.success)
  assert(metricResult1.metricValues.nonEmpty)
  assert(metricResult1.metricValues.exists(value => value.metricName.equals("metric.tcc")))

  assert(metricResult1.metricValues.exists(value => value.entityIdent.equals("hello1") && value.metricValue == 0.3 ) )



  /** use 3rd file to test, this project has no directly connected methods */

  val fileToTest2 = new File(getClass.getResource("/demo/empty.jar").getPath)
  val analysisToTest2 = new TCCAnalysis()


  val result2 = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest2, fileToTest2, appConfig, Map.empty[Symbol, Any])
  private val metricResult2 = result2.head

  assert(metricResult2.success)
  assert(metricResult2.metricValues.nonEmpty)
  assert(metricResult2.metricValues.exists(value => value.metricName.equals("metric.tcc")))

  assert(metricResult2.metricValues.exists(value => value.entityIdent.equals("empty") && value.metricValue == 0.0 ) )

}
