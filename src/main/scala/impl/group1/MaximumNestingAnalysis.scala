/**
 * Für die Umsetzung der Metrik MaximumNesting wurde als Grundgerüst
 * der Quelltext von Gruppe5: "Metrik Loop" verwendet
 * Wir bedanken uns für die gute Codebasis und die freundliche Nutzungsgenehmigung
 *
 *
 *Anmerkung zu Nesting Metriken:
 *Es kann sein, dass leicht unterschiedlicher Quelltext zu identischem Bytecode führt:
 *
 *aus den beiden Java-Methoden
 *
 *int test9(int zahl1, int zahl2)
 *   {
 *       if(zahl1 >= zahl2)
 *       {
 *           if(zahl1 > zahl2)
 *               return zahl1;
 *       }
 *       return zahl2;
 *   }
 *
 *   int test10(int zahl1, int zahl2)
 *   {
 *       if(zahl1 >= zahl2 && zahl1 > zahl2)
 *       {
 *               return zahl1;
 *       }
 *       return zahl2;
 *   }
 *
 * wird folgender identischer Bytecode:
 *
 * int test9(int, int);
 *   Code:
 *      0: iload_1
 *      1: iload_2
 *      2: if_icmplt     12
 *      5: iload_1
 *      6: iload_2
 *      7: if_icmple     12
 *     10: iload_1
 *     11: ireturn
 *     12: iload_2
 *     13: ireturn
 *
 * int test10(int, int);
 *   Code:
 *      0: iload_1
 *      1: iload_2
 *      2: if_icmplt     12
 *      5: iload_1
 *      6: iload_2
 *      7: if_icmple     12
 *     10: iload_1
 *     11: ireturn
 *     12: iload_2
 *     13: ireturn
 *
 *Damit gibt errechnet die Nestingmetrik für beide Varianten den selben Wert von 2 aus
 *Erwartet hätte man doch eher unterschiedliche Werte:
 *Nesting(test9) = 2
 *Nesting(test10) = 1
 *
 *Dies scheint jedoch unerreichbar zu sein
 */

package org.tud.sse.metrics
package impl.group1

import analysis.{ClassFileAnalysis, MetricValue}
import input.CliParser.OptionMap

import org.opalj.br.ClassFile
import org.opalj.br.analyses.Project

import java.net.URL
import scala.collection.mutable.ListBuffer
import scala.util.Try

/**
 * Die Klasse MaximumNestingAnalysis stellt die Implementierung der Metrik Maximum Nesting (NEST) aus dem paper
 * "An Empirical Evaluation of Fault-Proneness Models" von Giovanni Denaro und Mauro Pezze dar.
 *
 * Diese Metrik berechnet für jede Methode die Anzahl der in einander verschachtelten
 * if/else, switch und "loop" Kontrollstrukturen und gibt die maximale Schachteilungstiefe aus.
 *
 *
 * Optional CLI argument:
 *  - --only-class Es wird nur das absolute Maximum einer Klasse berechnet
 *  - --only-methods es werden nur die Maxima der einzelnen Methoden ausgegeben
 *  standardmäßig werden sowohl die Klassen, als auch die Methodenwerte ausgegeben
 *
 */

class MaximumNestingAnalysis extends ClassFileAnalysis {

  private val onlyMethodsSymbol: Symbol = Symbol("only-methods")
  private val onlyClassesSymbol: Symbol = Symbol("only-classes")

  /**
   * Die Methode führt die Metrik MaximumNesting aus.
   *
   * @param classFile     Ist ein Interface von Projekt
   * @param project       Bekommt die vom Framework gelesene jar file in einer Präsentationsform
   * @param customOptions Einstellungsmöglichkeiten der Analyse
   * @return Das Ergebnis wird in der Liste für die Ausgabe gespeichert
   */
  override def analyzeClassFile(classFile: ClassFile, project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try {

    val onlyMethods = customOptions.contains(onlyMethodsSymbol)
    val onlyClasses = customOptions.contains(onlyClassesSymbol)

    if (onlyClasses && onlyMethods) {
      log.error("Fehler! Argumente sind inkompatibel")
    }

    val methods = classFile.methodsWithBody
    val rlist = new ListBuffer[MetricValue]()
    var maximaleTiefe = 0
    var tiefenDerMethoden = new ListBuffer[Int]

    while (methods.hasNext) {
      val method = methods.next()
      val body = method.body
      var maximaleTiefeMethode = 0
      var Schichten = new ListBuffer[(Int, Int)]()
      if (body.isEmpty) {
        tiefenDerMethoden += 0
      }
      else {
        val code = body.get
        val instructions = code.instructions
        var instructionPosition = 0
        for (instruction <- instructions) {
          // Im Array vom Framework können leere Felder sein, die mit null belegt sind
          if (instruction != null) {


            if (instruction.opcode == 170) {
              val isInstruction = instruction.asTABLESWITCH
              val Sprungziel = instructionPosition + isInstruction.jumpOffsets.max.max(isInstruction.defaultOffset)
              Schichten += ((instructionPosition, Sprungziel))
              if (Schichten.size > maximaleTiefeMethode) {
                maximaleTiefeMethode = Schichten.size
              }
            }

            if (instruction.opcode == 171) {
              val isInstruction = instruction.asLOOKUPSWITCH
              val Sprungziel = instructionPosition + isInstruction.jumpOffsets.max.max(isInstruction.defaultOffset)
              Schichten += ((instructionPosition, Sprungziel))
              if (Schichten.size > maximaleTiefeMethode) {
                maximaleTiefeMethode = Schichten.size
              }
            }

            val isInstruction = instruction.opcode match {
              case 153 => instruction.asIF0Instruction
              case 154 => instruction.asIF0Instruction
              case 155 => instruction.asIF0Instruction
              case 156 => instruction.asIF0Instruction
              case 157 => instruction.asIF0Instruction
              case 158 => instruction.asIF0Instruction
              case 159 => instruction.asIFICMPInstruction
              case 160 => instruction.asIFICMPInstruction
              case 161 => instruction.asIFICMPInstruction
              case 162 => instruction.asIFICMPInstruction
              case 163 => instruction.asIFICMPInstruction
              case 164 => instruction.asIFICMPInstruction
              case 165 => instruction.asIFACMPInstruction
              case 166 => instruction.asIFACMPInstruction
              case 167 => instruction.asGotoInstruction
              case 198 => instruction.asIFXNullInstruction
              case 199 => instruction.asIFXNullInstruction
              case _ => null
            }

            if (isInstruction != null) {
              //Bereich den die Kontrollstruktur abdeckt
              var Sprungziel = instructionPosition + isInstruction.branchoffset
              //Neue Kontrollstruktur, die kein loop ist
              if (isInstruction.branchoffset > 0) {
                //Kontrollstrukturen in Schicht 0
                if (Schichten.isEmpty) {
                  //Den Bereich abspeichern, den die Kontrollstruktur abdeckt
                  Schichten += ((instructionPosition, Sprungziel))
                  if (Schichten.size > maximaleTiefeMethode) {
                    maximaleTiefeMethode = Schichten.size
                  }
                }
                else {
                  //Liegt die aktuell betrachtete Kontrollstruktur innerhalb eines Bereich, der von einer
                  //anderen zuvor betrachteten Kontrollstruktur abgedeckt wird? Wenn ja, dann gibt es eine
                  //neue Verschachteltungsschicht
                  if (Schichten.last._1 < instructionPosition && Sprungziel <= Schichten.last._2) {
                    Schichten += ((instructionPosition, Sprungziel))
                    if (Schichten.size > maximaleTiefeMethode) {
                      maximaleTiefeMethode = Schichten.size
                    }
                  }
                    //Die aktuell betrachtete Kontrollstruktur liegt nach dem abgedeckten Bereich der zuletzt
                    //betrachteten Kontrollstruktur? Dann kann die letzte Struktur von Stapel (aus der Liste) entfernt
                    //werden, denn die beiden sind nicht in ineinander verschachtelt
                    //Wiederhole solange nötig
                  else if (instructionPosition >= Schichten.last._2) {
                    while (Schichten.nonEmpty && (instructionPosition > Schichten.last._2)) {
                      Schichten = Schichten.dropRight(1)
                    }
                    Schichten += ((instructionPosition, Sprungziel))
                    if (Schichten.size > maximaleTiefeMethode) {
                      maximaleTiefeMethode = Schichten.size
                    }
                  }
                }
              }
              //Lösung für zusammengefasste Instruktionen: Existenz des Phänomens noch nicht abschließend geklärt
              else if(isInstruction.branchoffset < 0 && !isInstruction.isGotoInstruction)
              {
                if (Schichten.isEmpty) {
                  //Den Bereich abspeichern, den die Kontrollstruktur abdeckt
                  //Wird zurückgesprungen, dann liegt das Sprungziel vor der instructionPosition
                  Schichten += ((Sprungziel,instructionPosition))
                  if (Schichten.size > maximaleTiefeMethode) {
                    maximaleTiefeMethode = Schichten.size
                  }
                }
                else {
                  //Wird zurückgesprungen bei einer zusammengefassten Anweisung, dann wird nach dem ursprünglichem
                  //Sprungziel in den vorherigen Verzweigungen gesucht
                  var currentItemPos = Schichten.size-1
                  while (currentItemPos > 0 && (Sprungziel > Schichten(currentItemPos)._1)) {
                  currentItemPos -= 1
                }
                  Sprungziel = Schichten(currentItemPos)._2
                  //Der errechnete Bereich der Kontrollstruktur liegt garantiert innerhalb eines Bereich, der von einer
                  //anderen zuvor betrachteten Kontrollstruktur abgedeckt wird
                    Schichten += ((instructionPosition,Sprungziel))
                    if (Schichten.size > maximaleTiefeMethode) {
                      maximaleTiefeMethode = Schichten.size
                    }
                }
              }
            }
          }
          instructionPosition += 1
        }
      }
      if (maximaleTiefeMethode > maximaleTiefe) {
        maximaleTiefe = maximaleTiefeMethode
      }

      if (!onlyClasses) {
        rlist += MetricValue("method: " + method.fullyQualifiedSignature, this.analysisName, maximaleTiefeMethode)
      }
    }

    if (!onlyMethods) {
      rlist += MetricValue("class: " + classFile.fqn, this.analysisName, maximaleTiefe)
    }

    rlist.toList
  }


  /**
   * The name for this analysis implementation. Will be used to include and exclude analyses via CLI.
   */
  override def analysisName: String = "MaximumNesting"

}