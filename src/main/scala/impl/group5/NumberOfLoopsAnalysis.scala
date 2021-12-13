package org.tud.sse.metrics
package impl.group5

import analysis.{ClassFileAnalysis, MetricValue}
import input.CliParser.OptionMap

import org.opalj.br.ClassFile
import org.opalj.br.analyses.Project

import java.net.URL
import scala.collection.mutable.ListBuffer
import scala.util.Try

/**
 * Die Klasse NumberOfLoopsAnalysis stellt die Implementierung  der Metrik Number of Loops (Loop) da.
 * Diese Metrik zählt für jede Methode die Anzahl der Schleifen und gibt sie aus.
 *
 * Die Default Einstellung ist, dass nur die Methoden ausgeben werden, in denen sich Loops befinden. Die anderen Methoden in denen Loops 0 ist werden weggelassen
 *
 * Optional CLI argument:
 *  - --only-class Es wird nur die Anzahl an Loops in einer Klasse angezeigt und nicht für jede Methode
 *  - --out-all-loops Es werden alle Methoden ausgeben, auch die mit Loop 0.
 *  - --no-class Es wird nicht die gesamte Zahl der Loops einer Klasse ausgeben
 *  - --only-class-with-loops Gibt nur Klassen mit Loops aus.
 */

class NumberOfLoopsAnalysis extends ClassFileAnalysis {

  private val onlyClassSymbol: Symbol = Symbol("only-class")
  private val outAllSymbol: Symbol = Symbol("out-all-loops")
  private val noClassSymbol: Symbol = Symbol("no-class")
  private  val onlyClassWithLoopsSymbol: Symbol = Symbol("only-class-with-loops")

  /**
   * Die Methode führt die Metrik Loop aus.
   * @param classFile Ist ein Interface von Projekt
   * @param project Bekommt die vom Framework gelesende jar file in einer Präsentationsform
   * @param customOptions Einstellungs Möglichkeiten der Analyse
   * @return Das Ergebniss wird in der Liste für die Ausgabe gespeichert
   */
  override def analyzeClassFile(classFile: ClassFile, project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try {

    val outAllMethode = customOptions.contains(outAllSymbol)
    val onlyClass = customOptions.contains(onlyClassSymbol)
    val noClass = customOptions.contains(noClassSymbol)
    val onClassLoops = customOptions.contains(onlyClassWithLoopsSymbol)

    if(noClass &&(onlyClass||onClassLoops))
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
        var linelegth = 0
        for (instruction <- instructions) {
          // Im Array vom Framework können leere Felder sein, die mit null belegt sind
          if (instruction != null) {
            //opcode von goto ist 167
            if (instruction.opcode == 167) {
              val gotoInstruction = instruction.asGotoInstruction
              if (gotoInstruction.branchoffset < 0) {
                //Überprüfe ob die Schleife schon gezählt wurde
                //Bei if Anweisungen in einer Schleife, kann es bei der Code Optimierung vorkommen das goto nicht
                // zum ende des Code blocks geht sondern zurück zum Schleifenrumpf. Das kommt vor wenn nach dem if block keine Anweisungen mehr stehen
                val index = linelegth + gotoInstruction.branchoffset
                if(!line.contains(index))
                  {

                    loops += 1
                    line +=index
                  }


              }

            }
          }
          linelegth += 1
        }


      }
      classloops += loops

      if(loops>0 && !onlyClass) {
        rlist += MetricValue(method.fullyQualifiedSignature, this.analysisName, loops)
      }
      else if(outAllMethode)
        {
          rlist += MetricValue(method.fullyQualifiedSignature, this.analysisName, loops)
        }

    }

    if(!noClass)
    {
      if(onClassLoops && classloops>0.0) {
        rlist += MetricValue("Class:" + classFile.thisType.fqn, this.analysisName, classloops)
      }
      else if(!onClassLoops) rlist += MetricValue("Class:" + classFile.thisType.fqn, this.analysisName, classloops)
    }

    rlist.toList




  }



  /**
   * The name for this analysis implementation. Will be used to include and exclude analyses via CLI.
   */
  override def analysisName: String = "methods.loop"

}