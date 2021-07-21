package org.tud.sse.metrics


import opal.{ClassStreamReader, OPALLogAdapter}

import org.slf4j.{Logger, LoggerFactory}
import SingleFileAnalysisCliParser.batchModeSymbol
import input.CliParser
import input.CliParser.OptionMap
import singlefileanalysis.SingleFileAnalysis

import output.CsvFileOutput

import java.io.{File, FilenameFilter}
import java.nio.file.{Files, Paths}
import scala.util.{Failure, Success}

/**
 * Command-Line parser for analyses that process single JAR files. Extends the base parser with
 * support for the --batch-mode switch, that enables processing of multiple JAR files at once.
 */
class SingleFileAnalysisCliParser
  extends CliParser("Usage: MetricsBasedAnalysis [--batch-mode] [--out-file file] filename"){

  override val additionalOptions: List[String] = List()
  override val additionalSwitches: List[String] = List(batchModeSymbol.name)

}

object SingleFileAnalysisCliParser {
  val batchModeSymbol: Symbol = Symbol("batch-mode")

  def apply() = new SingleFileAnalysisCliParser()
}

/**
 * Trait that provides an entrypoint for a SingleFileAnalysis. CLI arguments will be parsed and
 * processed accordingly. Custom arguments will be forwarded to the analysis. Analysis results
 * will be written to an output file if the corresponding CLI argument is set.
 *
 * Required CLI arguments:
 *  - Path to input file or directory (always the last, unnamed argument)
 *
 * Optional CLI argument:
 *  - --out-file <path> Path to output file, results will be written in CSV format
 *  - --is-library If set, all JARs will be interpreted als libraries (important for entry-point detection)
 *  - --opal-logging If set, OPAL logging will be output to CLI
 *  - --batch-mode If set, the input file path must point to a directory. Analysis will be
 *                 executed for all JAR files contained in that directory.
 *
 *
 * @author Johannes Düsing
 */
trait SingleFileAnalysisApplication extends CsvFileOutput {

  /**
   * The Logger for this instance
   */
  private val log: Logger = LoggerFactory.getLogger(this.getClass)

  /**
   * Method that builds the SingleFileAnalysis object for this application. Must be implemented
   * by all subclasses. This method is called exactly once, right after all mandatory CLI
   * parameters have been verified.
   *
   * @return Instance of type SingleFileAnalysis
   */
  protected def buildAnalysis(): SingleFileAnalysis

  private def initializeAnalysis(appOptions: OptionMap, analysisOptions: OptionMap): Unit = {
    val inputFilePath = appOptions(CliParser.inFileSymbol).toString
    val batchModeEnabled = appOptions.contains(SingleFileAnalysisCliParser.batchModeSymbol)
    val isLibraryFile = appOptions.contains(CliParser.isLibrarySymbol)
    val outFileOption = appOptions.get(CliParser.outFileSymbol).map(_.toString)
    val opalLoggingEnabled = appOptions.contains(CliParser.enableOpalLoggingSymbol)


    log.info("Running analysis with parameters:")
    log.info(s"\t- Input: $inputFilePath")
    log.info(s"\t- Batch Mode Enabled: $batchModeEnabled")
    log.info(s"\t- Output File: ${outFileOption.getOrElse("None")}")
    log.info(s"\t- Treat JAR as Library: $isLibraryFile")
    log.info(s"\t- OPAL Logging Enabled: $opalLoggingEnabled")


    OPALLogAdapter.setOpalLoggingEnabled(opalLoggingEnabled)

    if(!Files.exists(Paths.get(inputFilePath))){
      log.error("Input file does not exist")
    } else if(batchModeEnabled && !Files.isDirectory(Paths.get(inputFilePath))){
      log.error("Batch mode enabled but input file is no directory")
    } else if(!batchModeEnabled && Files.isDirectory(Paths.get(inputFilePath))){
      log.error("No batch mode enabled but input file is directory")
    } else {
      val analysis = buildAnalysis()

      val file = new File(inputFilePath)

      analysis.initialize()

      val results = if(!batchModeEnabled){

        log.debug(s"Initializing OPAL project for input file ${file.getName} ...")
        val opalProject = ClassStreamReader.createProject(file.toURI.toURL, isLibraryFile)
        log.info(s"Done initializing OPAL project.")

        analysis.analyzeProject(opalProject, analysisOptions) match {
          case Success(values) =>
            List(JarFileMetricsResult(file, success = true, values))
          case Failure(ex) =>
            log.error(s"Unexpected failure while processing JAR file", ex)
            List(JarFileMetricsResult.analysisFailed(file))
        }
      } else {
        handleBatch(analysis, file, analysisOptions, isLibraryFile)
      }

      results.foreach{ res =>
        log.info(s"Results for ${res.jarFile.getName}:")
        res.metricValues.foreach{ v =>
          log.info(s"\t-${v.metricName}: ${v.value}")
        }
      }

      log.info(s"Done processing JAR file ${file.getName}")

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

  private def handleBatch(analysis: SingleFileAnalysis,
                          inputDirectory: File,
                          customOptions: OptionMap,
                          loadAsLibrary: Boolean): List[JarFileMetricsResult] = {

    log.info(s"Batch mode: Processing all JAR files in ${inputDirectory.getPath}...")
    var count = 0

    val results = inputDirectory
      .listFiles(new FilenameFilter {
        override def accept(dir: File, name: String): Boolean = name.toLowerCase.endsWith(".jar")
      })
      .map{ file =>
        count += 1
        val project = ClassStreamReader.createProject(file.toURI.toURL, loadAsLibrary)

        log.debug(s"Successfully initialized OPAL project for ${file.getName}")

        analysis.analyzeProject(project, customOptions) match {
          case Success(values) =>
            JarFileMetricsResult(file, success = true, values)
          case Failure(ex) =>
            log.error(s"Unexpected failure while processing JAR file", ex)
            JarFileMetricsResult.analysisFailed(file)
        }
      }
      .toList

    log.info(s"Batch mode: Done processing $count JAR files in ${inputDirectory.getName}")

    results
  }

  final def main(arguments: Array[String]): Unit = {
    val parser: CliParser = SingleFileAnalysisCliParser()
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
