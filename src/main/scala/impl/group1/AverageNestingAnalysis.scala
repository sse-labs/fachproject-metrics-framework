/**
 * Für die Umsetzung der Metrik AverageNesting wurde als Grundgerüst
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
 * Die Klasse AverageNestingAnalysis stellt die Implementierung der Metrik AvgMaxNesting aus
 * dem paper "Software Metrics as Indicators of Security Vulnerabilities"
 * von Nádia Medeiros, Naghmeh Ivaki, Pedro Costa und Marco Vieira dar
 * Diese Metrik berechnet für jede Methode die maximale Anzahl der in einander verschachtelten
 * if/else, switch und "loop" Kontrollstrukturen und gibt die durchschnittliche maximale Schachteilungstiefe aus.
 * Es werden sowohl das arithmetische Mittel als auch der median der maximalen Schachtelungstiefe berechnet
 *
 * Optional CLI argument:
 *  - --only-means Es wird nur das arithmetische Mittel maximalen Tiefen einer Klasse ausgegeben
 *  - --only-medians es wird nur der Median der  Tiefen einer Klasse ausgegeben
 *  standardmäßig wird sowohl das arithmetische Mittel, als auch der Median ausgegeben
 *
 */

class AverageNestingAnalysis extends ClassFileAnalysis {

  private val onlyMeansSymbol: Symbol = Symbol("only-mean")
  private val onlyMediansSymbol: Symbol = Symbol("only-median")

  /**
   * Die Methode führt die Metrik AverageNesting aus.
   *
   * @param classFile     Ist ein Interface von Projekt
   * @param project       Bekommt die vom Framework gelesene jar file in einer Präsentationsform
   * @param customOptions Einstellungsmöglichkeiten der Analyse
   * @return Das Ergebnis wird in der Liste für die Ausgabe gespeichert
   */
  override def analyzeClassFile(classFile: ClassFile, project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try {

    val onlyMeans = customOptions.contains(onlyMeansSymbol)
    val onlyMedians = customOptions.contains(onlyMediansSymbol)

    if (onlyMeans && onlyMedians) {
      log.error("Fehler! Argumente sind inkompatibel")
    }

    val methods = classFile.methodsWithBody
    val rlist = new ListBuffer[MetricValue]()
    var maximaleTiefe = 0
    var tiefenDerMethoden = new ListBuffer[Int]

    if(methods.isEmpty)
    {
      tiefenDerMethoden += 0
    }

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
      tiefenDerMethoden += maximaleTiefeMethode


    }


    assert(tiefenDerMethoden.nonEmpty)

    val mean = tiefenDerMethoden.sum.toDouble / tiefenDerMethoden.size.toDouble
    val median: Double = calculateMean(tiefenDerMethoden)

    if (!onlyMedians) {
      rlist += MetricValue("mean of: " +  classFile.fqn, this.analysisName, mean)
    }

    if (!onlyMeans) {
      rlist += MetricValue("median of: " +  classFile.fqn, this.analysisName, median)
    }
    rlist.toList
  }


  /**
   * The name for this analysis implementation. Will be used to include and exclude analyses via CLI.
   */
  override def analysisName: String = "AverageNesting"

  def calculateMean(Buffer: ListBuffer[Int]): Double =
  {
    if(Buffer.isEmpty)
    {
      return 0.0
    }
    val listBuffer = Buffer.sorted
    var median: Double = 0
    if (listBuffer.nonEmpty && listBuffer.size % 2 == 0) {
      val mitteR = listBuffer.size / 2
      val mitteL = mitteR - 1
      median = (listBuffer(mitteL).toDouble + listBuffer(mitteR).toDouble) / 2.0
    } else if (listBuffer.nonEmpty) {
      val mitte = listBuffer.size / 2
      median = listBuffer(mitte).toDouble
    }
    median

  }
}