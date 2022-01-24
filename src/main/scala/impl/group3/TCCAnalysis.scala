package org.tud.sse.metrics
package impl.group3

import analysis.{MetricValue, SingleFileAnalysis}
import input.CliParser.OptionMap

import org.opalj.br.analyses.Project

import java.net.URL
import scala.util.Try
import scala.util.control.Breaks.{break, breakable}

class TCCAnalysis extends SingleFileAnalysis {

  override def analyzeProject(project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try {
    // calculate the metric
    /**
     * Visible methods are those that are public.
     * Directly-Connected methods are those that access at least 1 same class variables.
     * The metric measures the ratio between the actual number of visible directly connected methods in a class NDC(C)
     * divided by the number of maximal possible number of connections between the visible methods of a class NP(C).
     */
    var resultList = List[MetricValue]()
    project.allProjectClassFiles.foreach(  // search for all class files, check one by one, c represents one of them
      c => {
        val className = c.thisType.simpleName
        val publicMethodsCount = c.methods.filter(_.isPublic).size       //get the size of public methods
        val numberOfPossibleConnections = (publicMethodsCount * (publicMethodsCount-1) / 2).toDouble   //definition

        val allPublicMethods= c.methods.filter(_.isPublic)         // public method itself
//        println("All public method's body: " + c.methods.filter(_.isPublic).foreach(m => println(m.body + "\n")))

        var directlyConnectedMethodPairs = 0.toDouble
        if(c.fields.nonEmpty){           // according to definition, fields (variables) is what need to calculate for TCC
          allPublicMethods.combinations(2).foreach(seq =>      //random combination(not repeated) of 2 methods
              breakable{
                c.fields.foreach(field =>              //also definition, check if 2 method using same field
                  if(seq.forall( method => method.body.get.toString().contains(className.concat("." + field.name)))){
                    directlyConnectedMethodPairs = directlyConnectedMethodPairs + 1
  //                  println("sequence: " +seq + "\n" + "field: "+ className.concat("." + field.name + "\ncounter: " +directlyConnectedMethodPairs)) // for debugging
                    break
                  }
                )
              }
          )    //below form calculates the final TCC result, and save the method pairs in list
          resultList = MetricValue(className, this.analysisName, directlyConnectedMethodPairs/numberOfPossibleConnections)::resultList
        }else {
          resultList = MetricValue(className, this.analysisName, 0)::resultList
        }
      }
    )
    resultList
  }

  override def analysisName: String = "metric.tcc"
}