package org.tud.sse.metrics
package impl.group2

import java.net.URL
import scala.util.Try

import org.opalj.br.Method
import org.opalj.br.analyses.Project

import analysis.{MethodAnalysis, MetricValue}
import input.CliParser.OptionMap


class ClassesReferencedAnalysis extends MethodAnalysis {

  override def analysisName: String = "methods.CRef"

  override def analyzeMethod(method: Method, project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try {
    if(project.isProjectType(method.classFile.thisType)) {
      List(MetricValue(method.fullyQualifiedSignature, this.analysisName, 0))
    } else {
      List.empty
    }
  }
}
