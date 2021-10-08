package org.tud.sse.metrics

import org.slf4j.Logger
import org.tud.sse.metrics.input.CliParser
import org.tud.sse.metrics.input.CliParser.OptionMap

import java.io.File

class ApplicationConfiguration(val inputFilePath: String,
                               val treatFilesAsLibrary: Boolean,
                               val outFileOption: Option[String],
                               val opalLoggingEnabled: Boolean,
                               val batchModeEnabled: Option[Boolean],
                               val excludedAnalysesNames: List[String],
                               val includedAnalysesNames: List[String]) {

  def includeSpecificationsApply(): Boolean = includedAnalysesNames.nonEmpty

  def excludeSpecificationsApply(): Boolean = includedAnalysesNames.isEmpty && excludedAnalysesNames.nonEmpty

  def allAnalysesApply(): Boolean = includedAnalysesNames.isEmpty && excludedAnalysesNames.isEmpty

  val inputFile: File = new File(inputFilePath)

  def logInfo(log: Logger): Unit = {
    log.info("Running analysis with parameters:")
    log.info(s"\t- Input: $inputFilePath")

    if(batchModeEnabled.isDefined)
      log.info(s"\t- Batch Mode Enabled: ${batchModeEnabled.get}")

    log.info(s"\t- Output File: ${outFileOption.getOrElse("None")}")
    log.info(s"\t- Treat JAR as Library: $treatFilesAsLibrary")

    if(includeSpecificationsApply())
      log.info(s"\t- Included Analyses: ${includedAnalysesNames.mkString(",")}")
    else if(excludeSpecificationsApply())
      log.info(s"\t- Excluded Analyses: ${excludedAnalysesNames.mkString(",")}")

    log.info(s"\t- OPAL Logging Enabled: $opalLoggingEnabled")
  }

  def getActiveAnalysisNamesFor(analyses: TraversableOnce[NamedAnalysis]): List[String] = {
    analyses
      .map(_.analysisName)
      .filter { name =>
        if(includeSpecificationsApply()) includedAnalysesNames.contains(name)
        else if(excludeSpecificationsApply()) !excludedAnalysesNames.contains(name)
        else true
      }
      .toList
  }

}

object ApplicationConfiguration {

  private def fromOptions(appOptions: OptionMap, isSingleFile: Boolean): ApplicationConfiguration = {

    val excludedAnalysesNames = appOptions
      .get(CliParser.excludeAnalysisSymbol)
      .map(_.asInstanceOf[List[String]])
      .getOrElse(List.empty)

    val includedAnalysesNames = appOptions
      .get(CliParser.includeAnalysisSymbol)
      .map(_.asInstanceOf[List[String]])
      .getOrElse(List.empty)

    val batchModeEnabled = appOptions.contains(SingleFileAnalysisCliParser.batchModeSymbol)

    new ApplicationConfiguration(appOptions(CliParser.inFileSymbol).toString,
      appOptions.contains(CliParser.isLibrarySymbol),
      appOptions.get(CliParser.outFileSymbol).map(_.toString),
      appOptions.contains(CliParser.enableOpalLoggingSymbol),
      if(isSingleFile) Some(batchModeEnabled) else None,
      excludedAnalysesNames,
      includedAnalysesNames)
  }

  def fromOptionsSingleFile(appOptions: OptionMap): ApplicationConfiguration = fromOptions(appOptions, isSingleFile = true)

  def fromOptionsMultiFile(appOptions: OptionMap): ApplicationConfiguration = fromOptions(appOptions, isSingleFile = false)
}
