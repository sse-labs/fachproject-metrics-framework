package org.tud.sse.metrics
package output

import java.io.{BufferedWriter, FileWriter}
import scala.util.Try
import com.opencsv.CSVWriter
import analysis.MetricsResult

import scala.collection.mutable

/**
 * Trait providing functionality to export a list of metrics results to a CSV file.
 *
 * @author Johannes Düsing
 */
trait CsvFileOutput {


  def writeResultsToFile(outputFilePath: String, results: List[MetricsResult]): Try[Unit] = Try {
    val fileWriter = new BufferedWriter(new FileWriter(outputFilePath))
    val csvWriter = new CSVWriter(fileWriter)

    val metricNames = results.flatMap(_.metricValues.map(_.metricName)).distinct
    val headings = (List("Path", "Entity") ++ metricNames).toArray
    csvWriter.writeNext(headings)

    val fileMetricsMap: mutable.Map[String, mutable.Map[String, Double]] = new mutable.HashMap()

    results.foreach{ res =>
      res.metricValues.foreach { value =>

        val entityIdent = res.jarFile.getPath + "$" + value.entityIdent

        if(!fileMetricsMap.contains(entityIdent)){
          fileMetricsMap.put(entityIdent, new mutable.HashMap())
        }

        val theMap = fileMetricsMap(entityIdent)

        if(!theMap.contains(value.metricName))
          theMap.put(value.metricName, value.metricValue)
      }
    }

    fileMetricsMap.map{ tuple =>
      val splitIndex = tuple._1.indexOf("$")
      val fileName = tuple._1.substring(0, splitIndex)
      val entityName = tuple._1.substring(splitIndex + 1)
      (List(fileName, entityName) ++
        (2 until headings.length).map{ index =>
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
