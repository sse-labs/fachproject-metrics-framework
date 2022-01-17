package org.tud.sse.metrics
package impl.group4

import analysis.{ClassFileAnalysis, MetricValue}
import input.CliParser.OptionMap

import org.opalj.br.ClassFile
import org.opalj.br.analyses.Project

import java.net.URL
import scala.util.Try

/**
 * DACAnalysis implements the DAC metric as described in https://ieeexplore.ieee.org/document/748920
 */

class DACAnalysis extends ClassFileAnalysis {

  /**
   * This method calculates the value of the DAC metric by counting reference type fields in the class.
   *
   * @param classFile     is a class in the project.
   * @param project       Fully initialize OPAL project representing the JAR file under analysis.
   * @param customOptions Custom analysis options taken from the CLI. Can be used to modify behavior
   *                      of the analysis via command-line.
   * @return Try[Iterable[MetricValue]]  object holding the intermediate result, if successful.
   */
  override def analyzeClassFile(classFile: ClassFile, project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try {
    var metric = 0
    classFile.fields.foreach(f=>{
      if (f.fieldType.isReferenceType) metric=metric+1
    })
    List(MetricValue(classFile.fqn, "class.dac", metric))
  }

  override def analysisName: String = "dac"
}
