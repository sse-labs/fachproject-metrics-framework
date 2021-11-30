package org.tud.sse.metrics
package impl.demo

import org.scalatest.{FlatSpec, Matchers}
import org.tud.sse.metrics.testutils.AnalysisTestUtils

import java.io.File

class MethodSizeAnalysisTest extends FlatSpec with Matchers{

  "The MethodSizeAnalysis" must "calculate valid results for single JARs" in {

    val fileToTest = new File(getClass.getResource("/demo/utils-3.5-beta76.jar").getPath)
    val analysisToTest = new MethodSizeAnalysis()

    val appConfig = new ApplicationConfiguration(inputFilePath = "", treatFilesAsLibrary = true, outFileOption = None,
      opalLoggingEnabled = false, batchModeEnabled = Some(false), excludedAnalysesNames = List(), includedAnalysesNames = List(),
      excludeJreClasses = true, additionalClassesDir = None, loadAdditionalClassesAsInterface = false)

    val result = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest, appConfig, Map.empty[Symbol, Any])

    assert(result.size == 1)

    val metricResult = result.head

    assert(metricResult.success)
    assert(metricResult.metricValues.nonEmpty)

    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("void org.renjin.utils.StringPrinter.print(int)") && value.metricValue == 68.0))

  }

}
