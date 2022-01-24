package org.tud.sse.metrics
package impl.group3

import analysis.{MetricValue, SingleFileAnalysis}
import input.CliParser.OptionMap

import org.opalj.br.analyses.Project

import java.io.File
import java.net.URL
import scala.util.Try

class LOCproAnalysis extends SingleFileAnalysis{

  /** calculates all class methods, from all line number table get each method body lines, then add 1 as method itself
   * for void method which ist without return value will not add 1 (as in review suggested)
   *
   */
  override def analyzeProject(project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try {

    var lineCounter = 0

    project.allProjectClassFiles.foreach(
      c => {
        c.methodsWithBody.foreach(
          m =>
            if (m.body.exists(_.lineNumberTable.nonEmpty)) {

              if(m.returnType != null){

                lineCounter = lineCounter + m.body.get.lineNumberTable.get.lineNumbers.size + 1
              }

              else lineCounter = lineCounter + m.body.get.lineNumberTable.get.lineNumbers.size

            } else lineCounter = lineCounter + 0
        )
      }

    )

    List(MetricValue("File: ", this.analysisName, lineCounter))
  }

  /**
   * The name for this analysis implementation. Will be used to include and exclude analyses via CLI.
   */
  override def analysisName: String = "loc.pro"
}
