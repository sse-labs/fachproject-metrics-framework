package org.tud.sse.metrics
package impl.group1

import java.io.File

import org.scalatest.{FlatSpec, Matchers}
import org.tud.sse.metrics.testutils.AnalysisTestUtils

/**
 * Tests for the class NumberOfChildrenAnalysis [[NumberOfChildrenAnalysis]]
 */
class NumberOfChildrenAnalysisTest extends FlatSpec with Matchers{

  "The NumberOfChildrenAnalysis" must "calculate valid results for single JARs" in {

    val fileToTest = new File(getClass.getResource("/group1/sejda-model-4.0.15.jar").getPath)
    val analysisNOC = new NumberOfChildrenAnalysis()

    val appConfig = new ApplicationConfiguration(inputFilePath = "", treatFilesAsLibrary = true, outFileOption = None,
      opalLoggingEnabled = false, batchModeEnabled = Some(false), excludedAnalysesNames = List(), includedAnalysesNames = List(),
      excludeJreClasses = true, additionalClassesDir = None, loadAdditionalClassesAsInterface = false)

    val result = AnalysisTestUtils.runSingleFileAnalysis(analysisNOC, fileToTest, appConfig, Map.empty[Symbol, Any])
    val resultNOCMetric = result.head


    // Tests using the Exception classes of the sejda-model-4.0.15.jar to count the number of direct children for each class.

    assert(resultNOCMetric.success)
    assert(resultNOCMetric.metricValues.nonEmpty)
    assert(resultNOCMetric.metricValues.exists(value => value.entityIdent.equals("org/sejda/model/exception/ConfigurationException") && value.metricValue == 0.0))
    assert(resultNOCMetric.metricValues.exists(value => value.entityIdent.equals("org/sejda/model/exception/InvalidTaskParametersException") && value.metricValue == 0.0))
    assert(resultNOCMetric.metricValues.exists(value => value.entityIdent.equals("org/sejda/model/exception/NotificationContextException") && value.metricValue == 0.0))
    assert(resultNOCMetric.metricValues.exists(value => value.entityIdent.equals("org/sejda/model/exception/SejdaRuntimeException") && value.metricValue == 1.0))
    assert(resultNOCMetric.metricValues.exists(value => value.entityIdent.equals("org/sejda/model/exception/TaskCancelledException") && value.metricValue == 0.0))
    assert(resultNOCMetric.metricValues.exists(value => value.entityIdent.equals("org/sejda/model/exception/TaskException") && value.metricValue == 7.0))
    assert(resultNOCMetric.metricValues.exists(value => value.entityIdent.equals("org/sejda/model/exception/TaskExecutionException") && value.metricValue == 1.0))
    assert(resultNOCMetric.metricValues.exists(value => value.entityIdent.equals("org/sejda/model/exception/TaskIOException") && value.metricValue == 2.0))
    assert(resultNOCMetric.metricValues.exists(value => value.entityIdent.equals("org/sejda/model/exception/TaskNonLenientExecutionException") && value.metricValue == 0.0))
    assert(resultNOCMetric.metricValues.exists(value => value.entityIdent.equals("org/sejda/model/exception/TaskNotFoundException") && value.metricValue == 0.0))
    assert(resultNOCMetric.metricValues.exists(value => value.entityIdent.equals("org/sejda/model/exception/TaskOutputVisitException") && value.metricValue == 0.0))
    assert(resultNOCMetric.metricValues.exists(value => value.entityIdent.equals("org/sejda/model/exception/TaskPermissionsException") && value.metricValue == 0.0))
    assert(resultNOCMetric.metricValues.exists(value => value.entityIdent.equals("org/sejda/model/exception/TaskWrongPasswordException") && value.metricValue == 0.0))
    assert(resultNOCMetric.metricValues.exists(value => value.entityIdent.equals("org/sejda/model/exception/UnsupportedTextException") && value.metricValue == 0.0))
  }
}
