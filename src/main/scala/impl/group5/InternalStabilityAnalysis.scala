package org.tud.sse.metrics
package impl.group5

import analysis.{MetricValue, MetricsResult, MultiFileAnalysis}

import org.opalj.br.analyses.Project
import org.opalj.br.instructions.{INVOKEDYNAMIC, INVOKESTATIC, INVOKEVIRTUAL, InvocationInstruction}
import org.tud.sse.metrics.input.CliParser.OptionMap

import java.io.File
import java.net.URL
import scala.collection.{Map, Set}
import scala.collection.mutable.ListBuffer
import scala.util.Try
import scala.util.control.Breaks.{break, breakable}

class InternalStabilityAnalysis(jarDir: File) extends MultiFileAnalysis[(Double, Double, Double, String)](jarDir) {

  private val sym_all: Symbol = Symbol("all_value")
  var previousPackagesEdgePackages: Map[String, ListBuffer[(String,String,String)]] = Map[String, ListBuffer[(String,String,String)]]()
  var currentfile: String = ""
  var previousfile: String = ""
  var all =false

  override def produceAnalysisResultForJAR(project: Project[URL], file: File,
                                           lastResult: Option[(Double, Double, Double, String)],
                                           customOptions: OptionMap): Try[(Double, Double, Double, String)] = {
    currentfile = file.toString
    produceAnalysisResultForJAR(project, lastResult, customOptions)
  }

  override def produceAnalysisResultForJAR(project: Project[URL], lastResult: Option[(Double, Double, Double, String)], customOptions: OptionMap): Try[(Double, Double, Double, String)] = {

    all = customOptions.contains(sym_all)
    var IS =0.0
    var PrelA = 0.0
    var PrelR = 0.0
    var currentPackagesEdgePackages: Map[String, ListBuffer[(String,String,String)]] = Map[String, ListBuffer[(String,String,String)]]()
    val currentPackage = project.classesPerPackage
    currentPackage.foreach(Package =>{
      val currentEdgeClass = collection.mutable.ListBuffer[(String,String,String)]()
      Package._2.foreach(classFile =>{

        classFile.methods.foreach(method =>{
          method.body match {
            case None =>
            case Some(code) =>code.instructions.foreach {
              case invokestatic: INVOKESTATIC =>{
                if(!invokestatic.declaringClass.packageName.equals(Package._1))
                  {
                    val tuple = (classFile.fqn,invokestatic.declaringClass.fqn,invokestatic.declaringClass.packageName)
                    if(!currentEdgeClass.contains(tuple))
                      {
                        currentEdgeClass += tuple
                      }
                  }
              }
              case invokevirtual : INVOKEVIRTUAL => {
                if(!invokevirtual.declaringClass.mostPreciseObjectType.packageName.equals(Package._1))
                {
                  val tuple = (classFile.fqn,invokevirtual.declaringClass.mostPreciseObjectType.fqn,invokevirtual.declaringClass.mostPreciseObjectType.packageName)
                  if(!currentEdgeClass.contains(tuple))
                  {
                    currentEdgeClass += tuple
                  }
                }
              }
              case invokedynamic: INVOKEDYNAMIC =>{
                println(invokedynamic.name)
              }
              case _=>
            }

          }
        })

      })
        currentPackagesEdgePackages = currentPackagesEdgePackages + (Package._1 -> currentEdgeClass)
    })
    var workPreviousPackage = previousPackagesEdgePackages
    var workCurrentPackage = currentPackagesEdgePackages
    val current = collection.mutable.ListBuffer[(String)]()
    if(!previousfile.equals(""))
    {

      //Filter zuerst die Entfernte oder neu hinzugefügt Package raus
        previousPackagesEdgePackages.keys.foreach(Package =>{
          if(!currentPackagesEdgePackages.contains(Package))
            {
              workPreviousPackage-=(Package)
              workCurrentPackage -=(Package)
              current += Package

            }
        })
        currentPackagesEdgePackages.keys.foreach(Package =>{
          if(!previousPackagesEdgePackages.contains(Package))
          {
            current += Package
            workPreviousPackage-=(Package)
            workCurrentPackage -=(Package)

          }
        })
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
      var betrag = 0
      workPreviousPackage.foreach(Package =>{
        Package._2.foreach(list =>
        {
          workCurrentPackage.get(Package._1).foreach(rlist => rlist.foreach(listC =>{
            if(listC._1.equals(list._1)&&listC._2.equals(list._2)&&listC._3.equals(list._3))
              {
                betrag +=1
              }
          }))

          })



      })
      println(betrag)


      //Berechne die einzelnen Werte der Relationship zwischen zwei Packages
      workPreviousPackage.foreach(Package1 => {

          var relSchnitt = 0.0

          var sumP = 0.0
          var sumC = 0.0
          val p1p2PrevBuffer = collection.mutable.ListBuffer[(String,String,String)]()
          val p1p2CurBuffer = collection.mutable.ListBuffer[(String,String,String)]()
          val P2buffer = collection.mutable.ListBuffer[(String)]()
          val list = Package1._2

        //Berechne die Anzahl der Verbindunngen zwischen zwei Packages

        //
        list.foreach(elem => {
            breakable {
              if(P2buffer.contains(elem._3))break
              list.foreach(elem2 =>
                if (elem2._3.equals(elem._3)) {
                  //Gefunden Elemente werden gespeichert
                  p1p2PrevBuffer += elem2
                  sumP +=1
                }
              )
              P2buffer += elem._3
            }
          })
        P2buffer.clear()
          workCurrentPackage.get(Package1._1).foreach(list =>
            list.foreach(elem =>
              breakable {
                if(P2buffer.contains(elem._3))break
                list.foreach(elem2 =>
                  if (elem2._3.equals(elem._3)) {
                    //Gefunden Elemente werden gespeichert
                    p1p2CurBuffer += elem2
                    sumC += 1
                  }
                )
                P2buffer += elem._3
              }
            )

          )


        //Berechne den Betrag der des Schnitts der in beiden Versionen vorhanden verbindungen
        p1p2PrevBuffer.foreach(elem => p1p2CurBuffer.foreach(elem2 =>
          if(elem._1.equals(elem2._1)&& elem._2.equals(elem2._2))
            {
                relSchnitt +=1
            }
          )
        )





        //Nach der Formel im Paper wird PRELa und PRELr berechnet
          if(relSchnitt !=0) {
            PrelA += 1 - (relSchnitt / sumC)
            PrelR += 1 - (relSchnitt / sumP)
          }





      })



      //Am Ende wird IS berechnet aus den PRELa und PRELr und den betrag aller Reletionships
      val sum = (PrelA+PrelR)/2
      IS = sum/betrag
      println(PrelR+PrelA)
      println(IS)

    }





    previousPackagesEdgePackages = currentPackagesEdgePackages
    var entity_ident: String = "Difference between: " + previousfile + " and " + currentfile
    previousfile =currentfile
    Try(IS, PrelR,PrelA, entity_ident)
  }


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


  override def analysisName: String = "Internal Stability"
}
