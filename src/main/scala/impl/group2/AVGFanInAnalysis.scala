package org.tud.sse.metrics
package impl.group2

import java.net.URL
import scala.util.Try

import org.opalj.br.analyses.Project

import input.CliParser.OptionMap
import analysis.{MetricValue, SingleFileAnalysis}


/*
 * Implementation of metric "Average Fan In" (AVGFanIn)
 *
 * The metric builds the average over the "fan in" of methods in a class.
 * The average is rounded to two places after the decimal separator.
 *
 * See FanInAnalysis for metric details.
 */
class AVGFanInAnalysis extends SingleFileAnalysis {

  override def analysisName: String = "class.AVGFanIn"

  override def analyzeProject(project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try {
    // get the FanIn value of all methods by executing the FanInAnalysis
    val methodFanIns = new FanInAnalysis().analyzeProject(project, customOptions).get

    // build avg for every class
    for (classFile <- project.allProjectClassFiles) yield {
      // get all FanIn values of methods belonging to current class
      val metrics = methodFanIns.filter(mv => classFile.methods.map(_.fullyQualifiedSignature).contains(mv.entityIdent))
      // calculate the average
      val avg =
        if(metrics.nonEmpty)
          metrics.map(_.metricValue).sum / metrics.size
        else 0
      // return the rounded result
      MetricValue(classFile.fqn, analysisName, round(avg))
    }
  }

  // round double to two digits after the separator
  private def round(x: Double): Double = BigDecimal(x).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
}
