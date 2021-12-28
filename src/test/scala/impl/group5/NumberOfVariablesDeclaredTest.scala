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
    //Konstruktor variablen werden hier nicht gezählt, zählt Konstruktor als methode?
    assert(metricsResultDemoOnlyMethods.metricValues.exists(value => value.entityIdent.equals("Anzahl von lokalen Variablen in allen Methoden von testclass") && value.metricValue == 10.0))
    assert(metricsResultDemoOnlyMethods.metricValues.exists(value => value.entityIdent.equals("Anzahl lokaler Variablen in java.lang.String testclass.welcome(java.lang.String)") && value.metricValue == 2.0))



    val resultDemoNoUnused = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTestDemo, appConfig, Map(Symbol("no-unusedfield") -> true))
    val metricsResultDemoNoUnused = resultDemoNoUnused.head
    assert(!metricsResultDemoNoUnused.metricValues.exists(value => value.entityIdent.equals("Ungenutzte Field in der Klasse testclassUndev") && value.metricValue == 1.0))
    assert(metricsResultDemoNoUnused.metricValues.exists(value => value.entityIdent.equals("Anzahl deklarierter Variablen in allen Klassen: ") && value.metricValue == 8.0))







    val resultDemoWrongOptions = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTestDemo, appConfig, Map(Symbol("no-class") -> true, Symbol("no-method") -> true))
    val metricsResultDemoWrongOptions = resultDemoWrongOptions.head
    assert(!metricsResultDemoWrongOptions.metricValues.exists(value => value.entityIdent.equals("Anzahl von Klassenvariablen in testclass") && value.metricValue == 17.0))

    //Undefined
    val resultDemoUndefinedVariables = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTestDemo, appConfig, Map(Symbol("undefined-variables") -> true))
    val metricsResultDemoUndefinedVariables = resultDemoUndefinedVariables.head
    assert(metricsResultDemoUndefinedVariables.metricValues.exists(value => value.entityIdent.equals("Anzahl von Klassenvariablen in testclassUndev") && value.metricValue == 5.0))

    val resultDemoUndefinedVariablesOnlyMethod = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTestDemo, appConfig, Map(Symbol("undefined-variables") -> true,Symbol("no-class") -> true))
    val metricsResultDemoUndefinedVariablesOnlyMethod = resultDemoUndefinedVariablesOnlyMethod.head
    assert(!metricsResultDemoUndefinedVariablesOnlyMethod.metricValues.exists(value => value.entityIdent.equals("Anzahl von Klassenvariablen in testclassUndev") && value.metricValue == 1.0))

    val resultDemoUndefinedVariablesOnlyClass = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTestDemo, appConfig, Map(Symbol("undefined-variables") -> true,Symbol("no-method") -> true))
    val metricsResultDemoUndefinedVariablesOnlyClass = resultDemoUndefinedVariablesOnlyClass.head
    assert(!metricsResultDemoUndefinedVariablesOnlyClass.metricValues.exists(value => value.entityIdent.equals("Anzahl von Klassenvariablen in testclassUndev") && value.metricValue == 1.0))












    val fileToTest2 = new File(getClass.getResource("/group5/h2-2.0.202.jar").getPath)

    val resultH2 = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest2, appConfig, Map.empty[Symbol, Any])
    val metricResultH2 = resultH2.head
    assert(!metricResultH2.metricValues.exists(value => value.entityIdent.equals("Anzahl lokaler Variablen in boolean org.h2.index.Index.mayHaveNullDuplicates(org.h2.result.SearchRow)") && value.metricValue == 5.0))
    assert(!metricResultH2.metricValues.exists(value => value.entityIdent.equals("Anzahl lokaler Variablen in java.lang.String org.h2.jdbc.JdbcConnection.translateSQLImpl(java.lang.String)") && value.metricValue == 6.0))
    assert(!metricResultH2.metricValues.exists(value => value.entityIdent.equals("Anzahl lokaler Variablen in void org.h2.server.TcpServer.shutdown(java.lang.String,java.lang.String,boolean,boolean)") && value.metricValue == 7.0))
    assert(!metricResultH2.metricValues.exists(value => value.entityIdent.equals("Anzahl von Klassenvariablen in org/h2/engine/Setting") && value.metricValue == 14.0))

    val resultH2ClassOnly = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest2, appConfig, Map.empty[Symbol, Any])











  }

}
