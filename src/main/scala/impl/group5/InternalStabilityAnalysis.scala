package org.tud.sse.metrics
package impl.group5

import analysis.{MetricValue, MetricsResult, MultiFileAnalysis}

import org.opalj.br.analyses.Project
import org.opalj.br.instructions.{INVOKESPECIAL, INVOKESTATIC, INVOKEVIRTUAL}
import input.CliParser.OptionMap



import java.io.File
import java.net.URL
import scala.collection.Map
import scala.collection.mutable.ListBuffer
import scala.util.Try
import scala.util.control.Breaks.{break, breakable}

/**
 *
 * Die Klasse stellt die Implementierung der Metrik Internal Stability da.
 *
 * Die Metrik Internal Stability zählt die Verbindungen zwischen zwei Packages. Ruft eine Klasse eine andere Klasse im
 * anderen Package auf wird diese Verbindung gezählt. Wird diese mehrmals von der selben Klasse aufgerufen so werden diese aufrufe nicht gezählt.
 *
 * Eine Klasse im Package A kann mehre Verbindungen nach Package B haben aber jede Verbindung wird einmal gezählt, egal wie oft sie verwendet wird.
 *
 *
 * Optional CLI argument:
 *  --all_value: Gibt die aufsummierten Prel A und PrelR mit aus.
 */
class InternalStabilityAnalysis(jarDir: File) extends MultiFileAnalysis[(Double, Double, Double, String)](jarDir) {

  private val sym_all: Symbol = Symbol("all_value")
  var previousPackagesEdgePackages: Map[String, ListBuffer[(String,String,String)]] = Map[String, ListBuffer[(String,String,String)]]()
  var currentfile: String = ""
  var previousfile: String = ""
  var all =false

  /**
   * Diese Funktion berechnet die Internal Stability nach dem Paper "Constantinou, E. and Stamelos, I. - Identifying evolution patterns: a metrics-based approach for external library reuse"
   *
   * @param project Fully initialized OPAL project representing the JAR file under analysis
   * @param file The file object for which the OPAL project has been generated
   * @param lastResult Option that contains the intermediate result for the previous JAR file, if
   *                   available. This makes differential analyses easier to implement. This argument
   *                   may be None if either this is the first JAR file or the last calculation failed.
   * @param customOptions Custom analysis options taken from the CLI. Can be used to modify behavior
   *                      of the analysis via command-line
   *  @return Try[T] object holding the intermediate result, if successful
   */
  override def produceAnalysisResultForJAR(project: Project[URL], file: File,
                                           lastResult: Option[(Double, Double, Double, String)],
                                           customOptions: OptionMap): Try[(Double, Double, Double, String)] = {
    currentfile = file.toString
    produceAnalysisResultForJAR(project, lastResult, customOptions)
  }

  /**
   * Diese Funktion berechnet die Internal Stability nach dem Paper "Constantinou, E. and Stamelos, I. - Identifying evolution patterns: a metrics-based approach for external library reuse"
   *
   * @param project Fully initialized OPAL project representing the JAR file under analysis
   * @param lastResult Option that contains the intermediate result for the previous JAR file, if
   *                   available. This makes differential analyses easier to implement. This argument
   *                   may be None if either this is the first JAR file or the last calculation failed.
   * @param customOptions Custom analysis options taken from the CLI. Can be used to modify behavior
   *                      of the analysis via command-line
   *  @return Try[T] object holding the intermediate result, if successful
   */
  override def produceAnalysisResultForJAR(project: Project[URL], lastResult: Option[(Double, Double, Double, String)], customOptions: OptionMap): Try[(Double, Double, Double, String)] = {

    all = customOptions.contains(sym_all)
    var IS =0.0
    var PrelA = 0.0
    var PrelR = 0.0
    var currentPackagesEdgePackages: Map[String, ListBuffer[(String,String,String)]] = Map[String, ListBuffer[(String,String,String)]]()
    val projektPackage: scala.collection.Set[String] =project.projectPackages


    projektPackage.foreach(packageName =>{
      val currentEdgeClass = collection.mutable.ListBuffer[(String,String,String)]()
      project.classesPerPackage(packageName).foreach(classFile =>{

        //Prüft in allen Methoden nach Verbindungen zu anderen Packages und speichert die Ergebnisse ab
        classFile.methods.foreach(method =>{
          method.body match {
            case None =>
            case Some(code) =>code.instructions.foreach {
              case invokestatic: INVOKESTATIC =>
                if(!invokestatic.declaringClass.packageName.equals(packageName)) {
                  val tuple = (classFile.fqn, invokestatic.declaringClass.fqn, invokestatic.declaringClass.packageName)
                  if (!currentEdgeClass.contains(tuple)) {
                    currentEdgeClass += tuple
                  }
                }

              case invokespecial:INVOKESPECIAL =>
                if(!invokespecial.declaringClass.packageName.equals(packageName)) {
                  val tuple = (classFile.fqn, invokespecial.declaringClass.fqn, invokespecial.declaringClass.packageName)
                  if (!currentEdgeClass.contains(tuple)) {
                    currentEdgeClass += tuple
                  }
                }

              case invokevirtual : INVOKEVIRTUAL =>
                if(!invokevirtual.declaringClass.mostPreciseObjectType.packageName.equals(packageName))
                {
                  val tuple = (classFile.fqn,invokevirtual.declaringClass.mostPreciseObjectType.fqn,invokevirtual.declaringClass.mostPreciseObjectType.packageName)
                  if(!currentEdgeClass.contains(tuple))
                  {
                    currentEdgeClass += tuple
                  }
                }

              case _=>
            }

          }
        })

      })
        currentPackagesEdgePackages = currentPackagesEdgePackages + (packageName -> currentEdgeClass)
    })

    //Vorbreitungen für die weiteren Berchnungen
    var workPreviousPackage: Map[String, ListBuffer[(String,String,String)]] = Map[String, ListBuffer[(String,String,String)]]()
    workPreviousPackage= previousPackagesEdgePackages
    var workCurrentPackage: Map[String, ListBuffer[(String,String,String)]] = Map[String, ListBuffer[(String,String,String)]]()
    workCurrentPackage= currentPackagesEdgePackages
    val current = collection.mutable.ListBuffer[String]()
    if(!previousfile.equals(""))
    {

      //Filter zuerst die Entfernte oder neu hinzugefügt Package raus, da sie in der Metrik nicht betrachtet werden
        previousPackagesEdgePackages.keys.foreach(Package =>{
          if(!currentPackagesEdgePackages.contains(Package))
            {
              workPreviousPackage-=Package
              workCurrentPackage -=Package
              current += Package

            }
        })
        currentPackagesEdgePackages.keys.foreach(Package =>{
          if(!previousPackagesEdgePackages.contains(Package))
          {
            current += Package
            workPreviousPackage-=Package
            workCurrentPackage -=Package

          }
        })
      //Da alle Realtionships zu denn entfernten Package werden hier entfernt.
      current.foreach(Package =>{
        workPreviousPackage.foreach(Edges =>{
          Edges._2.foreach(list => {
            if(list._3.equals(Package))
              {
                Edges._2 -= list
              }
          })
        })
        workCurrentPackage.foreach(Edges =>{
          Edges._2.foreach(list => {
            if(list._3.equals(Package))
            {
              Edges._2 -= list
            }
          })
        })
      })
      //Vereinigung Berechnen
      // Der Betrag zeigt an wie viele Verbindunen im Projekt gibt. Eine Realtionship hier ist wenn Package A eine Verbindung zu Package B hat.
      var betrag = 0

      val packageReletionship = ListBuffer[(String,String)]()
      workPreviousPackage.foreach(Package =>{
        Package._2.foreach(elem1 =>
        {
          workCurrentPackage.get(Package._1).foreach(lists=> lists.foreach(elem2 =>{
            val tuple = (Package._1,elem1._3)
            if(!packageReletionship.contains(tuple))
              {
                if(elem1._3.equals(elem2._3))
                  {
                    packageReletionship +=tuple
                  }

              }

          })
          )
        })


      })
      betrag = packageReletionship.size



      //Berechne die einzelnen Werte der Relationship zwischen zwei Packages
      workPreviousPackage.foreach(Package1 => {




          val p1p2PrevBuffer = collection.mutable.ListBuffer[(String,String,String)]()
          val p1p2CurBuffer = collection.mutable.ListBuffer[(String,String,String)]()
          val P2buffer = collection.mutable.ListBuffer[String]()
          val list = Package1._2
          val listCur = workCurrentPackage(Package1._1)

        //Berechne die Anzahl der Verbindunngen zwischen zwei Packages

        //
        list.foreach(elem => {
            breakable {
              if(P2buffer.contains(elem._3))break
              var sumP = 0.0
              var sumC = 0.0
              list.foreach(elem2 =>
                if (elem2._3.equals(elem._3)) {
                  //Gefunden Elemente werden gespeichert
                  p1p2PrevBuffer += elem2
                  sumP +=1
                }
              )

              listCur.foreach(elem2 =>
                if (elem2._3.equals(elem._3)) {
                  //Gefunden Elemente werden gespeichert
                  p1p2CurBuffer += elem2
                  sumC += 1
                }
              )
              P2buffer += elem._3

              //Hier Werden die gleichen Elemente entfernt. Am Ende bleiben nur die Realtionships übrig die nicht in beiden Packages sind
              val workp1p2Cur = p1p2CurBuffer.clone()
              val workp1p2Prev = p1p2PrevBuffer.clone()
              p1p2PrevBuffer.foreach(tuple => workp1p2Cur.foreach(elem2 =>
                if(tuple._1.equals(elem2._1)&& tuple._2.equals(elem2._2))
                {
                  workp1p2Cur -= elem2
                }
              )
              )

              p1p2CurBuffer.foreach(tuple => workp1p2Prev.foreach(elem2 =>
                if(tuple._1.equals(elem2._1)&& tuple._2.equals(elem2._2))
                {
                  workp1p2Prev -= elem2
                }
              )
              )





              //Nach der Formel im Paper wird PRELa und PRELr berechnet

              if(sumC !=0.0)
                {
                  PrelA += 1 - (workp1p2Cur.size.toDouble / sumC)
                }
              if(sumP !=0.0) {
                PrelR += 1 - (workp1p2Prev.size.toDouble / sumP)
              }

              p1p2CurBuffer.clear()
              p1p2PrevBuffer.clear()
            }

          })










      })



      //Am Ende wird IS berechnet aus den PRELa und PRELr und den betrag aller Reletionships
      val sum = (PrelA+PrelR)/2
      IS = sum/betrag


    }





    previousPackagesEdgePackages = currentPackagesEdgePackages
    val entity_ident: String = "Difference between: " + previousfile + " and " + currentfile
    previousfile =currentfile
    Try(IS, PrelR,PrelA, entity_ident)
  }


  /**
   * Erstellt die Ausgabe
   *  @return List of JarFileMetricsResults
   */
  override def produceMetricValues(): List[MetricsResult] = {

    val stability = analysisResultsPerFile.values.map(_.get)
      .toList
      .map(value => MetricValue(value._4, "Internal_stability", value._1))
    val removed = analysisResultsPerFile.values.map(_.get)
      .toList
      .map(value => MetricValue(value._4, "PREL_Remove", value._2))
    val append = analysisResultsPerFile.values.map(_.get)
      .toList
      .map(value => MetricValue(value._4, "PREL_Append", value._3))


    val MetricsResultBuffer = collection.mutable.ListBuffer[MetricsResult]()
    val valueMetrik = collection.mutable.ListBuffer[MetricValue]()

    if(all) {
      valueMetrik.appendAll(removed)
      valueMetrik.appendAll(append)
    }
    valueMetrik.appendAll(stability)

    MetricsResultBuffer.append(MetricsResult(analysisName, jarDir, success = true, metricValues = valueMetrik.toList))





    MetricsResultBuffer.toList
  }

  /**
   * The name for this analysis implementation. Will be used to include and exclude analyses via CLI.
   */
  override def analysisName: String = "Internal Stability"
}
