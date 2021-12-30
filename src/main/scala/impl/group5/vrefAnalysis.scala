package org.tud.sse.metrics
package impl.group5

import analysis.{ClassFileAnalysis, MetricValue}
import input.CliParser.OptionMap

import org.opalj.br.ClassFile
import org.opalj.br.analyses.Project
import org.opalj.br.instructions.{ GETFIELD, LoadConstantInstruction, LoadLocalVariableInstruction, PUTFIELD, StoreLocalVariableInstruction}

import java.net.URL
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.Try


/**
 *
 * Die Klasse stellt die Implementierung der Metrik Vref da.
 *
 * Die Default Einstellung ist, dass die Anzahl der Referenzen von this, null und die summe von load und store in einer Methode ausgeben wird und am Ende alle Referenzen in der Klasse.
 *
 *
 * Optional CLI argument:
 *  - --outstoreandloadcount Gibt die Referenz aufgeteilt nach load und store aus
 *  - --nonullreferenz Gibt nicht die anzahl der Null Referenzen aus
 *  - --no-methoden Gibt nicht die anzahl der Referenzen für eine Methode aus
 *  - --no-class Gibt nicht die gesamt Anzahl der Referenzen in der Klasse aus
 *  - --infozuvariablen Wenn verfügtbar wird die Anzahl der load und store Referenz und der Variable name ausgeben
 *  - --no-this Gibt keine Anzahl der Referenzen zur this Variable
 */
class vrefAnalysis extends ClassFileAnalysis{
  private val outStoreAndLoadCount: Symbol = Symbol("outstoreandloadcount")
  private val noNullReferenzCount: Symbol = Symbol("nonullreferenz")
  private val noMethodenSymbol: Symbol = Symbol("no-methoden")
  private val noClassSymbol: Symbol = Symbol("no-class")
  private val infoVariablen:Symbol = Symbol("infozuvariablen")
  private val noThis: Symbol = Symbol("no-this")


  /**
   * Die Methode implementiert die Metrik Vref
   *
   * @param classFile Ist ein Interface von Projekt
   * @param project Bekommt die vom Framework gelesende jar file in einer Präsentationsform
   * @param customOptions Einstellungs Möglichkeiten der Analyse
   * @return Das Ergebniss wird in der Liste für die Ausgabe gespeichert
   */
  override def analyzeClassFile(classFile: ClassFile, project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try {

    // CustomOptions
    val noNullReferenz = customOptions.contains(noNullReferenzCount)
    val outStoreAndLoad = customOptions.contains(outStoreAndLoadCount)
    val noMethoden = customOptions.contains(noMethodenSymbol)
    val noClass = customOptions.contains(noClassSymbol)
    val infoZuVariablen = customOptions.contains(infoVariablen)
    val nothis = customOptions.contains(noThis)

  // Variablen
    val methods = classFile.methodsWithBody
    var nullReferenzClass = 0
    var sumLoadClass = 0
    var sumStoreClass = 0
    var sumThisClass = 0
    var sumStoreFieldClass = 0
    var sumLoadFieldClass = 0
    val rlist = new ListBuffer[MetricValue]()

    //Schleife die über alle Methoden geht
    while(methods.hasNext)
      {
        val method = methods.next()
        val loadInstructionmap = mutable.Map[LoadLocalVariableInstruction,Int]()
        val storeInstructionmap = mutable.Map[StoreLocalVariableInstruction,Int]()
        val getfieldCount = mutable.Map[GETFIELD,Int]()
        val putfieldCount = mutable.Map[PUTFIELD,Int]()
        var nullReferenz = 0

        val localtable = method.body.get.localVariableTable

        //Überprüfung auf load und store instruktion
        method.body match {
          case None =>
          case Some(code) =>code.instructions.foreach {
            case load: LoadLocalVariableInstruction =>
              if(loadInstructionmap.contains(load))
              {

                loadInstructionmap(load) = loadInstructionmap(load)+1
              }
              else
              {
                loadInstructionmap += (load -> 1)
              }

            case store: StoreLocalVariableInstruction =>

                if(storeInstructionmap.contains(store))
                {

                  storeInstructionmap(store) = storeInstructionmap(store)+1
                }
                else
                {
                  storeInstructionmap += (store -> 1)
                }

            case nullInstruction: LoadConstantInstruction[Null] => if(nullInstruction.opcode ==1) nullReferenz += 1

            case putfield: PUTFIELD =>
              if(putfieldCount.contains(putfield))
              {

                putfieldCount(putfield) = putfieldCount(putfield)+1
              }
              else
              {
                putfieldCount += (putfield -> 1)
              }

            case getfield: GETFIELD =>
              if(getfieldCount.contains(getfield))
              {

                getfieldCount(getfield) = getfieldCount(getfield)+1
              }
              else
              {
                getfieldCount += (getfield -> 1)
              }
            case _ =>
          }
        }


        var sumLoad = 0
        var sumStore = 0
        var sumthisLoad = 0
        var sumthisStore = 0
        var sumFieldLoad = 0
        var sumFieldStore = 0

        //Summiert einzelne load und store Referenzen auf
        loadInstructionmap.foreach( y => sumLoad += y._2)
        storeInstructionmap.foreach(y => sumStore += y._2)
        getfieldCount.foreach(y => sumFieldLoad += y._2)
        putfieldCount.foreach(y => sumFieldStore += y._2)


        //Guckt auf this Referenzen und zieht die von den aufsummerten  load und store Referenzen
        if(localtable.nonEmpty)
        {

          var thisIndex = -1
          val  list=   localtable.get
          list.foreach(y =>
            if(y.name =="this") {
              thisIndex = y.index
            })
          loadInstructionmap.foreach(y => {
              if(y._1.lvIndex==thisIndex) sumthisLoad = y._2
          })
          storeInstructionmap.foreach(y => {
            if(y._1.lvIndex==thisIndex) sumthisLoad = y._2
          })
          sumLoad -= sumthisLoad
          sumStore -= sumthisStore
        }
        else if(sumFieldLoad !=0 || sumFieldStore !=0) {
          log.warn("this kann nicht aufgelösst werden und wird als Referenz mitgezählt, da die jar die localVariableTable nicht mitliefert")
        }


        if(!noMethoden) {
          if (outStoreAndLoad) {
            rlist += MetricValue(method.fullyQualifiedSignature + " Anzahl der Load Referenzen: ", this.analysisName, sumLoad)
            rlist += MetricValue(method.fullyQualifiedSignature + " Anzahl der Store Referenzen: ", this.analysisName, sumStore)
            rlist += MetricValue(method.fullyQualifiedSignature + " Anzahl der Load Field Referenzen: ", this.analysisName, sumFieldLoad)
            rlist += MetricValue(method.fullyQualifiedSignature + " Anzahl der Store Field Referenzen: ", this.analysisName, sumFieldStore)
          }


          if(localtable.nonEmpty && infoZuVariablen)
            {
              val  list=   localtable.get
              list.foreach(x => {
                  loadInstructionmap.foreach(y=> {
                    if (y._1.lvIndex == x.index) {
                      rlist += MetricValue(method.fullyQualifiedSignature +"Die Anzahl der load Referenzen der Variable " + x.name + " :", this.analysisName, y._2)
                    }
                  } )
                  storeInstructionmap.foreach(y =>{
                  if(y._1.lvIndex==x.index)
                    {
                    rlist += MetricValue(method.fullyQualifiedSignature +"Die Anzahl der store Referenzen der Variable " + x.name +" :", this.analysisName, y._2)
                    }
                  })
              })
            }
          else if(localtable.isEmpty && infoZuVariablen)
            {
              log.warn("localVariableTable ist in der jar File nicht vorhanden und deswegen kann die optionen infoZuVariablen nur eingeschränkt genutzt werden")
            }
            if(infoZuVariablen)
              {
                getfieldCount.foreach(y => rlist += MetricValue(method.fullyQualifiedSignature +"Die Anzahl der load Referenzen der Field in der Methode " + y._1.name +" :", this.analysisName, y._2) )
                putfieldCount.foreach(y => rlist += MetricValue(method.fullyQualifiedSignature +"Die Anzahl der Store Referenzen der Field in der Methode " + y._1.name +" :", this.analysisName, y._2) )
              }

          if (!noNullReferenz) {
            rlist += MetricValue(method.fullyQualifiedSignature + " Anzahl der Null Referenzen: ", this.analysisName, nullReferenz)
          }
          if(!nothis)
            {
              rlist += MetricValue(method.fullyQualifiedSignature + " Anzahl der this Referenzen: ", this.analysisName, sumthisLoad+ sumthisStore )
            }
          rlist += MetricValue(method.fullyQualifiedSignature + " Anzahl der Field Referenzen: ", this.analysisName, sumFieldLoad +sumFieldStore )
          rlist += MetricValue(method.fullyQualifiedSignature + " Anzahl der gesamten Variablen Referenzen : ", this.analysisName, sumStore + sumLoad)
        }
        sumStoreClass += sumStore
        sumLoadClass += sumLoad
        sumLoadFieldClass += sumFieldLoad
        sumStoreFieldClass += sumFieldStore
        sumThisClass += sumthisStore +sumthisLoad
        nullReferenzClass += nullReferenz

      }
      if(!noClass)
        {
          if(!noNullReferenz)
            {
              rlist += MetricValue(classFile.thisType.fqn + " Anzahl aller Null Referenzen: ", this.analysisName, nullReferenzClass)
            }
          if(outStoreAndLoad)
              {
                rlist += MetricValue(classFile.thisType.fqn + " Anzahl der Load Referenzen in der Klasse: ", this.analysisName, sumLoadClass)
                rlist += MetricValue(classFile.thisType.fqn + " Anzahl der Store Referenzen in der Klasse: ", this.analysisName, sumStoreClass)
                rlist += MetricValue(classFile.thisType.fqn + " Anzahl der Load Field Referenzen in der Klasse: ", this.analysisName, sumLoadFieldClass)
                rlist += MetricValue(classFile.thisType.fqn + " Anzahl der Store Field Referenzen in der Klasse: ", this.analysisName, sumStoreClass)
              }
          if(!nothis) {
                rlist += MetricValue(classFile.thisType.fqn + "  Anzahl aller Referenzen von this:", this.analysisName, sumThisClass)
          }
          rlist += MetricValue(classFile.thisType.fqn + "  Anzahl aller Referenzen der Variablen:", this.analysisName, sumLoadClass+sumStoreClass)
          rlist += MetricValue(classFile.thisType.fqn + "  Anzahl aller Field Referenzen :", this.analysisName, sumLoadFieldClass +sumStoreFieldClass)
        }



    rlist.toList



  }





  /**
   * The name for this analysis implementation. Will be used to include and exclude analyses via CLI.
   */
  override def analysisName: String = "methods.vref"
}
