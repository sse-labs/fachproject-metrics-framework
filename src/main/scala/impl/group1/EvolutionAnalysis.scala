package impl.group1

import java.io.File
import java.net.URL

import org.opalj.br.analyses.Project
import org.tud.sse.metrics.analysis.{MetricsResult, MultiFileAnalysis}
import org.tud.sse.metrics.input.CliParser.OptionMap

import scala.util.Try

/**
 * the Evolution metric containing External Evolution and Internal Evolution
 * as defined in the paper "Identifying evolution patterns: a metrics-based approach for
 * external library reuse" by Eleni Constantinou und Ioannis Stamelos
 *
 * @param jarDir directory containing the different jar-file versions of a software to analyze
 *               Optional CLI arguments:
 *  - --ext_evo outputs values for partial metric external_evolution: metric determines newly added Packages in subsequent Softwareprojects
 *  - --int_evo outputs values for partial metric internal_evolution: "metric measures the ratio of packages, which interact with new packages"
 *  - --verbose log more information: file names, simple statistics, metric results
 *  - --ultra_verbose (like verbose) + list all package names and class (names) contained in each package
 *
 */
class EvolutionAnalysis(jarDir: File) extends MultiFileAnalysis[(Double,Double,Double,String)](jarDir) {

  var previousFile: String = ""
  var currentFile: String = ""
  var ext_evo: Boolean = false
  var int_evo: Boolean = false
  var previousPackages: Set[String] = Set[String]()

  var currentClassesInPackages: Map[String, Set[String]] = Map[String, Set[String]]()
  var initialRound: Boolean = true
  var roundCounter: Integer = 0






  /**
   * Calculate the Evolution metric containing External Evolution and Internal Evolution
   * as defined in the paper "Identifying evolution patterns: a metrics-based approach for
   * external library reuse" by Eleni Constantinou und Ioannis Stamelos
   *
   * @param project       Fully initialized OPAL project representing the JAR file under analysis
   * @param lastResult    Option that contains the intermediate result for the previous JAR file, if
   *                      available. This makes differential analyses easier to implement. This argument
   *                      may be None if either this is the first JAR file or the last calculation failed.
   * @param customOptions Custom analysis options taken from the CLI. Can be used to modify behavior
   *                      of the analysis via command-line
   * @return Try[T] object holding the intermediate result, if successful
   */
  override protected def produceAnalysisResultForJAR(project: Project[URL],file: File,
                                                     lastResult: Option[(Double,Double,Double,String)],
                                                     customOptions: OptionMap):Try[(Double,Double,Double,String)] = {
    currentFile = file.toString
    produceAnalysisResultForJAR(project,lastResult,customOptions)
  }

  override def produceAnalysisResultForJAR(project: Project[URL],
                                           lastResult: Option[(Double, Double, Double, String)],
                                           customOptions: OptionMap): Try[(Double, Double, Double, String)] = {
    var evolution: Double = 1
    var externalEvolution: Double = 1
    var internalEvolution: Double = 1
    var entityIdent: String = ""

    val currentPackages: scala.collection.Set[String] = project.projectPackages
    val currentNumberOfClasses = project.projectClassFilesCount

    //Calculate the new packages that doesn't exist in the previous version
    val newPackages = currentPackages.diff(previousPackages)
    log.info("new Packages: " + newPackages.toString())
    var numberOfClassesInNewPackages = 0

    // calculate the Number of Classes for the new Packages

    newPackages.foreach(p => numberOfClassesInNewPackages += project.classesPerPackage(p).size)
    log.info("Classes in new Packages Count: " + numberOfClassesInNewPackages)
    if(currentNumberOfClasses != 0){
      externalEvolution = numberOfClassesInNewPackages/currentNumberOfClasses
    }

    log.info("externalEvolution: " + externalEvolution)

    entityIdent = "Difference between: " + previousFile + " and " + currentFile



    Try(evolution, externalEvolution,internalEvolution,entityIdent)

  }

  /**
   * This method is called after all individual intermediate results have been calculated. It may
   * consume those intermediate results and produce a list of JAR file metrics, which can either
   * concern each JAR file individually, or the batch of analyzed files as a whole.
   *
   * @return List of JarFileMetricsResults
   */
  override def produceMetricValues(): List[MetricsResult] = ???

  /**
   * The name for this analysis implementation. Will be used to include and exclude analyses via CLI.
   */
  override def analysisName: String = "Evolution"
}


