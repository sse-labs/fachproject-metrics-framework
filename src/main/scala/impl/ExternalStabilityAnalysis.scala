package org.tud.sse.metrics
package impl

import analysis.{MetricValue, MetricsResult, MultiFileAnalysis}
import input.CliParser.{OptionMap, additionalClassesDirSymbol, inFileSymbol}

import org.opalj.br.ObjectType
import org.opalj.br.analyses.Project
import org.opalj.da.ClassFile

import java.io.File
import java.net.URL
import scala.collection.mutable.ListBuffer
import scala.util.Try

import scala.collection.Set
import scala.collection.Map

/**
 * the ExternalStability metric
 * as defined in the paper "Identifying evolution patterns: a metrics-based approach for
 * external library reuse" by Eleni Constantinou und Ioannis Stamelos
 * @param jarDir directory containing the different jar-file versions of a software to analyze
 */
class ExternalStabilityAnalysis(jarDir: File) extends MultiFileAnalysis[(Double, Double, Double, String)](jarDir) {

  var previousPackages: Set[String] = Set[String]()
  var previousPackagesSize: Map[String, Int] = Map[String, Int]()
  var previousClassesInPackages: Map[String, Set[ObjectType]] = Map[String, Set[ObjectType]]()
  var previousNumberOfClasses: Int = 0
  var firstRound: Boolean = true
  var counter: Double = 0
  var verbose: Boolean = true

  /**
   * Calculcate the ExternalStability metric
   * as defined in the paper "Identifying evolution patterns: a metrics-based approach for
   * external library reuse" by Eleni Constantinou und Ioannis Stamelos
   * @param project Fully initialized OPAL project representing the JAR file under analysis
   * @param lastResult Option that contains the intermediate result for the previous JAR file, if
   *                   available. This makes differential analyses easier to implement. This argument
   *                   may be None if either this is the first JAR file or the last calculation failed.
   * @param customOptions Custom analysis options taken from the CLI. Can be used to modify behavior
   *                      of the analysis via command-line
   *  @return Try[T] object holding the intermediate result, if successful
   */
  override def produceAnalysisResultForJAR(project: Project[URL],
                                           lastResult: Option[(Double, Double, Double, String)],
                                           customOptions: OptionMap): Try[(Double, Double, Double, String)] = {
    //The ExternalStability metric consists of two parts ES_rem and ES_red
    //ES_rem is a metric that measures the relation between the number of classes which where contained
    //in removed packages and the total number of classes in a jar

    //Calculate ES_rem: first part of the ExternalStability metric

    val currentPackages: scala.collection.Set[String] = project.projectPackages
    val currentNumberOfClasses = project.projectClassFilesCount


    //Calculate the packages which have been removed in the newer version
    val packagesRemoved = previousPackages.diff(currentPackages)

    //Sum the number of the classes in the removed packages
    var numberOfClassesRemoved = 0
    // foreach removed packages get the size and sum all the sizes up
    packagesRemoved.foreach(p => numberOfClassesRemoved += previousPackagesSize(p))

    //calculate current round number
    counter += 1

    if(verbose)
     {
       log.info("Round: " + counter)
       log.info("Number of current Packages: " + currentPackages.size)
       log.info("Number of previous Packages: " + previousPackages.size)
       log.info("Number of Classes in Project: " + currentNumberOfClasses)
       log.info("Number of previousClasses in Project: " + previousNumberOfClasses)
       log.info("Number of Packages removed: " + packagesRemoved.size)
       log.info("Number of Classes Removed: " + numberOfClassesRemoved)
     }

    //set a default value for ES_rem
    var ES_rem: Double = 1.0

    //Nothing to compare in the first round in a differential metric
    //Needs the data of two consequential jar-file versions
    //In the first round only data will be prepared for the differential analysis,starting from the second round
    if (!firstRound && previousNumberOfClasses > 0) {
      ES_rem = 1.0 - numberOfClassesRemoved.toDouble / previousNumberOfClasses.toDouble
    }

    if(verbose)
    {
      log.info("ES_rem: " + ES_rem)
    }


    //Calculate the second partial metric ES_red
    // It is necessary to have access to the package-class relationship information in the following round
    // This information needs to be calculated and than needs to be stored in variable outside of this function
    var currentClassesInPackages: Map[String, Set[ObjectType]] = Map[String, Set[ObjectType]]()

    // For every package...
    for (curPack <- currentPackages) {
      var classes = Set[ObjectType]()
      // ...get every contained class
      for (curClass <- project.classesPerPackage(curPack)) {
        //store all the classes of a package in a Set[]
        classes = classes + curClass.thisType
      }
      //..and store package-classes relationship information in a Map[]...
      currentClassesInPackages = currentClassesInPackages + (curPack -> classes)
    }




    var sum = 0
    //set a default value for ES_rem
    var ES_red: Double = 1.0

    //Nothing to compare in the first round in a differential metric
    //Needs the data of two consequential jar-file versions
    //In the first round only data will be prepared for the differential analysis,starting from the second round
    if (!firstRound && previousNumberOfClasses > 0) {
      //calculate packages, which exist in both jar-versions
      val packagesShared = previousPackages.intersect(currentPackages)
      // for every shared package calculate the number of classes removed from the subsequent package
      for (packShared <- packagesShared) {
        // calculate the classes, which where removed from a shared package
        val classesRemoved = previousClassesInPackages(packShared).diff(currentClassesInPackages(packShared))
        sum += classesRemoved.size
      }
    ES_red = 1.0 - sum.toDouble / previousNumberOfClasses.toDouble
    }

    if(verbose)
      {
        log.info("ES_red: " + ES_red)
      }

    previousPackages = currentPackages
    previousNumberOfClasses = currentNumberOfClasses
    firstRound = false
    previousPackagesSize = previousPackagesSize.empty
    currentPackages.foreach(p => previousPackagesSize = previousPackagesSize + (p -> project.classesPerPackage(p).size))
    //List all classes as organized in packages

    //Store the package-class relationship information for the processing in the next round
    previousClassesInPackages = currentClassesInPackages

    //The final StabilityMetric is calculated by ES_rem * ES_red
    //The result contains the StabilityMetric, the two partial metrics and round in which these metrics were calculated
    Try(ES_rem * ES_red, ES_rem, ES_red, counter.toString)

  }

  override def produceMetricValues(): List[MetricsResult] = {

    val stability_values = analysisResultsPerFile.values.map(_.get).toList.map(a => MetricValue(a._4, "ES_stability", a._1))//, MetricValue(a._4, "ES_Removed", a._2), MetricValue(a._4, "ES_Remained", a._3))
      val removed_values = analysisResultsPerFile.values.map(_.get).toList.map(a => MetricValue(a._4, "ES_Removed", a._2))
      val remained_values = analysisResultsPerFile.values.map(_.get).toList.map(a => MetricValue(a._4, "ES_Remained", a._3))

    val LBUff = collection.mutable.ListBuffer[MetricsResult]()
    for (i <- stability_values.indices) {
      LBUff.append(MetricsResult(analysisName, jarDir, success = true, metricValues = List(stability_values(i),removed_values(i),remained_values(i))))
    }
    val metricList: List[MetricsResult] = LBUff.toList.sortWith((a, b) => a.metricValues.toSeq.head.entityIdent > b.metricValues.toSeq.head.entityIdent)
    metricList.foreach(a => println(a.toString))
    metricList
  }

  override def analysisName: String = "External Stability"


}
