package org.tud.sse.metrics
package impl.group5

import org.scalatest.{FlatSpec, Matchers}
import org.tud.sse.metrics.testutils.AnalysisTestUtils

import java.io.File

/**
 * Testet die Klasse NumberOfLooopsAnalysis {@link NumberOfLoopsAnalysis}
 */
class NumberOfLoopsAnalysisTest extends FlatSpec with Matchers{

  val fileToTest = new File(getClass.getResource("/demo/pdfbox-2.0.24.jar").getPath)
  val analysisToTest = new NumberOfLoopsAnalysis()

  val appConfig = new ApplicationConfiguration(inputFilePath = "", treatFilesAsLibrary = true, outFileOption = None,
    opalLoggingEnabled = false, batchModeEnabled = Some(false), excludedAnalysesNames = List(), includedAnalysesNames = List(),
    excludeJreClasses = true, additionalClassesDir = None, loadAdditionalClassesAsInterface = false)

  val result = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest, appConfig, Map.empty[Symbol, Any])

  //Testet die default einstellung
  val metricResult = result.head
  assert(metricResult.success)
  assert(metricResult.metricValues.nonEmpty)
  assert(metricResult.metricValues.exists(value => value.entityIdent.equals("Class:org/apache/pdfbox/contentstream/PDFGraphicsStreamEngine") && value.metricValue == 0.0))
  assert(metricResult.metricValues.exists(value => value.entityIdent.equals("java.lang.String org.apache.pdfbox.cos.COSDictionary.getDictionaryString(org.apache.pdfbox.cos.COSBase,java.util.List)") && value.metricValue == 2.0))
  assert(metricResult.metricValues.exists(value => value.entityIdent.equals("Class:org/apache/pdfbox/cos/COSDictionary") && value.metricValue == 7.0))
  assert(!metricResult.metricValues.exists(value => value.entityIdent.equals("void org.apache.pdfbox.pdmodel.graphics.pattern.PDTilingPattern.setBBox(org.apache.pdfbox.pdmodel.common.PDRectangle)") && value.metricValue == 0.0))

//Keine Klassen ausgeben
  val noClassSymbol: Symbol = Symbol("no-class")
  val result2 = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest, appConfig, Map(Symbol("no-class") -> true))
  val metricResult2 = result2.head
  assert(metricResult2.success)
  assert(metricResult2.metricValues.nonEmpty)
  assert(!metricResult2.metricValues.exists(value => value.entityIdent.equals("Class:org/apache/pdfbox/cos/COSDictionary") && value.metricValue == 7.0))
  assert(!metricResult2.metricValues.exists(value => value.entityIdent.equals("void org.apache.pdfbox.pdmodel.graphics.pattern.PDTilingPattern.setBBox(org.apache.pdfbox.pdmodel.common.PDRectangle)") && value.metricValue == 0.0))
  assert(metricResult2.metricValues.exists(value => value.entityIdent.equals("java.lang.String org.apache.pdfbox.cos.COSDictionary.getDictionaryString(org.apache.pdfbox.cos.COSBase,java.util.List)") && value.metricValue == 2.0))

//Nur Klassen
  val result3 = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest, appConfig, Map(Symbol("only-class") -> true))
  val metricResult3 = result3.head
  assert(metricResult3.success)
  assert(metricResult3.metricValues.nonEmpty)
  assert(metricResult3.metricValues.exists(value => value.entityIdent.equals("Class:org/apache/pdfbox/contentstream/PDFGraphicsStreamEngine") && value.metricValue == 0.0))
  assert(metricResult3.metricValues.exists(value => value.entityIdent.equals("Class:org/apache/pdfbox/cos/COSDictionary") && value.metricValue == 7.0))
  assert(!metricResult3.metricValues.exists(value => value.entityIdent.equals("void org.apache.pdfbox.pdmodel.graphics.pattern.PDTilingPattern.setBBox(org.apache.pdfbox.pdmodel.common.PDRectangle)") && value.metricValue == 0.0))
  assert(!metricResult3.metricValues.exists(value => value.entityIdent.equals("java.lang.String org.apache.pdfbox.cos.COSDictionary.getDictionaryString(org.apache.pdfbox.cos.COSBase,java.util.List)") && value.metricValue == 2.0))

  //Nur Klassen mit loops ausgeben
  val result4 = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest, appConfig, Map(Symbol("only-class-with-loops") -> true))
  val metricResult4 = result4.head
  assert(metricResult4.success)
  assert(metricResult4.metricValues.nonEmpty)
  assert(!metricResult4.metricValues.exists(value => value.entityIdent.equals("Class:org/apache/pdfbox/contentstream/PDFGraphicsStreamEngine") && value.metricValue == 0.0))
  assert(metricResult4.metricValues.exists(value => value.entityIdent.equals("Class:org/apache/pdfbox/cos/COSDictionary") && value.metricValue == 7.0))
  assert(!metricResult4.metricValues.exists(value => value.entityIdent.equals("void org.apache.pdfbox.pdmodel.graphics.pattern.PDTilingPattern.setBBox(org.apache.pdfbox.pdmodel.common.PDRectangle)") && value.metricValue == 0.0))
  assert(metricResult4.metricValues.exists(value => value.entityIdent.equals("java.lang.String org.apache.pdfbox.cos.COSDictionary.getDictionaryString(org.apache.pdfbox.cos.COSBase,java.util.List)") && value.metricValue == 2.0))

//Alles ausgeben
  val result5 = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest, appConfig, Map(Symbol("out-all-loops") -> true))
  val metricResult5 = result5.head
  assert(metricResult5.success)
  assert(metricResult5.metricValues.nonEmpty)
  assert(metricResult5.metricValues.exists(value => value.entityIdent.equals("Class:org/apache/pdfbox/cos/COSDictionary") && value.metricValue == 7.0))
  assert(metricResult5.metricValues.exists(value => value.entityIdent.equals("void org.apache.pdfbox.pdmodel.graphics.pattern.PDTilingPattern.setBBox(org.apache.pdfbox.pdmodel.common.PDRectangle)") && value.metricValue == 0.0))
  assert(metricResult5.metricValues.exists(value => value.entityIdent.equals("java.lang.String org.apache.pdfbox.cos.COSDictionary.getDictionaryString(org.apache.pdfbox.cos.COSBase,java.util.List)") && value.metricValue == 2.0))

  //Test Argument fehler
  val result6 = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest, appConfig, Map(Symbol("only-class") -> true, Symbol("no-class") -> true ))
  val metricResult6 = result6.head
  assert(metricResult6.success)
  assert(!metricResult6.metricValues.nonEmpty)
  assert(!metricResult6.metricValues.exists(value => value.entityIdent.equals("Class:org/apache/pdfbox/cos/COSDictionary") && value.metricValue == 7.0))
  assert(!metricResult6.metricValues.exists(value => value.entityIdent.equals("void org.apache.pdfbox.pdmodel.graphics.pattern.PDTilingPattern.setBBox(org.apache.pdfbox.pdmodel.common.PDRectangle)") && value.metricValue == 0.0))
  assert(!metricResult6.metricValues.exists(value => value.entityIdent.equals("java.lang.String org.apache.pdfbox.cos.COSDictionary.getDictionaryString(org.apache.pdfbox.cos.COSBase,java.util.List)") && value.metricValue == 2.0))

}
