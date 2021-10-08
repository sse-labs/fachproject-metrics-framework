package org.tud.sse.metrics


import opal.{ClassStreamReader, OPALLogAdapter}

import org.slf4j.{Logger, LoggerFactory}
import SingleFileAnalysisCliParser.batchModeSymbol
import input.CliParser
import input.CliParser.{OptionMap, includeAnalysisSymbol}
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
 * Trait that provides an entrypoint for running SingleFileAnalyses. CLI arguments will be parsed and
 * processed accordingly. Custom arguments will be forwarded to each analysis. Analysis results
 * will be written to an output file if the corresponding CLI argument is set. Multiple Analyses implementations
 * can be registered for a single application. Individual analysis implementations can be excluded or included via
 * CLI.
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
 *  - --exclude-analysis <name> Used to specify the name of an analysis that will be excluded when running the
 *                              application. May be specified multiple times to exclude multiple analyses.
 *  - --include-analysis <name> Used to specify the name of an analysis that will be included when running the
 *                              application. May be specified multiple times to include multiple analyses. Specifying
 *                              this option will disabled all 'exclude' specifications.
 *
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
   * Override this method to return all analyses that this application can execute.
   *
   * @return Sequence of SingleFileAnalyses that can be enabled or disabled via CLI parameters
   */
  protected def registeredAnalyses(): Seq[SingleFileAnalysis]

  def validateApplicationOptions(appOptions: OptionMap): Option[ApplicationConfiguration] = {
    log.info(s"The following analyses are available for execution :${registeredAnalyses().map(_.analysisName).mkString(",")}")

    val appConfiguration = ApplicationConfiguration.fromOptionsSingleFile(appOptions)

    // Validate usage of analysis includes and exculdes
    if(appConfiguration.excludedAnalysesNames.nonEmpty && appConfiguration.includedAnalysesNames.nonEmpty){
      log.warn(s"Both analysis includes and analysis excludes have been specified. Only includes will be accounted for.")
    }

    // Validate input file path
    if(!Files.exists(appConfiguration.inputFile.toPath)){
      log.error("Input file does not exist")
      None
    } else if(appConfiguration.batchModeEnabled.get && !Files.isDirectory(appConfiguration.inputFile.toPath)){
      log.error("Batch mode enabled but input file is no directory")
      None
    } else if(!appConfiguration.batchModeEnabled.get && Files.isDirectory(appConfiguration.inputFile.toPath)) {
      log.error("No batch mode enabled but input file is directory")
      None
    } else {
      appConfiguration.logInfo(log)
      Some(appConfiguration)
    }
  }

  def calculateResults(appConfiguration: ApplicationConfiguration, analysisOptions: OptionMap): List[JarFileMetricsResult] = {
    val effectiveAnalysisNames = appConfiguration.getActiveAnalysisNamesFor(registeredAnalyses())

    OPALLogAdapter.setOpalLoggingEnabled(appConfiguration.opalLoggingEnabled)

    val analyses = registeredAnalyses()
      .filter(a => effectiveAnalysisNames.contains(a.analysisName))

    analyses.foreach(_.initialize())

    if(!appConfiguration.batchModeEnabled.get){

      val file = appConfiguration.inputFile

      log.debug(s"Initializing OPAL project for input file ${file.getName} ...")
      val opalProject = ClassStreamReader.createProject(file.toURI.toURL, appConfiguration.treatFilesAsLibrary)
      log.info(s"Done initializing OPAL project.")

      analyses
        .map { analysis =>
          analysis.analyzeProject(opalProject, analysisOptions) match {
            case Success(values) =>
              JarFileMetricsResult(analysis.analysisName, file, success = true, values)
            case Failure(ex) =>
              log.error(s"Unexpected failure while processing JAR file", ex)
              JarFileMetricsResult.analysisFailed(analysis.analysisName, file)
          }
        }
        .toList
    } else {
      handleBatch(analyses, appConfiguration.inputFile, analysisOptions, appConfiguration.treatFilesAsLibrary)
    }
  }

  def handleResults(results: List[JarFileMetricsResult], appConfiguration: ApplicationConfiguration): Unit = {
    results.foreach{ res =>
      log.info(s"Results for analysis '${res.analysisName}' on file ${res.jarFile.getName}:")
      res.metricValues.foreach{ v =>
        log.info(s"\t-${v.metricName}: ${v.value}")
      }
    }

    if(appConfiguration.outFileOption.isDefined){
      log.info(s"Writing results to output file ${appConfiguration.outFileOption.get}")
      writeResultsToFile(appConfiguration.outFileOption.get, results) match {
        case Failure(ex) =>
          log.error("Error writing results", ex)
        case Success(_) =>
          log.info(s"Done writing results to file")
      }
    }
  }

  private def handleBatch(analyses: Seq[SingleFileAnalysis],
                          inputDirectory: File,
                          customOptions: OptionMap,
                          loadAsLibrary: Boolean): List[JarFileMetricsResult] = {

    log.info(s"Batch mode: Processing all JAR files in ${inputDirectory.getPath}...")
    var count = 0

    val results = inputDirectory
      .listFiles(new FilenameFilter {
        override def accept(dir: File, name: String): Boolean = name.toLowerCase.endsWith(".jar")
      })
      .flatMap{ file =>
        count += 1
        val project = ClassStreamReader.createProject(file.toURI.toURL, loadAsLibrary)

        log.debug(s"Successfully initialized OPAL project for ${file.getName}")

        analyses
          .map { analysis =>
            analysis.analyzeProject(project, customOptions) match {
              case Success(values) =>
                JarFileMetricsResult(analysis.analysisName, file, success = true, values)
              case Failure(ex) =>
                log.error(s"Unexpected failure while processing JAR file", ex)
                JarFileMetricsResult.analysisFailed(analysis.analysisName, file)
            }
          }
          .toList
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
        validateApplicationOptions(appOptions) match {
          case Some(appConfig) =>
            val results = calculateResults(appConfig, analysisOptions)
            handleResults(results, appConfig)
          case None =>
            System.exit(1)
        }
      case Failure(ex) =>
        log.error(s"Error parsing options: ${ex.getMessage}")
    }

  }


}
