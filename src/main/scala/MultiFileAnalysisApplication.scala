package org.tud.sse.metrics

import input.CliParser

import org.slf4j.{Logger, LoggerFactory}
import multifileanalysis.MultiFileAnalysis
import input.CliParser.OptionMap
import opal.{ClassStreamReader, OPALLogAdapter}

import output.CsvFileOutput

import java.io.{File, FilenameFilter}
import java.nio.file.{Files, Paths}
import scala.util.{Failure, Success}

class MultiFileAnalysisCliParser extends CliParser("TODO") {
  override val additionalOptions: List[String] = List()
  override val additionalSwitches: List[String] = List()
}

object MultiFileAnalysisCliParser { def apply() = new MultiFileAnalysisCliParser()}

trait MultiFileAnalysisApplication[T] extends CsvFileOutput {

  protected val log: Logger = LoggerFactory.getLogger(this.getClass)

  protected def buildAnalysis(directory: File, analysisOptions: OptionMap): MultiFileAnalysis[T]

  private def initializeAnalysis(appOptions: OptionMap, analysisOptions: OptionMap): Unit = {

    val inputFilePath = appOptions(CliParser.inFileSymbol).toString
    val isLibraryFile = appOptions.contains(CliParser.isLibrarySymbol)
    val outFileOption = appOptions.get(CliParser.outFileSymbol).map(_.toString)
    val opalLoggingEnabled = appOptions.contains(CliParser.enableOpalLoggingSymbol)

    log.info("Running multi-file analysis with parameters:")
    log.info(s"\t- Input: $inputFilePath")
    log.info(s"\t- Output File: ${outFileOption.getOrElse("None")}")
    log.info(s"\t- Treat JAR as Library: $isLibraryFile")
    log.info(s"\t- OPAL Logging Enabled: $opalLoggingEnabled")

    OPALLogAdapter.setOpalLoggingEnabled(opalLoggingEnabled)

    if(!Files.exists(Paths.get(inputFilePath))){
      log.error("Input file does not exist")
    } else if(!Files.isDirectory(Paths.get(inputFilePath))){
      log.error("Input file is no directory")
    } else {
      val inputDirectory = new File(inputFilePath)
      val analysis = buildAnalysis(inputDirectory, analysisOptions)

      log.info(s"Processing all JAR files in ${inputDirectory.getPath}...")

      var count = 0
      var lastResult: Option[T] = None
      analysis.initialize()

      inputDirectory
        .listFiles(new FilenameFilter {
          override def accept(dir: File, name: String): Boolean = name.toLowerCase.endsWith(".jar")
        })
        .foreach { file =>
          count += 1
          val project = ClassStreamReader.createProject(file.toURI.toURL, isLibraryFile)

          log.debug(s"Successfully initialized OPAL project for ${file.getName}")

          val result = analysis.produceAnalysisResultForJAR(project, lastResult)
          analysis.analysisResultsPerFile.put(file, result)

          if (result.isFailure) {
            log.error(s"Error while processing a JAR file: $file", result.failed.get)
          }

          lastResult = result.toOption
        }

      log.info(s"Done processing $count JAR files in ${inputDirectory.getName}")

      val results = analysis.produceMetricValues()

      results.foreach{ res =>
        log.info(s"Results for ${res.jarFile.getName}:")
        res.metricValues.foreach{ v =>
          log.info(s"\t-${v.metricName}: ${v.value}")
        }
      }

      if(outFileOption.isDefined){
        log.info(s"Writing results to output file ${outFileOption.get}")
        writeResultsToFile(outFileOption.get, results) match {
          case Failure(ex) =>
            log.error("Error writing results", ex)
          case Success(_) =>
            log.info(s"Done writing results to file")
        }
      }
    }
  }

  final def main(arguments: Array[String]): Unit = {
    val parser: CliParser = MultiFileAnalysisCliParser()
    parser.parseArguments(arguments.toList) match {
      case Success((appOptions, _))
        if !appOptions.contains(CliParser.inFileSymbol) =>
        log.error("Missing required parameter infile")
      case Success((appOptions, analysisOptions)) =>
        initializeAnalysis(appOptions, analysisOptions)
      case Failure(ex) =>
        log.error(s"Error parsing options: ${ex.getMessage}")
    }
  }

}
