package proximus

case class Proximus(
  private val latitudeGeoTrie: Node,
  private val longitudeGeoTrie: Node
) {

  private val Precision = 3

  def toList(): List[PointOfInterst] = latitudeGeoTrie.toList

  def contains(point: PointOfInterst): Boolean =
    latitudeGeoTrie.findLeaf(point.coordinates.latitudePath) match {
      case Some(leaf: Leaf) => leaf.location == point
      case _ => false
    }

  def findSurrounding(coordinates: Coordinates): List[PointOfInterst] = {
    val looseLatitudePath = coordinates.latitudePath.slice(0, Precision)
    val latitudeMatches = latitudeGeoTrie.findLeaf(looseLatitudePath) match {
      case Some(node: Node) => node.toList
      case _ => List()
    }
    val looseLongitudePath = coordinates.longitudePath.slice(0, Precision)
    val longitudeMatches = longitudeGeoTrie.findLeaf(looseLongitudePath) match {
      case Some(node: Node) => node.toList
      case _ => List()
    }
    latitudeMatches intersect longitudeMatches
  }

  def :+(item: PointOfInterst): Proximus =
    this.copy(
      latitudeGeoTrie=(this.latitudeGeoTrie.insertAtPath(item, item.coordinates.latitudePath)),
      longitudeGeoTrie=(this.longitudeGeoTrie.insertAtPath(item, item.coordinates.longitudePath))
    )
}

object Proximus {

  def apply(initialItems: Seq[PointOfInterst]): Proximus =
    Proximus(
      latitudeGeoTrie = GeoTrie(initialItems, "latitude"),
      longitudeGeoTrie = GeoTrie(initialItems, "longitude")
    )
}
