package org.tud.sse.metrics
package multifileanalysis

import org.opalj.br.analyses.Project
import org.slf4j.{Logger, LoggerFactory}
import input.CliParser.OptionMap

import java.io.File
import java.net.URL
import scala.collection.mutable
import scala.util.Try

abstract class MultiFileAnalysis[T](directory: File, customOptions: OptionMap) {

  protected val log: Logger = LoggerFactory.getLogger(this.getClass)

  val analysisResultsPerFile: mutable.Map[File, Try[T]] = mutable.HashMap[File, Try[T]]()

  def initialize(): Unit = {
    log.info("Analysis initialized")
  }

  def produceAnalysisResultForJAR(project: Project[URL], lastResult: Option[T]): Try[T]

  def produceMetricValues(): List[JarFileMetricsResult]

}
