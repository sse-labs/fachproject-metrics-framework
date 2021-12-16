package org.tud.sse.metrics
package impl.group4

import analysis.{MethodAnalysis, MetricValue}
import input.CliParser.OptionMap

import org.opalj.br.Method
import org.opalj.br.analyses.Project

import java.net.URL
import scala.util.Try

class LOCphyAnalysis extends MethodAnalysis{
  override def analyzeMethod(m: Method, project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try{
    if(project.isProjectType(m.classFile.thisType)){
      var result=0
      if(m.body.isDefined) {
        if(m.body.get.lineNumberTable.isDefined) {
          result = m.body.get.lineNumberTable.get.lineNumbers.length
          // Voids have 1 extra line probably because of the return line https://stackoverflow.com/questions/42673288/does-return-at-the-end-of-a-void-method-in-java-have-any-impact-on-performanc/42673380
          /*if(m.returnType==VoidType){
            result=result-1
          }*/
        }
        List(MetricValue(m.fullyQualifiedSignature, "method.locphy", result))
      }else{
        log.warn(m.fullyQualifiedSignature+" could not be analyzed")
        List.empty
      }
    } else {
      List.empty
    }


  }

  /**
   * The name for this analysis implementation. Will be used to include and exclude analyses via CLI.
   */
  override def analysisName: String = "method.locphy"
}
