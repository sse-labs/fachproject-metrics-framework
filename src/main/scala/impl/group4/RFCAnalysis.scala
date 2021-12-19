package org.tud.sse.metrics
package impl.group4

import analysis.{ClassFileAnalysis, MetricValue}
import input.CliParser.OptionMap

import org.opalj.br.ClassFile
import org.opalj.br.analyses.Project

import java.net.URL
import scala.util.Try

class RFCAnalysis extends ClassFileAnalysis {

  /**
   * Calculates the value of the RFC Metric by adding the number of methods in the class to the number of method invocations in each method
   */
  override def analyzeClassFile(classFile: ClassFile, project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try {
    var rfc = classFile.methods.length
    classFile.methods.foreach(m=> {
      if(m.body.isDefined){m.body.get.instructions.foreach(i => {
        if(i!=null) {
          if (i.isMethodInvocationInstruction) {
            rfc = rfc + 1
          }
        }
      })
    }
    })

    List(MetricValue(classFile.fqn, "class.rfc", rfc))
  }
  /**
   * The name for this analysis implementation.
   */
  override def analysisName: String = "rfc"
}
