package org.tud.sse.metrics
package impl.group1

import testutils.AnalysisTestUtils

import org.scalatest.{FlatSpec, Matchers}

import java.io.File

class AverageNestingTest extends FlatSpec with Matchers{

  "The AverageNestingAnalysis" must "calculate valid results for single JARs" in {

    val fileToTest = new File(getClass.getResource("/group1/11-commons-collections-3.2.2.jar").getPath)
    val analysisToTest = new AverageNestingAnalysis()

    val appConfig = new ApplicationConfiguration(inputFilePath = "", treatFilesAsLibrary = true, outFileOption = None,
      opalLoggingEnabled = false, batchModeEnabled = Some(false), excludedAnalysesNames = List(), includedAnalysesNames = List(),
      excludeJreClasses = true, additionalClassesDir = None, loadAdditionalClassesAsInterface = false)

    val result = AnalysisTestUtils.runSingleFileAnalysis(analysisToTest, fileToTest, appConfig, Map.empty[Symbol, Any])

    assert(result.nonEmpty)

    val metricResult = result.head

    assert(metricResult.success)
    assert(metricResult.metricValues.nonEmpty)
    assert(metricResult.metricValues.exists(value => value.entityIdent.contains("median of: org/apache/commons/collections/set/PredicatedSet") && value.metricValue == 0.0))
    assert(metricResult.metricValues.exists(value => value.entityIdent.contains("median of: org/apache/commons/collections/CollectionUtils") && value.metricValue == 1.0))
    assert(metricResult.metricValues.exists(value => value.entityIdent.contains("mean of: org/apache/commons/collections/map/ListOrderedMap") && value.metricValue == 0.39285714285714285))
    assert(metricResult.metricValues.exists(value => value.entityIdent.contains("mean of: org/apache/commons/collections/iterators/SingletonIterator") && value.metricValue == 0.8333333333333334))
    assert(metricResult.metricValues.exists(value => value.entityIdent.contains("median of: org/apache/commons/collections/collection/SynchronizedCollection") && value.metricValue == 0.0))
    assert(metricResult.metricValues.exists(value => value.entityIdent.contains("mean of: org/apache/commons/collections/list/CursorableLinkedList$Cursor") && value.metricValue == 0.8888888888888888))
    assert(metricResult.metricValues.exists(value => value.entityIdent.contains("mean of: org/apache/commons/collections/map/AbstractMapDecorator") && value.metricValue == 0.1111111111111111))
    assert(metricResult.metricValues.exists(value => value.entityIdent.contains("mean of: org/apache/commons/collections/iterators/IteratorChain") && value.metricValue == 0.5625))
    assert(metricResult.metricValues.exists(value => value.entityIdent.contains("median of: org/apache/commons/collections/FastHashMap$KeySet") && value.metricValue == 0.0))
    assert(metricResult.metricValues.exists(value => value.entityIdent.contains("median of: org/apache/commons/collections/set/CompositeSet") && value.metricValue == 1.0))
    assert(metricResult.metricValues.exists(value => value.entityIdent.contains("mean of: org/apache/commons/collections/map/IdentityMap") && value.metricValue == 0.18181818181818182))
    assert(metricResult.metricValues.exists(value => value.entityIdent.contains("mean of: org/apache/commons/collections/buffer/BoundedBuffer") && value.metricValue == 0.3333333333333333))
    assert(metricResult.metricValues.exists(value => value.entityIdent.contains("median of: org/apache/commons/collections/map/AbstractReferenceMap$ReferenceKeySetIterator") && value.metricValue == 0.0))
    assert(metricResult.metricValues.exists(value => value.entityIdent.contains("mean of: org/apache/commons/collections/StaticBucketMap$Node") && value.metricValue == 0.42857142857142855))
    assert(metricResult.metricValues.exists(value => value.entityIdent.contains("mean of: org/apache/commons/collections/FastArrayList") && value.metricValue == 0.9705882352941176))



  }

}

