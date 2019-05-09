package proximus

case class Coordinates(
  latitude: Double,
  longitude: Double
) {

  private def toBase(base: Int, number: Long): String = BigInt(number).toString(base)

  // The lower the breadth, the deeper the tree and thus, the more precision levels available.
  private val TreeBreadth = 4
  private val GPSPrecision = 1000000
  // 360 is the maximum value a GPS coordinate can take. So it is the trie depth we need.
  private val TreeDepth = toBase(TreeBreadth, 360 * GPSPrecision).length

  // Returns an 7-characters-long base-32 number as a String
  private def toPaddedBase4(number: Double): String =
    toBase(TreeBreadth, Math.round(number * GPSPrecision.toDouble))
      .reverse
      // We need to pad the numbers to ensure all the paths have the same length i.e. all of the
      // trie's leaves are on the same level.
      .padTo(TreeDepth, "0")
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
