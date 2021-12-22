package impl.group2

import java.net.URL

import org.opalj.br.{ClassFile, Method}
import org.opalj.br.analyses.Project
import org.tud.sse.metrics.analysis.{ClassFileAnalysis, MetricValue}
import org.tud.sse.metrics.input.CliParser.OptionMap
import org.opalj.br.instructions.FieldAccess
import scala.collection.mutable
import scala.util.Try

/*
 * Implementierung der "LackOfCohesionInMethodsAnalysis" (LCOM)
 *
 * Implementierung beruht auf der Defintion nach Chidamber und Kemerer aus
 * "A Metrics Suite for Object Oriented Design" (1994)
 *
 * Die Liste mit den Methoden der Klasse beinhaltet keine Konstruktoren, keine statischen Methoden und
 * keine statischen Initialisierer. Ausserdem sind keine geerbten Methoden in der Liste enthalten.
 * Die Attributmengen der Methoden beinhalten nur Attribute, die in der Klasse (fuer die der LCOM-Wert
 * berechnet wird) deklariert werden. Das bedeutet insbesondere, dass keine geerbten Attribute in der Menge
 * enthalten sind.
 *
 * Fuer LCOM gilt (falls Ergebnis >=0): LCOM =    #Methodenpaare, mit disjunkten Attributmengen
 *                                             - #Methodenpaare, mit nicht disjuntkten Attributmengen
 * Wenn das Ergebnis <=0 ist, ist LCOM = 0
 *
 */

class LackOfCohesionInMethodsAnalysis extends ClassFileAnalysis{

  override def analyzeClassFile(classFile: ClassFile, project: Project[URL], customOptions: OptionMap): Try[Iterable[MetricValue]] = Try {
    // methods beinhaltet keine Konstruktoren, keine statischen Methoden und keine statischen Initialisierer
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
          case fieldAccess: FieldAccess => {
            if (fieldAccess.declaringClass.equals(method.classFile.thisType)){
              attributes.add(fieldAccess.name)
            }
          }
          case _ =>
        }
      }
      attributes
    }
    List(MetricValue(classFile.thisType.simpleName, "classes.LCOM", metric))
  }

  override def analysisName: String = "classes.LCOM"

}
