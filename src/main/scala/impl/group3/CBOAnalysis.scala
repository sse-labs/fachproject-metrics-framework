package org.tud.sse.metrics
package impl.group3


import analysis.{MetricValue, SingleFileAnalysis}
import input.CliParser.OptionMap

import org.opalj.br.analyses.Project

import java.net.URL
import scala.util.Try

class CBOAnalysis extends SingleFileAnalysis{
  /** checked all classfile in jar and calculate the number of coupled classes, which have objects of another class
   * and the objects are also used in the class
   * */
  def analyzeProject(project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try {
    var resultList = List[MetricValue]()
    //Creating a set of all classes in the project
    var allProjectClasses = Set[String]()
    project.allProjectClassFiles.foreach(c=>
      allProjectClasses += c.fqn.replaceAll("/", ".")
    )
    //Iterating over all the methods of each class and find the classes which they access to. CBO is defined as the number of those classes which are being accessed from this class.
    project.allProjectClassFiles.foreach(c =>{
      log.info("Calculating CBO of class: " + c.fqn)
      var coupledWith = Set[String]()
      c.methods.foreach( m=>{
        //         log.info("method name: " + m.name)
        if(m.body.isDefined) {
          //           m.body.get.instructions.foreach(i => if (i != null) println("-- " + i))
          allProjectClasses.foreach(classInProject =>
            if(classInProject!= c.fqn.replaceAll("/", ".") && m.body.get.instructions.mkString.contains(classInProject))
              coupledWith += classInProject
          )
        }
      }
      )
      log.info("coupled with: " + coupledWith)
      val className = c.thisType.simpleName
      resultList = MetricValue(className, this.analysisName, coupledWith.size)::resultList
    }
    )
    resultList
  }

  /**
   * The name for this analysis implementation. Will be used to include and exclude analyses via CLI.
   */
  override def analysisName: String = "metric.cbo"
}