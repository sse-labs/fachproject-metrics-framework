package org.tud.sse.metrics
package impl

import singlefileanalysis.SingleFileAnalysis

import org.opalj.br.analyses.Project
import org.opalj.br.fpcf.PropertyStoreKey
import org.opalj.tac.cg.{CHACallGraphKey, RTACallGraphKey, XTACallGraphKey}
import org.tud.sse.metrics.input.CliParser.OptionMap

import java.net.URL
import scala.util.{Success, Try}

class CgTestImpl extends SingleFileAnalysis{

  override def analyzeProject(project: Project[URL], customOptions: OptionMap): Try[Iterable[JarFileMetricValue]] = {
    val cg = project.get(XTACallGraphKey)

    val ps = project.get(PropertyStoreKey)
    ps.shutdown()

    var cnt = 0
    for(m <- cg.reachableMethods()) {
      //println(m.toJava)
      cnt += 1
    }

    Runtime.getRuntime.gc()

    Success(List(JarFileMetricValue("reachable-methods.count", cnt), JarFileMetricValue("project.codesize", project.codeSize)))
  }

  /**
   * The name for this analysis implementation. Will be used to report results.
   */
  override def analysisName: String = "callgraph"
}
