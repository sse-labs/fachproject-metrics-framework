package org.tud.sse.metrics
package application

import org.slf4j.{Logger, LoggerFactory}

import java.io.File
import scala.util.Try

/**
 * Simple ordering based on Semantic versioning. We have that:
 * 1.1.0 < 1.10.0
 * 1.1 < 1.1.1
 * 1.1-ALPHA < 1.1
 * 1.1-ALPHA < 1.1-BETA
 * 1.1.0 = 1.1
 * 1.1-ABC.1-ALPHA = 1.1.1-ALPHA (Only last suffix is considered, "intermediate suffixes" are lost)
 *
 * Currently, File names need to have the following format:
 *  <text>-<version>.jar
 *
 * NOTE: Currently a "-" in the "version" part is not allowed. As soon as we find a better way to encode the version inside
 * the filename, we can allow "-" in version numbers again. Then, we only need to adapt "getVersionFromFile(x:File): String".
 */
class SemVerOrdering extends Ordering[File]{

  private final val log: Logger = LoggerFactory.getLogger(getClass)

  private class SemanticVersion(rawVersion: String){

    private var parts: List[Int] = List.empty
    private var suffix: Option[String] = None

    parse()

    def hasSuffix: Boolean = suffix.nonEmpty

    def getSuffix: String = suffix.get

    def getPart(i: Int): Int = parts(i)

    def noOfParts: Int = parts.length

    private def parse(): Unit ={
      // Split version at dots
      val split = rawVersion.split(".")

      this.parts = split.map { part =>
        // Try to convert each part directly into an integer. If that's possible, we are done for this part already
        if(Try(part.toInt).isSuccess){
          part.toInt
        } else if(part.contains("-")) { // If converting to integer fails, it may be because the part looks like this: <number>-<text>. We try to parse this, too.
          val numAndSuffix = part.split("-")
          // Only accept this case if there is exactly one "-" and the first part is a number
          if(numAndSuffix.length == 2 && Try(numAndSuffix(0).toInt).isSuccess){
            if(numAndSuffix(1).nonEmpty) this.suffix = Some(numAndSuffix(1)) // Save suffix. Only the last suffix will be saved after all parsing is done
            numAndSuffix(0).toInt
          } else {
            log.warn(s"Failed to parse SemVer part: $part (of Version $rawVersion)")
            throw new IllegalStateException()
          }
        } else {
          log.warn(s"Failed to parse SemVer part: $part (of Version $rawVersion)")
          throw new IllegalStateException()
        }
      }.toList
    }


  }



  override def compare(x: File, y: File): Int = {
    val xVerOpt = getVersionFromFile(x)
    val yVerOpt = getVersionFromFile(y)

    // If we failed to extract versions from file for both files, they are "equal"
    if(xVerOpt.isEmpty && yVerOpt.isEmpty) 0
    // If only x failed to extract, "y is bigger"
    else if (xVerOpt.isEmpty) -1
    // If only y failed to extract, "x is bigger"
    else if (yVerOpt.isEmpty) +1
    // If both were extracted, we do some actual ordering
    else {
      // Try to parse both strings into semantic versions
      val xVerT = Try(new SemanticVersion(xVerOpt.get))
      val yVerT = Try(new SemanticVersion(yVerOpt.get))

      // As seen before: If both failed to parse, they are "equal", etc..
      if(xVerT.isFailure && yVerT.isFailure) 0
      else if(xVerT.isFailure) -1
      else if(yVerT.isFailure) +1
      else {
        val xVer = xVerT.get
        val yVer = yVerT.get

        for(i <- Range(0, Math.min(xVer.noOfParts, yVer.noOfParts))){
          val xPart = xVer.getPart(i)
          val yPart = yVer.getPart(i)
          // If at the same part x is bigger than y, then x is the bigger version number
          if(xPart > yPart) return +1
          // If at the same part y is bigger than x, then y is the bigger version number
          else if (xPart < yPart) return -1
          // If at the same part x and y are equal, we need to check the next part
        }

        // We came here, so versions have been equal for their minimum length. We might have 1.1 & 1.1 OR 1.1 & 1.1.2 OR 1.1.2 & 1.1

        // If x has more parts than y, x is bigger IF AND ONLY IF one of those "extra parts" is larger than 0. So 1.1.0 IS EQUAL TO 1.1, but 1.1.0.0.0.0.4 IS LARGER THAN 1.1
        if(xVer.noOfParts > yVer.noOfParts){
          val startIndex = yVer.noOfParts
          for(i <- Range(startIndex, xVer.noOfParts)){
            val part = xVer.getPart(i)
            if(part > 0) return +1
          }
        } else if(yVer.noOfParts > xVer.noOfParts){
          // If y has more parts than x, y is bigger IF AND ONLY IF one of those "extra parts" is larger than 0. So 1.1 IS EQUAL TO 1.1.0, but 1.1 IS LESS THAN 1.1.0.0.0.0.4
          val startIndex = xVer.noOfParts
          for (i <- Range(startIndex, yVer.noOfParts)) {
            val part = yVer.getPart(i)
            if (part > 0) return +1
          }
        }

        // We came here, so versions are equal considering every numerical part. Now we can compare based on suffixes, is available.

        // If both have no suffix, they are equal
        if(!xVer.hasSuffix && !yVer.hasSuffix) 0
        // If x has a suffix and y not, y is bigger
        else if(xVer.hasSuffix && !yVer.hasSuffix) -1
        // If y has a suffix and x not, x is bigger
        else if(yVer.hasSuffix && !xVer.hasSuffix) +1
        // If both have a suffix, we compare those alphabetically
        else {
          xVer.getSuffix.compareTo(yVer.getSuffix)
        }
      }
    }
  }


  /**
   * Get Version number from a file name of format <any-string>-<version>.jar . NOTE: version cannot contain a "-", otherwise this will not work!
   * @param file File object to extract version from
   * @return Version as String
   */
  private def getVersionFromFile(file: File): Option[String] = {
    var fileName = file.getName.toLowerCase

    val startIndex = fileName.lastIndexOf("-")

    if(startIndex >= 0 && startIndex < fileName.length - 1){
      fileName = fileName.substring(startIndex + 1)
      Some(fileName.replace(".jar", ""))
    } else {
      log.warn(s"Filename does not contain a version number: $fileName")
      None
    }


  }
}
