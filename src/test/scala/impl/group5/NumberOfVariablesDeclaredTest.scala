package org.tud.sse.metrics
package impl.group5

import testutils.AnalysisTestUtils
import org.scalatest.{FlatSpec, Matchers}


import java.io.File

class NumberOfVariablesDeclaredTest extends FlatSpec with Matchers{



  "The Variables Declared Analysis" must "calculate valid results for single JARs" in {

    val analysisToTest = new NumberOfVariablesDeclaredAnalysis()

    val appConfig = new ApplicationConfiguration(inputFilePath = "", treatFilesAsLibrary = true, outFileOption = None,
      opalLoggingEnabled = false, batchModeEnabled = Some(false), excludedAnalysesNames = List(), includedAnalysesNames = List(),
      excludeJreClasses = true, additionalClassesDir = None, loadAdditionalClassesAsInterface = false)




    val fileToTestDemo = new File(getClass.getResource("/group5/vdec-test-1.0.jar").getPath)

    val resultDemo = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTestDemo, appConfig, Map.empty[Symbol, Any])
    val metricsResultDemo = resultDemo.head
    assert(metricsResultDemo.metricValues.exists(value => value.entityIdent.equals("Anzahl von Klassenvariablen in testclass") && value.metricValue == 3.0))
    assert(metricsResultDemo.metricValues.exists(value => value.entityIdent.equals("Anzahl lokaler Variablen in java.lang.String testclass.welcome(java.lang.String)") && value.metricValue == 2.0))
    assert(metricsResultDemo.metricValues.exists(value => value.entityIdent.equals("Anzahl deklarierter Variablen in allen Klassen: ") && value.metricValue == 8.0))


    val resultDemoOnlyClass = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTestDemo, appConfig, Map(Symbol("no-method") -> true))
    val metricsResultDemoOnlyClass = resultDemoOnlyClass.head
    assert(metricsResultDemoOnlyClass.metricValues.exists(value => value.entityIdent.equals("Anzahl von Klassenvariablen in testclass") && value.metricValue == 3.0))
    assert(!metricsResultDemoOnlyClass.metricValues.exists(value => value.entityIdent.equals("Anzahl lokaler Variablen in java.lang.String testclass.welcome(java.lang.String)") && value.metricValue == 2.0))


    val resultDemoOnlyMethods = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTestDemo, appConfig, Map(Symbol("no-class") -> true))
    val metricsResultDemoOnlyMethods = resultDemoOnlyMethods.head

    assert(metricsResultDemoOnlyMethods.metricValues.exists(value => value.entityIdent.equals("Anzahl von lokalen Variablen in allen Methoden von testclass") && value.metricValue == 13.0))
    assert(metricsResultDemoOnlyMethods.metricValues.exists(value => value.entityIdent.equals("Anzahl lokaler Variablen in java.lang.String testclass.welcome(java.lang.String)") && value.metricValue == 2.0))



    val resultDemoNoUnused = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTestDemo, appConfig, Map(Symbol("no-unusedfield") -> true))
    val metricsResultDemoNoUnused = resultDemoNoUnused.head
    assert(!metricsResultDemoNoUnused.metricValues.exists(value => value.entityIdent.equals("Ungenutzte Field in der Klasse testclassUndev") && value.metricValue == 1.0))
    assert(metricsResultDemoNoUnused.metricValues.exists(value => value.entityIdent.equals("Anzahl deklarierter Variablen in allen Klassen: ") && value.metricValue == 8.0))







    val resultDemoWrongOptions = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTestDemo, appConfig, Map(Symbol("no-class") -> true, Symbol("no-method") -> true))
    val metricsResultDemoWrongOptions = resultDemoWrongOptions.head
    assert(metricsResultDemoWrongOptions.metricValues.exists(value => value.entityIdent.equals("Anzahl von Klassenvariablen in testclass") && value.metricValue == 3.0))

    //Undefined
    val resultDemoUndefinedVariables = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTestDemo, appConfig, Map(Symbol("undefined-variables") -> true))
    val metricsResultDemoUndefinedVariables = resultDemoUndefinedVariables.head
    assert(metricsResultDemoUndefinedVariables.metricValues.exists(value => value.entityIdent.equals("Anzahl von Klassenvariablen in testclassUndev") && value.metricValue == 5.0))

    val resultDemoUndefinedVariablesOnlyMethod = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTestDemo, appConfig, Map(Symbol("undefined-variables") -> true,Symbol("no-class") -> true))
    val metricsResultDemoUndefinedVariablesOnlyMethod = resultDemoUndefinedVariablesOnlyMethod.head
    assert(!metricsResultDemoUndefinedVariablesOnlyMethod.metricValues.exists(value => value.entityIdent.equals("Anzahl von Klassenvariablen in testclassUndev") && value.metricValue == 1.0))

    val resultDemoUndefinedVariablesOnlyClass = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTestDemo, appConfig, Map(Symbol("undefined-variables") -> true,Symbol("no-method") -> true))
    val metricsResultDemoUndefinedVariablesOnlyClass = resultDemoUndefinedVariablesOnlyClass.head
    assert(metricsResultDemoUndefinedVariablesOnlyClass.metricValues.exists(value => value.entityIdent.equals("Anzahl von Klassenvariablen in testclassUndev") && value.metricValue == 5.0))


    val fileToTest1 = new File(getClass.getResource("/group5/pdfbox-2.0.24.jar").getPath)
    val resultPdf = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest1, appConfig, Map.empty[Symbol, Any])
    val metricResultPdf = resultPdf.head
    assert(metricResultPdf.metricValues.exists(value => value.entityIdent.equals("Anzahl von lokalen Variablen in allen Methoden von org/apache/pdfbox/pdmodel/graphics/shading/PDShadingType1") && value.metricValue == 7.0))
    assert(metricResultPdf.metricValues.exists(value => value.entityIdent.equals("Anzahl lokaler Variablen in java.awt.geom.Rectangle2D org.apache.pdfbox.pdmodel.graphics.shading.PDMeshBasedShadingType.getBounds(java.awt.geom.AffineTransform,org.apache.pdfbox.util.Matrix,int)") && value.metricValue == 6.0))
    assert(metricResultPdf.metricValues.exists(value => value.entityIdent.equals("Ungenutzte Field in der Klasse org/apache/pdfbox/pdmodel/documentinterchange/logicalstructure/PDStructureTreeRoot") && value.metricValue == 1.0))
    assert(metricResultPdf.metricValues.exists(value => value.entityIdent.equals("void org.apache.pdfbox.cos.UnmodifiableCOSDictionary.setBoolean(java.lang.String,boolean)Anzahl nicht benuzte Methoden Argumente:") && value.metricValue == 3.0))
    assert(metricResultPdf.metricValues.exists(value => value.entityIdent.equals("Anzahl von Klassenvariablen in org/apache/pdfbox/cos/COSInteger") && value.metricValue == 11.0))
    assert(metricResultPdf.metricValues.exists(value => value.entityIdent.equals("Ungenutzte Field in der Klasse org/apache/pdfbox/pdmodel/interactive/form/PDRadioButton") && value.metricValue == 1.0))

    val resultPdfIncompatibleOptions = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest1, appConfig, Map(Symbol("no-class") -> true, Symbol("no-method") -> true))
    //Falls back to default options since they would return no results at all
    val metricResultPdfIncompatibleOptions = resultPdfIncompatibleOptions.head
    assert(metricResultPdfIncompatibleOptions.metricValues.exists(value => value.entityIdent.equals("Anzahl von lokalen Variablen in allen Methoden von org/apache/pdfbox/pdmodel/graphics/shading/PDShadingType1") && value.metricValue == 7.0))
    assert(metricResultPdfIncompatibleOptions.metricValues.exists(value => value.entityIdent.equals("Anzahl lokaler Variablen in java.awt.geom.Rectangle2D org.apache.pdfbox.pdmodel.graphics.shading.PDMeshBasedShadingType.getBounds(java.awt.geom.AffineTransform,org.apache.pdfbox.util.Matrix,int)") && value.metricValue == 6.0))
    assert(metricResultPdfIncompatibleOptions.metricValues.exists(value => value.entityIdent.equals("Ungenutzte Field in der Klasse org/apache/pdfbox/pdmodel/documentinterchange/logicalstructure/PDStructureTreeRoot") && value.metricValue == 1.0))
    assert(metricResultPdfIncompatibleOptions.metricValues.exists(value => value.entityIdent.equals("void org.apache.pdfbox.cos.UnmodifiableCOSDictionary.setBoolean(java.lang.String,boolean)Anzahl nicht benuzte Methoden Argumente:") && value.metricValue == 3.0))
    assert(metricResultPdfIncompatibleOptions.metricValues.exists(value => value.entityIdent.equals("Anzahl von Klassenvariablen in org/apache/pdfbox/cos/COSInteger") && value.metricValue == 11.0))


    val resultPdfNoMethods = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest1, appConfig, Map(Symbol("no-method") -> true))
    val metricResultPdfNoMethods = resultPdfNoMethods.head

    assert(!metricResultPdfNoMethods.metricValues.exists(value => value.entityIdent.equals("Anzahl von lokalen Variablen in allen Methoden von org/apache/pdfbox/pdmodel/graphics/shading/PDShadingType1") && value.metricValue == 7.0))
    assert(!metricResultPdfNoMethods.metricValues.exists(value => value.entityIdent.equals("Anzahl lokaler Variablen in java.awt.geom.Rectangle2D org.apache.pdfbox.pdmodel.graphics.shading.PDMeshBasedShadingType.getBounds(java.awt.geom.AffineTransform,org.apache.pdfbox.util.Matrix,int)") && value.metricValue == 6.0))
    assert(metricResultPdfNoMethods.metricValues.exists(value => value.entityIdent.equals("Ungenutzte Field in der Klasse org/apache/pdfbox/pdmodel/documentinterchange/logicalstructure/PDStructureTreeRoot") && value.metricValue == 1.0))
    assert(metricResultPdfNoMethods.metricValues.exists(value => value.entityIdent.equals("void org.apache.pdfbox.cos.UnmodifiableCOSDictionary.setBoolean(java.lang.String,boolean)Anzahl nicht benuzte Methoden Argumente:") && value.metricValue == 3.0))
    assert(metricResultPdfNoMethods.metricValues.exists(value => value.entityIdent.equals("Anzahl von Klassenvariablen in org/apache/pdfbox/cos/COSInteger") && value.metricValue == 11.0))



    val resultPdfOnlyMethods = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest1, appConfig, Map(Symbol("no-class") -> true))
    val metricsResultPdfOnlyMethods = resultPdfOnlyMethods.head

    assert(!metricsResultPdfOnlyMethods.metricValues.exists(value => value.entityIdent.equals("Anzahl von Klassenvariablen in org/apache/pdfbox/cos/COSInteger") && value.metricValue == 11.0))
    assert(metricResultPdf.metricValues.exists(value => value.entityIdent.equals("Anzahl von lokalen Variablen in allen Methoden von org/apache/pdfbox/pdmodel/graphics/shading/PDShadingType1") && value.metricValue == 7.0))


    val resultPdfNoUnusedFields = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest1, appConfig, Map(Symbol("no-unusedfield") -> true))
    val metricsResultNoUnusedField = resultPdfNoUnusedFields.head

    assert(!metricsResultNoUnusedField.metricValues.exists(value => value.entityIdent.equals("Ungenutzte Field in der Klasse org/apache/pdfbox/pdmodel/interactive/form/PDRadioButton") && value.metricValue == 1.0))











    val fileToTest2 = new File(getClass.getResource("/group5/h2-2.0.202.jar").getPath)

    val resultH2 = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest2, appConfig, Map.empty[Symbol, Any])
    val metricResultH2 = resultH2.head
    //h2 doesn't supply local variable tables so all options relying on this return 0, a error message is given in console
    assert(metricResultH2.metricValues.exists(value => value.entityIdent.equals("Anzahl lokaler Variablen in boolean org.h2.index.Index.mayHaveNullDuplicates(org.h2.result.SearchRow)") && value.metricValue == 0.0))
    assert(metricResultH2.metricValues.exists(value => value.entityIdent.equals("Anzahl von Klassenvariablen in org/h2/util/geometry/EWKBUtils") && value.metricValue == 3.0))
    assert(metricResultH2.metricValues.exists(value => value.entityIdent.equals("Anzahl von Klassenvariablen in org/h2/command/ddl/AlterTableAddConstraint") && value.metricValue == 17.0))
    assert(metricResultH2.metricValues.exists(value => value.entityIdent.equals("Anzahl von Klassenvariablen in org/h2/engine/Setting") && value.metricValue == 2.0))













  }

}
