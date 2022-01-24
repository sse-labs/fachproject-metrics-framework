package org.tud.sse.metrics
package impl.group1

import analysis.{ClassFileAnalysis, MetricValue}
import input.CliParser.OptionMap

import org.opalj.br.ClassFile
import org.opalj.br.analyses.Project

import java.net.URL
import scala.util.Try
// ClassFileAnalysis is used to determine the inheritance tree between classes
class DepthOfInheritanceTreeAnalysis extends ClassFileAnalysis {

/**
 *  Counts supertypes for each class. Based on the Definition from "A Metrics Suite for Object Oriented Design" from Chidamber and Kemerer.
 *  "Definition: DIT = maximum length from node to root of the inheritance tree"
 * @author feho243
 * @param classFile class that the DIT metric is calculated from
 * @param project jar project file that includes the classes for the metric
 * @param customOptions custom settings for the Analysis (not needed for this Metric)
 * @return iterable MetricValue for all classes
 */
override def analyzeClassFile(classFile: ClassFile, project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try{
val supertypes = project.classHierarchy.allSupertypes(classFile.thisType).size
val className = classFile.thisType.fqn
List(MetricValue(className,this.analysisName,supertypes))
}

/**
 * The name for this analysis implementation. Named dit
 */
override def analysisName: String = "class.dit"
}