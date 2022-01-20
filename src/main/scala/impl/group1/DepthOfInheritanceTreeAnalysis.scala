package org.tud.sse.metrics
package impl.group1

import analysis.{MetricValue, SingleFileAnalysis}
import input.CliParser.OptionMap

import org.opalj.br.analyses.Project

import java.net.URL
import scala.collection.mutable.ListBuffer
import scala.util.Try


class DepthOfInheritanceTreeAnalysis extends SingleFileAnalysis {

override def analyzeProject(project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try {
val list = new ListBuffer[MetricValue]()
project.allProjectClassFiles.foreach(c => {
val supertypes = project.classHierarchy.directSupertypes(c.thisType).size
list += MetricValue(c.fqn, this.analysisName, supertypes)
})
list.toList
}

override def analysisName: String = "class.inheritancetree"
}