package org.tud.sse.metrics
package impl.group1

import testutils.AnalysisTestUtils

import org.scalatest.{FlatSpec, Matchers}
import analysis.{MetricValue, MetricsResult}

import java.io.File

class ExternalStabilityTest extends FlatSpec with Matchers{

  "The ExternalStabilityAnalysis" must "calculate valid results for multiple JARs" in {

    val filesToTest = new File(getClass.getResource("/group1/commons-collections").getPath)
    val analysisToTest = new ExternalStabilityAnalysis(filesToTest)

    val appConfig = new ApplicationConfiguration(inputFilePath = "", treatFilesAsLibrary = true, outFileOption = None,
      opalLoggingEnabled = false, batchModeEnabled = Some(false), excludedAnalysesNames = List(), includedAnalysesNames = List(),
      excludeJreClasses = true, additionalClassesDir = None, loadAdditionalClassesAsInterface = false)

    val optionsMap : Map[Symbol,Any]  = Map(Symbol("es_red") -> "es_red", Symbol("es_rem")-> "es_rem")
    val result  = AnalysisTestUtils.runMultiFileAnalysis(_ => analysisToTest,filesToTest,appConfig,optionsMap)

    assert(result.size == 1)

    val metricResult = result.head
    assert(metricResult.success)
    assert(metricResult.metricValues.nonEmpty)


    val values = result.head.metricValues.toList

    assert(diffOf0001(values(8)))
    assert(values(8).metricValue == 1.0)
    assert(diffOf0001(values(20)))
    assert(values(20).metricValue == 1.0)
    assert(diffOf0001(values(32)))
    assert(values(32).metricValue == 1.0)


    assert(diffOf0102(values(1)))
    assert(values(1).metricValue ==  0.9770114942528736)
    assert(diffOf0102(values(13)))
    assert(values(13).metricValue == 1.0)
    assert(diffOf0102(values(25)))
    assert(values(25).metricValue ==  0.9770114942528736)

    assert(diffOf0203(values(6)))
    assert(values(6).metricValue == 1.0)
    assert(diffOf0203(values(18)))
    assert(values(18).metricValue == 1.0)
    assert(diffOf0203(values(30)))
    assert(values(30).metricValue == 1.0)

    assert(diffOf0304(values(4)))
    assert(values(4).metricValue == 1.0)
    assert(diffOf0304(values(16)))
    assert(values(16).metricValue == 1.0)
    assert(diffOf0304(values(28)))
    assert(values(28).metricValue == 1.0)

    assert(diffOf0405(values(7)))
    assert(values(7).metricValue == 0.77584427342689)
    assert(diffOf0405(values(19)))
    assert(values(19).metricValue ==  0.9446808510638298)
    assert(diffOf0405(values(31)))
    assert(values(31).metricValue == 0.8212765957446808)

    assert(diffOf0506(values(2)))
    assert(values(2).metricValue == 1.0)
    assert(diffOf0506(values(14)))
    assert(values(14).metricValue == 1.0)
    assert(diffOf0506(values(26)))
    assert(values(26).metricValue == 1.0)

    assert(diffOf0607(values.head))
    assert(values.head.metricValue ==  0.7272727272727273)
    assert(diffOf0607(values(12)))
    assert(values(12).metricValue == 1.0)
    assert(diffOf0607(values(24)))
    assert(values(24).metricValue ==  0.7272727272727273)

    assert(diffOf0708(values(11)))
    assert(values(11).metricValue == 0.9533799533799534)
    assert(diffOf0708(values(23)))
    assert(values(23).metricValue == 1.0)
    assert(diffOf0708(values(35)))
    assert(values(35).metricValue == 0.9533799533799534)

    assert(diffOf0809(values(5)))
    assert(values(5).metricValue == 1.0)
    assert(diffOf0809(values(17)))
    assert(values(17).metricValue == 1.0)
    assert(diffOf0809(values(29)))
    assert(values(29).metricValue == 1.0)

    assert(diffOf0910(values(3)))
    assert(values(3).metricValue == 0.982532751091703)
    assert(diffOf0910(values(15)))
    assert(values(15).metricValue == 1.0)
    assert(diffOf0910(values(27)))
    assert(values(27).metricValue == 0.982532751091703)

    assert(diffOf1011(values(10)))
    assert(values(10).metricValue == 1.0)
    assert(diffOf1011(values(22)))
    assert(values(22).metricValue == 1.0)
    assert(diffOf1011(values(34)))
    assert(values(34).metricValue == 1.0)
  }


  def diffOf0001(value: MetricValue): Boolean = {
    value.entityIdent.contains("00-commons-collections-1.0.jar") &&
      value.entityIdent.contains("01-commons-collections-2.0.jar")
  }

  def diffOf0102(value: MetricValue): Boolean = {
    value.entityIdent.contains("01-commons-collections-2.0.jar") &&
     value.entityIdent.contains("02-commons-collections-2.0.20020914.015953.jar")
  }

  def diffOf0203(value: MetricValue): Boolean = {
    value.entityIdent.contains("02-commons-collections-2.0.20020914.015953.jar") &&
      value.entityIdent.contains("03-commons-collections-2.0.20020914.020746")
  }

  def diffOf0304(value: MetricValue): Boolean = {
    value.entityIdent.contains("03-commons-collections-2.0.20020914.020746.jar") &&
      value.entityIdent.contains("04-commons-collections-2.0.20020914.020858.jar")
  }

  def diffOf0405(value: MetricValue): Boolean = {
    value.entityIdent.contains("04-commons-collections-2.0.20020914.020858.jar") &&
      value.entityIdent.contains("05-commons-collections-2.1.jar")
  }

  def diffOf0506(value: MetricValue): Boolean = {
    value.entityIdent.contains("05-commons-collections-2.1.jar") &&
      value.entityIdent.contains("06-commons-collections-2.1.1.jar")
  }

  def diffOf0607(value: MetricValue): Boolean = {
    value.entityIdent.contains("06-commons-collections-2.1.1.jar") &&
      value.entityIdent.contains("07-commons-collections-3.0.jar")
  }

  def diffOf0708(value: MetricValue): Boolean = {
    value.entityIdent.contains("07-commons-collections-3.0.jar") &&
      value.entityIdent.contains("08-commons-collections-3.1.jar")
  }

  def diffOf0809(value: MetricValue): Boolean = {
    value.entityIdent.contains("08-commons-collections-3.1.jar") &&
      value.entityIdent.contains("09-commons-collections-3.2.jar")
  }

  def diffOf0910(value: MetricValue): Boolean = {
    value.entityIdent.contains("09-commons-collections-3.2.jar") &&
      value.entityIdent.contains("10-commons-collections-3.2.1.jar")
  }

  def diffOf1011(value: MetricValue): Boolean = {
    value.entityIdent.contains("10-commons-collections-3.2.1.jar") &&
      value.entityIdent.contains("11-commons-collections-3.2.2.jar")
  }

}
