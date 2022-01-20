package impl.group1

import java.io.File

import org.scalatest.{FlatSpec, Matchers}
import org.tud.sse.metrics.ApplicationConfiguration
import org.tud.sse.metrics.analysis.MetricsResult
import org.tud.sse.metrics.testutils.AnalysisTestUtils


class EvolutionAnalysisTest extends FlatSpec with Matchers{

  val dirWithTestFiles = new File(getClass.getResource("/group1/commons-collections").getPath)
  val evolutionAnalysisToTest = new EvolutionAnalysis(dirWithTestFiles)

  val appConfig = new ApplicationConfiguration(inputFilePath = "", treatFilesAsLibrary = true, outFileOption = None,
    opalLoggingEnabled = false, batchModeEnabled = Some(false), excludedAnalysesNames = List(), includedAnalysesNames = List(),
    excludeJreClasses = true, additionalClassesDir = None, loadAdditionalClassesAsInterface = false)

  val optionsMap : Map[Symbol,Any]  = Map(Symbol("ext_evo") -> "ext_evo", Symbol("int_evo")-> "int_evo")

  val results:List[MetricsResult]  = AnalysisTestUtils.runMultiFileAnalysis(_ =>
    evolutionAnalysisToTest,dirWithTestFiles,appConfig,optionsMap)


  assert(results.size == 1)

  val evolutionResult:MetricsResult = results.head
  assert(evolutionResult.success)
  assert(evolutionResult.metricValues.size == 36)



  // Targeted Testing for single Values of evolution, internal evolution or external evolution.
  for(metricValueToTest <- results.head.metricValues){
    // Tests for Evolution value Evolution = (internal evolution + external evolution)/2
    if(metricValueToTest.metricName == "Evolution"){
      // Change from 00 to 01 is minimal and contains no new Packages
      if(metricValueToTest.entityIdent.contains("00-commons-collections-1.0.jar") &&
        metricValueToTest.entityIdent.contains("01-commons-collections-2.0.jar")){
        // no new Packages all evolution Values will be 0
        assert(metricValueToTest.metricValue == 0.0)
      } else if(metricValueToTest.entityIdent.contains("02-commons-collections-2.0.20020914.015953.jar") &&
        metricValueToTest.entityIdent.contains("03-commons-collections-2.0.20020914.020746")){
        // Internal Evolution = 0.3333333333333333, external evolution = 0.06808510638297872
        assert(metricValueToTest.metricValue == 0.200709219858156)
      }
    }
    // Tests for Internal Evolution value
    // internal Evolution is the number of Packages that exist in both versions and interact with newly added Packages
    // divided by the Number of Packages that exist in both versions.
    else if(metricValueToTest.metricName == "Internal Evolution"){
      if(metricValueToTest.entityIdent.contains("02-commons-collections-2.0.20020914.015953.jar") &&
        metricValueToTest.entityIdent.contains("03-commons-collections-2.0.20020914.020746")){
        // maintained Packages: 3, new Packages 1 with 1 interaction from maintained Packages (1/3)
        assert(metricValueToTest.metricValue == 0.3333333333333333)
      }
      // Change from 00 to 01 is minimal and contains no new Packages
      else if(metricValueToTest.entityIdent.contains("00-commons-collections-1.0.jar") &&
        metricValueToTest.entityIdent.contains("01-commons-collections-2.0.jar")){
        // no new Packages all evolution Values will be 0
        assert(metricValueToTest.metricValue == 0.0)
      }
    }
    // Tests for external Evolution
    // external evolution is the number of classes in newly introduced packages
    // divided by the total amount of classes in the project.
    else{
      if(metricValueToTest.entityIdent.contains("02-commons-collections-2.0.20020914.015953.jar") &&
        metricValueToTest.entityIdent.contains("03-commons-collections-2.0.20020914.020746")){
        // Classes in new Packages 16, Number of Classes 235 (16/235)
        assert(metricValueToTest.metricValue ==  0.06808510638297872)
      }
      // Change from 00 to 01 is minimal and contains no new Packages
      else if(metricValueToTest.entityIdent.contains("00-commons-collections-1.0.jar") &&
        metricValueToTest.entityIdent.contains("01-commons-collections-2.0.jar")){
        // no new Packages all evolution Values will be 0
        assert(metricValueToTest.metricValue == 0.0)
      }
    }
  }

  results.foreach(item => println(item.metricValues))

}
