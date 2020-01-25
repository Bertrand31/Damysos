package utils

import java.lang.{Integer => JavaInt}

object MathUtils {

  implicit class IntImprovements(val i: Int) {

    def toBase: Int => String = JavaInt.toString(i, _)
  }
}
