package utils

object StringUtils {

  implicit class StringImprovements(val s: String) {

    def padLeft(minLength: Int, padWith: Char): String =
      if (s.length >= minLength) s
      else {
        val sb = new scala.collection.mutable.StringBuilder
        var diff = minLength - s.size
        while (diff > 0) {
          sb += padWith
          diff -= 1
        }
        sb ++= s
        sb.result
      }
  }
}
