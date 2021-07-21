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
import org.tud.sse.metrics.singlefileanalysis.SingleFileAnalysis
import org.tud.sse.metrics.SingleFileAnalysisApplication

import org.opalj.br.analyses.Project

import java.net.URL
import scala.util.Try

object NumberOfMethodsAnalysis extends SingleFileAnalysisApplication with SingleFileAnalysis {

  override protected def buildAnalysis(): SingleFileAnalysis = this

  override def analyzeProject(project: Project[URL],
                              customOptions: OptionMap): Try[Iterable[JarFileMetricValue]] = Try {
    val metric = project.methodsCount

    List(JarFileMetricValue("methods.count", metric))
  }
}
```
Since in the above example `NumberOfMethodsAnalysis` extends `SingleFileAnalysisApplication`, it
already is a self-contained, executable analysis. You can execute it for example with the following
parameters:
```
--batch-mode --out-file out.csv /path/to/jar/folder
```
This will execute the analysis for all JAR files contained in `/path/to/jar/folder`, and create 
a result report containing the file names and their method count at `out.csv`. Executing the analysis
for a single JAR file is done with the following parameters:
```
--out-file out.csv /path/to/jar/file.jar
```
For more information on default CLI parameters, see **CLI Reference**.

## Complex Analyses
As seen above, a `SingleFileAnalysis` extracts metrics from JAR files independently. It is meant to
be *stateless*, as it does not regard previous analysis results when processing a JAR file. However,
sometimes metrics values depend on multiple JAR files (ie. all releases of a library), or are 
calculated based on the differences between two JAR files (ie. number of *new* methods). For these
cases, the `MultiFileAnalysis` can be used.

TODO

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
|`--batch-mode` | Yes | No | If set, the `<inputfile>` will be interpreted as a directory, and the `SingleFileAnalysis` will be executed for every JAR file contained in that directory.|