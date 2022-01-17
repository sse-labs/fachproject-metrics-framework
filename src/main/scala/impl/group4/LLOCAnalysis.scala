package org.tud.sse.metrics
package impl.group4

import analysis.{MethodAnalysis, MetricValue}
import input.CliParser.OptionMap

import org.opalj.br.Method
import org.opalj.br.analyses.Project

import java.net.URL
import scala.util.Try

/**
 * LLOCAnalysis implements the Logical Lines Of Code (LLOC) metric
 */
class LLOCAnalysis extends MethodAnalysis{
  //if this option is selected the Analysis will return the LLOC of the bytecode instead of the source code
  private val byteCodeSymbol: Symbol = Symbol("bytecode-lloc")

  /**
   * Calculates the value of the LLOC Metric by calculating the number of elements in the line number table
   *
   * @param m             is a method in a class.
   * @param project       Fully initialize OPAL project representing the JAR file under analysis.
   * @param customOptions Custom analysis options taken from the CLI. Can be used to modify behavior
   *                      of the analysis via command-line.
   * @return Try[Iterable[MetricValue]]  object holding the intermediate result, if successful.
   */
  override def analyzeMethod(m: Method, project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try {
    var  metric=0;
    if (project.isProjectType(m.classFile.thisType) && m.body.isDefined && m.body.get.lineNumberTable.isDefined) {
      if (customOptions.contains(byteCodeSymbol)) m.body.get.instructions.foreach(i => { if (i!=null) metric+=1})
      else {
        metric = m.body.get.lineNumberTable.get.lineNumbers.size
        //ignore return instruction
        if(m.returnType.isVoidType){metric=metric-1}
      }
      List(MetricValue(m.fullyQualifiedSignature, this.analysisName, metric))
    }
    else List.empty}

  /**
   * The name for this analysis implementation. Will be used to include and exclude analyses via CLI.
   */
  override def analysisName: String = "lloc"
}