package app

import domain.Anagram
import helpers.WordUtils
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

import scala.io.Source

object Solver {
  def main(args: Array[String]): Unit = {
    if (args.length != 3) {
      println("""USAGE: sbt "run \"anagram sentence\" expectedHash dictionaryFileLocation"""")
    } else {
      val sparkConf = new SparkConf()
        .setMaster("local[4]")
        .setAppName("AnagramSolver")

      implicit val sparkSession: SparkSession = SparkSession
        .builder
        .config(sparkConf)
        .getOrCreate()

      val anagram = args(0)
      val expectedHash = args(1)
      val dictionaryFileLocation = args(2)

      val dictionary = prepareDictionary(dictionaryFileLocation, anagram)

      println("Started...")

      val start = System.currentTimeMillis()

      println(new Anagram().findAnagram(anagram, expectedHash, dictionary, dictionary.size))

      println(s"Elapsed = ${System.currentTimeMillis() - start} ms.")

      sparkSession.stop
    }
  }

  def prepareDictionary(fileLocation: String, anagram: String): List[String] = {
    val words = Source.fromFile(fileLocation).getLines.map(l => l.toLowerCase).toSet
    val unusedLetters = words.flatMap(w => w.toSet).diff(anagram.toSet)

    words
      .filter(w => w.toSet.intersect(unusedLetters).isEmpty)
      .filter(w => WordUtils.isSubsetOfChars(WordUtils.getCharMap(w), WordUtils.getCharMap(anagram)))
      .toList
      .sortBy(-_.length)
  }
}
