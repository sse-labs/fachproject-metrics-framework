package org.tud.sse.metrics
package impl.group3

import analysis.{MetricValue, SingleFileAnalysis}
import input.CliParser.OptionMap

import org.opalj.br.analyses.Project

import java.net.URL
import scala.:+
import scala.util.Try
import scala.util.control.Breaks.{break, breakable}

class LCCAnalysis extends SingleFileAnalysis {

  override def analyzeProject(project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try {
    /**
     * Visible methods are those that are public.
     * Directly-Connected methods are those that access at least 1 same class variables.
     * When 2 methods are not directly connected, but they are connected via other methods, we call them indirectly connected.
     * Example: A - B - C are direct connections. A is indirectly connected to C (via B).
     * The metric measures the ratio between the actual number of visible directly connected methods in a class NDC(C)
     * plus the number of indirect connections NIC(C)
     * divided by the number of maximal possible number of connections between the visible methods of a class NP(C).
     */
    var resultList = List[MetricValue]()
    project.allProjectClassFiles.foreach(
      c => {
        val className = c.thisType.simpleName
        val publicMethodsCount = c.methods.filter(_.isPublic).size
        val numberOfPossibleConnections = (publicMethodsCount * (publicMethodsCount-1) / 2).toDouble
        val allPublicMethods= c.methods.filter(_.isPublic)
        var directlyConnectedMethodPairs = 0.toDouble
        val map = scala.collection.mutable.Map[String, List[String]]()
        if(c.fields.nonEmpty){
          allPublicMethods.combinations(2).foreach(seq =>{
            breakable{
              c.fields.foreach(field =>
                if(seq.forall( method => method.body.get.toString().contains(className.concat("." + field.name)))){
                  directlyConnectedMethodPairs += 1
                  map.get(seq.head.name) match {
                    case Some(l : List[String]) => map.update(seq.head.name, l :+ seq.last.name)
                    case None => map.update(seq.head.name, List(seq.last.name))
                  }
                  map.get(seq.last.name) match {
                    case Some(l : List[String]) =>
                      map.update(seq.last.name, l :+ seq.head.name)
                    case None =>
                      map.update(seq.last.name, List(seq.head.name))
                  }
                  break
                }
              )
            }
          }
          )

          var indirectConnectionsCount = 0.toDouble
          val indirectConnMap = scala.collection.mutable.Map[String, Set[String]]()
          /**
           * Find the amount of indirect connections from the map (If A->B & B->C then A,C are indirectly connected)
           * Needs extra testing!
           */
            map.foreach(mapEntry1 =>
              map.foreach(mapEntry2 =>
                if(mapEntry1!=mapEntry2){
                  mapEntry1._2.foreach(valueOfMap1 =>
                    if(mapEntry2._1 == valueOfMap1){
                      mapEntry2._2.foreach(valueOfMap2 =>
                        if(!mapEntry1._1.equals(valueOfMap2) && !mapEntry1._2.contains(valueOfMap2)) {
                          indirectConnMap.get(mapEntry1._1) match {
                            case Some(l : Set[String]) => indirectConnMap.update(mapEntry1._1, l.+(valueOfMap2))
                            case None => indirectConnMap.update(mapEntry1._1, Set(valueOfMap2))
                          }
                        }
                      )
                    }
                  )
                }
              )
            )
          indirectConnMap.foreach(entry => {
            indirectConnectionsCount += entry._2.size
          })
          val resultForThisClass = (directlyConnectedMethodPairs+indirectConnectionsCount/2)/numberOfPossibleConnections
          resultList = MetricValue(className, this.analysisName, resultForThisClass)::resultList
        }else {
          resultList = MetricValue(className, this.analysisName, 0)::resultList
        }
      }
    )
    resultList
  }

  override def analysisName: String = "metric.lcc"
}