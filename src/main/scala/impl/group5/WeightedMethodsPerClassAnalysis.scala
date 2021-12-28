package org.tud.sse.metrics
package impl.group5

import analysis.{ClassFileAnalysis, MetricValue, SingleFileAnalysis}
import input.CliParser.OptionMap

import org.opalj.br.ClassFile
import org.opalj.br.analyses.Project
import org.opalj.br.instructions.{ALOAD, FieldAccess}

import java.net.URL
import scala.collection.mutable.ListBuffer
import scala.util.Try

class WeightedMethodsPerClassAnalysis extends ClassFileAnalysis{


  override def analyzeClassFile(classFile: ClassFile, project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try {


    val methods = classFile.methodsWithBody
    var WMC = 0
    val rlist = new ListBuffer[MetricValue]()
    while (methods.hasNext) {
      val method = methods.next()
      val body = method.body
      //McAbe complexity von Gruppe 4
      var e = 0
      var n = 0
      body.get.instructions.foreach { instruction =>
        Try {
          if (instruction.isCompoundConditionalBranchInstruction) {
            e = e + instruction.asCompoundConditionalBranchInstruction.jumpOffsets.size
          }
          else if (instruction.isSimpleConditionalBranchInstruction) {
            e = e + 2
          }
          else {
            if (!instruction.isReturnInstruction) e = e + 1
          }
          n = n + 1
        }
      }
      //Complexity for current method
      val complexity = e - n + 2
      WMC = WMC + complexity

    }
    rlist += MetricValue("WMC von "+classFile.thisType.fqn, this.analysisName, WMC)


  }
  override def analysisName: String = "wmc"
}
