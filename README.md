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

##Complex Analyses

##CLI Reference