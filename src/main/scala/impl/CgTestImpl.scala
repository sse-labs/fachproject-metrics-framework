package org.tud.sse.metrics
package impl

import analysis.{MetricValue, SingleFileAnalysis}
import input.CliParser.OptionMap

import org.opalj.br.analyses.Project
import org.opalj.br.fpcf.PropertyStoreKey
import org.opalj.tac.cg.XTACallGraphKey

import java.net.URL
import scala.util.{Success, Try}

class CgTestImpl extends SingleFileAnalysis{

  override def analyzeProject(project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = {
    val cg = project.get(XTACallGraphKey)

    val ps = project.get(PropertyStoreKey)
    ps.shutdown()

    var cnt = 0
    for(_ <- cg.reachableMethods()) {
      //println(m.toJava)
      cnt += 1
    }

    Runtime.getRuntime.gc()

    Success(List(MetricValue("file", "reachable-methods.count", cnt), MetricValue("file", "project.codesize", project.codeSize)))
  }

  /**
   * The name for this analysis implementation. Will be used to report results.
   */
  override def analysisName: String = "callgraph"
}
