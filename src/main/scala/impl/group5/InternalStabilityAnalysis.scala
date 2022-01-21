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

class InternalStabilityAnalysis(jarDir: File) extends MultiFileAnalysis[(Double, Double, Double, String)](jarDir) {

  var previousPackagesEdgePackages: Map[String, ListBuffer[(String,String,String)]] = Map[String, ListBuffer[(String,String,String)]]()
  var currentfile: String = ""
  var previousfile: String = ""

  override def produceAnalysisResultForJAR(project: Project[URL], file: File,
                                           lastResult: Option[(Double, Double, Double, String)],
                                           customOptions: OptionMap): Try[(Double, Double, Double, String)] = {
    currentfile = file.toString
    produceAnalysisResultForJAR(project, lastResult, customOptions)
  }

  override def produceAnalysisResultForJAR(project: Project[URL], lastResult: Option[(Double, Double, Double, String)], customOptions: OptionMap): Try[(Double, Double, Double, String)] = {


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
      workPreviousPackage.foreach(Package =>{
        Package._2.foreach(list =>
        {


        })
      })


    }





    previousPackagesEdgePackages = currentPackagesEdgePackages
    var entity_ident: String = "Difference between: " + previousfile + " and " + currentfile
    previousfile =currentfile
    Try(1.0, 2,3, entity_ident)
  }


  override def produceMetricValues(): List[MetricsResult] = {

    val MetricsResultBuffer = collection.mutable.ListBuffer[MetricsResult]()





    MetricsResultBuffer.toList
  }


  override def analysisName: String = "Internal Stability"
}
