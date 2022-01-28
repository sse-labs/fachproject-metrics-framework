package org.tud.sse.metrics
package application

import analysis.{MetricsResult, NamedAnalysis}
import input.CliParser
import input.CliParser.OptionMap
import output.{CsvFileOutput, StatisticsOutput}

import org.slf4j.{Logger, LoggerFactory}

import scala.util.{Failure, Success}

/**
 * Base trait for applications that execute any kind of analysis on JAR files. Provides Lifecycle Hooks,
 * export functionality and logging access.
 */
trait FileAnalysisApplication extends CsvFileOutput{

  /**
   * The logger for this instance
   */
  protected final val log: Logger = LoggerFactory.getLogger(this.getClass)

  /**
   * The CLI Parser instance to use for this application.
   */
  protected val cliParser: CliParser

  /**
   * Method that validates the application options and, if they are valid, creates a corresponding
   * ApplicationConfiguration object. If the options are invalid, None shall be returned. This method
   * is called before any analysis is initialized.
   * @param appOptions The application options produced by the CLI parser
   * @return Option holding the ApplicationConfiguration, or None
   */
  def validateApplicationOptions(appOptions: OptionMap): Option[ApplicationConfiguration]

  /**
   * Method that executes all analyses on the input file(s) and produces the resulting List of JarFileMetrics.
   * @param appConfiguration ApplicationConfiguration object as produced by "validateApplicationOptions"
   * @param analysisOptions Custom analysis options produced by the CLI parser
   * @return Tuple containing 1) List of JarFileMetricsResults and 2) the ApplicationPerformanceStatistics
   */
  def calculateResults(appConfiguration: ApplicationConfiguration,
                       analysisOptions: OptionMap): (List[MetricsResult], ApplicationPerformanceStatistics)

  /**
   * Prints results to the CLI and writes them to a CSV report if specified by the
   * application configuration.
   * @param results Results to process
   * @param appConfiguration ApplicationConfiguration object
   */
  def handleResults(results: List[MetricsResult], appConfiguration: ApplicationConfiguration): Unit = {

    if (appConfiguration.outFileOption.isDefined) {
      log.info(s"Writing results to output file ${appConfiguration.outFileOption.get}")
      writeResultsToFile(appConfiguration.outFileOption.get, results) match {
        case Failure(ex) =>
          log.error("Error writing results", ex)
        case Success(_) =>
          log.info(s"Done writing results to file")
      }
    } else {
      results.foreach { res =>
        log.info(s"Results for analysis '${res.analysisName}' on file ${res.jarFile.getName}:")
        res.metricValues.foreach { v =>
          log.info(s"\t- ${v.metricName} on ${v.entityIdent}: ${v.metricValue}")
        }
      }
    }
  }

  /**
   * Executes a given operation and measures the corresponding execution time (wall clock). Returns a tuple of the operation's
   * result and it's execution time.
   *
   * @param codeToExecute The function to measure
   * @tparam T Function return type
   * @return Tuple of execution time in MS and the function's result
   */
  protected def measureExecutionTime[T] (implicit codeToExecute: () => T): (Long, T) = {
    val startTime = System.nanoTime()
    val result = codeToExecute.apply()
    val durationMs: Long = (System.nanoTime() - startTime) / 1000000L
    (durationMs, result)
  }

  /**
   * Checks the names of registered analyses, excludes and includes for ineffective or invalid configuration and prints
   * errors to the console.
   * @param analyses Set of analyses
   * @param appConfig App Configuration
   */
  protected def validateAnalysesNames(analyses: Seq[NamedAnalysis], appConfig: ApplicationConfiguration): Unit = {
    val allNames = analyses.map(_.analysisName).toList

    allNames
      .foreach{ name =>
        val isDuplicate = allNames
          .count(n => n.toLowerCase().equals(name.toLowerCase())) > 1

        if(isDuplicate){
          log.warn(s"Configuration error: Analysis name $name is used more than once")
        }
      }

    if(appConfig.excludeSpecificationsApply()){
      appConfig
        .excludedAnalysesNames
        .filter{ excludedName => !allNames.contains(excludedName)}
        .foreach{ name => log.warn(s"Configuration error: Excluded analysis name $name has no effect since it is not contained in the set of registered analyses.")}
    } else if(appConfig.includeSpecificationsApply()){
      appConfig
        .includedAnalysesNames
        .filter{ includedName => !allNames.contains(includedName)}
        .foreach{ name => log.error(s"Configuration error: Included analysis name $name is not contained in the set of registered analyses.")}
    }
  }

  /**
   * The main entrypoint for every analysis application. Parses CLI input and controls the application lifecycle.
   * @param arguments List of arguments
   */
  final def main(arguments: Array[String]): Unit = {
    cliParser.parseArguments(arguments.toList) match {

      case Success((appOptions, _))
        if !appOptions.contains(CliParser.inFileSymbol) =>
        log.error("Missing required parameter infile")

      case Success((appOptions, analysisOptions)) =>

        validateApplicationOptions(appOptions) match {

          case Some(appConfig) =>
            val (results, stats) = calculateResults(appConfig, analysisOptions)
            handleResults(results, appConfig)

            if(appConfig.performanceEvaluationEnabled){
              val reportFilePath = "performance-report.csv"
              log.info(s"Writing performance report to: $reportFilePath")
              StatisticsOutput.writeStatisticsToFile(reportFilePath, stats)
            }


          case None =>
            System.exit(1)
        }

      case Failure(ex) =>
        log.error(s"Error parsing options: ${ex.getMessage}")
    }
  }
}
