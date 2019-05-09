package proximus

case class Coordinates(
  latitude: Double,
  longitude: Double
) {

  private def toBase(base: Int, number: Long): String =
    BigInt(Math.round(number)).toString(base)

  private val Breadth = 4
  private val TreeDepth = toBase(Breadth, 360 * 1000000).length

  // Returns an 7-characters-long base-32 number as a String
  private def toPaddedBase4(number: Double): String =
    toBase(Breadth, (number * 1000000).toLong)
      .reverse
      .padTo(TreeDepth, "0") // A base 4 number comprised between 0 and 360 is 26 chars long maximmum
      .reverse
      .mkString

  def latitudePath(): Seq[Char] =
    toPaddedBase4(latitude + 90)
      .toCharArray
      .toList

  def longitudePath(): Seq[Char] =
    toPaddedBase4(longitude + 180)
      .toCharArray
      .toList
}

case class PointOfInterst(
  name: String,
  coordinates: Coordinates
)
