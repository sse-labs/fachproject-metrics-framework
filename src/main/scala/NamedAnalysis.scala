package org.tud.sse.metrics

import org.slf4j.{Logger, LoggerFactory}

trait NamedAnalysis {

  /**
   * The logger for this instance
   */
  protected val log: Logger = LoggerFactory.getLogger(this.getClass)

  /**
   * The name for this analysis implementation. Will be used to include and exclude analyses via CLI.
   */
  def analysisName: String

  /**
   * This method is being called by an enclosing application after this analysis
   * is initialized, but before any JAR files are being processed. It can be used to initialize
   * custom data structures.
   */
  def initialize(): Unit = {
    log.debug("Analysis initialized")
  }


}
