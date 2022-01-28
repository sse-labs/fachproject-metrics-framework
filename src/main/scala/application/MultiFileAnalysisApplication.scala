package org.tud.sse.metrics
package application

import analysis.{MetricsResult, MultiFileAnalysis}
import input.CliParser
import input.CliParser.OptionMap
import opal.{OPALLogAdapter, OPALProjectHelper}

import java.io.{File, FileInputStream, FilenameFilter}
import java.nio.file.Files
import scala.util.{Failure, Success, Try}

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
 *  - --no-jre-classes Will not load JRE class files when initializing the OPAL project
 *  - --exclude-analysis <name> Used to specify the name of an analysis that will be excluded when running the
 *                              application. May be specified multiple times to exclude multiple analyses.
 *  - --include-analysis <name> Used to specify the name of an analysis that will be included when running the
 *                              application. May be specified multiple times to include multiple analyses. Specifying
 *                              this option will disabled all 'exclude' specifications.
 *  - --no-jre-classes Will not load JRE class files when initializing the OPAL project
 *  - --additional-classes-dir path to a JAR file or directory containing JAR files. All classes contained in those JAR
 *                             files will be loaded as library classes when initializing the OPAL project
 *  - --load-additional-classes-as-interfaces If set, all additional classes will be loaded as interfaces only, without
 *                                            their actual implementation
 *
 * @author Johannes Düsing
 */
trait MultiFileAnalysisApplication extends FileAnalysisApplication {

  override final val cliParser: CliParser = MultiFileAnalysisCliParser()

  private final var registeredAnalyses: Seq[MultiFileAnalysis[_]] = Seq()

  /**
   * Method that builds the sequence of analyses available in this application. This method will only be
   * executed once the input validation has succeeded.
   * @param jarDirectory File object representing the directory that is being analyzed
   * @return List of MultiFileAnalysis objects that can be selected for execution in this application
   */
  protected def buildAnalyses(jarDirectory: File): Seq[MultiFileAnalysis[_]]

  /**
   * Ordering that determines the order in which JAR files are processed. Per default, JARs
   * are processed in alphabetical order based on their name. Override this method to
   * implement custom orderings.
   *
   * @return An instance of type Ordering[File], that defines the ordering relation
   */
  protected def fileOrdering: Ordering[File] = (x: File, y: File) => x.getName.compare(y.getName)

  /**
   * Function that defines which files are processed by this analysis application. Per default, only files
   * that end in ".jar" are processed.
   *
   * @return Instance of a function from (file, name) to Boolean, where "file" is the file object
   */
  protected def fileFilter: (File, String) => Boolean = (_, name) => name.toLowerCase().endsWith(".jar")


  override final def validateApplicationOptions(appOptions: OptionMap): Option[ApplicationConfiguration] = {
    val appConfiguration = ApplicationConfiguration.fromOptionsMultiFile(appOptions)

    // Validate usage of analysis includes and exculdes
    if(appConfiguration.excludedAnalysesNames.nonEmpty && appConfiguration.includedAnalysesNames.nonEmpty){
      log.warn(s"Both analysis includes and analysis excludes have been specified. Only includes will be accounted for.")
    }

    // Validate input file path
    if(!Files.exists(appConfiguration.inputFile.toPath)){
      log.error("Input file does not exist")
      None
    } else if(!Files.isDirectory(appConfiguration.inputFile.toPath)){
      log.error("Input file is no directory")
      None
    } else {
      validateAnalysesNames(registeredAnalyses, appConfiguration)
      appConfiguration.logInfo(log)
      Some(appConfiguration)
    }
  }

  override final def calculateResults(appConfiguration: ApplicationConfiguration,
                                      analysisOptions: OptionMap): (List[MetricsResult], ApplicationPerformanceStatistics) = {
    registeredAnalyses = buildAnalyses(appConfiguration.inputFile)

    val effectiveAnalysisNames = appConfiguration.getActiveAnalysisNamesFor(registeredAnalyses)
    log.info(s"The following analyses are available for execution :${registeredAnalyses.map(_.analysisName).mkString(",")}")

    OPALLogAdapter.setOpalLoggingEnabled(appConfiguration.opalLoggingEnabled)

    val analyses = registeredAnalyses
      .filter(a => effectiveAnalysisNames.contains(a.analysisName))

    val initTimeMs = measureExecutionTime (() => analyses.foreach(_.initialize()))._1
    val appStatistics = ApplicationPerformanceStatistics(initTimeMs)

    var count = 0

    appConfiguration
      .inputFile
      .listFiles(new FilenameFilter {
        override def accept(dir: File, name: String): Boolean = fileFilter(dir, name)
      })
      .sorted(fileOrdering)
      .foreach { file =>
        count += 1

        Try {
          measureExecutionTime{ () =>
            val projectClasses = OPALProjectHelper.readClassesFromJarStream(new FileInputStream(file), file.toURI.toURL)
            val additionalClasses = appConfiguration
              .additionalClassesDir
              .flatMap(dir => OPALProjectHelper.readClassesFromFileStructure(new File(dir), !appConfiguration.loadAdditionalClassesAsInterface).toOption)
              .getOrElse(List.empty)
            OPALProjectHelper.buildOPALProject(projectClasses.get, additionalClasses, appConfiguration.treatFilesAsLibrary, appConfiguration.excludeJreClasses)
          }
        } match {
          case Success((initTime, project)) =>

            val fileStatistics = FilePerformanceStatistics(file.getPath, initTime)

            log.debug(s"Successfully initialized OPAL project for ${file.getName}")

            analyses.foreach { analysis =>
              val (execTime, results) = measureExecutionTime { () => analysis.analyzeNext(project, file, analysisOptions) }
              fileStatistics.putAnalysisExecutionTime(analysis.analysisName, execTime)

              if (results.isFailure) {
                log.error(s"Error while processing a JAR file: $file", results.failed.get)
              }
            }

            appStatistics.putFileStatistics(fileStatistics)

          case Failure(ex) =>
            log.error(s"Failure while initializing OPAL project for ${file.getName}", ex)
        }
      }

    log.info(s"Done processing $count JAR files in ${appConfiguration.inputFile.getName}")

    // Use a "dummy" FilePerformanceStatistics object to capture calculation of metric values
    val metricsCalculationStats = FilePerformanceStatistics("<metric calculation>", 0)

    val metricResults = analyses
      .flatMap{ analysis =>
        val (execTime, results) = measureExecutionTime { () => analysis.produceMetricValues() }
        metricsCalculationStats.putAnalysisExecutionTime(analysis.analysisName, execTime)
        results
      }
      .toList

    appStatistics.putFileStatistics(metricsCalculationStats)

    (metricResults, appStatistics)
  }

}
