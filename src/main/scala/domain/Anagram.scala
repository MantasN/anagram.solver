package domain

import helpers.{Cryptography, WordUtils}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

import scala.annotation.tailrec

class Anagram {
  def findAnagram(forValue: String, withHash: String, dictionary: List[String], maxWords: Int)
                 (implicit sparkSession: SparkSession): Option[String] = {

    if (maxWords < 1)
      throw new RuntimeException("Max words must be greater than or equal to one!")

    val anagramCharMap = WordUtils.getCharMap(forValue)

    @tailrec
    def findRecursive(toTry: RDD[String], maxWords: Int): Option[String] = {
      maxWords match {
        case 0 => Option.empty
        case _ =>
          val maybeResult = toTry
            .filter(r =>
              r.length == forValue.length
              && WordUtils.sortLetters(r) == WordUtils.sortLetters(forValue)
              && Cryptography.getMD5Hash(r) == withHash
            )
            .take(1)
            .headOption

          if (maybeResult.isEmpty) {
            val nextToTry: RDD[String] = toTry
              .flatMap(x => dictionary.map(y => s"$x $y"))
              .filter(r => WordUtils.isSubsetOfChars(WordUtils.getCharMap(r), anagramCharMap))

            findRecursive(nextToTry, maxWords - 1)
          } else {
            maybeResult
          }
      }
    }

    findRecursive(sparkSession.sparkContext.parallelize(dictionary), maxWords)
  }
}
