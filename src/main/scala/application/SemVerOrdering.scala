package org.tud.sse.metrics
package application

import de.skuzzle.semantic.Version

class SemVerOrdering extends Ordering[String] {

  override def compare(x: String, y: String): Int = {

    // If one of the two compared names has no digit, then we cannot find any semantic version numbers, therefore we
    // use regular string comparison
    if(!x.exists(_.isDigit) || !y.exists(_.isDigit)){
      x.compareTo(y)
    } else {
      val xVerString = withoutExtension(x).substring(indexOfFirstNumber(withoutExtension(x)))
      val yVerString = withoutExtension(y).substring(indexOfFirstNumber(withoutExtension(y)))

      val xVer = toVersion(xVerString)
      val yVer = toVersion(yVerString)

      Version.compare(xVer, yVer)
    }
  }

  private def toVersion(s: String): Version = {
    val splitString = s.split("\\.")

    splitString.length match {
      case 1 =>
        Version.create(splitString.head.toInt)
      case 2 =>
        Version.create(splitString(0).toInt, splitString(1).toInt)
      case _ =>
        Version.parseVersion(s, true)
    }
  }

  private def withoutExtension(s: String): String =
    s.replace(".jar", "").replace(".jmod", "")

  private def indexOfFirstNumber(s: String): Int = {
    for(sC <- s){
      if( sC.isDigit ){
         return s.indexOf(sC)
      }
    }

    -1
  }


}
