package org.tud.sse.metrics
package impl.group5

import analysis.{SingleFileAnalysis, MetricValue}
import input.CliParser.OptionMap

import org.opalj.br.analyses.Project


import java.net.URL
import scala.util.Try

class NumberOfVariablesDeclaredAnalysis extends SingleFileAnalysis {

  private val noMethodVariablesSymbol: Symbol = Symbol("no-method-variables")
  private val onlyMethodVariablesSymbol: Symbol = Symbol("only-method-variables")


  override def analyzeProject(project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try {

    var noMethodVariables = customOptions.contains(noMethodVariablesSymbol)
    var onlyMethodVariables = customOptions.contains(onlyMethodVariablesSymbol)

    if (noMethodVariables && onlyMethodVariables) {
      println("Nur Methodenvariablen und keine Methodenvaraiblen können nicht zusammen ausgewählt werden. Das Ergebnis wird mit Standart Optionen ausgegeben.")
      noMethodVariables = false
      onlyMethodVariables = false
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
      if (!onlyMethodVariables) {
        for (f <- c.fields) {
          println("Field is : " + f.fieldType)
          temporaryClassVariables = temporaryClassVariables + 1
          numberOfClassVariables = numberOfClassVariables + 1
        }
        println("Anzahl von Klassenvariablen in " +c.toString()+ " beträgt: "+temporaryClassVariables)
      }
      //Getting all variables declared in Methods
      if (!noMethodVariables) {
        if (c.methods != null) {
          for (m <- c.methodsWithBody) {
            //Counts local Variables for this Method only
            var temporaryMethodVariables = 0
            //Check if method body has local variables table
            val localVariableTable = m.body.get.localVariableTable
            if (localVariableTable.nonEmpty) {
              println("Local Variables: " + localVariableTable)
              numberOfMethodVariables = numberOfMethodVariables + localVariableTable.get.size
              temporaryMethodVariables = localVariableTable.get.size
              temporaryMethodVariablesSum = temporaryMethodVariablesSum + temporaryMethodVariables

            }
            println("Anzahl lokaler Variablen in "+m.toString()+ " beträgt: "+temporaryMethodVariables)
          }
        }
        println("Anzahl von lokalen Variablen in allen Methoden von "+c.toString()+" beträgt: "+temporaryMethodVariablesSum)
      }
    }
    println("------------------------------------------------------------------------------------------------------------------")
    println("Anzahl deklarierter Variablen in den Klassen: " + numberOfClassVariables)
    println("------------------------------------------------------------------------------------------------------------------")
    println("Anzahl deklarierter Variablen in den Methoden: " + numberOfMethodVariables)
    println("------------------------------------------------------------------------------------------------------------------")
    metric = numberOfMethodVariables + numberOfClassVariables

    //TODO In welchem Format Ergebnis zurückgeben? Für Klassen aufgeteilt oder ganzes Projekt
    MetricValue("Project:" + project.toString(), this.analysisName, metric)
    List(MetricValue("file", "VDEC", metric))
  }

  override def analysisName: String = "VariablesDeclared.count"
}
