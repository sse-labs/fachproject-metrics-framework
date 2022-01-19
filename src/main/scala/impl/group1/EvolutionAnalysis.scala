package impl.group1

import java.io.File
import java.net.URL

import org.opalj.br.analyses.Project
import org.tud.sse.metrics.analysis.{MetricValue, MetricsResult, MultiFileAnalysis}
import org.tud.sse.metrics.input.CliParser.OptionMap

import scala.util.Try
import scala.util.control.Breaks.{break, breakable}

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
  var previousPackages: scala.collection.Set[String] = Set[String]()

  var currentClassesInPackages: Map[String, Set[String]] = Map[String, Set[String]]()
  var initialRound: Boolean = true
  var roundCounter: Integer = 0

  private val sym_ext_evo: Symbol = Symbol("ext_evo")
  private val sym_int_evo: Symbol = Symbol("int_evo")

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

    ext_evo = customOptions.contains(sym_ext_evo)
    int_evo = customOptions.contains(sym_int_evo)

    // external Evolution
    // external evolution is the number of classes in newly introduced packages divided by the total amount of classes in the project.
    val currentPackages: scala.collection.Set[String] = project.projectPackages
    val currentNumberOfClasses:Double = project.projectClassFilesCount

    if(!initialRound){
      //Calculate the new packages that doesn't exist in the previous version
      val newPackages = currentPackages.diff(previousPackages)
      log.info(s"new Packages: ${newPackages.size}")
      var numberOfClassesInNewPackages: Double = 0

      newPackages.foreach(p => numberOfClassesInNewPackages += project.classesPerPackage(p).size)
      log.info(s"Classes in new Packages Count: $numberOfClassesInNewPackages")

      if(currentNumberOfClasses != 0){
        log.info(s"Number of Classes: $currentNumberOfClasses")
        externalEvolution = numberOfClassesInNewPackages/currentNumberOfClasses
      }
      log.info(s"externalEvolution: $externalEvolution")

      // calculate the Number of Classes for the new Packages

      entityIdent = s"Difference between: $previousFile and $currentFile"
      log.info(s"entityIdent: $entityIdent")

      // internal Evolution
      // internal Evolution is the number of Packages that exist in both versions and interact with newly added Packages divided by the Number of Packages that exist in both versions.


      // calculate the denominator (maintainedPackagesSize) Number of Packages that exist in previous and current Project.
      val maintainedPackages = currentPackages.intersect(previousPackages)
      val maintainedPackagesSize:Double = maintainedPackages.size
      log.info(s"maintainedPackages: $maintainedPackagesSize")
      // calculate the nominator (interactionWithNewPackages) Packages that exist in both versions (maintainedPackages) and interact with the new added Packages (newPackages).
      // interaction of Packages = if a class in Package A uses Objects (classes) from Package B the Packages interact and vice versa. (similar to Coupling between Objects)
      var interactionsWithNewPackages:Double = 0

      // iterate through classes for each Package to check if there are interactions with the classes from new Packages

      for(maintainedPackage <- maintainedPackages){
        for(newPackage <- newPackages){
          val classesFromMaintainedPackage = project.classesPerPackage(maintainedPackage)
          breakable{
            val classesFromNewPackage = project.classesPerPackage(newPackage)
            for(maintainedClass <- classesFromMaintainedPackage){
              for(newClass <- classesFromNewPackage){
                maintainedClass.methods.foreach(m =>
                  if(m.body.isDefined){
                    if(m.body.get.instructions.mkString.contains(newClass.thisType.simpleName)){
                      log.info(s"maintainedPackageName: $maintainedPackage")
                      log.info(s"newPackageName: $newPackage")
                      log.info(s"Klasseninteraktion von maintainedClass: ${maintainedClass.thisType.fqn} und newClass: ${newClass.thisType.fqn}")
                      interactionsWithNewPackages = interactionsWithNewPackages +1
                      break
                    }
                  }
                  )
              }
            }
          }
        }
      }

      log.info(s"Maintained Packages interactions with new Packages: $interactionsWithNewPackages")

      internalEvolution = interactionsWithNewPackages/maintainedPackagesSize
      log.info(s"internal Evolution: $internalEvolution")
    } else{
      log.info(s"inital Round!!!")
      log.info(s"Custom Option for external Evolution is: ${ext_evo.toString}")
      log.info(s"Custom Option for internal Evolution is: ${int_evo.toString}")
      log.info(s"inital Round!!!")
    }

    // Evolution as defined in the Paper
    evolution = (externalEvolution + internalEvolution)/2
    log.info(s"Evolution: $evolution")

    previousFile = currentFile
    previousPackages = currentPackages
    initialRound = false

    Try(evolution, externalEvolution,internalEvolution,entityIdent)

  }

  /**
   * This method is called after all individual intermediate results have been calculated. It may
   * consume those intermediate results and produce a list of JAR file metrics, which can either
   * concern each JAR file individually, or the batch of analyzed files as a whole.
   *
   * @return List of JarFileMetricsResults
   */
  override def produceMetricValues(): List[MetricsResult] = {

    //value == (evolution, externalEvolution, internalEvolution, entityIdent)
    val evolutionMetric = analysisResultsPerFile.values.map(_.get).
      toList.map(value => MetricValue(value._4,"Evolution",value._1))
    val externalEvoMetric = analysisResultsPerFile.values.map(_.get).
      toList.map(value => MetricValue(value._4,"External Evolution",value._2))
    val internalEvoMetric = analysisResultsPerFile.values.map(_.get).
      toList.map(value => MetricValue(value._4,"Internal Evolution",value._3))

    val listBuffer = collection.mutable.ListBuffer[MetricsResult]()

    // Calculates the metricresult depending on the custom options ext_evo and int_evo
    if(ext_evo && int_evo){
      for(i <- evolutionMetric.indices){
        listBuffer.append(MetricsResult(analysisName, jarDir, success = true,
          metricValues = List(evolutionMetric(i), externalEvoMetric(i), internalEvoMetric(i))))
      }
    } else if(ext_evo) {
      for (i <- evolutionMetric.indices) {
        listBuffer.append(MetricsResult(analysisName, jarDir, success = true,
          metricValues = List(evolutionMetric(i), externalEvoMetric(i))))
      }
    }else if(int_evo){
        for(i <- evolutionMetric.indices){
          listBuffer.append(MetricsResult(analysisName, jarDir, success = true,
            metricValues = List(evolutionMetric(i), externalEvoMetric(i))))
      }
    } else{
      for(i <- evolutionMetric.indices){
        listBuffer.append(MetricsResult(analysisName, jarDir, success = true,
          metricValues = List(evolutionMetric(i))))
      }
    }

    val metricResultList: List[MetricsResult] = listBuffer.toList

    metricResultList
  }

  /**
   * The name for this analysis implementation. Will be used to include and exclude analyses via CLI.
   */
  override def analysisName: String = "Evolution"
}


