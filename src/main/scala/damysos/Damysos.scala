package damysos

import utils.MathUtils

case class Damysos(private val geoTrie: Node = Node()) {

  // The lower the breadth, the deeper the tree and thus, the more precision levels available.
  private val TreeBreadth = 4
  private val GPSDecimals = 6
  // 360 is the maximum value a GPS coordinate can take. So it is the trie depth we need.
  private val TreeDepth =
    MathUtils.toBase(TreeBreadth, 360L * Math.pow(10, GPSDecimals).toLong).length

  // Returns an `TreeDepth`-characters-long base-`TreeBreadth` number as a String
  private def toPaddedBase(base: Int, number: Double): String =
    MathUtils.toBase(base, Math.round(number * Math.pow(10, GPSDecimals).toLong))
      .reverse
      // We need to pad the numbers to ensure all the paths have the same length i.e. all of the
      // trie's leaves are on the same level.
      .padTo(TreeDepth, "0")
      .reverse
      .mkString

  private def makePath(minValue: Int, coordinate: Double): List[Int] =
    toPaddedBase(TreeBreadth, coordinate - minValue).toCharArray.map(_.toString.toInt).toList

  private def latLongPath(coordinates: Coordinates): List[(Int, Int)] =
    makePath(-90, coordinates.latitude) // Latitude spans from -90 (90N) to 90 (90S)
      .zip(makePath(-180, coordinates.longitude)) // Longitude spans from -180 (180W) to 180 (180E)

  def toArray: Array[PointOfInterst] = geoTrie.toArray

  lazy val size: Int = geoTrie.size

  // We arbitrarily use the latitudeGeoTrie for this operation, but either would be fine
  def contains(point: PointOfInterst): Boolean =
    geoTrie.findLeaf(latLongPath(point.coordinates)) match {
      case Some(leaf: Leaf) => leaf.locations.contains(point)
      case _                => false
    }

  def findSurrounding(coordinates: Coordinates, precision: Int = 6): Array[PointOfInterst] =
    geoTrie.findLeaf(latLongPath(coordinates).take(precision)) match {
      case Some(node: Node) => node.toArray
      case _                => Array()
    }

  def +(item: PointOfInterst): Damysos =
    this.copy(geoTrie=geoTrie.insertAtPath(item, latLongPath(item.coordinates)))

  // TraversableOnce encompasses both normal collections and Iterator. So this method can be used
  // either with a normal collection or a lazy one, like reading from a file line by line.
  def ++(items: TraversableOnce[PointOfInterst]): Damysos = items.foldLeft(this)(_ + _)

  def -(item: PointOfInterst): Damysos =
    this.copy(geoTrie=geoTrie.removeAtPath(item, latLongPath(item.coordinates)))

  def --(items: TraversableOnce[PointOfInterst]): Damysos = items.foldLeft(this)(_ - _)
}
