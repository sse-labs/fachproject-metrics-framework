package org.tud.sse.metrics
package impl.demo

import analysis.{MethodAnalysis, MetricValue}
import input.CliParser.OptionMap

import org.opalj.br.Method
import org.opalj.br.analyses.Project

import java.net.URL
import scala.util.Try

class MethodSizeAnalysis extends MethodAnalysis{
  override def analyzeMethod(method: Method, project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try{
    if(project.isProjectType(method.classFile.thisType)){
      method.body.map(c => c.codeSize) match {
        case Some(codeSize) =>
          List(MetricValue(method.fullyQualifiedSignature, this.analysisName, codeSize))
        case None =>
          List.empty
      }
    } else {
      List.empty
    }


  }

  /**
   * The name for this analysis implementation. Will be used to include and exclude analyses via CLI.
   */
  override def analysisName: String = "methods.codesize"
}
