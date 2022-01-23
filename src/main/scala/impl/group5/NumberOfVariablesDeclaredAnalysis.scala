package org.tud.sse.metrics
package impl.group5

import analysis.{MetricValue, SingleFileAnalysis}
import input.CliParser.OptionMap

import org.opalj.br.Field
import org.opalj.br.analyses.Project
import org.opalj.br.instructions.{ FieldAccess, LoadLocalVariableInstruction}

import java.net.URL
import scala.collection.mutable.ListBuffer
import scala.util.Try



/**
 * Die Klasse NumberOfVariablesDeclaredAnalysis zählt die Anzahl der vorhanden Variablen in Klassen
 * Dabei werden sowohl Klassenargumente als auch Locale Varaiblen gezählt und ausgeben
 *
 * Die Metrik braucht die Informationen aus localVariableTable sonst, kann sie nicht richtig arbeiten
 * Die Default Einstellung ist, dass alles ausgeben wird
 *
 * Optional CLI argument:
 *  - --no-method Es werden nur Klassen ausgeben
 *  - --no-class Es werden nur Methoden ausgeben
 *  - --no-unusedfield Es wird nicht die anzahl an nicht benutzte Fields ausgeben
 *
 */
class NumberOfVariablesDeclaredAnalysis extends SingleFileAnalysis {

  private val noMethodSymbol: Symbol = Symbol("no-method")
  private val noClassSymbol: Symbol = Symbol("no-class")
  private val noUnusedFields: Symbol = Symbol("no-unusedfield")


  override def analyzeProject(project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try {

    var noMethod = customOptions.contains(noMethodSymbol)
    var noClass = customOptions.contains(noClassSymbol)
    val noUnUsedField = customOptions.contains(noUnusedFields)

    val rlist = new ListBuffer[MetricValue]()
    //Error for incompatible custum options and fallback to default settings
    if (noMethod && noClass) {
      log.warn("Nur Methodenvariablen und keine Methodenvariablen können nicht zusammen ausgewählt werden. Das Ergebnis wird mit Standart Optionen ausgegeben.")
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
      //Liste mit Fields
      val usedFields = new ListBuffer[FieldAccess]()


      numberOfClassVariables += c.fields.size

      //Getting count of class variables through its field size
      if (!noClass) {

        rlist += MetricValue("Anzahl von Klassenvariablen in " +c.fqn,this.analysisName,c.fields.size )
      }
      //Getting all variables declared in Methods

        if (c.methods != null) {
          for (m <- c.methodsWithBody) {
            //Counts local Variables for this Method only
            var temporaryMethodVariables = 0
            //Check if method body has local variables table

            val localVariableTable = m.body.get.localVariableTable
            val loadIn = new ListBuffer[LoadLocalVariableInstruction]()
            val fieldList = new ListBuffer[FieldAccess]()
            m.body match {
              case None =>
              case Some(code) => code.instructions.foreach {

                case loadInstruction: LoadLocalVariableInstruction => if (!loadIn.exists(y => {
                    y.lvIndex == loadInstruction.lvIndex
                  })) loadIn.append(loadInstruction)

                case fieldAccess: FieldAccess =>
                  if (!fieldList.exists(y => {
                    y.name == fieldAccess.name
                  }))
                    fieldList.append(fieldAccess)


                case _ =>

              }
            }
            var temporaryFieldVariables = 0
            //Check if we get the localVariableTable
            if(localVariableTable.isEmpty) log.warn("Die Metrik braucht die Infos der localVariableTable, um korrekt zu arbeiten")
            if (fieldList.nonEmpty) temporaryFieldVariables = fieldList.size
            if (localVariableTable.nonEmpty) {
              var thisIndex = 0
              val list = localVariableTable.get
              list.foreach(y =>
                if (y.name == "this") {
                  thisIndex = 1
                })
              //Gathering info for current Method and sum of all
              numberOfMethodVariables = numberOfMethodVariables + localVariableTable.get.size
              temporaryMethodVariables = localVariableTable.get.size
              temporaryMethodVariables -= thisIndex
              temporaryMethodVariablesSum = temporaryMethodVariablesSum + temporaryMethodVariables

            }
            //Creating a list of used fields
            fieldList.foreach(field => {
              if (!usedFields.exists(y => {
                y.name == field.name
              })) usedFields.append(field)

            })
            if (!noMethod) {

              rlist += MetricValue("Anzahl lokaler Variablen in " + m.fullyQualifiedSignature, this.analysisName, temporaryMethodVariables)
            }
            if (localVariableTable.nonEmpty) {
              var unusedarguments = 0
              localVariableTable.get.foreach(y => {
                if (loadIn.exists(x => x.lvIndex == y.index)) unusedarguments += 1
              })
              val unUsed = localVariableTable.get.size - unusedarguments
              rlist += MetricValue(m.fullyQualifiedSignature + "Anzahl nicht benuzte Methoden Argumente:", this.analysisName, unUsed)

            }
          }

        }
      if(!noMethod) {

        rlist += MetricValue("Anzahl von lokalen Variablen in allen Methoden von "+c.fqn, this.analysisName, temporaryMethodVariablesSum)

      }

      //Checking if our unused Fields are private because we can't tell if public fields are used out of class
      if(!noUnUsedField) {
        val privateFields = new ListBuffer[Field]()
        c.fields.foreach(field => {
          if(field.isPrivate)  privateFields.append(field)
        })

        var neverUsedField = 0
        privateFields.foreach(field => {
          if (!usedFields.exists(y => {
            y.name == field.name
          })) {
            neverUsedField += 1
          }
        })
        rlist += MetricValue("Ungenutzte Field in der Klasse "+c.fqn, this.analysisName, neverUsedField)
      }

    }

    metric = numberOfMethodVariables + numberOfClassVariables
    rlist +=MetricValue("Anzahl deklarierter Variablen in allen Klassen: ", this.analysisName, numberOfClassVariables)
    rlist +=MetricValue("Anzahl deklarierter Variablen in allen Methoden: ", this.analysisName, numberOfMethodVariables)


  }

  override def analysisName: String = "VariablesDeclared.count"
}
