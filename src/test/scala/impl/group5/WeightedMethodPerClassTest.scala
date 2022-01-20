package org.tud.sse.metrics
package impl.group5

import org.scalatest.{FlatSpec, Matchers}
import testutils.AnalysisTestUtils

import java.io.File

/**
 * Testet die Klasse WeightedMethodsPerClassAnalysis [[WeightedMethodsPerClassAnalysis]]
 */
class WeightedMethodPerClassTest extends FlatSpec with Matchers{

  val fileToTest = new File(getClass.getResource("/group5/pdfbox-2.0.24.jar").getPath)
  val analysisToTest = new WeightedMethodsPerClassAnalysis()

  val appConfig = new ApplicationConfiguration(inputFilePath = "", treatFilesAsLibrary = true, outFileOption = None,
    opalLoggingEnabled = false, batchModeEnabled = Some(false), excludedAnalysesNames = List(), includedAnalysesNames = List(),
    excludeJreClasses = true, additionalClassesDir = None, loadAdditionalClassesAsInterface = false)


  private val resultPdf = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest, appConfig, Map.empty[Symbol, Any])
  val metricsResultPdf = resultPdf.head

}
