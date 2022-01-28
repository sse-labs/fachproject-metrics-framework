package org.tud.sse.metrics
package impl.group1

import testutils.AnalysisTestUtils
import org.scalatest.{FlatSpec, Matchers}
import java.io.File

/**
 * @author feho243
 * Tests for the class DepthOfInheritanceTreeAnalysis [[DepthOfInheritanceTreeAnalysis]]
 */
class DepthOfInheritanceTreeAnalysisTest extends FlatSpec with Matchers{

  "The DepthOfInheritanceTreeAnalysis" must "calculate valid results for single JARs" in {

    val fileToTest = new File(getClass.getResource("/group1/sejda-model-4.0.15.jar").getPath)
    //resource is given in the folder of group 1 names sejda-model-4.0.15.jar
    val analysisDIT = new DepthOfInheritanceTreeAnalysis()

    val appConfig = new ApplicationConfiguration(inputFilePath = "", treatFilesAsLibrary = true, outFileOption = None,
      opalLoggingEnabled = false, batchModeEnabled = Some(false), excludedAnalysesNames = List(), includedAnalysesNames = List(),
      excludeJreClasses = true, additionalClassesDir = None, loadAdditionalClassesAsInterface = false)

    val result = AnalysisTestUtils.runSingleFileAnalysis(analysisDIT, fileToTest, appConfig, Map.empty[Symbol, Any])
    val resultDITMetric = result.head


    // Tests using the Exception classes of the sejda-model-4.0.15.jar to calculate the longest path of inheritance for each class.

    assert(resultDITMetric.success)
    assert(resultDITMetric.metricValues.nonEmpty)
    assert(resultDITMetric.metricValues.exists(value => value.entityIdent.equals("org/sejda/model/exception/ConfigurationException") && value.metricValue == 4.0))
    assert(resultDITMetric.metricValues.exists(value => value.entityIdent.equals("org/sejda/model/exception/InvalidTaskParametersException") && value.metricValue == 5.0))
    assert(resultDITMetric.metricValues.exists(value => value.entityIdent.equals("org/sejda/model/exception/NotificationContextException") && value.metricValue == 6.0))
    assert(resultDITMetric.metricValues.exists(value => value.entityIdent.equals("org/sejda/model/exception/SejdaRuntimeException") && value.metricValue == 5.0))
    assert(resultDITMetric.metricValues.exists(value => value.entityIdent.equals("org/sejda/model/exception/TaskCancelledException") && value.metricValue == 5.0))
    assert(resultDITMetric.metricValues.exists(value => value.entityIdent.equals("org/sejda/model/exception/TaskException") && value.metricValue == 4.0))
    assert(resultDITMetric.metricValues.exists(value => value.entityIdent.equals("org/sejda/model/exception/TaskExecutionException") && value.metricValue == 5.0))
    assert(resultDITMetric.metricValues.exists(value => value.entityIdent.equals("org/sejda/model/exception/TaskIOException") && value.metricValue == 5.0))
    assert(resultDITMetric.metricValues.exists(value => value.entityIdent.equals("org/sejda/model/exception/TaskNonLenientExecutionException") && value.metricValue == 6.0))
    assert(resultDITMetric.metricValues.exists(value => value.entityIdent.equals("org/sejda/model/exception/TaskNotFoundException") && value.metricValue == 5.0))
    assert(resultDITMetric.metricValues.exists(value => value.entityIdent.equals("org/sejda/model/exception/TaskOutputVisitException") && value.metricValue == 5.0))
    assert(resultDITMetric.metricValues.exists(value => value.entityIdent.equals("org/sejda/model/exception/TaskPermissionsException") && value.metricValue == 5.0))
    assert(resultDITMetric.metricValues.exists(value => value.entityIdent.equals("org/sejda/model/exception/TaskWrongPasswordException") && value.metricValue == 6.0))
    assert(resultDITMetric.metricValues.exists(value => value.entityIdent.equals("org/sejda/model/exception/UnsupportedTextException") && value.metricValue == 6.0))
  }
}
