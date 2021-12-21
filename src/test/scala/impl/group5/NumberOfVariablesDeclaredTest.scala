package org.tud.sse.metrics
package impl.group5

import testutils.AnalysisTestUtils

import org.scalatest.{FlatSpec, Matchers}

import java.io.File

class NumberOfVariablesDeclaredTest extends FlatSpec with Matchers{

  "The MethodSizeAnalysis" must "calculate valid results for single JARs" in {

    val fileToTest = new File(getClass.getResource("/group5/vdec-test-1.0.jar").getPath)
    val analysisToTest = new NumberOfVariablesDeclaredAnalysis()

    val appConfig = new ApplicationConfiguration(inputFilePath = "", treatFilesAsLibrary = true, outFileOption = None,
      opalLoggingEnabled = false, batchModeEnabled = Some(false), excludedAnalysesNames = List(), includedAnalysesNames = List(),
      excludeJreClasses = true, additionalClassesDir = None, loadAdditionalClassesAsInterface = false)

    val result = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest, appConfig, Map.empty[Symbol, Any])

    //TODO Test noch fehlerhaft
    val metricResult = result.head
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("Anzahl von Klassenvariablen in testclassUndev") && value.metricValue == 4.0))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("Anzahl von Klassenvariablen in testclass") && value.metricValue == 2.0))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("Anzahl lokaler Variablen in int testclassUndev.rechner(int,int)") && value.metricValue == 4.0))
    assert(metricResult.metricValues.exists(value => value.entityIdent.equals("Anzahl lokaler Variablen in int testclass.rechner(int,int)") && value.metricValue == 4.0))








  }

}
