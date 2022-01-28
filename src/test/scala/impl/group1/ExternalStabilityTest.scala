package org.tud.sse.metrics
package impl.group1

import testutils.AnalysisTestUtils

import org.scalatest.{FlatSpec, Matchers}
import analysis.{MetricValue, MetricsResult}

import java.io.File

class ExternalStabilityTest extends FlatSpec with Matchers {

  "The ExternalStabilityAnalysis" must "calculate valid results for multiple JARs" in {

    val filesToTest = new File(getClass.getResource("/group1/commons-collections").getPath)
    val analysisToTest = new ExternalStabilityAnalysis(filesToTest)

    val appConfig = new ApplicationConfiguration(inputFilePath = "", treatFilesAsLibrary = true, outFileOption = None,
      opalLoggingEnabled = false, batchModeEnabled = Some(false), excludedAnalysesNames = List(), includedAnalysesNames = List(),
      excludeJreClasses = true, additionalClassesDir = None, loadAdditionalClassesAsInterface = false)

    val optionsMap: Map[Symbol, Any] = Map(Symbol("es_red") -> "es_red", Symbol("es_rem") -> "es_rem")
    val result = AnalysisTestUtils.runMultiFileAnalysis(_ => analysisToTest, filesToTest, appConfig, optionsMap)

    assert(result.size == 1)

    val metricResult = result.head
    assert(metricResult.success)
    assert(metricResult.metricValues.nonEmpty)


    val values = result.head.metricValues.toList

    assert(values.find(value => value.metricName.contains("ES_stability") &&
      value.entityIdent.contains("00-commons-collections-1.0.jar") &&
      value.entityIdent.contains("01-commons-collections-2.0.jar")).get.metricValue == 1.0)

    assert(values.find(value => value.metricName.contains("ES_Removed") &&
      value.entityIdent.contains("00-commons-collections-1.0.jar") &&
      value.entityIdent.contains("01-commons-collections-2.0.jar")).get.metricValue == 1.0)

    assert(values.find(value => value.metricName.contains("ES_Remained") &&
      value.entityIdent.contains("00-commons-collections-1.0.jar") &&
      value.entityIdent.contains("01-commons-collections-2.0.jar")).get.metricValue == 1.0)


    assert(values.find(value => value.metricName.contains("ES_stability") &&
      value.entityIdent.contains("01-commons-collections-2.0.jar") &&
      value.entityIdent.contains("02-commons-collections-2.0.20020914.015953.jar")).get.metricValue == 0.9770114942528736)

    assert(values.find(value => value.metricName.contains("ES_Removed") &&
      value.entityIdent.contains("01-commons-collections-2.0.jar") &&
      value.entityIdent.contains("02-commons-collections-2.0.20020914.015953.jar")).get.metricValue == 1.0)

    assert(values.find(value => value.metricName.contains("ES_Remained") &&
      value.entityIdent.contains("01-commons-collections-2.0.jar") &&
      value.entityIdent.contains("02-commons-collections-2.0.20020914.015953.jar")).get.metricValue == 0.9770114942528736)


    assert(values.find(value => value.metricName.contains("ES_stability") &&
      value.entityIdent.contains("02-commons-collections-2.0.20020914.015953.jar") &&
      value.entityIdent.contains("03-commons-collections-2.0.20020914.020746")).get.metricValue == 1.0)

    assert(values.find(value => value.metricName.contains("ES_Removed") &&
      value.entityIdent.contains("02-commons-collections-2.0.20020914.015953.jar") &&
      value.entityIdent.contains("03-commons-collections-2.0.20020914.020746")).get.metricValue == 1.0)

    assert(values.find(value => value.metricName.contains("ES_Remained") &&
      value.entityIdent.contains("02-commons-collections-2.0.20020914.015953.jar") &&
      value.entityIdent.contains("03-commons-collections-2.0.20020914.020746")).get.metricValue == 1.0)


    assert(values.find(value =>  value.metricName.contains("ES_stability") &&
      value.entityIdent.contains("03-commons-collections-2.0.20020914.020746.jar") &&
      value.entityIdent.contains("04-commons-collections-2.0.20020914.020858.jar")).get.metricValue == 1.0)

    assert(values.find(value => value.metricName.contains("ES_Removed") &&
      value.entityIdent.contains("03-commons-collections-2.0.20020914.020746.jar") &&
      value.entityIdent.contains("04-commons-collections-2.0.20020914.020858.jar")).get.metricValue == 1.0)

    assert(values.find(value => value.metricName.contains("ES_Remained") &&
      value.entityIdent.contains("03-commons-collections-2.0.20020914.020746.jar") &&
      value.entityIdent.contains("04-commons-collections-2.0.20020914.020858.jar")).get.metricValue == 1.0)


    assert(values.find(value => value.metricName.contains("ES_stability") &&
      value.entityIdent.contains("04-commons-collections-2.0.20020914.020858.jar") &&
      value.entityIdent.contains("05-commons-collections-2.1.jar")).get.metricValue == 0.77584427342689)

    assert(values.find(value => value.metricName.contains("ES_Removed") &&
      value.entityIdent.contains("04-commons-collections-2.0.20020914.020858.jar") &&
      value.entityIdent.contains("05-commons-collections-2.1.jar")).get.metricValue == 0.9446808510638298)

    assert(values.find(value => value.metricName.contains("ES_Remained") &&
      value.entityIdent.contains("04-commons-collections-2.0.20020914.020858.jar") &&
      value.entityIdent.contains("05-commons-collections-2.1.jar")).get.metricValue == 0.8212765957446808)


    assert(values.find(value => value.metricName.contains("ES_stability") &&
      value.entityIdent.contains("05-commons-collections-2.1.jar") &&
      value.entityIdent.contains("06-commons-collections-2.1.1.jar")).get.metricValue == 1.0)

    assert(values.find(value => value.metricName.contains("ES_Removed") &&
      value.entityIdent.contains("05-commons-collections-2.1.jar") &&
      value.entityIdent.contains("06-commons-collections-2.1.1.jar")).get.metricValue == 1.0)

    assert(values.find(value => value.metricName.contains("ES_Remained") &&
      value.entityIdent.contains("05-commons-collections-2.1.jar") &&
      value.entityIdent.contains("06-commons-collections-2.1.1.jar")).get.metricValue == 1.0)


    assert(values.find(value => value.metricName.contains("ES_stability") &&
      value.entityIdent.contains("06-commons-collections-2.1.1.jar") &&
      value.entityIdent.contains("07-commons-collections-3.0.jar")).get.metricValue == 0.7272727272727273)

    assert(values.find(value => value.metricName.contains("ES_Removed") &&
      value.entityIdent.contains("06-commons-collections-2.1.1.jar") &&
      value.entityIdent.contains("07-commons-collections-3.0.jar")).get.metricValue == 1.0)

    assert(values.find(value => value.metricName.contains("ES_Remained") &&
      value.entityIdent.contains("06-commons-collections-2.1.1.jar") &&
      value.entityIdent.contains("07-commons-collections-3.0.jar")).get.metricValue == 0.7272727272727273)


    assert(values.find(value => value.metricName.contains("ES_stability") &&
      value.entityIdent.contains("07-commons-collections-3.0.jar") &&
      value.entityIdent.contains("08-commons-collections-3.1.jar")).get.metricValue == 0.9533799533799534)

    assert(values.find(value => value.metricName.contains("ES_Removed") &&
      value.entityIdent.contains("07-commons-collections-3.0.jar") &&
      value.entityIdent.contains("08-commons-collections-3.1.jar")).get.metricValue == 1.0)

    assert(values.find(value => value.metricName.contains("ES_Remained") &&
      value.entityIdent.contains("07-commons-collections-3.0.jar") &&
      value.entityIdent.contains("08-commons-collections-3.1.jar")).get.metricValue == 0.9533799533799534)


    assert(values.find(value => value.metricName.contains("ES_stability") &&
      value.entityIdent.contains("08-commons-collections-3.1.jar") &&
      value.entityIdent.contains("09-commons-collections-3.2.jar")).get.metricValue == 1.0)

    assert(values.find(value => value.metricName.contains("ES_Removed") &&
      value.entityIdent.contains("08-commons-collections-3.1.jar") &&
      value.entityIdent.contains("09-commons-collections-3.2.jar")).get.metricValue == 1.0)

    assert(values.find(value => value.metricName.contains("ES_Remained") &&
      value.entityIdent.contains("08-commons-collections-3.1.jar") &&
      value.entityIdent.contains("09-commons-collections-3.2.jar")).get.metricValue == 1.0)


    assert(values.find(value => value.metricName.contains("ES_stability") &&
      value.entityIdent.contains("09-commons-collections-3.2.jar") &&
      value.entityIdent.contains("10-commons-collections-3.2.1.jar")).get.metricValue == 0.982532751091703)

    assert(values.find(value => value.metricName.contains("ES_Removed") &&
      value.entityIdent.contains("09-commons-collections-3.2.jar") &&
      value.entityIdent.contains("10-commons-collections-3.2.1.jar")).get.metricValue == 1.0)

    assert(values.find(value => value.metricName.contains("ES_Remained") &&
      value.entityIdent.contains("09-commons-collections-3.2.jar") &&
      value.entityIdent.contains("10-commons-collections-3.2.1.jar")).get.metricValue == 0.982532751091703)


    assert(values.find(value => value.metricName.contains("ES_stability") &&
      value.entityIdent.contains("10-commons-collections-3.2.1.jar") &&
      value.entityIdent.contains("11-commons-collections-3.2.2.jar")).get.metricValue == 1.0)

    assert(values.find(value => value.metricName.contains("ES_Removed") &&
      value.entityIdent.contains("10-commons-collections-3.2.1.jar") &&
      value.entityIdent.contains("11-commons-collections-3.2.2.jar")).get.metricValue == 1.0)

    assert(values.find(value => value.metricName.contains("ES_Remained") &&
      value.entityIdent.contains("10-commons-collections-3.2.1.jar") &&
      value.entityIdent.contains("11-commons-collections-3.2.2.jar")).get.metricValue == 1.0)
  }

}
