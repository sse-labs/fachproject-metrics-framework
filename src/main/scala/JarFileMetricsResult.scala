package org.tud.sse.metrics

import java.io.File

case class JarFileMetricsResult (jarFile: File, success: Boolean, metricValues: Iterable[JarFileMetricValue])

object JarFileMetricsResult {
  def analysisFailed(jarFile: File): JarFileMetricsResult = JarFileMetricsResult(jarFile, success = false, List())
}

case class JarFileMetricValue(metricName: String, value: Double)
