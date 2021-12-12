package impl.group1

import java.net.URL

import org.opalj.br.ClassFile
import org.opalj.br.analyses.Project
import org.tud.sse.metrics.analysis.{ClassFileAnalysis, MetricValue}
import org.tud.sse.metrics.input.CliParser.OptionMap

import scala.util.Try

class NumberOfChildrenAnalysis extends ClassFileAnalysis {

  /**
   *
   * @param classFile class that the metric is calculated from
   * @param project that contains the class
   * @param customOptions settings for the Analysis
   * @return iterable MetricValue
   */
  override def analyzeClassFile(classFile: ClassFile, project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try{
    val subCount = project.classHierarchy.directSubtypesCount(classFile.thisType)
    List(MetricValue(classFile.getClass.getName,this.analysisName,subCount))
  }

  /**
   * The name for this analysis implementation. Will be used to include and exclude analyses via CLI.
   */
  override def analysisName: String = "class.NumberOfChildren"
}
