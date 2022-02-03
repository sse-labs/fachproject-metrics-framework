package org.tud.sse.metrics
package input

import input.CliParser._

import scala.annotation.tailrec
import scala.util.{Failure, Try}

/**
 * Base class for all CLI Parsers, handles parsing of common input arguments. Also takes care
 * of separating application arguments and analysis arguments.
 *
 * @param usage Usage string that will be output if there is an error while parsing
 *
 * @author Johannes Düsing
 */
abstract class CliParser(usage: String) {

  /**
   * List of additional options. If set, any input matching --<option> <value> will be appended to
   * the applicationOptions while parsing.
   */
  val additionalOptions: List[String]

  /**
   * List of additional switches. If set, any input matching --option will be appended to the
   * applicationOptions while parsing.
   */
  val additionalSwitches: List[String]

  /**
   * Entry point that processes a list of strings and outputs the application arguments and analysis
   * arguments, if successful.
   * @param argList List of CLI arguments (split at whitespaces)
   * @return Try that, if successful, contains the tuple (applicationOptions, analysisOptions)
   */
  def parseArguments(argList: List[String]): Try[(OptionMap, OptionMap)] = {
    if(argList.isEmpty){
      Failure(new Exception(usage))
    } else {
      Try(nextOption(Map(), Map(), argList))
    }
  }

  private def isAdditionalOption(value: String) =
    value.startsWith("--") && additionalOptions.contains(value.substring(2))

  private def isAdditionalSwitch(value: String) =
    value.startsWith("--") && additionalSwitches.contains(value.substring(2))

  private def isCliArgument(value: String) = value.startsWith("--")

  @tailrec
  private def nextOption(appOptions: OptionMap, analysisOptions: OptionMap, list: List[String]) : (OptionMap, OptionMap) = {

    list match {
        // Terminate Recursion at Nil
      case Nil => (appOptions, analysisOptions)
        // Hardcoded options valid for all analyses
      case "--out-file" :: value :: tail =>
        nextOption(appOptions ++ Map(outFileSymbol -> value), analysisOptions, tail)
      case "--exclude-analysis" :: value :: tail if appOptions.contains(excludeAnalysisSymbol) =>
        val newAppOptions = appOptions.updated(excludeAnalysisSymbol,
          appOptions(excludeAnalysisSymbol).asInstanceOf[List[String]] ++ List(value))
        nextOption(newAppOptions, analysisOptions, tail)
      case "--exclude-analysis" :: value :: tail =>
        nextOption(appOptions ++ Map(excludeAnalysisSymbol -> List(value)), analysisOptions, tail)
      case "--include-analysis" :: value :: tail if appOptions.contains(includeAnalysisSymbol) =>
        val newAppOptions = appOptions.updated(includeAnalysisSymbol,
          appOptions(includeAnalysisSymbol).asInstanceOf[List[String]] ++ List(value))
        nextOption(newAppOptions, analysisOptions, tail)
      case "--include-analysis" :: value :: tail =>
        nextOption(appOptions ++ Map(includeAnalysisSymbol -> List(value)), analysisOptions, tail)
      case "--additional-classes-dir" :: value :: tail =>
        nextOption(appOptions ++ Map(additionalClassesDirSymbol -> value), analysisOptions, tail)
        // Hardcoded switches valid for all analyses
      case "--load-additional-classes-as-interfaces" :: tail =>
        nextOption(appOptions ++ Map(loadAdditionalClassesAsInterfacesSymbol -> true), analysisOptions, tail)
      case "--no-jre-classes" :: tail =>
        nextOption(appOptions ++ Map(noJreClassesSymbol -> true), analysisOptions, tail)
      case "--is-library" :: tail =>
        nextOption(appOptions ++ Map(isLibrarySymbol -> true), analysisOptions, tail)
      case "--opal-logging" :: tail =>
        nextOption(appOptions ++ Map(enableOpalLoggingSymbol -> true), analysisOptions, tail)
      case "--evaluate-performance" :: tail =>
        nextOption(appOptions ++ Map(evaluatePerformanceSymbol -> true), analysisOptions, tail)
        // The one unnamed argument is the input file -> Must be last
      case string :: Nil =>
        nextOption(appOptions ++ Map(inFileSymbol -> string), analysisOptions, list.tail)
        // Additional Options for the application
      case option :: value :: tail if isAdditionalOption(option) =>
        nextOption(appOptions ++ Map(Symbol(option.substring(2)) -> value), analysisOptions, tail)
        // Additional switches for the application
      case switch :: tail if isAdditionalSwitch(switch) =>
        nextOption(appOptions ++ Map(Symbol(switch.substring(2)) -> true), analysisOptions, tail)
        // Unknown options are passed as custom analysis arguments
      case option :: value :: tail if isCliArgument(option) && !isCliArgument(value) && tail != Nil =>
        nextOption(appOptions, analysisOptions ++ Map(Symbol(option.substring(2)) -> value), tail)
        // Unknown switches are passed as custom analysis arguments
      case switch :: tail if isCliArgument(switch) =>
        nextOption(appOptions, analysisOptions ++ Map(Symbol(switch.substring(2)) -> true), tail)
        // Error in all other cases
      case option :: _ =>
        throw new Exception("Unknown option that is not a switch: " + option)
    }
  }

}

object CliParser {
  type OptionMap = Map[Symbol, Any]

  val outFileSymbol: Symbol = Symbol("out-file")
  val inFileSymbol: Symbol = Symbol("infile")
  val isLibrarySymbol: Symbol = Symbol("is-library")
  val enableOpalLoggingSymbol: Symbol = Symbol("enabled-logging")
  val excludeAnalysisSymbol: Symbol = Symbol("exclude-analysis")
  val includeAnalysisSymbol: Symbol = Symbol("include-analysis")
  val noJreClassesSymbol: Symbol = Symbol("no-jre-classes")
  val additionalClassesDirSymbol: Symbol = Symbol("additional-classes-dir")
  val loadAdditionalClassesAsInterfacesSymbol: Symbol = Symbol("load-additional-classes-as-interfaces")
  val evaluatePerformanceSymbol: Symbol = Symbol("evaluate-performance")
}
