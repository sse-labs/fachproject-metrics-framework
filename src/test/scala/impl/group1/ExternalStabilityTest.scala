package org.tud.sse.metrics
package impl.group1

import analysis.MetricsResult
import testutils.AnalysisTestUtils

import org.scalatest.{FlatSpec, Matchers}

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

    assert(result.size > 1)

    val metricResult = result.head
    assert(metricResult.success)
    assert(metricResult.metricValues.nonEmpty)

    for(item <- result)
    {
      if(diffOf0001(item))
        {
          assert(getES(item) == 1.0)
          assert(getES_REM(item) == 1.0)
          assert(getES_RED(item) == 1.0)
        }
      else if(diffOf0102(item))
        {
          assert(getES(item) == 0.9770114942528736)
          assert(getES_REM(item) == 1.0)
          assert(getES_RED(item) == 0.9770114942528736)
        }
      else if(diffOf0203(item))
      {
        assert(getES(item) == 1.0)
        assert(getES_REM(item) == 1.0)
        assert(getES_RED(item) == 1.0)
      }
      else if(diffOf0304(item))
      {
        assert(getES(item) == 1.0)
        assert(getES_REM(item) == 1.0)
        assert(getES_RED(item) == 1.0)
      }
      else if(diffOf0405(item))
      {
        assert(getES(item) == 0.77584427342689)
        assert(getES_REM(item) == 0.9446808510638298)
        assert(getES_RED(item) == 0.8212765957446808)
      }
      else if(diffOf0506(item))
      {
        assert(getES(item) == 1.0)
        assert(getES_REM(item) == 1.0)
        assert(getES_RED(item) == 1.0)
      }
      else if(diffOf0607(item))
      {
        assert(getES(item) == 0.7272727272727273)
        assert(getES_REM(item) == 1.0)
        assert(getES_RED(item) == 0.7272727272727273)
      }
      else if(diffOf0708(item))
      {
        assert(getES(item) == 0.9533799533799534)
        assert(getES_REM(item) == 1.0)
        assert(getES_RED(item) == 0.9533799533799534)
      }
      else if(diffOf0809(item))
      {
        assert(getES(item) == 1.0)
        assert(getES_REM(item) == 1.0)
        assert(getES_RED(item) == 1.0)
      }
      else if(diffOf0910(item))
      {
        assert(getES(item) == 0.982532751091703)
        assert(getES_REM(item) == 1.0)
        assert(getES_RED(item) == 0.982532751091703)
      }
      else if(diffOf1011(item))
      {
        assert(getES(item) == 1.0)
        assert(getES_REM(item) == 1.0)
        assert(getES_RED(item) == 1.0)
      }
    }
     result.foreach(item => println(item.metricValues))
  }

  def getES(res: MetricsResult): Double = {
    res.metricValues.toSeq.head.metricValue
  }

  def getES_REM(res: MetricsResult): Double = {
    res.metricValues.toSeq(1).metricValue
  }

  def getES_RED(res: MetricsResult): Double = {
    res.metricValues.toSeq(2).metricValue
  }

  def diffOf0001(res: MetricsResult): Boolean = {
    res.metricValues.toSeq.head.entityIdent.contains("00-commons-collections-1.0jar") &&
      res.metricValues.toSeq.head.entityIdent.contains("01-commons-collections-2.0.jar")
  }

  def diffOf0102(res: MetricsResult): Boolean = {
    res.metricValues.toSeq.head.entityIdent.contains("01-commons-collections-2.0.jar") &&
      res.metricValues.toSeq.head.entityIdent.contains("02-commons-collections-2.0.20020914.015953.jar")
  }

  def diffOf0203(res: MetricsResult): Boolean = {
    res.metricValues.toSeq.head.entityIdent.contains("02-commons-collections-2.0.20020914.015953.jar") &&
      res.metricValues.toSeq.head.entityIdent.contains("03-commons-collections-2.0.20020914.020746")
  }

  def diffOf0304(res: MetricsResult): Boolean = {
    res.metricValues.toSeq.head.entityIdent.contains("03-commons-collections-2.0.20020914.020746.jar") &&
      res.metricValues.toSeq.head.entityIdent.contains("04-commons-collections-2.0.20020914.020858.jar")
  }

  def diffOf0405(res: MetricsResult): Boolean = {
    res.metricValues.toSeq.head.entityIdent.contains("04-commons-collections-2.0.20020914.020858.jar") &&
      res.metricValues.toSeq.head.entityIdent.contains("05-commons-collections-2.1.jar")
  }

  def diffOf0506(res: MetricsResult): Boolean = {
    res.metricValues.toSeq.head.entityIdent.contains("05-commons-collections-2.1.jar") &&
      res.metricValues.toSeq.head.entityIdent.contains("06-commons-collections-2.1.1.jar")
  }

  def diffOf0607(res: MetricsResult): Boolean = {
    res.metricValues.toSeq.head.entityIdent.contains("06-commons-collections-2.1.1.jar") &&
      res.metricValues.toSeq.head.entityIdent.contains("07-commons-collections-2.0.jar")
  }

  def diffOf0708(res: MetricsResult): Boolean = {
    res.metricValues.toSeq.head.entityIdent.contains("07-commons-collections-3.0.jar") &&
      res.metricValues.toSeq.head.entityIdent.contains("08-commons-collections-3.1.jar")
  }

  def diffOf0809(res: MetricsResult): Boolean = {
    res.metricValues.toSeq.head.entityIdent.contains("08-commons-collections-3.1.jar") &&
      res.metricValues.toSeq.head.entityIdent.contains("09-commons-collections-3.2.jar")
  }

  def diffOf0910(res: MetricsResult): Boolean = {
    res.metricValues.toSeq.head.entityIdent.contains("09-commons-collections-3.2.jar") &&
      res.metricValues.toSeq.head.entityIdent.contains("10-commons-collections-3.2.1.jar")
  }

  def diffOf1011(res: MetricsResult): Boolean = {
    res.metricValues.toSeq.head.entityIdent.contains("10-commons-collections-3.2.1.jar") &&
      res.metricValues.toSeq.head.entityIdent.contains("11-commons-collections-3.2.2.jar")
  }

}
