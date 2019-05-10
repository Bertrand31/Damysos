package damysos

import scala.annotation.tailrec
import monocle.Lens
import monocle.macros.GenLens

sealed trait GeoTrie
final case class Leaf(locations: Set[PointOfInterst] = Set()) extends GeoTrie
final case class Node(children: Map[Char, GeoTrie] = Map()) extends GeoTrie {

  private val childrenL: Lens[Node, Map[Char, GeoTrie]] = GenLens[Node](_.children)
  private val locationsL: Lens[Leaf, Set[PointOfInterst]] = GenLens[Leaf](_.locations)

  def toList: List[PointOfInterst] =
    children.foldLeft(List[PointOfInterst]())((acc, t) =>
      t._2 match {
        case node: Node      => node.toList ++ acc
        case Leaf(locations) => locations.toList ++ acc
      }
    )

  def size: Int =
    children.foldLeft(0)((acc, t) =>
      t._2 match {
        case node: Node => acc + node.size
        case Leaf(location) => acc + location.size
      }
    )

  @tailrec
  def findLeaf(path: List[Char], trie: Option[GeoTrie] = Some(this)): Option[GeoTrie] =
    path match {
      case head +: Nil => trie.collect({ case x: Node => x.children.get(head) }).flatten
      case head +: tail =>
        trie match {
          case Some(x: Node) => findLeaf(tail, x.children.get(head))
          case _             => None
        }
    }

  private def pathLocationsL(update: Set[PointOfInterst] => Set[PointOfInterst])
                            (path: List[Char], node: Node): Node =
    path match {
      case head +: Nil => {
        val leaf = node.children.get(head).collect({ case x: Leaf => x }).getOrElse(Leaf())
        childrenL.modify(_ + (head -> locationsL.modify(update)(leaf)))(node)
      }
      case head +: tail => {
        val subNode = node.children.get(head).collect({ case x: Node => x }).getOrElse(Node())
        childrenL.modify(_ + (head -> pathLocationsL(update)(tail, subNode)))(node)
      }
    }

  def insertAtPath(item: PointOfInterst, path: List[Char], node: Node = this): Node =
    pathLocationsL(_ + item)(path, node)

  def removeAtPath(item: PointOfInterst, path: List[Char], node: Node = this): Node =
    pathLocationsL(_ - item)(path, node)
}
