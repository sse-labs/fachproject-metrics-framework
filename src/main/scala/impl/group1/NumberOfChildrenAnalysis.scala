package impl.group1

import java.net.URL

import org.opalj.br.ClassFile
import org.opalj.br.analyses.Project
import org.tud.sse.metrics.analysis.{ClassFileAnalysis, MetricValue}
import org.tud.sse.metrics.input.CliParser.OptionMap

import scala.util.Try

class NumberOfChildrenAnalysis extends ClassFileAnalysis {

  /**
   *  Counts the direct Children for each class. Based on the Definition from "A Metrics Suite for Object Oriented Design" from Chidamber and Kemerer.
   *  "Definition: NOC = number of immediate subclasses subordinated to a class in the class hierarchy."
   *
   * @param classFile class that the NOC metric is calculated from
   * @param project jar project file that includes the classes for the metric
   * @param customOptions custom settings for the Analysis (not needed for the NOC Metric)
   * @return iterable MetricValue for all classes
   */
  override def analyzeClassFile(classFile: ClassFile, project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try{

    val directChildrenCount = project.classHierarchy.directSubtypesCount(classFile.thisType)
    val className = classFile.thisType.fqn

    List(MetricValue(className,this.analysisName,directChildrenCount))
  }

  /**
   * The name for this analysis implementation. Will be used to include and exclude analyses via CLI.
   */
  override def analysisName: String = "class.NumberOfChildren"
}
