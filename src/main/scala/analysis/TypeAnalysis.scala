package org.tud.sse.metrics
package analysis

import input.CliParser.OptionMap

import org.opalj.br.ObjectType
import org.opalj.br.analyses.Project

import java.net.URL
import scala.util.{Success, Try}

trait TypeAnalysis extends SingleFileAnalysis {

  override final def analyzeProject(project: Project[URL],
                                    customOptions: OptionMap): Try[Iterable[MetricValue]] = {
    val typeResults = project
      .allProjectClassFiles
      .map(_.thisType)
      .map( typeObj => analyzeType(typeObj, project, customOptions))

    if(typeResults.exists(_.isFailure)){
      typeResults.find(_.isFailure).get
    } else {
      Success(typeResults.flatMap(_.get))
    }

  }

  def analyzeType(typeObj: ObjectType, project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]]

}
