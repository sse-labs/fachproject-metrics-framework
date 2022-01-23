package org.tud.sse.metrics
package impl.group5

import org.scalatest.{FlatSpec, Matchers}
import org.tud.sse.metrics.testutils.AnalysisTestUtils

import java.io.File

class InternalStabilityAnalysisTest extends FlatSpec with Matchers{


  val appConfig = new ApplicationConfiguration(inputFilePath = "", treatFilesAsLibrary = true, outFileOption = None,
    opalLoggingEnabled = false, batchModeEnabled = Some(false), excludedAnalysesNames = List(), includedAnalysesNames = List(),
    excludeJreClasses = true, additionalClassesDir = None, loadAdditionalClassesAsInterface = false)
  val filesToTestjars = new File(getClass.getResource("/group5/testJars").getPath)
  val analysisToTestjars = new InternalStabilityAnalysis(filesToTestjars)
  val resultjars  = AnalysisTestUtils.runMultiFileAnalysis(_ => analysisToTestjars,filesToTestjars,appConfig,Map.empty[Symbol, Any])
  val metricResultjars = resultjars.head
  assert(metricResultjars.success)
  assert(metricResultjars.metricValues.exists(value => value.entityIdent.equals("Difference between: testInternalST-2.0.jar and testInternalST-2.1.jar") && value.metricValue == 1.0))
  assert(metricResultjars.metricValues.exists(value => value.entityIdent.equals("Difference between: testInternalST-2.1.jar and testInternalST-2.2.jar") && value.metricValue == 0.96875))
  assert(metricResultjars.metricValues.exists(value => value.entityIdent.equals("Difference between: testInternalST-3.0.jar and testInternalST-3.1.jar") && value.metricValue == 0.9375))
  assert(!metricResultjars.metricValues.exists(value => value.entityIdent.equals("Difference between: testInternalST-1.2.jar and testInternalST-2.0.jar") &&value.metricName.equals("PREL_Remove") && value.metricValue == 4))
  assert(!metricResultjars.metricValues.exists(value => value.entityIdent.equals("Difference between: testInternalST-1.2.jar and testInternalST-2.0.jar") &&value.metricName.equals("PREL_Append") && value.metricValue == 4))
  assert(!metricResultjars.metricValues.exists(value => value.entityIdent.equals("Difference between: testInternalST-2.1.jar and testInternalST-2.2.jar") &&value.metricName.equals("PREL_Append") && value.metricValue == 7.5))
  assert(!metricResultjars.metricValues.exists(value => value.entityIdent.equals("Difference between: testInternalST-2.1.jar and testInternalST-2.2.jar") &&value.metricName.equals("PREL_Remove") && value.metricValue == 7))
  assert(!metricResultjars.metricValues.exists(value => value.entityIdent.equals("Difference between: testInternalST-3.1.jar and testInternalST-3.2.jar") &&value.metricName.equals("PREL_Remove") && value.metricValue == 6.5))
  assert(!metricResultjars.metricValues.exists(value => value.entityIdent.equals("Difference between: testInternalST-3.1.jar and testInternalST-3.2.jar") &&value.metricName.equals("PREL_Append") && value.metricValue == 7))
  val filesToTestjars2 = new File(getClass.getResource("/group5/testJars").getPath)
  val analysisToTestjars2 = new InternalStabilityAnalysis(filesToTestjars)
  val resultjars2  = AnalysisTestUtils.runMultiFileAnalysis(_ => analysisToTestjars,filesToTestjars,appConfig,Map(Symbol("all_value") -> true))
  val metricResultjars2 = resultjars2.head
  assert(metricResultjars2.metricValues.exists(value => value.entityIdent.equals("Difference between: testInternalST-2.0.jar and testInternalST-2.1.jar") && value.metricValue == 1.0))
  assert(metricResultjars2.metricValues.exists(value => value.entityIdent.equals("Difference between: testInternalST-2.1.jar and testInternalST-2.2.jar") && value.metricValue == 0.96875))
  assert(metricResultjars2.metricValues.exists(value => value.entityIdent.equals("Difference between: testInternalST-3.0.jar and testInternalST-3.1.jar") && value.metricValue == 0.9375))
  assert(metricResultjars2.metricValues.exists(value => value.entityIdent.equals("Difference between: testInternalST-1.2.jar and testInternalST-2.0.jar") &&value.metricName.equals("PREL_Remove") && value.metricValue == 4))
  assert(metricResultjars2.metricValues.exists(value => value.entityIdent.equals("Difference between: testInternalST-1.2.jar and testInternalST-2.0.jar") &&value.metricName.equals("PREL_Append") && value.metricValue == 4))
  assert(metricResultjars2.metricValues.exists(value => value.entityIdent.equals("Difference between: testInternalST-2.1.jar and testInternalST-2.2.jar") &&value.metricName.equals("PREL_Append") && value.metricValue == 7.5))
  assert(metricResultjars2.metricValues.exists(value => value.entityIdent.equals("Difference between: testInternalST-2.1.jar and testInternalST-2.2.jar") &&value.metricName.equals("PREL_Remove") && value.metricValue == 8.0))
  assert(metricResultjars2.metricValues.exists(value => value.entityIdent.equals("Difference between: testInternalST-3.1.jar and testInternalST-3.2.jar") &&value.metricName.equals("PREL_Remove") && value.metricValue == 6.5))
  assert(metricResultjars2.metricValues.exists(value => value.entityIdent.equals("Difference between: testInternalST-3.1.jar and testInternalST-3.2.jar") &&value.metricName.equals("PREL_Append") && value.metricValue == 7.0))








  val filesToTestGson = new File(getClass.getResource("/group5/gson").getPath)
  val analysisToTestGson = new InternalStabilityAnalysis(filesToTestGson)
  val resultGson  = AnalysisTestUtils.runMultiFileAnalysis(_ => analysisToTestGson,filesToTestGson,appConfig,Map.empty[Symbol, Any])
  val metricResultGson = resultGson.head
  assert(metricResultGson.success)
  assert(metricResultGson.metricValues.exists(value => value.entityIdent.equals("Difference between: gson-1.4.jar and gson-1.5.jar") && value.metricValue == 0.9449349481258368))
  assert(metricResultGson.metricValues.exists(value => value.entityIdent.equals("Difference between: gson-2.2.jar and gson-2.4.jar") && value.metricValue == 0.91516736266809))

  println("Bye")

}
