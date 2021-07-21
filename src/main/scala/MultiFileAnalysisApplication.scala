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

/**
 * Command-Line parser for analyses that process multiple JAR files as a unit.
 */
private class MultiFileAnalysisCliParser extends CliParser("TODO") {
  override val additionalOptions: List[String] = List()
  override val additionalSwitches: List[String] = List()
}

private object MultiFileAnalysisCliParser { def apply() = new MultiFileAnalysisCliParser()}

/**
 * Trait that provides an entrypoint for a MultiFileAnalysis. CLI arguments will be parsed and
 * processed accordingly. Custom arguments will be forwarded to the analysis. Analysis results
 * will be written to an output file if the corresponding CLI argument is set.
 *
 * Required CLI arguments:
 *  - Path to input folder, all contained JAR files will be processed (always the last, unnamed argument)
 *
 * Optional CLI argument:
 *  - --out-file <path> Path to output file, results will be written in CSV format
 *  - --is-library If set, all JARs will be interpreted als libraries (important for entry-point detection)
 *  - --opal-logging If set, OPAL logging will be output to CLI
 *
 * @tparam T Intermediate Result Type of the MultiFileAnalysis
 *
 * @author Johannes Düsing
 */
trait MultiFileAnalysisApplication[T] extends CsvFileOutput {

  /**
   * The Logger for this instance
   */
  private val log: Logger = LoggerFactory.getLogger(this.getClass)

  /**
   * Method that builds the MultiFileAnalysis object for this application. Must be implemented
   * by all subclasses. This method is called exactly once, right after all mandatory CLI
   * parameters have been verified.
   *
   * @param directory Directory that contains the JAR files that are being analyzed
   * @param analysisOptions Custom analysis options parsed from the CLI invocation
   * @return Instance of type MultiFileAnalysis with intermediate result type T
   */
  protected def buildAnalysis(directory: File, analysisOptions: OptionMap): MultiFileAnalysis[T]


  /**
   * Ordering that determines the order in which JAR files are processed. Per default, JARs
   * are processed in alphabetical order based on their name. Override this method to
   * implement custom orderings.
   *
   * @return An instance of type Ordering[File], that defines the ordering relation
   */
  protected def fileOrdering: Ordering[File] = (x: File, y: File) => x.getName.compare(y.getName)

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
        .sorted(fileOrdering)
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
