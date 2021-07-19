package org.tud.sse.metrics
package output

import java.io.{BufferedWriter, FileWriter}
import scala.util.Try
import com.opencsv.CSVWriter

trait CsvFileOutput {

  def writeResultsToFile(outputFilePath: String, results: List[JarFileMetricsResult]): Try[Unit] = Try {
    val fileWriter = new BufferedWriter(new FileWriter(outputFilePath))
    val csvWriter = new CSVWriter(fileWriter)

    val headings = (List("Path") ++ results.flatMap(_.metricValues.map(_.metricName))).toArray
    csvWriter.writeNext(headings)

    results
      .map { result =>
        (List(result.jarFile.getPath) ++
          (1 until headings.length)
            .map {index =>
              result
                .metricValues
                .filter(_.metricName.equals(headings(index)))
                .map(_.value)
                .headOption
                .getOrElse(-1D)
                .toString
            }
          ).toArray
      }
      .foreach(t => csvWriter.writeNext(t))

    csvWriter.flush()
    fileWriter.flush()
    csvWriter.close()
    fileWriter.close()
  }

}
