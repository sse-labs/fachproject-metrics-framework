package org.tud.sse.metrics
package application

import scala.collection.mutable

//TODO: Timestamp / Configuration?
class ApplicationPerformanceStatistics() {

  private var totalInitTimeMs: Long = 0

  private val perFilePerformanceStatistics: mutable.Map[String, FilePerformanceStatistics] = new mutable.HashMap()

  def putFileStatistics(statistics:FilePerformanceStatistics): Unit = {
    perFilePerformanceStatistics.put(statistics.filePath, statistics)
  }

  def analysesInitializationTime: Long = totalInitTimeMs
  def fileStatisticsMap: Map[String, FilePerformanceStatistics] = perFilePerformanceStatistics.toMap

}

object ApplicationPerformanceStatistics {

  def apply(initTime: Long): ApplicationPerformanceStatistics = {
    val stats = new ApplicationPerformanceStatistics()
    stats.totalInitTimeMs = initTime
    stats
  }

  def apply(): ApplicationPerformanceStatistics = {
    new ApplicationPerformanceStatistics
  }


}

class FilePerformanceStatistics(val filePath: String) {

  private var opalInitTimeMs: Long = 0

  private val perAnalysisExecutionTimesMs: mutable.Map[String, Long] = new mutable.HashMap()

  def putAnalysisExecutionTime(analysisName: String, executionTimeMs: Long): Unit =  {
    perAnalysisExecutionTimesMs.put(analysisName, executionTimeMs)
  }

  def analysisExecutionTimes: Map[String, Long] = perAnalysisExecutionTimesMs.toMap

  def opalInitializationTime: Long = opalInitTimeMs

}

object FilePerformanceStatistics {

  def apply(filePath: String, initTimeMs: Long): FilePerformanceStatistics = {
    val stat = new FilePerformanceStatistics(filePath)
    stat.opalInitTimeMs = initTimeMs
    stat
  }

  def apply(filePath: String) = new FilePerformanceStatistics(filePath)
}
