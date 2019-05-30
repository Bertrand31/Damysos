package damysos

import scala.annotation.tailrec

sealed trait GeoTrie
final case class Leaf(locations: Set[PointOfInterst] = Set()) extends GeoTrie
final case class Node(children: Array[GeoTrie] = Array.ofDim(4)) extends GeoTrie {

  def toSet: Set[PointOfInterst] =
    children.foldLeft(Set[PointOfInterst]())((acc, kv) =>
      acc ++ {
        kv match {
          case node: Node      => node.toSet
          case Leaf(locations) => locations
          case _ => acc
        }
      }
    )

  lazy val size: Int =
    children.foldLeft(0)((acc, kv) =>
      acc + {
        kv match {
          case node: Node => node.size
          case Leaf(location) => location.size
          case _ => 0
        }
      }
    )

  @tailrec
  def findLeaf(path: List[Char], trie: GeoTrie = this): Option[GeoTrie] =
    path match {
      case head +: Nil => {
        val index = head.toString.toInt
        trie match {
          case Node(children) => {
            if (children.isDefinedAt(index)) Some(children(index))
            else None
          }
          case _ => None
        }
      }
      case head +: tail => {
        val index = head.toString.toInt
        trie match {
          case Node(children) => {
            if (children.isDefinedAt(index)) findLeaf(tail, children(index))
            else None
          }
          case _ => None
        }
      }
    }

  private def pathLocationsL(update: Set[PointOfInterst] => Set[PointOfInterst])
                            (path: List[Char], node: Node): Node =
    path match {
      case head +: Nil => {
        val index = head.toString.toInt
        val leaf = {
          if (node.children.isDefinedAt(index)) {
            node.children(index) match {
              case leaf: Leaf => leaf
              case _ => Leaf()
            }
          }
          else Leaf()
        }
        node.copy(children=node.children.updated(head.toString.toInt, Leaf(update(leaf.locations))))
      }
      case head +: tail => {
        val index = head.toString.toInt
        val subNode = {
          if (node.children.isDefinedAt(index)) {
            node.children(index) match {
              case node: Node => node
              case _ => Node()
            }
          }
          else Node()
        }
        node.copy(children=node.children.updated(head.toString.toInt, pathLocationsL(update)(tail, subNode)))
      }
    }

  def insertAtPath(item: PointOfInterst, path: List[Char], node: Node = this): Node =
    pathLocationsL(_ + item)(path, node)

  def removeAtPath(item: PointOfInterst, path: List[Char], node: Node = this): Node =
    pathLocationsL(_ - item)(path, node)
}
