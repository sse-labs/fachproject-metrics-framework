package org.tud.sse.metrics

import java.io.File

/**
 * Class representing the result of metrics calculations for a given file or directory. As an analysis
 * may calculate multiple metrics values for a single file / directory, it contains a list of
 * JarFileMetricValues.
 *
 * @param jarFile JAR file / directory that has been analyzed
 * @param success Boolean indicating the success of all calculations involved
 * @param metricValues Iterable of MetricsValues for this file
 */
case class JarFileMetricsResult (analysisName: String, jarFile: File, success: Boolean, metricValues: Iterable[JarFileMetricValue])

object JarFileMetricsResult {
  /**
   * Generates a JarFileMetricsResult for a failed analysis.
   * @param jarFile JAR file or directory for which the analysis has failed
   * @return JarFileMetricsResult with success set to false, and an empty List of MetricValues
   */
  def analysisFailed(analysisName: String, jarFile: File): JarFileMetricsResult =
    JarFileMetricsResult(analysisName, jarFile, success = false, List())
}

/**
 * Class representing the value of a single metric
 * @param metricName Name of the metric
 * @param value Value of the metric
 */
case class JarFileMetricValue(metricName: String, value: Double)
