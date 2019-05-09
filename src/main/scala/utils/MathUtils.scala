package utils

object MathUtils {

  def toBase(base: Int, number: Long): String =
    BigInt(number).toString(base)
}
