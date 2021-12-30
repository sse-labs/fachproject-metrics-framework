package org.tud.sse.metrics
package impl.group5

import testutils.AnalysisTestUtils

import org.scalatest.{FlatSpec, Matchers}

import java.io.File

class VrefAnalysisTest extends FlatSpec with Matchers{

  val fileToTest = new File(getClass.getResource("/group5/vref-1.0-SNAPSHOT.jar").getPath)

  val analysisToTest = new vrefAnalysis()

  val appConfig = new ApplicationConfiguration(inputFilePath = "", treatFilesAsLibrary = true, outFileOption = None,
    opalLoggingEnabled = false, batchModeEnabled = Some(false), excludedAnalysesNames = List(), includedAnalysesNames = List(),
    excludeJreClasses = true, additionalClassesDir = None, loadAdditionalClassesAsInterface = false)
  private val result = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest, appConfig, Map.empty[Symbol, Any])
  private val metricResult = result.head
  assert(metricResult.success)
  assert(metricResult.metricValues.nonEmpty)
  assert(metricResult.metricValues.exists(value => value.entityIdent.equals("void testclass.<init>(int,java.lang.String,float,java.lang.String) Anzahl der Null Referenzen: ") && value.metricValue == 1.0))
  assert(metricResult.metricValues.exists(value => value.entityIdent.equals("int testclass.rechner(int,int) Anzahl der gesamten Variablen Referenzen : ") && value.metricValue == 10.0))
  assert(metricResult.metricValues.exists(value => value.entityIdent.equals("int testclass.rechner(int,int) Anzahl der Field Referenzen: ") && value.metricValue == 1.0))
  assert(metricResult.metricValues.exists(value => value.entityIdent.equals("void testclass.<init>(int,java.lang.String,float,java.lang.String) Anzahl der this Referenzen: ") && value.metricValue == 6.0))
  assert(metricResult.metricValues.exists(value => value.entityIdent.equals("testclass  Anzahl aller Field Referenzen :") && value.metricValue == 6.0))
  assert(metricResult.metricValues.exists(value => value.entityIdent.equals("testclass  Anzahl aller Referenzen der Variablen:") && value.metricValue == 14.0))
  assert(metricResult.metricValues.exists(value => value.entityIdent.equals("int testclass.rechner(int,int) Anzahl der Null Referenzen: ") && value.metricValue == 1.0))
  assert(metricResult.metricValues.exists(value => value.entityIdent.equals("testclass Anzahl aller Null Referenzen: ") && value.metricValue == 2.0))
  assert(!metricResult.metricValues.exists(value => value.entityIdent.equals("int testclass.rechner(int,int)Die Anzahl der load Referenzen der Variable local :") && value.metricValue == 2.0))
  assert(!metricResult.metricValues.exists(value => value.entityIdent.equals("int testclass.rechner(int,int)Die Anzahl der store Referenzen der Variable local2 :") && value.metricValue == 3.0))
  assert(!metricResult.metricValues.exists(value => value.entityIdent.equals("int testclass.rechner(int,int)Die Anzahl der load Referenzen der Field in der Methode zahl :") && value.metricValue == 1.0))
  assert(!metricResult.metricValues.exists(value => value.entityIdent.equals("void testclass.<init>(int,java.lang.String,float,java.lang.String)Die Anzahl der Store Referenzen der Field in der Methode zahl :") && value.metricValue == 1.0))
  assert(!metricResult.metricValues.exists(value => value.entityIdent.equals("void vrefmain.main(java.lang.String[]) Anzahl der Load Field Referenzen: ") && value.metricValue == 0.0))
  assert(!metricResult.metricValues.exists(value => value.entityIdent.equals("void testnothis.bild(int,java.lang.String) Anzahl der Load Referenzen: ") && value.metricValue == 2.0))
  assert(!metricResult.metricValues.exists(value => value.entityIdent.equals("int testclass.rechner(int,int) Anzahl der Store Referenzen: ") && value.metricValue == 5.0))
  assert(!metricResult.metricValues.exists(value => value.entityIdent.equals("int testclass.rechner(int,int) Anzahl der Load Field Referenzen: ") && value.metricValue == 1.0))


  private val result2 = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest, appConfig, Map(Symbol("no-methoden") -> true))
  private val metricResult2 = result2.head
  assert(metricResult2.success)
  assert(metricResult2.metricValues.nonEmpty)
  assert(!metricResult2.metricValues.exists(value => value.entityIdent.equals("void testclass.<init>(int,java.lang.String,float,java.lang.String) Anzahl der Null Referenzen: ") && value.metricValue == 1.0))
  assert(!metricResult2.metricValues.exists(value => value.entityIdent.equals("int testclass.rechner(int,int) Anzahl der gesamten Variablen Referenzen : ") && value.metricValue == 10.0))
  assert(!metricResult2.metricValues.exists(value => value.entityIdent.equals("int testclass.rechner(int,int) Anzahl der Field Referenzen: ") && value.metricValue == 1.0))
  assert(!metricResult2.metricValues.exists(value => value.entityIdent.equals("void testclass.<init>(int,java.lang.String,float,java.lang.String) Anzahl der this Referenzen: ") && value.metricValue == 6.0))
  assert(metricResult2.metricValues.exists(value => value.entityIdent.equals("testclass  Anzahl aller Field Referenzen :") && value.metricValue == 6.0))
  assert(metricResult2.metricValues.exists(value => value.entityIdent.equals("testclass  Anzahl aller Referenzen der Variablen:") && value.metricValue == 14.0))
  assert(!metricResult2.metricValues.exists(value => value.entityIdent.equals("int testclass.rechner(int,int) Anzahl der Null Referenzen: ") && value.metricValue == 1.0))
  assert(metricResult2.metricValues.exists(value => value.entityIdent.equals("testclass Anzahl aller Null Referenzen: ") && value.metricValue == 2.0))

  private val result3 = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest, appConfig, Map(Symbol("no-this") -> true))
  private val metricResult3 = result3.head
  assert(metricResult3.success)
  assert(metricResult3.metricValues.nonEmpty)
  assert(metricResult3.metricValues.exists(value => value.entityIdent.equals("void testclass.<init>(int,java.lang.String,float,java.lang.String) Anzahl der Null Referenzen: ") && value.metricValue == 1.0))
  assert(metricResult3.metricValues.exists(value => value.entityIdent.equals("int testclass.rechner(int,int) Anzahl der gesamten Variablen Referenzen : ") && value.metricValue == 10.0))
  assert(metricResult3.metricValues.exists(value => value.entityIdent.equals("int testclass.rechner(int,int) Anzahl der Field Referenzen: ") && value.metricValue == 1.0))
  assert(!metricResult3.metricValues.exists(value => value.entityIdent.equals("void testclass.<init>(int,java.lang.String,float,java.lang.String) Anzahl der this Referenzen: ") && value.metricValue == 6.0))
  assert(metricResult3.metricValues.exists(value => value.entityIdent.equals("testclass  Anzahl aller Field Referenzen :") && value.metricValue == 6.0))
  assert(metricResult3.metricValues.exists(value => value.entityIdent.equals("testclass  Anzahl aller Referenzen der Variablen:") && value.metricValue == 14.0))
  assert(metricResult3.metricValues.exists(value => value.entityIdent.equals("int testclass.rechner(int,int) Anzahl der Null Referenzen: ") && value.metricValue == 1.0))
  assert(metricResult3.metricValues.exists(value => value.entityIdent.equals("testclass Anzahl aller Null Referenzen: ") && value.metricValue == 2.0))



  private val result4 = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest, appConfig, Map(Symbol("infozuvariablen") -> true))
  private val metricResult4 = result4.head
  assert(metricResult4.success)
  assert(metricResult4.metricValues.nonEmpty)
  assert(metricResult4.metricValues.exists(value => value.entityIdent.equals("void testclass.<init>(int,java.lang.String,float,java.lang.String) Anzahl der Null Referenzen: ") && value.metricValue == 1.0))
  assert(metricResult4.metricValues.exists(value => value.entityIdent.equals("int testclass.rechner(int,int) Anzahl der gesamten Variablen Referenzen : ") && value.metricValue == 10.0))
  assert(metricResult4.metricValues.exists(value => value.entityIdent.equals("int testclass.rechner(int,int) Anzahl der Field Referenzen: ") && value.metricValue == 1.0))
  assert(metricResult4.metricValues.exists(value => value.entityIdent.equals("void testclass.<init>(int,java.lang.String,float,java.lang.String) Anzahl der this Referenzen: ") && value.metricValue == 6.0))
  assert(metricResult4.metricValues.exists(value => value.entityIdent.equals("testclass  Anzahl aller Field Referenzen :") && value.metricValue == 6.0))
  assert(metricResult4.metricValues.exists(value => value.entityIdent.equals("testclass  Anzahl aller Referenzen der Variablen:") && value.metricValue == 14.0))
  assert(metricResult4.metricValues.exists(value => value.entityIdent.equals("int testclass.rechner(int,int) Anzahl der Null Referenzen: ") && value.metricValue == 1.0))
  assert(metricResult4.metricValues.exists(value => value.entityIdent.equals("testclass Anzahl aller Null Referenzen: ") && value.metricValue == 2.0))
  assert(metricResult4.metricValues.exists(value => value.entityIdent.equals("int testclass.rechner(int,int)Die Anzahl der load Referenzen der Variable local :") && value.metricValue == 2.0))
  assert(metricResult4.metricValues.exists(value => value.entityIdent.equals("int testclass.rechner(int,int)Die Anzahl der store Referenzen der Variable local2 :") && value.metricValue == 3.0))
  assert(metricResult4.metricValues.exists(value => value.entityIdent.equals("int testclass.rechner(int,int)Die Anzahl der load Referenzen der Field in der Methode zahl :") && value.metricValue == 1.0))
  assert(metricResult4.metricValues.exists(value => value.entityIdent.equals("void testclass.<init>(int,java.lang.String,float,java.lang.String)Die Anzahl der Store Referenzen der Field in der Methode zahl :") && value.metricValue == 1.0))




  private val result5 = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest, appConfig, Map(Symbol("no-class") -> true))
  private val metricResult5 = result5.head
  assert(metricResult5.success)
  assert(metricResult5.metricValues.nonEmpty)
  assert(metricResult5.metricValues.exists(value => value.entityIdent.equals("void testclass.<init>(int,java.lang.String,float,java.lang.String) Anzahl der Null Referenzen: ") && value.metricValue == 1.0))
  assert(metricResult5.metricValues.exists(value => value.entityIdent.equals("int testclass.rechner(int,int) Anzahl der gesamten Variablen Referenzen : ") && value.metricValue == 10.0))
  assert(metricResult5.metricValues.exists(value => value.entityIdent.equals("int testclass.rechner(int,int) Anzahl der Field Referenzen: ") && value.metricValue == 1.0))
  assert(metricResult5.metricValues.exists(value => value.entityIdent.equals("void testclass.<init>(int,java.lang.String,float,java.lang.String) Anzahl der this Referenzen: ") && value.metricValue == 6.0))
  assert(!metricResult5.metricValues.exists(value => value.entityIdent.equals("testclass  Anzahl aller Field Referenzen :") && value.metricValue == 6.0))
  assert(!metricResult5.metricValues.exists(value => value.entityIdent.equals("testclass  Anzahl aller Referenzen der Variablen:") && value.metricValue == 14.0))
  assert(metricResult5.metricValues.exists(value => value.entityIdent.equals("int testclass.rechner(int,int) Anzahl der Null Referenzen: ") && value.metricValue == 1.0))
  assert(!metricResult5.metricValues.exists(value => value.entityIdent.equals("testclass Anzahl aller Null Referenzen: ") && value.metricValue == 2.0))



  private val result6 = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest, appConfig, Map(Symbol("nonullreferenz") -> true))
  private val metricResult6 = result6.head
  assert(metricResult6.success)
  assert(metricResult6.metricValues.nonEmpty)
  assert(!metricResult6.metricValues.exists(value => value.entityIdent.equals("void testclass.<init>(int,java.lang.String,float,java.lang.String) Anzahl der Null Referenzen: ") && value.metricValue == 1.0))
  assert(metricResult6.metricValues.exists(value => value.entityIdent.equals("int testclass.rechner(int,int) Anzahl der gesamten Variablen Referenzen : ") && value.metricValue == 10.0))
  assert(metricResult6.metricValues.exists(value => value.entityIdent.equals("int testclass.rechner(int,int) Anzahl der Field Referenzen: ") && value.metricValue == 1.0))
  assert(metricResult6.metricValues.exists(value => value.entityIdent.equals("void testclass.<init>(int,java.lang.String,float,java.lang.String) Anzahl der this Referenzen: ") && value.metricValue == 6.0))
  assert(metricResult6.metricValues.exists(value => value.entityIdent.equals("testclass  Anzahl aller Field Referenzen :") && value.metricValue == 6.0))
  assert(metricResult6.metricValues.exists(value => value.entityIdent.equals("testclass  Anzahl aller Referenzen der Variablen:") && value.metricValue == 14.0))
  assert(!metricResult6.metricValues.exists(value => value.entityIdent.equals("int testclass.rechner(int,int) Anzahl der Null Referenzen: ") && value.metricValue == 1.0))
  assert(!metricResult6.metricValues.exists(value => value.entityIdent.equals("testclass Anzahl aller Null Referenzen: ") && value.metricValue == 2.0))




  private val result7 = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest, appConfig, Map(Symbol("outstoreandloadcount") -> true))
  private val metricResult7 = result7.head
  assert(metricResult7.success)
  assert(metricResult7.metricValues.nonEmpty)
  assert(metricResult7.metricValues.exists(value => value.entityIdent.equals("void vrefmain.main(java.lang.String[]) Anzahl der Load Field Referenzen: ") && value.metricValue == 0.0))
  assert(metricResult7.metricValues.exists(value => value.entityIdent.equals("void testnothis.bild(int,java.lang.String) Anzahl der Load Referenzen: ") && value.metricValue == 2.0))
  assert(metricResult7.metricValues.exists(value => value.entityIdent.equals("int testclass.rechner(int,int) Anzahl der Store Referenzen: ") && value.metricValue == 5.0))
  assert(metricResult7.metricValues.exists(value => value.entityIdent.equals("int testclass.rechner(int,int) Anzahl der Load Field Referenzen: ") && value.metricValue == 1.0))


  //PDFBOX
  private val fileToTestBox = new File(getClass.getResource("/group5/pdfbox-2.0.24.jar").getPath)
  private val resultBox = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTestBox, appConfig, Map.empty[Symbol, Any])
  private val metricResultBox = resultBox.head
  assert(metricResultBox.success)
  assert(metricResultBox.metricValues.nonEmpty)
  assert(metricResultBox.metricValues.exists(value => value.entityIdent.equals("org/apache/pdfbox/util/Version  Anzahl aller Referenzen der Variablen:") && value.metricValue == 16.0))
  assert(metricResultBox.metricValues.exists(value => value.entityIdent.equals("void org.apache.pdfbox.util.Vector.<init>(float,float) Anzahl der gesamten Variablen Referenzen : ") && value.metricValue == 2.0))
  assert(metricResultBox.metricValues.exists(value => value.entityIdent.equals("void org.apache.pdfbox.util.Vector.<init>(float,float) Anzahl der Field Referenzen: ") && value.metricValue == 2.0))
  assert(metricResultBox.metricValues.exists(value => value.entityIdent.equals("int org.apache.pdfbox.util.NumberFormatUtil.formatFloatFast(float,int,byte[]) Anzahl der gesamten Variablen Referenzen : ") && value.metricValue == 39.0))
  assert(metricResultBox.metricValues.exists(value => value.entityIdent.equals("org/apache/pdfbox/util/NumberFormatUtil  Anzahl aller Referenzen der Variablen:") && value.metricValue == 85.0))
  assert(metricResultBox.metricValues.exists(value => value.entityIdent.equals("org.apache.pdfbox.util.Matrix org.apache.pdfbox.util.Matrix.extractTranslating() Anzahl der this Referenzen: ") && value.metricValue == 2.0))
  assert(metricResultBox.metricValues.exists(value => value.entityIdent.equals("java.lang.String org.apache.pdfbox.util.DateConverter.toString(java.util.Calendar) Anzahl der Null Referenzen: ") && value.metricValue == 1.0))

  private val resultBoxInfo = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTestBox, appConfig, Map(Symbol("infozuvariablen") -> true))
  private val metricResultBoxInfo = resultBoxInfo.head
  assert(metricResultBoxInfo.success)
  assert(metricResultBoxInfo.metricValues.nonEmpty)
  assert(metricResultBoxInfo.metricValues.exists(value => value.entityIdent.equals("void org.apache.pdfbox.pdmodel.interactive.action.PDActionResetForm.setFlags(int)Die Anzahl der load Referenzen der Field in der Methode action :") && value.metricValue == 1.0))
  assert(metricResultBoxInfo.metricValues.exists(value => value.entityIdent.equals("org.apache.pdfbox.cos.COSArray org.apache.pdfbox.pdmodel.interactive.action.PDActionResetForm.getFields()Die Anzahl der load Referenzen der Variable retval :") && value.metricValue == 2.0))

  private val resultBoxNoThis = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTestBox, appConfig, Map(Symbol("no-this") -> true))
  private val metricResultBoxNoThis = resultBoxNoThis.head
  assert(metricResultBoxNoThis.success)
  assert(metricResultBoxNoThis.metricValues.nonEmpty)
  assert(!metricResultBoxNoThis.metricValues.exists(value => value.entityIdent.equals("org.apache.pdfbox.util.Matrix org.apache.pdfbox.util.Matrix.extractTranslating() Anzahl der this Referenzen: ") && value.metricValue == 2.0))




  // H2 Datenbank
  private val fileToTestH2 = new File(getClass.getResource("/group5/h2-2.0.202.jar").getPath)
  private val resultH2 = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTestH2, appConfig, Map.empty[Symbol, Any])
  private val metricResultH2 = resultH2.head
  assert(metricResultH2.success)
  assert(metricResultH2.metricValues.nonEmpty)
  assert(metricResultH2.metricValues.exists(value => value.entityIdent.equals("org/h2/result/FetchedResult  Anzahl aller Referenzen der Variablen:") && value.metricValue == 14.0))
  assert(metricResultH2.metricValues.exists(value => value.entityIdent.equals("boolean org.h2.result.FetchedResult.next() Anzahl der Null Referenzen: ") && value.metricValue == 2.0))
  assert(metricResultH2.metricValues.exists(value => value.entityIdent.equals("org/h2/result/Sparse  Anzahl aller Referenzen der Variablen:") && value.metricValue == 38.0))
  //this Referenzen == 0, da kein local variables table vorhanden
  assert(metricResultH2.metricValues.exists(value => value.entityIdent.equals("void org.h2.result.Sparse.<init>(int,int,int[]) Anzahl der this Referenzen: ") && value.metricValue == 0.0))
  assert(metricResultH2.metricValues.exists(value => value.entityIdent.equals("void org.h2.result.Sparse.copyFrom(org.h2.result.SearchRow) Anzahl der gesamten Variablen Referenzen : ") && value.metricValue == 13.0))
  assert(metricResultH2.metricValues.exists(value => value.entityIdent.equals("void org.h2.result.Sparse.copyFrom(org.h2.result.SearchRow) Anzahl der Field Referenzen: ") && value.metricValue == 2.0))







//GSON
  private val fileToTestGSON = new File(getClass.getResource("/group5/gson-2.8.9.jar").getPath)
  private val resultGson = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTestGSON, appConfig, Map.empty[Symbol, Any])
  private val metricResultGson = resultGson.head
  assert(metricResultGson.success)
  assert(metricResultGson.metricValues.nonEmpty)
  assert(metricResultGson.metricValues.exists(value => value.entityIdent.equals("com.google.gson.stream.JsonReader com.google.gson.Gson.newJsonReader(java.io.Reader) Anzahl der this Referenzen: ") && value.metricValue == 1.0))
  assert(metricResultGson.metricValues.exists(value => value.entityIdent.equals("java.lang.String com.google.gson.Gson.toJson(java.lang.Object,java.lang.reflect.Type) Anzahl der gesamten Variablen Referenzen : ") && value.metricValue == 5.0))
  assert(metricResultGson.metricValues.exists(value => value.entityIdent.equals("com.google.gson.TypeAdapter com.google.gson.Gson.getDelegateAdapter(com.google.gson.TypeAdapterFactory,com.google.gson.reflect.TypeToken) Anzahl der Field Referenzen: ") && value.metricValue == 3.0))
  assert(metricResultGson.metricValues.exists(value => value.entityIdent.equals("com/google/gson/JsonStreamParser  Anzahl aller Referenzen der Variablen:") && value.metricValue == 18.0))


}
