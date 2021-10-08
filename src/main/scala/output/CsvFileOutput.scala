package org.tud.sse.metrics
package output

import java.io.{BufferedWriter, FileWriter}
import scala.util.Try
import com.opencsv.CSVWriter

import scala.collection.mutable

/**
 * Trait providing functionality to export a list of metrics results to a CSV file.
 *
 * @author Johannes Düsing
 */
trait CsvFileOutput {


  def writeResultsToFile(outputFilePath: String, results: List[JarFileMetricsResult]): Try[Unit] = Try {
    val fileWriter = new BufferedWriter(new FileWriter(outputFilePath))
    val csvWriter = new CSVWriter(fileWriter)

    val metricNames = results.flatMap(_.metricValues.map(_.metricName)).distinct
    val headings = (List("Path") ++ metricNames).toArray
    csvWriter.writeNext(headings)

    val fileMetricsMap: mutable.Map[String, mutable.Map[String, Double]] = new mutable.HashMap()

    results.foreach{ res =>
      val path = res.jarFile.getPath

      if(!fileMetricsMap.contains(path)){
        fileMetricsMap.put(path, new mutable.HashMap())
      }

      val theMap = fileMetricsMap(path)

      res.metricValues.foreach { value =>
        if(!theMap.contains(value.metricName))
          theMap.put(value.metricName, value.value)
      }
    }

    fileMetricsMap.map{ tuple =>
      (List(tuple._1) ++
        (1 until headings.length).map{ index =>
          tuple._2.get(headings(index)).map(_.toString).getOrElse("")
        }).toArray
    }
      .foreach(t => csvWriter.writeNext(t))

    csvWriter.flush()
    fileWriter.flush()
    csvWriter.close()
    fileWriter.close()
  }

}
