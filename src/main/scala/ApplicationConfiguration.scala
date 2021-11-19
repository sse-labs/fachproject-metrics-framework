package org.tud.sse.metrics

import analysis.NamedAnalysis
import application.SingleFileAnalysisCliParser
import input.CliParser
import input.CliParser.OptionMap

import org.slf4j.Logger

import java.io.File

class ApplicationConfiguration(val inputFilePath: String,
                               val treatFilesAsLibrary: Boolean,
                               val outFileOption: Option[String],
                               val opalLoggingEnabled: Boolean,
                               val batchModeEnabled: Option[Boolean],
                               val excludedAnalysesNames: List[String],
                               val includedAnalysesNames: List[String],
                               val excludeJreClasses: Boolean,
                               val additionalClassesDir: Option[String],
                               val loadAdditionalClassesAsInterface: Boolean) {

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

    if(additionalClassesDir.isDefined) {
      log.info(s"\t- Additional Classes Directory: ${additionalClassesDir.get}")
      log.info(s"\t- Load Additional Classes As Interfaces: $loadAdditionalClassesAsInterface")
    }

    log.info(s"\t- Exclude JRE classes: $excludeJreClasses")
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

    val additionalClassesDir = appOptions.get(CliParser.additionalClassesDirSymbol).map(_.toString)
    val loadAdditionalClassesAsInterfaces = appOptions.contains(CliParser.loadAdditionalClassesAsInterfacesSymbol)
    val jreClassesExcluded = appOptions.contains(CliParser.noJreClassesSymbol)

    new ApplicationConfiguration(appOptions(CliParser.inFileSymbol).toString,
      appOptions.contains(CliParser.isLibrarySymbol),
      appOptions.get(CliParser.outFileSymbol).map(_.toString),
      appOptions.contains(CliParser.enableOpalLoggingSymbol),
      if(isSingleFile) Some(batchModeEnabled) else None,
      excludedAnalysesNames,
      includedAnalysesNames,
      jreClassesExcluded,
      additionalClassesDir,
      loadAdditionalClassesAsInterfaces)
  }

  def fromOptionsSingleFile(appOptions: OptionMap): ApplicationConfiguration = fromOptions(appOptions, isSingleFile = true)

  def fromOptionsMultiFile(appOptions: OptionMap): ApplicationConfiguration = fromOptions(appOptions, isSingleFile = false)
}
