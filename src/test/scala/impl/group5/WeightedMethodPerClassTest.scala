package org.tud.sse.metrics
package impl.group5

import org.scalatest.{FlatSpec, Matchers}
import testutils.AnalysisTestUtils

import java.io.File

/**
 * Testet die Klasse WeightedMethodsPerClassAnalysis [[WeightedMethodsPerClassAnalysis]]
 */
class WeightedMethodPerClassTest extends FlatSpec with Matchers{

  "The WeightedMethodPerClassAnalysis" must "calculate valid results for single JARs" in {

    val fileToTestPdf = new File(getClass.getResource("/group5/pdfbox-2.0.24.jar").getPath)
    val fileToTestGrp4 = new File(getClass.getResource("/group4/maven-jar-sample-1.0-SNAPSHOT.jar").getPath)
    val fileToTestGson = new File(getClass.getResource("/group5/gson-2.8.9.jar").getPath)
    val analysisToTest = new WeightedMethodsPerClassAnalysis()

    val appConfig = new ApplicationConfiguration(inputFilePath = "", treatFilesAsLibrary = true, outFileOption = None,
      opalLoggingEnabled = false, batchModeEnabled = Some(false), excludedAnalysesNames = List(), includedAnalysesNames = List(),
      excludeJreClasses = true, additionalClassesDir = None, loadAdditionalClassesAsInterface = false)


    val resultPdf = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTestPdf, appConfig, Map.empty[Symbol, Any])
    val metricsResultPdf = resultPdf.head
    assert(metricsResultPdf.success)
    assert(metricsResultPdf.metricValues.nonEmpty)
    assert(metricsResultPdf.metricValues.exists(value => value.entityIdent.equals("WMC von org/apache/pdfbox/text/PDFTextStripperByArea") && value.metricValue == 14.0))
    assert(metricsResultPdf.metricValues.exists(value => value.entityIdent.equals("WMC von org/apache/pdfbox/pdmodel/graphics/shading/Type7ShadingContext") && value.metricValue == 1.0))
    assert(metricsResultPdf.metricValues.exists(value => value.entityIdent.equals("WMC von org/apache/pdfbox/pdmodel/encryption/MessageDigests") && value.metricValue == 4.0))


    val resultGson = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTestGson, appConfig, Map.empty[Symbol, Any])
    val metricsResultGson = resultGson.head
    assert(metricsResultGson.success)
    assert(metricsResultGson.metricValues.nonEmpty)
    assert(metricsResultGson.metricValues.exists(value => value.entityIdent.equals("WMC von com/google/gson/internal/JavaVersion") && value.metricValue == 11.0))
    assert(metricsResultGson.metricValues.exists(value => value.entityIdent.equals("WMC von com/google/gson/internal/bind/ObjectTypeAdapter") && value.metricValue == 9.0))
    assert(metricsResultGson.metricValues.exists(value => value.entityIdent.equals("WMC von com/google/gson/FieldNamingStrategy") && value.metricValue == 0.0))


    val resultGrp4 = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTestGrp4, appConfig, Map.empty[Symbol, Any])
    val metricsResultGrp4 = resultGrp4.head
    assert(metricsResultGrp4.metricValues.exists(value => value.entityIdent.equals("WMC von com/group4/sample/MCCCAnalysisTest") && value.metricValue == 12.0))
    assert(metricsResultGrp4.metricValues.exists(value => value.entityIdent.equals("WMC von com/group4/sample/RFCAnalysisTest1") && value.metricValue == 4.0))
    assert(metricsResultGrp4.metricValues.exists(value => value.entityIdent.equals("WMC von com/group4/sample/RFCAnalysisTest3") && value.metricValue == 1.0))
    assert(metricsResultGrp4.metricValues.exists(value => value.entityIdent.equals("WMC von com/group4/sample/RFCAnalysisTest2") && value.metricValue == 7.0))
    assert(metricsResultGrp4.metricValues.exists(value => value.entityIdent.equals("WMC von com/group4/sample/DACAnalysisTest2") && value.metricValue == 1.0))
    assert(metricsResultGrp4.metricValues.exists(value => value.entityIdent.equals("WMC von com/group4/sample/DACAnalysisTest1") && value.metricValue == 1.0))
    assert(metricsResultGrp4.metricValues.exists(value => value.entityIdent.equals("WMC von com/group4/sample/DACAnalysisTest3") && value.metricValue == 2.0))
    assert(metricsResultGrp4.metricValues.exists(value => value.entityIdent.equals("WMC von com/group4/sample/LOCphyAnalysisTest") && value.metricValue == 7.0))
    assert(metricsResultGrp4.metricValues.exists(value => value.entityIdent.equals("WMC von dem kompletten Projekt: ") && value.metricValue == 35.0))
    assert(metricsResultGrp4.metricValues.exists(value => value.entityIdent.equals("WMC Durschnitt von allen Projekt Klassen: ") && value.metricValue == 4.375))
    assert(metricsResultGrp4.metricValues.exists(value => value.entityIdent.equals("Höchster WMC Wert im Projekt ist in Klasse: com/group4/sample/MCCCAnalysisTest") && value.metricValue == 12.0))
  }


}
