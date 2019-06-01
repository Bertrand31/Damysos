package damysos

import utils.MathUtils

private object Constants {

  val LatitudeAmp = 90 // Latitude spans from -90 (90N) to 90 (90S)
  val LongitudeAmp = 180 // Longitude spans from -180 (180W) to 180 (180E)
  val MaxCoordinateValue: Long = Math.max(LatitudeAmp, LongitudeAmp) * 2
  val DefaultSearchPrecision = 6

  // The lower the breadth, the deeper the tree and thus, the more precision levels available.
  val TreeBreadth = 4
  val GPSDecimals = 6
}

case class Damysos(private val geoTrie: Node = Node()) {

  import Constants._

  // 360 is the maximum value a GPS coordinate can take. So it is the trie depth we need.
  private val TreeDepth =
    MathUtils.toBase(TreeBreadth, MaxCoordinateValue * Math.pow(10, GPSDecimals).toLong).length

  // Returns an `TreeDepth`-characters-long base-`TreeBreadth` number as a String
  private def toPaddedBase(base: Int, number: Double): String =
    MathUtils.toBase(base, Math.round(number * Math.pow(10, GPSDecimals).toLong))
      .reverse
      // We need to pad the numbers to ensure all the paths have the same length i.e. all of the
      // trie's leaves are on the same level: at the edges of the 3-simplex the GeoTrie is).
      .padTo(TreeDepth, "0")
      .reverse
      .mkString

  private def makePath(amplitude: Int, coordinate: Double): List[Int] =
    toPaddedBase(TreeBreadth, coordinate + amplitude).toCharArray.map(_.toString.toInt).toList

  private def latLongPath(coordinates: Coordinates): List[(Int, Int)] =
    makePath(LatitudeAmp, coordinates.latitude) zip makePath(LongitudeAmp, coordinates.longitude)

  def toArray: Array[PointOfInterest] = geoTrie.toArray

  lazy val size: Int = geoTrie.size

  def contains(point: PointOfInterest): Boolean =
    geoTrie.findLeaf(latLongPath(point.coordinates)) match {
      case Some(leaf: Leaf) => leaf.locations.contains(point)
      case _                => false
    }

  def findSurrounding(coordinates: Coordinates,
                      precision: Int = DefaultSearchPrecision): Array[PointOfInterest] =
    geoTrie.findLeaf(latLongPath(coordinates).take(precision)) match {
      case Some(node: Node) => node.toArray
      case _                => Array()
    }

  def +(item: PointOfInterest): Damysos =
    this.copy(geoTrie=geoTrie.insertAtPath(item, latLongPath(item.coordinates)))

  // TraversableOnce encompasses both normal collections and Iterator. So this method can be used
  // either with a normal collection or a lazy one, like reading from a file line by line.
  def ++(items: TraversableOnce[PointOfInterest]): Damysos = items.foldLeft(this)(_ + _)

  def -(item: PointOfInterest): Damysos =
    this.copy(geoTrie=geoTrie.removeAtPath(item, latLongPath(item.coordinates)))

  def --(items: TraversableOnce[PointOfInterest]): Damysos = items.foldLeft(this)(_ - _)
}
