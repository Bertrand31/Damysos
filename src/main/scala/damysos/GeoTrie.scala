package damysos

import scala.annotation.tailrec

sealed trait GeoTrie
final case class Leaf(locations: Array[PointOfInterst] = Array()) extends GeoTrie
final case class Node(children: Array[Array[GeoTrie]] = Array.fill(4)(Array.ofDim(4))) extends GeoTrie {

  def toArray: Array[PointOfInterst] =
    children.foldLeft(Array[PointOfInterst]())((acc, arr) =>
      arr.foldLeft(acc)((acc2, geoTrie) =>
        geoTrie match {
          case node: Node      => acc2 ++ node.toArray
          case Leaf(locations) => acc2 ++ locations
          case _ => acc2
        }
      )
    )

  lazy val size: Int =
    children.foldLeft(0)((acc, arr) =>
      arr.foldLeft(acc)((acc2, geoTrie) =>
        geoTrie match {
          case node: Node => acc2 + node.size
          case Leaf(location) => acc2 + location.size
          case _ => acc2
        }
      )
    )

  @tailrec
  def findLeaf(path: List[(Int, Int)], trie: GeoTrie = this): Option[GeoTrie] =
    path match {
      case head +: Nil =>
        trie match {
          case Node(children) => {
            val (latIndex, longIndex) = head
            if (children.isDefinedAt(latIndex) && children(latIndex).isDefinedAt(longIndex))
              Some(children(latIndex)(longIndex))
            else None
          }
          case _ => None
        }
      case head +: tail =>
        trie match {
          case Node(children) => {
            val (latIndex, longIndex) = head
            if (children.isDefinedAt(latIndex) && children(latIndex).isDefinedAt(longIndex))
              findLeaf(tail, children(latIndex)(longIndex))
            else None
          }
          case _ => None
        }
    }

  private def pathLocationsL(update: Array[PointOfInterst] => Array[PointOfInterst])
                            (path: List[(Int, Int)], node: Node): Node =
    path match {
      case head +: Nil => {
        val (latIndex, longIndex) = head
        val leaf = {
          if (node.children.isDefinedAt(latIndex) && children(latIndex).isDefinedAt(longIndex))
            node.children(latIndex)(longIndex) match {
              case leaf: Leaf => leaf
              case _ => Leaf()
            }
          else Leaf()
        }
        node.copy(
          children=node.children.updated(latIndex, node.children(latIndex).updated(longIndex, Leaf(update(leaf.locations))))
        )
      }
      case head +: tail => {
        val (latIndex, longIndex) = head
        val subNode = {
          if (node.children.isDefinedAt(latIndex) && node.children(latIndex).isDefinedAt(longIndex))
            node.children(latIndex)(longIndex) match {
              case node: Node => node
              case _ => Node()
            }
          else Node()
        }
        node.copy(
          children=node.children.updated(latIndex, node.children(latIndex).updated(longIndex, pathLocationsL(update)(tail, subNode)))
        )
      }
    }

  def insertAtPath(item: PointOfInterst, path: List[(Int, Int)], node: Node = this): Node =
    pathLocationsL(_ :+ item)(path, node)

  def removeAtPath(item: PointOfInterst, path: List[(Int, Int)], node: Node = this): Node =
    pathLocationsL(_.filter(_ != item))(path, node)
}
