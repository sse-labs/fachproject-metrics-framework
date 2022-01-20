package org.tud.sse.metrics
package analysis

import input.CliParser.OptionMap

import org.opalj.br.{ClassFile, ObjectType}
import org.opalj.br.analyses.Project

import java.net.URL
import scala.util.{Success, Try}

trait ClassFileAnalysis extends SingleFileAnalysis {

  override final def analyzeProject(project: Project[URL],
                                    customOptions: OptionMap): Try[Iterable[MetricValue]] = {
    val classResults = project
      .allProjectClassFiles
      .map( classFile => analyzeClassFile(classFile, project, customOptions))

    if(classResults.exists(_.isFailure)){
      classResults.find(_.isFailure).get
    } else {
      Success(classResults.flatMap(_.get))
    }

  }

  def analyzeClassFile(classFile: ClassFile, project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]]

}
