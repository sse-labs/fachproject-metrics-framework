package org.tud.sse.metrics
package impl

object ApplicationEntryPoint {

  final def main(args: Array[String]): Unit = {

    if (!args.contains("--multi-file")) {
      SingleFileAnalysisApplication.main(args)
    } else {
      MultiFileAnalysisApplication.main(args.filterNot(_.equals("--multi-file")))
    }

  }

}
