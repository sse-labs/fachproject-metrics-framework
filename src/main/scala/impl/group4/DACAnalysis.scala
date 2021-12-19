package org.tud.sse.metrics
package impl.group4

import analysis.{ClassFileAnalysis, MetricValue}
import input.CliParser.OptionMap

import org.opalj.br.ClassFile
import org.opalj.br.analyses.Project

import java.net.URL
import scala.util.Try

class DACAnalysis extends ClassFileAnalysis {


  /**
   * Calculates the value of the DAC metric by counting reference type fields in the class (see https://ieeexplore.ieee.org/document/748920)
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
