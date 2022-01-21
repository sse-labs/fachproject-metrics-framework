package impl.group1

import java.io.File

import org.scalatest.{FlatSpec, Matchers}
import org.tud.sse.metrics.ApplicationConfiguration
import org.tud.sse.metrics.analysis.MetricsResult
import org.tud.sse.metrics.testutils.AnalysisTestUtils


class EvolutionAnalysisOriginalTest extends FlatSpec with Matchers{

  val dirWithTestFiles = new File(getClass.getResource("/group1/commons-collections").getPath)
  val evolutionAnalysisToTest = new EvolutionAnalysis(dirWithTestFiles)

  val appConfig = new ApplicationConfiguration(inputFilePath = "", treatFilesAsLibrary = true, outFileOption = None,
    opalLoggingEnabled = false, batchModeEnabled = Some(false), excludedAnalysesNames = List(), includedAnalysesNames = List(),
    excludeJreClasses = true, additionalClassesDir = None, loadAdditionalClassesAsInterface = false)

  val optionsMapWithOriginal : Map[Symbol,Any]  = Map(Symbol("ext_evo") -> "ext_evo", Symbol("int_evo")-> "int_evo",Symbol("original")-> "original")


  val resultsOriginal:List[MetricsResult]  = AnalysisTestUtils.runMultiFileAnalysis(_ =>
    evolutionAnalysisToTest,dirWithTestFiles,appConfig,optionsMapWithOriginal)


  assert(resultsOriginal.size == 1)

  val evolutionResult:MetricsResult = resultsOriginal.head
  assert(evolutionResult.success)
  // all results for 12 jars, 3 results per run.
  assert(evolutionResult.metricValues.size == 36)



  // Targeted Testing for single Values of evolution, internal evolution or external evolution.
  // Tests with no new Packages and all values zero are the following (00 to 01, 03 to 04, 04 to 05, 05 to 06, 07 to 08, 08 to 09, 09 to 10, 10 to 11)
  // only from 00 to 01 added to show 0 results.
  // interesting Test cases (01 to 02, 02 to 03, 06 to 07)
  for(metricValueToTest <- resultsOriginal.head.metricValues){
    // Tests for Evolution value Evolution = (internal evolution + external evolution)/2
    if(metricValueToTest.metricName == "Evolution"){
      // Change from 00 to 01 is minimal and contains no new Packages
      if(metricValueToTest.entityIdent.contains("00-commons-collections-1.0.jar") &&
        metricValueToTest.entityIdent.contains("01-commons-collections-2.0.jar")){
        // no new Packages all evolution Values will be 0
        assert(metricValueToTest.metricValue == 0.0)
      } else if(metricValueToTest.entityIdent.contains("01-commons-collections-2.0.jar") &&
        metricValueToTest.entityIdent.contains("02-commons-collections-2.0.20020914.015953.jar")){
        // Internal Evolution = 0.0, external evolution = 0.06756756756756757
        assert(metricValueToTest.metricValue == 0.033783783783783786)
      } else if(metricValueToTest.entityIdent.contains("02-commons-collections-2.0.20020914.015953.jar") &&
        metricValueToTest.entityIdent.contains("03-commons-collections-2.0.20020914.020746")){
        // Internal Evolution = 0.3333333333333333, external evolution = 0.06808510638297872
        assert(metricValueToTest.metricValue == 0.200709219858156)
      } else if(metricValueToTest.entityIdent.contains("06-commons-collections-2.1.1.jar") &&
        metricValueToTest.entityIdent.contains("07-commons-collections-3.0.jar")){
        // internal Evolution: 0.6666666666666666, external Evolution: 0.5874125874125874
        assert(metricValueToTest.metricValue == 0.627039627039627)
      }
    }
    // Tests for Internal Evolution value
    // internal Evolution counts if a maintainedPackage interacts with newly added Packages (counts only once)
    // divided by the count of maintained Packages
    else if(metricValueToTest.metricName == "Internal Evolution"){
      if(metricValueToTest.entityIdent.contains("02-commons-collections-2.0.20020914.015953.jar") &&
        metricValueToTest.entityIdent.contains("03-commons-collections-2.0.20020914.020746")){
        // interactionsCounted: 1, maintainedPackages: 3 (2/3)
        assert(metricValueToTest.metricValue == 0.3333333333333333)
      } else if(metricValueToTest.entityIdent.contains("01-commons-collections-2.0.jar") &&
        metricValueToTest.entityIdent.contains("02-commons-collections-2.0.20020914.015953.jar")){
        // all Packages 3,newPackages 1 interactions with new Packages: 0 (0/4)
        assert(metricValueToTest.metricValue == 0.0)
      }
      // Change from 00 to 01 is minimal and contains no new Packages
      else if(metricValueToTest.entityIdent.contains("00-commons-collections-1.0.jar") &&
        metricValueToTest.entityIdent.contains("01-commons-collections-2.0.jar")){
        // no new Packages all evolution Values will be 0
        assert(metricValueToTest.metricValue == 0.0)
      } else if(metricValueToTest.entityIdent.contains("06-commons-collections-2.1.1.jar") &&
        metricValueToTest.entityIdent.contains("07-commons-collections-3.0.jar")){
        // interactionsCounted: 2, maintainedPackages: 3 (2/3)
        assert(metricValueToTest.metricValue == 0.6666666666666666)
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
      } else if(metricValueToTest.entityIdent.contains("01-commons-collections-2.0.jar") &&
        metricValueToTest.entityIdent.contains("02-commons-collections-2.0.20020914.015953.jar")){
        // Classes in new Packages 10, Number of Classes 148 (10/148)
        assert(metricValueToTest.metricValue == 0.06756756756756757)
      } else if(metricValueToTest.entityIdent.contains("06-commons-collections-2.1.1.jar") &&
        metricValueToTest.entityIdent.contains("07-commons-collections-3.0.jar")){
        // Classes in new Packages 252, Number of Classes 429 (252/429)
        assert(metricValueToTest.metricValue == 0.5874125874125874)
      }
      // Change from 00 to 01 is minimal and contains no new Packages
      else if(metricValueToTest.entityIdent.contains("00-commons-collections-1.0.jar") &&
        metricValueToTest.entityIdent.contains("01-commons-collections-2.0.jar")){
        // no new Packages all evolution Values will be 0
        assert(metricValueToTest.metricValue == 0.0)
      }
    }
  }

  resultsOriginal.foreach(item => println(item.metricValues))

}
