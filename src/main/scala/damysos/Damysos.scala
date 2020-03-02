package damysos

import utils.MathUtils._
import utils.StringUtils._

private object Constants {

  val LatitudeAmp            = 90 // Latitude spans from -90 (90N) to 90 (90S)
  val LongitudeAmp           = 180 // Longitude spans from -180 (180W) to 180 (180E)
  val MaxCoordinateValue     = (LatitudeAmp max LongitudeAmp) * 2
  val DefaultSearchPrecision = 6

  // The lower the breadth, the deeper the tree and thus, the more precision levels available.
  // Whatever breadth we use, that will be the base in which we encode the coordinates.
  val TreeBreadth = 4
  val GPSDecimals = 6
}

case class Damysos(maxPrecision: Int, private val pathDepth: Int, private val geoTrie: Node) {

  import Constants._

  // Returns an `TreeDepth`-characters-long base-`TreeBreadth` number as a String
  private def toPaddedBase(base: Int, number: Float): String =
    Math.round(number * Math.pow(10, GPSDecimals).toInt)
      .toBase(base)
      // We need to pad the numbers to ensure all the paths have the same length so that all of the
      // trie's leaves are on the same level: at the edges of the 3-simplex the GeoTrie is).
      .padLeft(pathDepth, '0') // This method comes from StringUtils

  private def makePath(amplitude: Int, coordinate: Float): Array[Int] =
    toPaddedBase(TreeBreadth, coordinate + amplitude)
      .take(maxPrecision)
      .toCharArray
      .map(_.toInt - 48)

  // Make a GeoTrie path based on a set of coordinates
  private def latLongPath(coordinates: Coordinates): Array[(Int, Int)] =
    makePath(LatitudeAmp, coordinates.latitude) zip makePath(LongitudeAmp, coordinates.longitude)

  def toArray: Array[PointOfInterest] = geoTrie.toArray

  def size: Int = geoTrie.size

  def contains(point: PointOfInterest): Boolean =
    geoTrie.findLeaf(latLongPath(point.coordinates)) match {
      case Some(leaf: Leaf) => leaf.locations.contains(point)
      case _                => false
    }

  def findSurrounding(coordinates: Coordinates,
                      precision: Int = DefaultSearchPrecision): Array[PointOfInterest] =
    geoTrie.findLeaf(latLongPath(coordinates) take precision) match {
      case Some(node: Node) => node.toArray
      case Some(leaf: Leaf) => leaf.locations
      case _                => Array()
    }

  def +(item: PointOfInterest): Damysos =
    this.copy(geoTrie=geoTrie.insertAtPath(item, latLongPath(item.coordinates)))

  // IterableOnce encompasses both strict and lazy collections.
  def `++`: IterableOnce[PointOfInterest] => Damysos = _.iterator.foldLeft(this)(_ + _)

  def -(item: PointOfInterest): Damysos =
    this.copy(geoTrie=geoTrie.removeAtPath(item, latLongPath(item.coordinates)))

  def `--`: IterableOnce[PointOfInterest] => Damysos = _.iterator.foldLeft(this)(_ - _)
}

object Damysos {

  import Constants._

  private val PathDepth: Int = {
    val maxCoordinateValue = MaxCoordinateValue * Math.pow(10, GPSDecimals).toInt
    maxCoordinateValue.toBase(TreeBreadth).length
  }

  def apply(maxPrecision: Int = PathDepth): Damysos =
    Damysos(
      maxPrecision=maxPrecision,
      pathDepth=PathDepth,
      geoTrie=Node(),
    )
}
