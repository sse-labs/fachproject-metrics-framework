package org.tud.sse.metrics
package impl
import input.CliParser.OptionMap

import org.opalj.br.analyses.Project
import singlefileanalysis.SingleFileAnalysis

import java.net.URL
import scala.util.Try

object NumberOfMethodsAnalysis extends SingleFileAnalysisApplication with SingleFileAnalysis {

  private val countProjectMethodsOnlySymbol: Symbol = Symbol("count-project-only")

  override protected def buildAnalysis(): SingleFileAnalysis = this

  override def analyzeProject(project: Project[URL], customOptions: OptionMap): Try[Iterable[JarFileMetricValue]] = Try {

    val onlyCountProjectMethods = customOptions.contains(countProjectMethodsOnlySymbol)

    val metric = if (onlyCountProjectMethods) project.projectMethodsCount else project.methodsCount

    List(JarFileMetricValue("methods.count", metric))
  }



}
