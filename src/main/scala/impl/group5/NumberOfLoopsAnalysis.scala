package org.tud.sse.metrics
package impl.group5

import analysis.{ClassFileAnalysis, MetricValue}
import input.CliParser.OptionMap

import org.opalj.br.ClassFile
import org.opalj.br.analyses.Project

import java.net.URL
import scala.collection.mutable.ListBuffer
import scala.util.Try
import scala.util.control.Breaks.break

/**
 * Die Klasse NumberOfLoopsAnalysis stellt die Implementierung  der Metrik Number of Loops (Loop) da.
 * Diese Metrik zählt für jede Methode die Anzahl der Schleifen und gibt sie aus.
 *
 * Die Default Einstellung ist, dass nur die Methoden ausgeben werden, in denen sich Loops befinden. Die anderen Methoden in denen Loops 0 ist werden weggelassen
 *
 * Optional CLI argument:
 *  - --no-methode Es werden nur Klassen ausgeben
 *  - --out-all Es wird alles ausgeben
 *  - --no-class Es werden nur Methoden ausgeben
 *  - --zeroloopclasses Gibt nur Klassen mit Loops aus.
 */

class NumberOfLoopsAnalysis extends ClassFileAnalysis {

  private val noMethodenSymbol: Symbol = Symbol("no-methoden")
  private val outAllSymbol: Symbol = Symbol("out-all")
  private val noClassSymbol: Symbol = Symbol("no-class")
  private  val nozeroLoopsClassesSymbol: Symbol = Symbol("no-zeroloopclasses")

  /**
   * Die Methode führt die Metrik Loop aus.
   * @param classFile Ist ein Interface von Projekt
   * @param project Bekommt die vom Framework gelesende jar file in einer Präsentationsform
   * @param customOptions Einstellungs Möglichkeiten der Analyse
   * @return Das Ergebniss wird in der Liste für die Ausgabe gespeichert
   */
  override def analyzeClassFile(classFile: ClassFile, project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try {

    val outAllMethode = customOptions.contains(outAllSymbol)
    val noMethoden = customOptions.contains(noMethodenSymbol)
    val noClass = customOptions.contains(noClassSymbol)
    val noZeroLoopsClasses = customOptions.contains(nozeroLoopsClassesSymbol)

    if(noClass &&(noMethoden||noZeroLoopsClasses))
      {
        log.error("Fehler! Argumente sind inkompatibel")
      }

    val methods= classFile.methodsWithBody
    var classloops = 0
    val rlist = new ListBuffer[MetricValue]()

    while(methods.hasNext) {
      val method = methods.next()
      val body = method.body
      var loops = 0
      val line = new ListBuffer[Int]()
      if (body.nonEmpty) {
        val code = body.get
        val instructions = code.instructions
        var instructionPosition = 0
        for (instruction <- instructions) {
          // Im Array vom Framework können leere Felder sein, die mit null belegt sind
          if (instruction != null) {
            val isInstruction = instruction.opcode match{
              case 159 => instruction.asIFICMPInstruction
              case 160 => instruction.asIFICMPInstruction
              case 161 => instruction.asIFICMPInstruction
              case 162 => instruction.asIFICMPInstruction
              case 163 => instruction.asIFICMPInstruction
              case 164 => instruction.asIFICMPInstruction
              case 167 => instruction.asGotoInstruction
              case _ => null
            }

            if(isInstruction !=null)
              {
                if(isInstruction.branchoffset<0)
                  {
                    //Überprüfe ob die Schleife schon gezählt wurde
                    //Bei if Anweisungen in einer Schleife, kann es bei der Code Optimierung vorkommen das goto nicht
                    // zum ende des Code blocks geht sondern zurück zum Schleifenrumpf. Das kommt vor wenn nach dem if block keine Anweisungen mehr stehen
                    val index = instructionPosition + isInstruction.branchoffset
                    if(!line.contains(index))
                    {

                      loops += 1
                      line +=index
                    }
                  }
              }

          }
          instructionPosition += 1
        }


      }
      classloops += loops

      if(loops>0 && !noMethoden) {
        rlist += MetricValue(method.fullyQualifiedSignature, this.analysisName, loops)
      }
      else if(outAllMethode)
        {
          rlist += MetricValue(method.fullyQualifiedSignature, this.analysisName, loops)
        }

    }

    if(!noClass)
    {
      if(noZeroLoopsClasses && classloops>0.0) {
        rlist += MetricValue("Class:" + classFile.thisType.fqn, this.analysisName, classloops)
      }
      else if(!noZeroLoopsClasses) rlist += MetricValue("Class:" + classFile.thisType.fqn, this.analysisName, classloops)
    }

    rlist.toList




  }



  /**
   * The name for this analysis implementation. Will be used to include and exclude analyses via CLI.
   */
  override def analysisName: String = "methods.loop"

}