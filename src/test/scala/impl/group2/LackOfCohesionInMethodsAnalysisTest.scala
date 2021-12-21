package impl.group2

import java.io.File

import org.scalatest.{FlatSpec, Matchers}
import org.tud.sse.metrics.ApplicationConfiguration
import org.tud.sse.metrics.testutils.AnalysisTestUtils

class LackOfCohesionInMethodsAnalysisTest extends FlatSpec with Matchers{

  val fileToTest = new File(getClass.getResource("/group2/LCOM_Test1.jar").getPath)
  val analysisToTest = new LackOfCohesionInMethodsAnalysis()

  val appConfig = new ApplicationConfiguration(inputFilePath = "", treatFilesAsLibrary = true, outFileOption = None,
    opalLoggingEnabled = false, batchModeEnabled = Some(false), excludedAnalysesNames = List(), includedAnalysesNames = List(),
    excludeJreClasses = true, additionalClassesDir = None, loadAdditionalClassesAsInterface = false)

  val result = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest, appConfig, Map.empty[Symbol, Any])

  assert(result.size == 1)

  val metricResult = result.head

  assert(metricResult.success)
  assert(metricResult.metricValues.nonEmpty)

  assert(metricResult.metricValues.exists(value => value.entityIdent.equals("ErsteKlasse") && value.metricValue == 0))
  assert(metricResult.metricValues.exists(value => value.entityIdent.equals("ZweiteKlasse") && value.metricValue == 9))
}
