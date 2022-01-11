package org.tud.sse.metrics
package impl.group3

import analysis.{MetricValue, SingleFileAnalysis}
import input.CliParser.OptionMap

import org.opalj.br.analyses.Project

import java.io.File
import java.net.URL
import scala.util.Try

class LOCproAnalysis extends SingleFileAnalysis{

  /** calculates all class methods, from all line number table get each method body lines, then add 1 as method itself*/
  override def analyzeProject(project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try {

    log.info("Please implement the LOCpro metric here")

    var metricsResult =  0.0

    var lineCounter = 0

    project.allProjectClassFiles.foreach(
      c => {
        c.methodsWithBody.foreach(
          m =>
            if (m.body.exists(_.lineNumberTable.nonEmpty)) {
              lineCounter = lineCounter + m.body.get.lineNumberTable.get.lineNumbers.size + 1
            }
        )
      }

    )

    metricsResult = lineCounter

    List(MetricValue("File: ", this.analysisName, metricsResult))
  }

  /**
   * The name for this analysis implementation. Will be used to include and exclude analyses via CLI.
   */
  override def analysisName: String = "loc.pro"
}
