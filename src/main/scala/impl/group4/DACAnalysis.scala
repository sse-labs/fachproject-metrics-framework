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
 * DACAnalysis implements the DAC metric as described in https://ieeexplore.ieee.org/document/748920
 */

class DACAnalysis extends ClassFileAnalysis {

  /**
   * This method calculates the value of the DAC metric by counting reference type fields in the class.
   *
   * @param classFile     is a class in the project.
   * @param project       Fully initialize OPAL project representing the JAR file under analysis.
   * @param customOptions Custom analysis options taken from the CLI. Can be used to modify behavior
   *                      of the analysis via command-line.
   * @return Try[Iterable[MetricValue]]  object holding the intermediate result, if successful.
   */
  override def analyzeClassFile(classFile: ClassFile, project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try {
    var classes = new ListBuffer[String]()

    //search for ADTs defined in fields
    classFile.fields.foreach(f=>{
      if (f.fieldType.isReferenceType & !classes.contains(f.asField.fieldType.toString)) {
        classes.append(f.asField.fieldType.toString)
      }
    })

    //search for ADTs defined in methods
    classFile.methods.foreach(method=>Try{
      method.body.get.localVariableTable.foreach(lv=>Try{
        lv.foreach(v=> {
          if (v.fieldType.isReferenceType & !classes.contains(v.fieldType.toString)) classes.append(v.fieldType.toString)
        })
      })
    })

    List(MetricValue(classFile.fqn, analysisName, classes.size))
  }

  override def analysisName: String = "dac"
}
