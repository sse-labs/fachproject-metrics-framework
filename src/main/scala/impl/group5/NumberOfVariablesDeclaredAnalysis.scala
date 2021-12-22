package org.tud.sse.metrics
package impl.group5

import analysis.{MetricValue, SingleFileAnalysis}
import input.CliParser.OptionMap


import org.opalj.br.analyses.Project
import org.opalj.br.instructions.FieldAccess

import java.net.URL
import scala.collection.mutable.ListBuffer
import scala.util.Try



/**
 * Die Klasse NumberOfVariablesDeclaredAnalysis zählt die Anzahl der vorhanden Variablen in Klassen
 * Dabei werden sowohl Klassenargumente als auch Locale Varaiblen gezählt und ausgeben
 *
 * Die Default Einstellung ist, dass alles ausgeben wird
 *
 * Optional CLI argument:
 *  - --no-methode Es werden nur Klassen ausgeben
 *  - --no-class Es werden nur Methoden ausgeben
 *
 */
class NumberOfVariablesDeclaredAnalysis extends SingleFileAnalysis {

  private val noMethodSymbol: Symbol = Symbol("no-method")
  private val noClassSymbol: Symbol = Symbol("no-class")

//TODO Varaiblen werden doppelt gezählt.
  override def analyzeProject(project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try {

    var noMethod = customOptions.contains(noMethodSymbol)
    var noClass = customOptions.contains(noClassSymbol)
    val rlist = new ListBuffer[MetricValue]()
    if (noMethod && noClass) {
      println("Nur Methodenvariablen und keine Methodenvaraiblen können nicht zusammen ausgewählt werden. Das Ergebnis wird mit Standart Optionen ausgegeben.")
      noMethod = false
      noClass = false
    }

    val classes = project.allProjectClassFiles
    //Counts Method Variables for all classes
    var numberOfMethodVariables = 0
    //Counts all Class Variables
    var numberOfClassVariables = 0
    var metric = 0

    //Getting all variables declared in the class

    for (c <- classes) {
      //Counts Method Variables for whole Class
      var temporaryMethodVariablesSum = 0
      //Counts only Class variables
      var temporaryClassVariables = 0
      if (!noClass) {
        for (f <- c.fields) {

          println("Field is : " + f.fieldType)
          temporaryClassVariables = temporaryClassVariables + 1
          numberOfClassVariables = numberOfClassVariables + 1
        }
        println("Anzahl von Klassenvariablen in " +c.toString()+ " beträgt: "+temporaryClassVariables)
        rlist += MetricValue("Anzahl von Klassenvariablen in " +c.fqn,this.analysisName, temporaryClassVariables)
      }
      //Getting all variables declared in Methods
      if (!noMethod) {
        if (c.methods != null) {
          for (m <- c.methodsWithBody) {
            //Counts local Variables for this Method only
            var temporaryMethodVariables = 0
            //Check if method body has local variables table

            val localVariableTable = m.body.get.localVariableTable
            val fieldList = new ListBuffer[FieldAccess]()
            m.body match {
              case None =>
              case Some(code) => code.instructions.foreach{

                case fieldAccess: FieldAccess => {
                  if(!fieldList.exists(y =>{y.name == fieldAccess.name}) )
                      fieldList.append(fieldAccess)
                }
                case _=>

              }
            }
            var temporaryFielVariablen = 0
            if(fieldList.nonEmpty) temporaryFielVariablen = fieldList.size
            if (localVariableTable.nonEmpty) {

              numberOfMethodVariables = numberOfMethodVariables + localVariableTable.get.size
              temporaryMethodVariables = localVariableTable.get.size
              temporaryMethodVariables -= temporaryFielVariablen
              temporaryMethodVariablesSum = temporaryMethodVariablesSum + temporaryMethodVariables

            }
            println("Anzahl lokaler Variablen in "+m.toString()+ " beträgt: "+temporaryMethodVariables)
            rlist += MetricValue("Anzahl lokaler Variablen in "+m.fullyQualifiedSignature, this.analysisName,temporaryMethodVariables)
          }
        }
        println("Anzahl von lokalen Variablen in allen Methoden von "+c.toString()+" beträgt: "+temporaryMethodVariablesSum)
        rlist += MetricValue("Anzahl von lokalen Variablen in allen Methoden von "+c.fqn, this.analysisName, temporaryMethodVariablesSum)
      }
    }
    println("------------------------------------------------------------------------------------------------------------------")
    println("Anzahl deklarierter Variablen in allen Klassen: " + numberOfClassVariables)
    println("------------------------------------------------------------------------------------------------------------------")
    println("Anzahl deklarierter Variablen in allen Methoden: " + numberOfMethodVariables)
    println("------------------------------------------------------------------------------------------------------------------")
    metric = numberOfMethodVariables + numberOfClassVariables
    rlist +=MetricValue("Anzahl deklarierter Variablen in allen Klassen: ", this.analysisName, numberOfClassVariables)
    rlist +=MetricValue("Anzahl deklarierter Variablen in allen Methoden: ", this.analysisName, numberOfMethodVariables)

    //TODO In welchem Format Ergebnis zurückgeben? Für Klassen aufgeteilt oder ganzes Projekt
    MetricValue("Project:" + project.toString(), this.analysisName, metric)
    rlist +=MetricValue("file", "VDEC", metric)
    rlist.toList
  }

  override def analysisName: String = "VariablesDeclared.count"
}
