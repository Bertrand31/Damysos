package utils

import java.lang.{Long => JavaLong}

object MathUtils {

  def toBase(base: Int, number: Int): String = JavaLong.toString(number, base)
}
