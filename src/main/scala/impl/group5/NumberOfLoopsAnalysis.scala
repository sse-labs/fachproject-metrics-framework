package org.tud.sse.metrics
package impl.group5

import analysis.{ClassFileAnalysis, MetricValue}
import input.CliParser.OptionMap

import org.opalj.br.{ClassFile, Method}
import org.opalj.br.analyses.Project

import java.net.URL
import scala.collection.mutable.ListBuffer
import scala.util.Try

/**
 * Die Klasse NumberOfLoopsAnalysis stellt die impliktion  der Metrik Number of Loops (Loop) da.
 * Diese Metrik zählt für jede Methode die anzahl der Schleifen und gibt sie aus.
 *
 */

class NumberOfLoopsAnalysis extends ClassFileAnalysis {


  /**
   * Die Methode führt die Metrik Loop aus.
   * @param classFile Ist ein Interface von Projekt
   * @param project Bekommt die vom Framework gelesende jar file in einer Präsedationsform
   * @param customOptions Einstell möglichkeiten der Analyze
   * @return Das Ergebniss wird in der Liste für die ausgabe gespeichert
   */
  override def analyzeClassFile(classFile: ClassFile, project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try {

    val methods= classFile.methodsWithBody
    var classloops = 0
    var rlist = new ListBuffer[MetricValue]
    while(methods.hasNext) {
      val method = methods.next()
      val body = method.body
      var loops = 0
      if (body.nonEmpty) {
        val inset = body.get
        val singleIn = inset.instructions
        for (code <- singleIn) {
          // Im Array vom Framework können leere Felder sein, die mit null belegt sind
          if (code != null) {
            //opcode von goto ist 167
            if (code.opcode == 167) {
              val gotoInstruction = code.asGotoInstruction
              if (gotoInstruction.branchoffset < 0) {
                loops += 1
              }

            }
          }
        }


      }
      classloops += loops
      rlist +=MetricValue(method.fullyQualifiedSignature, this.analysisName, loops)
      println(MetricValue(method.fullyQualifiedSignature, this.analysisName, loops))
    }
    println(MetricValue(classFile.fqn, this.analysisName, classloops))
    List.empty




  }



  /**
   * The name for this analysis implementation. Will be used to include and exclude analyses via CLI.
   */
  override def analysisName: String = "methods.loop"

}