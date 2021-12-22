package impl.group2

import java.net.URL

import org.opalj.br.{ClassFile, Method}
import org.opalj.br.analyses.Project
import org.tud.sse.metrics.analysis.{ClassFileAnalysis, MetricValue}
import org.tud.sse.metrics.input.CliParser.OptionMap
import org.opalj.br.instructions.FieldAccess
import scala.collection.mutable
import scala.util.Try

class LackOfCohesionInMethodsAnalysis extends ClassFileAnalysis{

  override def analyzeClassFile(classFile: ClassFile, project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try {
    // zu methods gehoeren keine Konstruktoren, statische Methoden und statische Initialisierer
    val methods = classFile.instanceMethods.toList
    var metric = 0 // Wert der LCOM-Metrik

    // es wird fuer alle Methodenpaare geprueft, ob die Attributmengen disjunkt sind
    // wenn ja, wird metric um 1 erhöht, sonst um 1 verringert
    var methods2 = methods
    for (i <- methods) {
      methods2 = methods2.tail
      for (j <- methods2){
        if (!i.equals(j)){
          if (disjunctAttributeSets(i, j)) {
            metric = metric + 1
          } else {
            metric = metric - 1
          }
        }
      }
    }

    // in der Definition ist LCOM nie negativ, das Minimum ist 0
    if (metric < 0) {
      metric = 0
    }

    // disjunctAttributeSets prueft, ob die Methoden m1 und m2 ausschliesslich auf unterschiedliche Attribute zugreifen
    def disjunctAttributeSets(m1: Method, m2: Method): Boolean = {
      var bool = true
      for (i <- getUsedAttributes(m1); j <- getUsedAttributes(m2)) {
        if (i.equals(j)) {
          bool = false
        }
      }
      bool
    }

    // getUsedAttributes gibt ein Menge mit den von der uebergebenen Methode verwendeten Attribute zurueck
    def getUsedAttributes (method: Method): mutable.Set[String] ={
      val attributes = mutable.Set[String]()
      method.body match {
        case None =>
        case Some(code) => code.instructions.foreach {
          case fieldAccess: FieldAccess => attributes.add(fieldAccess.name)
          case _ =>
        }
      }
      attributes
    }
    List(MetricValue(classFile.thisType.simpleName, "classes.LCOM", metric))
  }

  override def analysisName: String = "classes.LCOM"

}
