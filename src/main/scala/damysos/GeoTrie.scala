package damysos

import scala.annotation.tailrec

sealed trait GeoTrie
final case class Leaf(locations: Set[PointOfInterst] = Set()) extends GeoTrie
final case class Node(children: Map[Char, GeoTrie] = Map()) extends GeoTrie {

  def toList(): List[PointOfInterst] =
    children.values.toList.flatMap({
      case x: Node         => x.toList
      case Leaf(locations) => locations
    })

  @tailrec
  def findLeaf(path: List[Char], trie: Option[GeoTrie] = Some(this)): Option[GeoTrie] =
    path match {
      case head +: Nil =>
        trie match {
          case Some(x: Node) => x.children.get(head)
          case _             => None
        }
      case head +: tail =>
        trie match {
          case Some(x: Node) => findLeaf(tail, x.children.get(head))
          case _             => None
        }
    }

  def insertAtPath(item: PointOfInterst, path: List[Char], node: Node = this): Node =
    path match {
      case head +: Nil => {
        val newLeaf = node.children.get(head) match {
          case Some(x: Leaf) => x.copy(locations = (x.locations + item))
          case _             => Leaf(locations = Set(item))
        }
        node.copy(children = (node.children + (head -> newLeaf)))
      }
      case head +: tail =>
        node.children.get(head) match {
          case Some(subNode: Node) =>
            node.copy(children = (node.children + (head -> insertAtPath(item, tail, subNode))))
          // Based on the assumption that all point of interest strings are the same length, we
          // cannot encounter a Leaf if we haven't reached the last `indexes` character.
          case _ =>
            node.copy(children = (node.children + (head -> insertAtPath(item, tail, new Node))))
        }
    }
}
