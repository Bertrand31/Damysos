package damysos

import utils.MathUtils

case class Damysos(
  private val latitudeGeoTrie: Node = Node(),
  private val longitudeGeoTrie: Node = Node()
) {

  // The lower the breadth, the deeper the tree and thus, the more precision levels available.
  private val TreeBreadth = 4
  private val GPSPrecision = 1000000
  // 360 is the maximum value a GPS coordinate can take. So it is the trie depth we need.
  private val TreeDepth = MathUtils.toBase(TreeBreadth, 360 * GPSPrecision).length

  // Returns an 7-characters-long base-32 number as a String
  private def toPaddedBase(base: Int, number: Double): String =
    MathUtils.toBase(base, Math.round(number * GPSPrecision.toDouble))
      .reverse
      // We need to pad the numbers to ensure all the paths have the same length i.e. all of the
      // trie's leaves are on the same level.
      .padTo(TreeDepth, "0")
      .reverse
      .mkString

  private def latitudePath(coordinates: Coordinates): Seq[Char] =
    toPaddedBase(TreeBreadth, coordinates.latitude + 90)
      .toCharArray
      .toList

  private def longitudePath(coordinates: Coordinates): Seq[Char] =
    toPaddedBase(TreeBreadth, coordinates.longitude + 180)
      .toCharArray
      .toList

  private val DefaultPrecision = 6

  def toList(): List[PointOfInterst] = this.latitudeGeoTrie.toList

  // We arbitrarily use the latitudeGeoTrie for this operation, but either would be fine
  def contains(point: PointOfInterst): Boolean =
    this.latitudeGeoTrie.findLeaf(this.latitudePath(point.coordinates)) match {
      case Some(leaf: Leaf) => leaf.locations contains point
      case _                => false
    }

  def findSurrounding(
    coordinates: Coordinates,
    precision: Int = DefaultPrecision
  ): List[PointOfInterst] = {
    val partialLatitudePath = this.latitudePath(coordinates).take(precision)
    val latitudeMatches = this.latitudeGeoTrie.findLeaf(partialLatitudePath) match {
      case Some(node: Node) => node.toList
      case _                => List()
    }
    val partialLongitudePath = this.longitudePath(coordinates).take(precision)
    val longitudeMatches = this.longitudeGeoTrie.findLeaf(partialLongitudePath) match {
      case Some(node: Node) => node.toList
      case _                => List()
    }
    latitudeMatches intersect longitudeMatches
  }

  def :+(item: PointOfInterst): Damysos =
    this.copy(
      latitudeGeoTrie = (this.latitudeGeoTrie.insertAtPath(item, latitudePath(item.coordinates))),
      longitudeGeoTrie = (this.longitudeGeoTrie.insertAtPath(item, longitudePath(item.coordinates)))
    )

  // TraversableOnce encompasses both normal collections and Iterator. So this one method can be
  // used either with a normal collection or a lazy one, like reading from a file line by line.
  def ++(items: TraversableOnce[PointOfInterst]): Damysos = items.foldLeft(this)(_ :+ _)
}
