package impl.group1

import java.io.File

import org.scalatest.{FlatSpec, Matchers}


class EvolutionAnalysisTest extends FlatSpec with Matchers{

  val dirWithTestFiles = new File(getClass.getResource("/group1/commons-collections").getPath)
  val evolutionAnalysisToTest = new EvolutionAnalysis(dirWithTestFiles)



}
