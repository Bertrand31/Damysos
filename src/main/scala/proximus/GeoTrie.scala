package proximus

sealed trait GeoTrie
final case class Leaf(location: PointOfInterst) extends GeoTrie
final case class Node(children: Map[Char, GeoTrie] = Map()) extends GeoTrie {

  def toList(): List[PointOfInterst] =
    children.values.flatMap({
      case x: Node => x.toList
      case Leaf(location) => List(location)
    }).toList

  def findLeaf(path: Seq[Char], trie: GeoTrie = this): Option[GeoTrie] =
    path match {
      case head +: Nil => trie match {
        case x: Node => x.children.get(head)
        case _ => None
      }
      case head +: tail => trie match {
        case x: Node => x.children.get(head).flatMap(findLeaf(tail, _))
        case _ => None
      }
    }

  def insertAtPath(item: PointOfInterst, path: Seq[Char], node: Node = this): Node =
    path match {
      case head +: Nil => node.copy(children=(node.children + (head -> Leaf(item))))
      case head +: tail =>
        node.children.get(head) match {
          case Some(subNode: Node) =>
            node.copy(children=(node.children + (head -> insertAtPath(item, tail, subNode))))
          // Based on the assumption that all point of interest strings are the same length, we
          // cannot encounter a Leaf if we haven't reached the last `indexes` character.
          case _ =>
            node.copy(children=(node.children + (head -> insertAtPath(item, tail, Node()))))
        }
    }
}

object GeoTrie {

  def apply(initialItems: Seq[PointOfInterst], geoTrieType: String): Node =
    initialItems.foldLeft(Node())((node, item) => {
      val path = geoTrieType match {
        case "longitude" => item.coordinates.longitudePath
        case _ => item.coordinates.latitudePath
      }
      node.insertAtPath(item, path)
    })
}
