package utils

import java.lang.{Long => JavaLong}

object MathUtils {

  implicit class LongImprovements(val l: Long) {

    val toBase: Int => String = JavaLong.toString(l, _)
  }
}
