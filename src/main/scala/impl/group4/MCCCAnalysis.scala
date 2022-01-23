package org.tud.sse.metrics
package impl.group4

import analysis.{MethodAnalysis, MetricValue}
import input.CliParser.OptionMap

import org.opalj.br.Method
import org.opalj.br.analyses.Project
import java.net.URL
import scala.util.Try


//Dummy class so WMC can compile, overwrite when MCCC is done
class MCCCAnalysis extends MethodAnalysis {
  override def analyzeMethod(m: Method, project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try {
    List.empty
  }

  override def analysisName: String = "mccc"
}
