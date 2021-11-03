package org.tud.sse.metrics
package analysis
import input.CliParser.OptionMap

import org.opalj.br.Method
import org.opalj.br.analyses.Project

import java.net.URL
import scala.util.{Success, Try}

trait MethodAnalysis extends SingleFileAnalysis {

  override final def analyzeProject(project: Project[URL],
                              customOptions: OptionMap): Try[Iterable[MetricValue]] = {
    val methodResults = project
      .allMethods
      .map(m => analyzeMethod(m, project, customOptions))
      .toList

    if(methodResults.exists(_.isFailure)){
      methodResults.find(_.isFailure).get
    } else {
      Success(methodResults.flatMap(_.get))
    }

  }

  def analyzeMethod(method: Method, project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]]

}
