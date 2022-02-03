package org.tud.sse.metrics
package output

import application.ApplicationPerformanceStatistics

import com.opencsv.CSVWriter

import java.io.{BufferedWriter, FileWriter}
import scala.util.Try

object StatisticsOutput {

  def writeStatisticsToFile(filePath: String, statistics: ApplicationPerformanceStatistics): Unit = withCsvWriter(filePath) { csvWriter =>

    // Headings of CSV file: Column for each analysis name, plus first fixed column for init times!
    val allAnalysisNames = statistics.fileStatisticsMap.values.flatMap(_.analysisExecutionTimes.keySet).toList.distinct
    val headings = (List("File", "Initialization") ++ allAnalysisNames).toArray
    csvWriter.writeNext(headings)

    // First row just reports the initialization time of all analyses combined
    val metaRow = List("<framework init>", statistics.analysesInitializationTime.toString) ++ allAnalysisNames.map(_ => "/")
    csvWriter.writeNext(metaRow.toArray)

    // For each file: Produce a row with <filename>, <OPAL-Init-Duration>, <>, ... Durations for each analysis
    statistics.fileStatisticsMap.values.foreach{ fileStatistics =>

      val values = List(fileStatistics.opalInitializationTime.toString) ++ allAnalysisNames.map( name =>
        fileStatistics.analysisExecutionTimes.get(name).map(_.toString).getOrElse("no value")
      )

      val nextCsvRow = List(fileStatistics.filePath) ++ values

      csvWriter.writeNext(nextCsvRow.toArray)
    }

  }

  /**
   * Helper Method to initialize a CSV writer for the given filepath.
   * @param filePath Path for the CSV File that the writer should be initialized for
   * @param function The implicit function that is being executed with the CSV writer
   * @tparam T Function Result Type
   * @return Result of the function execution wrapped in a Try object
   */
  private[output] def withCsvWriter[T](filePath: String)
                                      (implicit function: CSVWriter => T): Try[T] =  Try {

    val fileWriter = new BufferedWriter(new FileWriter(filePath))
    val csvWriter = new CSVWriter(fileWriter)

    val result = function(csvWriter)

    fileWriter.flush()
    csvWriter.flush()
    csvWriter.close()
    fileWriter.close()

    result
  }

}
