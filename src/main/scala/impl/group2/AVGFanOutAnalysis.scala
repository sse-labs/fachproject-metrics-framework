package org.tud.sse.metrics
package impl.group2

import java.net.URL
import scala.util.Try

import org.opalj.br.ClassFile
import org.opalj.br.analyses.Project

import analysis.{ClassFileAnalysis, MetricValue}
import input.CliParser.OptionMap


/*
 * Implementation of metric "Average Fan Out" (AVGFanOut)
 *
 * This metric builds the average over the "fan out" of the methods
 * for a given class.
 *
 * See FanOutAnalysis for metric details.
 *
 */
class AVGFanOutAnalysis extends ClassFileAnalysis {

  override def analysisName: String = "class.AVGFanOut"

  override def analyzeClassFile(classFile: ClassFile, project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try {
    val methodFanOuts = classFile.methods.map(m => FanOut.calc(m))
    val avg =
      if (classFile.methods.size > 0)
        methodFanOuts.sum / classFile.methods.size
      else 0

    List(MetricValue(classFile.thisType.fqn, this.analysisName, avg))
  }
}
