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

class WeightedMethodsPerClassAnalysis extends SingleFileAnalysis{



  /**
   * Die Methode führt die Metrik WMC aus.
   * WMC rechnet die Summe an CC Werten aller Methoden in einer Klasse aus
   * Diese Implementation gibt zusätlich den Gesamtwert des Projekts, sowie Durchschnitt als auch Maximalwert um möglich Problemklassen zu finden
   * @param project Bekommt die vom Framework gelesende jar file in einer Präsentationsform
   * @param customOptions Einstellungs Möglichkeiten der Analyse
   * @return Das Ergebniss wird in der Liste für die Ausgabe gespeichert
   */
  override def analyzeProject(project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try {

    val classes = project.allProjectClassFiles
    var WMCProjectSum = 0
    val classesCount = classes.size
    val rlist = new ListBuffer[MetricValue]()
    var WMCMax = 0
    var WMCMaxName = ""

    //Iterating over all classes in project
    for ( classFile <-  classes) {
      val methods = classFile.methodsWithBody
      var WMC = 0
      //iterating over all methods in class
      while (methods.hasNext) {
        val method = methods.next()
        val body = method.body
        //McAbe complexity von Gruppe 4
        var e = 0
        var n = 0
        body.get.instructions.foreach { instruction =>
          Try {
            if (instruction.isCompoundConditionalBranchInstruction) e = e + instruction.asCompoundConditionalBranchInstruction.jumpOffsets.size
            else if (instruction.isSimpleConditionalBranchInstruction) e = e + 2
            else if (!instruction.isReturnInstruction) e = e + 1
            n = n + 1
          }
        }
        //Complexity for current method
        val complexity = e - n + 2
        WMC = WMC + complexity

      }
      //Checkinf if new max value found
      if(WMC > WMCMax) {
        WMCMax = WMC
        WMCMaxName = classFile.fqn
      }
      //Adding to sum and saving WMC of current class
      WMCProjectSum = WMCProjectSum + WMC
      rlist += MetricValue("WMC von " + classFile.thisType.fqn, this.analysisName, WMC)

    }
    //Saving project focused WMC Metrics
    rlist += MetricValue("Höchster WMC Wert im Projekt ist in Klasse: "+WMCMaxName,this.analysisName,WMCMax)
    rlist += MetricValue("WMC von dem kompletten Projekt: ",this.analysisName, WMCProjectSum)
    val WMCAverage = WMCProjectSum/classesCount
    rlist += MetricValue("WMC Durschnitt von allen Projekt Klassen: ",this.analysisName, WMCAverage)


  }
  override def analysisName: String = "wmc"
}
