package org.tud.sse.metrics
package opal

import org.opalj.log.{Fatal, GlobalLogContext, Info, Level, LogContext, LogMessage, OPALLogger, Warn}
import org.slf4j.LoggerFactory

/**
 * Custom OPALLogger implementation that forwards messages to the internal logger
 *
 * @author Johannes Düsing
 */
object OPALLogAdapter extends OPALLogger {

  private val internalLogger = LoggerFactory.getLogger("opal-logger")
  private var minLogLevel: Level = Warn

  private var opalLoggingEnabled = false

  OPALLogger.updateLogger(GlobalLogContext, this)


  def setMinLogLevel(level: Level): Unit = {
    this.minLogLevel = level
  }

  override def log(message: LogMessage)(implicit ctx: LogContext): Unit = {

    if(this.opalLoggingEnabled){
      message.level match {
        case Info if minLogLevel == Info =>
          internalLogger.info(message.message)
        case Warn if minLogLevel == Info || minLogLevel == Warn =>
          internalLogger.warn(message.message)
        case org.opalj.log.Error if minLogLevel == Info || minLogLevel == Warn || minLogLevel == org.opalj.log.Error =>
          internalLogger.error(message.message)
        case Fatal =>
          internalLogger.error(message.message)
      }
    }
  }

  /**
   * Method that enables or disables OPAL logging entirely. If enabled, all log levels of OPAL will
   * be forwarded to the internal analysis logger. If disabled, all logging output of OPAL will be
   * suppressed.
   *
   * @param enabled Parameter indicating whether to enable OPAL logging
   */
  def setOpalLoggingEnabled(enabled: Boolean): Unit = {
    opalLoggingEnabled = enabled
  }

}
