package org.tud.sse.metrics
package testutils

import analysis.{MetricsResult, MultiFileAnalysis, SingleFileAnalysis}

import org.scalatest.Matchers.fail
import org.tud.sse.metrics.application.{MultiFileAnalysisApplication, SingleFileAnalysisApplication, SingleFileAnalysisCliParser}
import org.tud.sse.metrics.input.CliParser
import org.tud.sse.metrics.input.CliParser.OptionMap

import java.io.File

object AnalysisTestUtils {

  private def appConfigToOptions(input: File, appConfig: ApplicationConfiguration): OptionMap = {

    var options: OptionMap = Map[Symbol, Any](
      CliParser.inFileSymbol -> input.getAbsolutePath,
      CliParser.isLibrarySymbol -> appConfig.treatFilesAsLibrary,
      CliParser.enableOpalLoggingSymbol -> appConfig.opalLoggingEnabled,
      CliParser.noJreClassesSymbol -> appConfig.excludeJreClasses,
      CliParser.loadAdditionalClassesAsInterfacesSymbol -> appConfig.loadAdditionalClassesAsInterface
    )

    if(appConfig.batchModeEnabled.isDefined)
      options = options ++ Map(SingleFileAnalysisCliParser.batchModeSymbol -> appConfig.batchModeEnabled.get)

    if(appConfig.additionalClassesDir.isDefined)
      options = options ++ Map(CliParser.additionalClassesDirSymbol -> appConfig.additionalClassesDir.get)

    options
  }

  /**
   * This method can be used to run a SingleFileAnalysis without having to pass CLI parameters as strings, and without
   * writing any results to file.
   * @param analysis The analysis to execute
   * @param input The input file to execute the analysis on. May be a folder if batchMode is set to true
   * @param batchMode Specifies whether the input file is interpreted as a single JAR / JMOD or a folder of JARs / JMODs
   * @param isLibrary Specifies whether the input JARs are interpreted as library files. Defaults to false
   * @param analysisOptions Custom analysis parameter map. Will be passed to the analysis after application arguments are validate. Defaults to empty
   * @return List of MetricsResult objects calculated by the analysis.
   *
   * @note This method may invoke fail() if the given parameter combinations are not valid
   */
  def runSingleFileAnalysis(analysis: SingleFileAnalysis, input: File, batchMode: Boolean, isLibrary: Boolean = false,
                            analysisOptions: OptionMap = Map.empty): List[MetricsResult] = {

    val app = new SingleFileAnalysisApplication {
      override protected val registeredAnalyses: Seq[SingleFileAnalysis] = Seq(analysis)
    }

    val options: OptionMap = Map[Symbol, Any](
      CliParser.inFileSymbol -> input.getAbsolutePath,
      CliParser.isLibrarySymbol -> isLibrary,
      SingleFileAnalysisCliParser.batchModeSymbol -> batchMode
    )

    app.validateApplicationOptions(options) match {
      case Some(appConfig) =>
        app.calculateResults(appConfig, analysisOptions)
      case None =>
        fail("Failed to validate application options while running analysis")
    }
  }

  /**
   * This method can be used to run a SingleFileAnalysis without having to pass CLI parameters as strings, and without
   * writing any results to file.
   * @param analysis The analysis to execute
   * @param input The input file to execute the analysis on. May be a folder if batchMode is set to true
   * @param appConfig The application configuration to use for the analysis run
   * @param analysisOptions Custom analysis parameter map. Will be passed to the analysis after application arguments are validate. Defaults to empty
   * @return List of MetricsResult objects calculated by the analysis.
   *
   * @note This method may invoke fail() if the given parameter combinations are not valid
   */
  def runSingleFileAnalysis(analysis: SingleFileAnalysis, input: File, appConfig: ApplicationConfiguration,
                            analysisOptions: OptionMap): List[MetricsResult] = {
    val app = new SingleFileAnalysisApplication {
      override protected val registeredAnalyses: Seq[SingleFileAnalysis] = Seq(analysis)
    }

    val options: OptionMap = appConfigToOptions(input, appConfig)

    app.validateApplicationOptions(options) match {
      case Some(appConfig) =>
        app.calculateResults(appConfig, analysisOptions)
      case None =>
        fail("Failed to validate application options while running analysis")
    }
  }

  /**
   * This method can be used to run a MultiFileAnalysis without having to pass CLI parameters as strings, and without
   * writing any results to file.
   * @param analysisBuilder A function that builds the analysis. The function parameter of type File is the directory the analysis is working on
   * @param input The input file to execute the analysis on. Must be a folder containing JAR / JMOD files.
   * @param isLibrary Specifies whether the input JARs / JMODs are interpreted as library files. Defaults to false
   * @param analysisOptions Custom analysis parameter map. Will be passed to the analysis after application arguments are validate. Defaults to empty
   * @return List of MetricsResult objects calculated by the analysis.
   *
   * @note This method may invoke fail() if the given parameter combinations are not valid
   */
  def runMultiFileAnalysis[T](analysisBuilder: File => MultiFileAnalysis[T], input: File,  isLibrary: Boolean = false,
                              analysisOptions: OptionMap = Map.empty): List[MetricsResult] = {

    val app = new MultiFileAnalysisApplication {
      override protected def buildAnalyses(jarDirectory: File): Seq[MultiFileAnalysis[_]] = Seq(analysisBuilder(jarDirectory))
    }

    val options: OptionMap = Map[Symbol, Any](
      CliParser.inFileSymbol -> input.getAbsolutePath,
      CliParser.isLibrarySymbol -> isLibrary
    )

    app.validateApplicationOptions(options) match {
      case Some(appConfig) =>
        app.calculateResults(appConfig, analysisOptions)
      case None =>
        fail("Failed to validate application options while running analysis")
    }

  }

  /**
   * This method can be used to run a MultiFileAnalysis without having to pass CLI parameters as strings, and without
   * writing any results to file.
   * @param analysisBuilder A function that builds the analysis. The function parameter of type File is the directory the analysis is working on
   * @param input The input file to execute the analysis on. Must be a folder containing JAR / JMOD files.
   * @param appConfig The application configuration to use for the analysis run
   * @param analysisOptions Custom analysis parameter map. Will be passed to the analysis after application arguments are validate.
   * @return List of MetricsResult objects calculated by the analysis.
   *
   * @note This method may invoke fail() if the given parameter combinations are not valid
   */
  def runMultiFileAnalysis[T](analysisBuilder: File => MultiFileAnalysis[T], input: File, appConfig: ApplicationConfiguration,
                              analysisOptions: OptionMap): List[MetricsResult] = {

    val app = new MultiFileAnalysisApplication {
      override protected def buildAnalyses(jarDirectory: File): Seq[MultiFileAnalysis[_]] = Seq(analysisBuilder(jarDirectory))
    }

    val options: OptionMap = appConfigToOptions(input, appConfig)

    app.validateApplicationOptions(options) match {
      case Some(appConfig) =>
        app.calculateResults(appConfig, analysisOptions)
      case None =>
        fail("Failed to validate application options while running analysis")
    }

  }

}
