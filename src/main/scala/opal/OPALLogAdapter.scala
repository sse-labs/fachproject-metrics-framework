package org.tud.sse.metrics
package opal

import org.opalj.log.{DevNullLogger, Fatal, GlobalLogContext, Info, LogContext, LogMessage, OPALLogger, StandardLogContext, Warn}
import org.slf4j.LoggerFactory

/**
 * Custom OPALLogger implementation that forwards messages to the internal logger
 *
 * @author Johannes Düsing
 */
object OPALLogAdapter extends OPALLogger {

  private val internalLogger = LoggerFactory.getLogger("opal-logger")
  private var opalLoggingEnabled = false

  final val emptyLogger = DevNullLogger
  final val consoleLogger = OPALLogAdapter

  var analysisLogContext = new StandardLogContext()

  OPALLogger.register(analysisLogContext, emptyLogger)
  OPALLogger.updateLogger(GlobalLogContext, emptyLogger)


  override def log(message: LogMessage)(implicit ctx: LogContext): Unit = {
    message.level match {
      case Info =>
        internalLogger.info(message.message)
      case Warn =>
        internalLogger.warn(message.message)
      case org.opalj.log.Error =>
        internalLogger.error(message.message)
      case Fatal =>
        internalLogger.error(message.message)
    }
  }

  /**
   * Logger currently used by the analysis framework
   */
  val analysisLogger: OPALLogger = if(opalLoggingEnabled) consoleLogger else emptyLogger

  /**
   * Method that enables or disables OPAL logging entirely. If enabled, all log levels of OPAL will
   * be forwarded to the internal analysis logger. If disabled, all logging output of OPAL will be
   * suppressed.
   *
   * @param enabled Parameter indicating whether to enable OPAL logging
   */
  def setOpalLoggingEnabled(enabled: Boolean): Unit = {
    opalLoggingEnabled = enabled

    if(OPALLogger.isUnregistered(analysisLogContext))
      OPALLogger.register(analysisLogContext, analysisLogger)
    else
      OPALLogger.updateLogger(analysisLogContext, analysisLogger)
  }

}
