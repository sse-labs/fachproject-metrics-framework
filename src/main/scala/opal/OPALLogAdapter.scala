package org.tud.sse.metrics
package opal

import org.opalj.log.{DevNullLogger, Fatal, GlobalLogContext, Info, LogContext, LogMessage, OPALLogger, StandardLogContext, Warn}
import org.slf4j.LoggerFactory

object OPALLogAdapter extends OPALLogger {

  private val internalLogger = LoggerFactory.getLogger("opal-logger")
  private var opalLoggingEnabled = false;

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

  def getAnalysisLogger() = if(opalLoggingEnabled) consoleLogger else emptyLogger

  def setOpalLoggingEnabled(enabled: Boolean): Unit = {
    opalLoggingEnabled = enabled

    if(!OPALLogger.isUnregistered(analysisLogContext))
      OPALLogger.unregister(analysisLogContext)

    analysisLogContext = new StandardLogContext()

    OPALLogger.register(analysisLogContext,
      if(enabled) consoleLogger else emptyLogger)
  }

}
