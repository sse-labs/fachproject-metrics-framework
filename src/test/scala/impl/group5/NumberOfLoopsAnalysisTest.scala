package org.tud.sse.metrics
package impl.group5

import org.scalatest.{FlatSpec, Matchers}
import testutils.AnalysisTestUtils

import java.io.File

/**
 * Testet die Klasse NumberOfLooopsAnalysis [[NumberOfLoopsAnalysis]]
 */
class NumberOfLoopsAnalysisTest extends FlatSpec with Matchers{

  val fileToTest = new File(getClass.getResource("/group5/pdfbox-2.0.24.jar").getPath)
  val analysisToTest = new NumberOfLoopsAnalysis()

  val appConfig = new ApplicationConfiguration(inputFilePath = "", treatFilesAsLibrary = true, outFileOption = None,
    opalLoggingEnabled = false, batchModeEnabled = Some(false), excludedAnalysesNames = List(), includedAnalysesNames = List(),
    excludeJreClasses = true, additionalClassesDir = None, loadAdditionalClassesAsInterface = false)

  private val result = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest, appConfig, Map.empty[Symbol, Any])

  //Testet die default einstellung
  private val metricResult = result.head
  assert(metricResult.success)
  assert(metricResult.metricValues.nonEmpty)
  assert(metricResult.metricValues.exists(value => value.entityIdent.equals("Class:org/apache/pdfbox/contentstream/PDFGraphicsStreamEngine") && value.metricValue == 0.0))
  assert(metricResult.metricValues.exists(value => value.entityIdent.equals("void org.apache.pdfbox.filter.Predictor.decodePredictorRow(int,int,int,int,byte[],byte[])") && value.metricValue == 9.0))
  assert(metricResult.metricValues.exists(value => value.entityIdent.equals("void org.apache.pdfbox.multipdf.PDFMergerUtility.appendDocument(org.apache.pdfbox.pdmodel.PDDocument,org.apache.pdfbox.pdmodel.PDDocument)") && value.metricValue == 10.0))
  assert(metricResult.metricValues.exists(value => value.entityIdent.equals("java.lang.String org.apache.pdfbox.cos.COSDictionary.getDictionaryString(org.apache.pdfbox.cos.COSBase,java.util.List)") && value.metricValue == 2.0))
  assert(metricResult.metricValues.exists(value => value.entityIdent.equals("Class:org/apache/pdfbox/cos/COSDictionary") && value.metricValue == 7.0))
  assert(!metricResult.metricValues.exists(value => value.entityIdent.equals("void org.apache.pdfbox.pdmodel.graphics.pattern.PDTilingPattern.setBBox(org.apache.pdfbox.pdmodel.common.PDRectangle)") && value.metricValue == 0.0))

//Keine Klassen ausgeben

  private  val result2 = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest, appConfig, Map(Symbol("no-class") -> true))
  private  val metricResult2 = result2.head
  assert(metricResult2.success)
  assert(metricResult2.metricValues.nonEmpty)
  assert(!metricResult2.metricValues.exists(value => value.entityIdent.equals("Class:org/apache/pdfbox/cos/COSDictionary") && value.metricValue == 7.0))
  assert(!metricResult2.metricValues.exists(value => value.entityIdent.equals("void org.apache.pdfbox.pdmodel.graphics.pattern.PDTilingPattern.setBBox(org.apache.pdfbox.pdmodel.common.PDRectangle)") && value.metricValue == 0.0))
  assert(metricResult2.metricValues.exists(value => value.entityIdent.equals("java.lang.String org.apache.pdfbox.cos.COSDictionary.getDictionaryString(org.apache.pdfbox.cos.COSBase,java.util.List)") && value.metricValue == 2.0))

//Nur Klassen
  private  val result3 = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest, appConfig, Map(Symbol("no-methoden") -> true))
  private  val metricResult3 = result3.head
  assert(metricResult3.success)
  assert(metricResult3.metricValues.nonEmpty)
  assert(metricResult3.metricValues.exists(value => value.entityIdent.equals("Class:org/apache/pdfbox/contentstream/PDFGraphicsStreamEngine") && value.metricValue == 0.0))
  assert(metricResult3.metricValues.exists(value => value.entityIdent.equals("Class:org/apache/pdfbox/cos/COSDictionary") && value.metricValue == 7.0))
  assert(!metricResult3.metricValues.exists(value => value.entityIdent.equals("void org.apache.pdfbox.pdmodel.graphics.pattern.PDTilingPattern.setBBox(org.apache.pdfbox.pdmodel.common.PDRectangle)") && value.metricValue == 0.0))
  assert(!metricResult3.metricValues.exists(value => value.entityIdent.equals("java.lang.String org.apache.pdfbox.cos.COSDictionary.getDictionaryString(org.apache.pdfbox.cos.COSBase,java.util.List)") && value.metricValue == 2.0))

  //Nur Klassen mit loops ausgeben
  private  val result4 = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest, appConfig, Map(Symbol("no-zeroloopclasses") -> true))
  private  val metricResult4 = result4.head
  assert(metricResult4.success)
  assert(metricResult4.metricValues.nonEmpty)
  assert(!metricResult4.metricValues.exists(value => value.entityIdent.equals("Class:org/apache/pdfbox/contentstream/PDFGraphicsStreamEngine") && value.metricValue == 0.0))
  assert(metricResult4.metricValues.exists(value => value.entityIdent.equals("Class:org/apache/pdfbox/cos/COSDictionary") && value.metricValue == 7.0))
  assert(!metricResult4.metricValues.exists(value => value.entityIdent.equals("void org.apache.pdfbox.pdmodel.graphics.pattern.PDTilingPattern.setBBox(org.apache.pdfbox.pdmodel.common.PDRectangle)") && value.metricValue == 0.0))
  assert(metricResult4.metricValues.exists(value => value.entityIdent.equals("java.lang.String org.apache.pdfbox.cos.COSDictionary.getDictionaryString(org.apache.pdfbox.cos.COSBase,java.util.List)") && value.metricValue == 2.0))

//Alles ausgeben
  private  val result5 = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest, appConfig, Map(Symbol("out-all") -> true))
  private  val metricResult5 = result5.head
  assert(metricResult5.success)
  assert(metricResult5.metricValues.nonEmpty)
  assert(metricResult5.metricValues.exists(value => value.entityIdent.equals("Class:org/apache/pdfbox/cos/COSDictionary") && value.metricValue == 7.0))
  assert(metricResult5.metricValues.exists(value => value.entityIdent.equals("void org.apache.pdfbox.pdmodel.graphics.pattern.PDTilingPattern.setBBox(org.apache.pdfbox.pdmodel.common.PDRectangle)") && value.metricValue == 0.0))
  assert(metricResult5.metricValues.exists(value => value.entityIdent.equals("java.lang.String org.apache.pdfbox.cos.COSDictionary.getDictionaryString(org.apache.pdfbox.cos.COSBase,java.util.List)") && value.metricValue == 2.0))

  //Test Argument fehler
  private val result6 = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest, appConfig, Map(Symbol("no-methoden") -> true, Symbol("no-class") -> true ))
  private val metricResult6 = result6.head
  assert(metricResult6.success)
  assert(metricResult6.metricValues.isEmpty)
  assert(!metricResult6.metricValues.exists(value => value.entityIdent.equals("Class:org/apache/pdfbox/cos/COSDictionary") && value.metricValue == 7.0))
  assert(!metricResult6.metricValues.exists(value => value.entityIdent.equals("void org.apache.pdfbox.pdmodel.graphics.pattern.PDTilingPattern.setBBox(org.apache.pdfbox.pdmodel.common.PDRectangle)") && value.metricValue == 0.0))
  assert(!metricResult6.metricValues.exists(value => value.entityIdent.equals("java.lang.String org.apache.pdfbox.cos.COSDictionary.getDictionaryString(org.apache.pdfbox.cos.COSBase,java.util.List)") && value.metricValue == 2.0))

  val fileToTest2 = new File(getClass.getResource("/group5/h2-2.0.202.jar").getPath)
  private val resultH2 = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest2, appConfig, Map.empty[Symbol, Any])
  private val metricResultH2 = resultH2.head
  assert(metricResultH2.success)
  assert(metricResultH2.metricValues.nonEmpty)
  assert(metricResultH2.metricValues.exists(value => value.entityIdent.equals("org.h2.result.ResultWithGeneratedKeys org.h2.command.CommandContainer.executeUpdateWithGeneratedKeys(org.h2.command.dml.DataChangeStatement,java.lang.Object)") && value.metricValue == 5.0))
  assert(metricResultH2.metricValues.exists(value => value.entityIdent.equals("int org.h2.expression.analysis.WindowFrame.getIndex(org.h2.engine.SessionLocal,java.util.ArrayList,org.h2.result.SortOrder,int,org.h2.expression.analysis.WindowFrameBound,boolean)") && value.metricValue == 8.0))
  assert(metricResultH2.metricValues.exists(value => value.entityIdent.equals("boolean org.h2.index.Index.mayHaveNullDuplicates(org.h2.result.SearchRow)") && value.metricValue == 2.0))
  assert(metricResultH2.metricValues.exists(value => value.entityIdent.equals("java.lang.String org.h2.jdbc.JdbcConnection.translateSQLImpl(java.lang.String)") && value.metricValue == 7.0))
  assert(metricResultH2.metricValues.exists(value => value.entityIdent.equals("java.util.ArrayList org.h2.mode.PgCatalogTable.generateRows(org.h2.engine.SessionLocal,org.h2.result.SearchRow,org.h2.result.SearchRow)") && value.metricValue == 14.0))
  assert(metricResultH2.metricValues.exists(value => value.entityIdent.equals("void org.h2.mvstore.db.MVSortedTempResult.<init>(org.h2.engine.Database,org.h2.expression.Expression[],boolean,int[],int,int,org.h2.result.SortOrder)") && value.metricValue == 6.0))
  assert(metricResultH2.metricValues.exists(value => value.entityIdent.equals("void org.h2.server.TcpServer.shutdown(java.lang.String,java.lang.String,boolean,boolean)") && value.metricValue == 1.0))








}
