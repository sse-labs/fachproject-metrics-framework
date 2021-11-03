package org.tud.sse.metrics
package analysis

import java.io.File

/**
 * Class representing the result of metrics calculations for a given file or directory. As an analysis
 * may calculate multiple metrics values for a single file / directory, it contains a list of
 * MetricValues.
 *
 * @param analysisName Name of the analysis that produced this result
 * @param jarFile JAR file / directory that has been analyzed
 * @param success Boolean indicating the success of all calculations involved
 * @param metricValues Iterable of MetricsValues for this file
 */
case class MetricsResult (analysisName: String, jarFile: File, success: Boolean, metricValues: Iterable[MetricValue])

object MetricsResult {
  /**
   * Generates a MetricsResult for a failed analysis.
   * @param jarFile JAR file or directory for which the analysis has failed
   * @return MetricsResult with success set to false, and an empty List of MetricValues
   */
  def analysisFailed(analysisName: String, jarFile: File): MetricsResult =
    MetricsResult(analysisName, jarFile, success = false, List())
}

/**
 *
 * @param entityIdent Entity this metric was calculate for. May be the entire file, a single method or any other entity.
 * @param metricName Name of the metric that was calculated here
 * @param metricValue Value calculated for this entity
 */
case class MetricValue(entityIdent: String, metricName: String, metricValue: Double)


