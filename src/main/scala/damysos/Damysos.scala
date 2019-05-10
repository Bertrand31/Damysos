package damysos

import utils.MathUtils

case class Damysos(
  private val latitudeGeoTrie: Node = Node(),
  private val longitudeGeoTrie: Node = Node()
) {

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

  private def makePath(minValue: Int, coordinate: Double): List[Char] =
    toPaddedBase(TreeBreadth, coordinate - minValue).toCharArray.toList

  private def latitudePath(coordinates: Coordinates): List[Char] =
    makePath(-90, coordinates.latitude) // Latitude spans from -90 (90N) to 90 (90S)

  private def longitudePath(coordinates: Coordinates): List[Char] =
    makePath(-180, coordinates.longitude) // Longitude spans from -180 (180W) to 180 (180E)

  private val DefaultPrecision = 6

  def toList(): List[PointOfInterst] = latitudeGeoTrie.toList

  def size(): Int = latitudeGeoTrie.size

  // We arbitrarily use the latitudeGeoTrie for this operation, but either would be fine
  def contains(point: PointOfInterst): Boolean =
    latitudeGeoTrie.findLeaf(latitudePath(point.coordinates)).collect({
      case leaf: Leaf => leaf.locations.contains(point)
    }).getOrElse(false)

  def findSurrounding(
    coordinates: Coordinates,
    precision: Int = DefaultPrecision
  ): List[PointOfInterst] = {
    def getMatches(path: List[Char], trie: Node) = trie.findLeaf(path.take(precision))
      .collect({ case node: Node => node.toList }).getOrElse(List())
    val latitudeMatches = getMatches(latitudePath(coordinates), latitudeGeoTrie)
    val longitudeMatches = getMatches(longitudePath(coordinates), longitudeGeoTrie)
    latitudeMatches intersect longitudeMatches
  }

  def +(item: PointOfInterst): Damysos =
    this.copy(
      latitudeGeoTrie=latitudeGeoTrie.insertAtPath(item, latitudePath(item.coordinates)),
      longitudeGeoTrie=longitudeGeoTrie.insertAtPath(item, longitudePath(item.coordinates))
    )

  // TraversableOnce encompasses both normal collections and Iterator. So this method can be used
  // either with a normal collection or a lazy one, like reading from a file line by line.
  def ++(items: TraversableOnce[PointOfInterst]): Damysos = items.foldLeft(this)(_ + _)

  def -(item: PointOfInterst): Damysos =
    this.copy(
      latitudeGeoTrie=latitudeGeoTrie.removeAtPath(item, latitudePath(item.coordinates)),
      longitudeGeoTrie=longitudeGeoTrie.removeAtPath(item, longitudePath(item.coordinates))
    )

  def --(items: TraversableOnce[PointOfInterst]): Damysos = items.foldLeft(this)(_ - _)
}
