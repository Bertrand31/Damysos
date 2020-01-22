package utils

import java.lang.{Long => JavaLong}

object MathUtils {

  implicit class IntImprovements(val i: Int) {

    def toBase: Int => String = JavaLong.toString(i, _)
  }
}
