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

    if(noMethodVariables && onlyMethodVariables) {
      println("Nur Methodenvariablen und keine Methodenvaraiblen können nicht zusammen ausgewählt werden. Das Ergebnis wird mit Standart Optionen ausgegeben.")
      noMethodVariables = false
      onlyMethodVariables = false
    }

    val classes = project.allProjectClassFiles
    var numberOfMethodVariables = 0
    var numberOfClassVariables = 0
    var metric = 0

    //Getting all variables declared in the class
    if(!onlyMethodVariables) {
      for (c <- classes) {
        for (f <- c.fields) {
          println("Field is : " + f.fieldType)
          numberOfClassVariables = numberOfClassVariables + 1

        }

      }
    }
    println("Anzahl deklarierter Variablen in den Klassen: "+numberOfClassVariables)

    println("------------------------------------------------------------------------------------------------------------------")
    //Getting all variables declared in Methods
    if(!noMethodVariables) {
      for (c <- classes) {
        if (c.methods != null) {
          for (m <- c.methodsWithBody) {
            //Check if method body has local variables table
            if (m.body.get.localVariableTable.nonEmpty) {
              println("Local Variables: " + m.body.get.localVariableTable)
              numberOfMethodVariables = numberOfMethodVariables + m.body.get.localVariableTable.size
            }

          }
        }
      }
    }

    println("Anzahl deklarierter Variablen in den Methoden: "+numberOfMethodVariables)

    println("------------------------------------------------------------------------------------------------------------------")
    metric = numberOfMethodVariables + numberOfClassVariables

    //TODO In welchem Format Ergebnis zurückgeben? Für Klassen aufgeteilt oder ganzes Projekt
    MetricValue("Project:" + project.toString(), this.analysisName, metric)
    List(MetricValue("file", "VDEC", metric))
  }

  override def analysisName: String = "VariablesDeclared.count"
}
