package org.tud.sse.metrics
package impl.group5

import analysis.{MethodAnalysis, MetricValue}
import input.CliParser.OptionMap
import org.opalj.br.Method
import org.opalj.br.analyses.Project
import java.net.URL
import scala.util.Try

/**
 * Die Klasse NumberOfLoopsAnalysis stellt die impliktion  der Metrik Number of Loops (Loop) da.
 * Diese Metrik zählt für jede Methode die anzahl der Schleifen und gibt sie aus.
 *
 */

class NumberOfLoopsAnalysis extends MethodAnalysis {


  /**
   * Die Methode führt die Metrik Loop aus.
   * @param method Ist ein Interface von Projekt
   * @param project Bekommt die vom Framework gelesende jar file in einer Präsedationsform
   * @param customOptions Einstell möglichkeiten der Analyze
   * @return Das Ergebniss wird in der Liste für die ausgabe gespeichert
   */
  override def analyzeMethod(method: Method, project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try {

      if (project.isProjectType(method.classFile.thisType)) {
        val body = method.body
        if (body.nonEmpty) {
        val inset = body.get
        val singleIn = inset.instructions
        var loops = 0
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
        List(MetricValue(method.fullyQualifiedSignature, this.analysisName, loops))
      }
    }
      List.empty
  }



  /**
   * The name for this analysis implementation. Will be used to include and exclude analyses via CLI.
   */
  override def analysisName: String = "methods.loop"

}