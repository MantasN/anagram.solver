package helpers

object WordUtils {
  def sortLetters(item: String): String = {
    item.replaceAll(" ", "").toList.sorted.mkString
  }

  def getCharMap(s: String): Map[Char, Int] = {
    s.replace(" ", "").groupBy(c => c.toLower).mapValues(group => group.length).toSeq.toMap
  }

  def isSubsetOfChars(testValue: Map[Char, Int], originalValue: Map[Char, Int]): Boolean = {
    !testValue.exists(c => c._2 > originalValue.getOrElse(c._1, 0))
  }
}
