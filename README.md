# Framework for Extracting JAR File Metrics
This framework builds on top of the [OPAL project](https://www.opal-project.de/) and provides a 
simple interface for extracting program metrics from JAR files. The framework handles CLI argument
processing, initialization of OPAL project instances, and the creation of result files. This allows
you to focus on the actual generation of program metrics, for which you can rely directly on the
OPAL interface.

## Simple Analysis Example
The following code shows how a simple analysis that counts the number of methods contained in a
JAR file is implemented:

```
import org.tud.sse.metrics.input.CliParser.OptionMap
import org.tud.sse.metrics.analysis.SingleFileAnalysis
import org.tud.sse.metrics.application.SingleFileAnalysisApplication

import org.opalj.br.analyses.Project

import java.net.URL
import scala.util.Try

object NumberOfMethodsAnalysis extends SingleFileAnalysis {

  override protected def buildAnalysis(): SingleFileAnalysis = this

  override def analyzeProject(project: Project[URL],
                              customOptions: OptionMap): Try[Iterable[JarFileMetricValue]] = Try {
    val metric = project.methodsCount

    List(JarFileMetricValue("methods.count", metric))
  }
  
  override def analysisName: String = "method.count"
}
```
To execute this analysis example, you need to register it in an executable `SingleFileAnalysisApplication`,
which may look like this:
```
object MyAnalysisApplication extends SingleFileAnalysisApplication {

  override protected val registeredAnalyses: Seq[SingleFileAnalysis] = 
    Seq(new NumberOfMethodsAnalysis())
}
```

Since in the above example `MyAnalysisApplication` extends `SingleFileAnalysisApplication`, it
already is a self-contained, executable analysis. You can execute it for example with the following
parameters:
```
--batch-mode --out-file out.csv /path/to/jar/folder
```
This will execute the analysis for all JAR files contained in `/path/to/jar/folder`, and create 
a result report containing the file names, and their method count at `out.csv`. Executing the analysis
for a single JAR file is done with the following parameters:
```
--out-file out.csv /path/to/jar/file.jar
```
For more information on default CLI parameters, see **CLI Reference**.

## Complex Analyses
As seen above, a `SingleFileAnalysis` extracts metrics from JAR files independently. It is meant to
be *stateless*, as it does not regard previous analysis results when processing a JAR file. However,
sometimes metrics values depend on multiple JAR files (i.e. all releases of a library), or are 
calculated based on the differences between two JAR files (i.e. number of *new* methods). For these
cases, the `MultiFileAnalysis` can be used.

A `MultiFileAnalysis` first calculates intermediate results of a generic type `T` for every JAR file,
and then processes those intermediate results to calculate one or more metric values. The example below
shows an analysis that calculates the average increase in the number of methods for all
analyzed JAR files. The intermediate result (the absolute increase in methods) is of type `Int`, therefore
the analysis extends `MultiFileAnalysis[Int]`.
```
class AverageMethodDifferenceAnalysis(jarDir: File) 
  extends MultiFileAnalysis[Int](jarDir){

  override def produceAnalysisResultForJAR(project: Project[URL],
                                           lastResult: Option[Int],
                                           customOptions: OptionMap): Try[Int] = {
    Try(project.methodsCount - lastResult.getOrElse(0))
  }

  override def produceMetricValues(): List[JarFileMetricsResult] = {

    val allNewMethodCounts = analysisResultsPerFile.values.map(_.get).sum
    val averageNewMethodCount = allNewMethodCounts.toDouble / analysisResultsPerFile.size
    val averageNewMethodMetric = JarFileMetricValue("newmethodcount.average", averageNewMethodCount)

    List(JarFileMetricsResult(jarDir, success = true, List(averageNewMethodMetric)))
  }
  
  override def analysisName: String = "method-difference.avg"
}
```

To make this example executable, it is sufficient to create an object that extends `MultiFileAnalysisApplication`,
as shown below:
```
object AverageMethodsDifferenceAnalysisApp extends MultiFileAnalysisApplication {

  override protected def buildAnalyses(directory: File): Seq[MultiFileAnalysis] =
    Seq(new AverageMethodDifferenceAnalysis(directory))
}
```

## CLI Reference
The general usage of all analyses is the following:
```
analysisApplication [<options>] <inputfile>
```
The table below lists all parameters that are available by default. Any additional parameters of 
the form `--<option> <value>` or `--<switch>` are passed directly to your analysis implementation
via the parameter `customOptions: OptionMap`, so you can customize your analysis via CLI.

|Option|`SingleFileAnalysis`|`MultiFileAnalysis`|Description|
---|---|---|---
|`--out-file <value>`| Yes | Yes | Specifies that the analysis results should be written to the given file. The result file will be a CSV table.|
|`--is-library` | Yes | Yes | If this switch is set, all OPAL projects will be loaded as libraries (as opposed to applications). This mainly affects analyses that depend on the entrypoints / callgraph of a project.|
|`--opal-logging` | Yes | Yes | If set, all OPAL logging will be forwarded to the console. By default, all OPAL logging output is suppressed.|
|`--batch-mode`| Yes | No | If set, the `<inputfile>` will be interpreted as a directory, and the `SingleFileAnalysis` will be executed for every JAR file contained in that directory.|
|`--exclude-analysis <name>`| Yes | Yes | Excludes the analysis with name `<value>` from the current analysis run. May be specified multiple times to exclude multiple analyses. All non-excluded analyses will be executed. Cannot be used in combination with `--include-analysis`.|
|`--include-analysis <name>`| Yes | Yes | Includes the analysis with name `<value>` for the current analysis run. May be specified multiple times to include multiple analyses. All non-included analyses will not be executed. If used in combination with usage of `--exclude-analysis`, only include specifications will apply.| 