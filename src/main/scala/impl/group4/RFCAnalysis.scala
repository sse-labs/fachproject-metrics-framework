package org.tud.sse.metrics
package impl.group4

import analysis.{ClassFileAnalysis, MetricValue}
import input.CliParser.OptionMap

import org.opalj.br.ClassFile
import org.opalj.br.analyses.Project

import java.net.URL
import scala.util.Try
import scala.collection.mutable.ListBuffer


/**
 * RFCAnalysis implements the Response For a Class (RFC) metric
 */
class RFCAnalysis extends ClassFileAnalysis {


  /**
   * Calculates the value of the RFC Metric by adding the number of methods in the class to the number of methods called in each method
   *
   * @param classFile     is a class in the project.
   * @param project       Fully initialize OPAL project representing the JAR file under analysis.
   * @param customOptions Custom analysis options taken from the CLI. Can be used to modify behavior
   *                      of the analysis via command-line.
   * @return Try[Iterable[MetricValue]]  object holding the intermediate result, if successful.
   */
  override def analyzeClassFile(classFile: ClassFile, project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try {
    var methods = new ListBuffer[String]()
    classFile.methodsWithBody.foreach(method=> {
        methods.append(formatMethodName(method.toString()))
        if(method.body.isDefined) {
          method.body.get.instructions.foreach(instruction => {
            if (instruction != null) if(instruction.isInvocationInstruction) {
              methods.append(formatInstruction(instruction.toString()))
            }
          })
        }

    })
    List(MetricValue(classFile.fqn, analysisName, methods.distinct.size.toDouble) )
  }

  /**
   * Extracts method name from instruction string
   */
  def formatInstruction(instruction: String): String ={
    if (instruction.startsWith("(")){
      return instruction.drop(1).dropRight(1)
    }
    else formatInstruction(instruction.drop(1))
  }

  /**
   * Extracts method name by removing modifiers
   */
  def formatMethodName(name: String): String={
    val modifiers=List("public ","private ","static ","protected ","final ","abstract ","transient ","synchronized ")
    var methodName=name
    modifiers.foreach(m=>{
      methodName=methodName.replace(m,"")
    })
    return methodName
  }


  /**
   * The name for this analysis implementation.
   */
  override def analysisName: String = "rfc"
}
