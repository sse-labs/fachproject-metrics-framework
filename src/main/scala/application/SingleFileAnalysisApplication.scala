package org.tud.sse.metrics
package application

import analysis.{MetricsResult, SingleFileAnalysis}
import application.SingleFileAnalysisCliParser.batchModeSymbol
import input.CliParser
import input.CliParser.OptionMap
import opal.{OPALLogAdapter, OPALProjectHelper}

import java.io.{File, FileInputStream, FilenameFilter}
import java.nio.file.Files
import java.util.jar.JarInputStream
import scala.util.{Failure, Success, Try}

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
 *  - --no-jre-classes Will not load JRE class files when initializing the OPAL project
 *  - --additional-classes-dir <path> path to a JAR file or directory containing JAR files. All classes contained in those JAR
 *                                    files will be loaded as library classes when initializing the OPAL project
 *
 *
 *
 * @author Johannes Düsing
 */
trait SingleFileAnalysisApplication extends FileAnalysisApplication {

  override val cliParser: CliParser = SingleFileAnalysisCliParser()

  /**
   * Sequence of analyses that can be selected for execution in this application
   */
  protected val registeredAnalyses: Seq[SingleFileAnalysis]

  override def validateApplicationOptions(appOptions: OptionMap): Option[ApplicationConfiguration] = {
    log.info(s"The following analyses are available for execution :${registeredAnalyses.map(_.analysisName).mkString(",")}")

    val appConfiguration = ApplicationConfiguration.fromOptionsSingleFile(appOptions)

    // Validate usage of analysis includes and excludes
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
      validateAnalysesNames(registeredAnalyses, appConfiguration)
      appConfiguration.logInfo(log)
      Some(appConfiguration)
    }
  }

  override def calculateResults(appConfiguration: ApplicationConfiguration, analysisOptions: OptionMap): List[MetricsResult] = {
    val effectiveAnalysisNames = appConfiguration.getActiveAnalysisNamesFor(registeredAnalyses)

    OPALLogAdapter.setOpalLoggingEnabled(appConfiguration.opalLoggingEnabled)

    val analyses = registeredAnalyses
      .filter(a => effectiveAnalysisNames.contains(a.analysisName))

    analyses.foreach(_.initialize())

    if(!appConfiguration.batchModeEnabled.get){

      val file = appConfiguration.inputFile

      log.debug(s"Initializing OPAL project for input file ${file.getName} ...")
      Try {
        val projectClasses = OPALProjectHelper.readClassesFromJarStream(new FileInputStream(file), file.toURI.toURL)
        val additionalClasses = appConfiguration
          .additionalClassesDir
          .flatMap(dir => OPALProjectHelper.readClassesFromFileStructure(new File(dir)).toOption)
          .getOrElse(List.empty)

        OPALProjectHelper.buildOPALProject(projectClasses.get, additionalClasses, appConfiguration.treatFilesAsLibrary, appConfiguration.excludeJreClasses)
      } match {
        case Success(project) =>
          log.info(s"Done initializing OPAL project.")
          analyses
            .map { analysis =>
              analysis.analyzeProject(project, analysisOptions) match {
                case Success(values) =>
                  MetricsResult(analysis.analysisName, file, success = true, values)
                case Failure(ex) =>
                  log.error(s"Unexpected failure while processing JAR file", ex)
                  MetricsResult.analysisFailed(analysis.analysisName, file)
              }
            }
            .toList
        case Failure(ex) =>
          log.error(s"Failure while initializing OPAL project for ${file.getName}", ex)
          List.empty
      }
    } else {
      handleBatch(analyses, appConfiguration.inputFile, appConfiguration, analysisOptions)
    }
  }

  private def handleBatch(analyses: Seq[SingleFileAnalysis],
                          inputDirectory: File,
                          appConfiguration: ApplicationConfiguration,
                          customOptions: OptionMap): List[MetricsResult] = {

    log.info(s"Batch mode: Processing all JAR files in ${inputDirectory.getPath}...")
    var count = 0

    val results = inputDirectory
      .listFiles(new FilenameFilter {
        override def accept(dir: File, name: String): Boolean = name.toLowerCase.endsWith(".jar")
      })
      .flatMap{ file =>
        count += 1

        Try {
          val projectClasses = OPALProjectHelper.readClassesFromJarStream(new FileInputStream(file), file.toURI.toURL)
          val additionalClasses = appConfiguration
            .additionalClassesDir
            .flatMap(dir => OPALProjectHelper.readClassesFromFileStructure(new File(dir)).toOption)
            .getOrElse(List.empty)
          OPALProjectHelper.buildOPALProject(projectClasses.get, additionalClasses, appConfiguration.treatFilesAsLibrary, appConfiguration.excludeJreClasses)
        } match {
          case Success(project) =>
            log.debug(s"Successfully initialized OPAL project for ${file.getName}")
            analyses
              .map { analysis =>
                analysis.analyzeProject(project, customOptions) match {
                  case Success(values) =>
                    MetricsResult(analysis.analysisName, file, success = true, values)
                  case Failure(ex) =>
                    log.error(s"Unexpected failure while processing JAR file", ex)
                    MetricsResult.analysisFailed(analysis.analysisName, file)
                }
              }
              .toList
          case Failure(ex) =>
            log.error(s"Failure while initializing OPAL project for ${file.getName}", ex)
            List.empty
        }
      }
      .toList

    log.info(s"Batch mode: Done processing $count JAR files in ${inputDirectory.getName}")

    results
  }
}
