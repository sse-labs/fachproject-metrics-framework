package org.tud.sse.metrics
package impl.group4

import analysis.{MethodAnalysis, MetricValue}
import input.CliParser.OptionMap

import org.opalj.br.Method
import org.opalj.br.analyses.Project
import org.opalj.br.instructions.Instruction

import java.net.URL
import scala.collection.mutable.ArrayBuffer
import scala.util.Try

/**
 * MCCAnalysis implements the MCCabe Cyclomatic Complexity (MCCC) metric
 * MCCC= E -N + 2P where E is the number of Edges, N is the number of Nodes and P is the number of connected components which is equal to 1 for single methods
 */
class MCCCAnalysis extends MethodAnalysis {

  /**
   * Calculates the MCCabe Metric by calculating the number of edges and vertices in the control flow graph
   * Since adding more vertices doesn't change e-n, each instruction is a node
   * Each node has one outgoing edge except for return instructions (0), SimpleConditionalBranchInstructions (2) and CompoundConditionalBranchInstructions(number of jumpOffsets)
   *
   * @param m             is a method in a class.
   * @param project       Fully initialize OPAL project representing the JAR file under analysis.
   * @param customOptions Custom analysis options taken from the CLI. Can be used to modify behavior
   *                      of the analysis via command-line.
   * @return Try[Iterable[MetricValue]]  object holding the intermediate result, if successful.
   */
  override def analyzeMethod(m: Method, project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try {
    if (project.isProjectType(m.classFile.thisType) && m.body.isDefined) {
      var e = 0
      var n = 0
      m.body.get.instructions.foreach { instruction =>
        Try {
          if (instruction.isCompoundConditionalBranchInstruction) e = e + instruction.asCompoundConditionalBranchInstruction.jumpOffsets.size
          else if (instruction.isSimpleConditionalBranchInstruction) e = e + 2
          else if (!instruction.isReturnInstruction) e = e + 1
          n = n + 1
        }
      }
      List(MetricValue(m.fullyQualifiedSignature, "method.mccc", e - n + 2))
    } else List.empty
  }

  /**
   * The name for this analysis implementation.
   */
  override def analysisName: String = "mccc"
}
