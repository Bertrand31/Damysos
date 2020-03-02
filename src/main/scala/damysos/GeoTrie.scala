package damysos

import scala.annotation.tailrec
import cats.implicits._
import Constants._

protected sealed trait GeoTrie
protected final case class Leaf(locations: Array[PointOfInterest] = Array.empty) extends GeoTrie
protected final case class Node(
  private val children: Array[Array[GeoTrie]] = Array.fill(TreeBreadth)(Array.ofDim(TreeBreadth)),
) extends GeoTrie {

  def toArray: Array[PointOfInterest] =
    children.foldLeft(Array.empty[PointOfInterest])((acc, arr) =>
      arr.foldLeft(acc)((acc2, geoTrie) =>
        geoTrie match {
          case node: Node      => acc2 concat node.toArray
          case Leaf(locations) => acc2 concat locations
          case _               => acc2
        }
      )
    )

  lazy val size: Int =
    children.foldLeft(0)((acc, arr) =>
      arr.foldLeft(acc)((acc2, geoTrie) =>
        geoTrie match {
          case node: Node     => acc2 |+| node.size
          case Leaf(location) => acc2 |+| location.size
          case _              => acc2
        }
      )
    )

  @tailrec
  def findLeaf(path: Array[(Int, Int)], trie: GeoTrie = this): Option[GeoTrie] =
    if (path.length == 1)
      trie match {
        case Node(children) => {
          val (latIndex, longIndex) = path.head
          if (children(latIndex).isDefinedAt(longIndex))
            Some(children(latIndex)(longIndex))
          else None
        }
        case _ => None
      }
    else
      trie match {
        case Node(children) => {
          val (latIndex, longIndex) = path.head
          if (children(latIndex).isDefinedAt(longIndex))
            findLeaf(path.tail, children(latIndex)(longIndex))
          else None
        }
        case _ => None
      }

  // Helper function to update a node's child at the given coordinates.
  private def nodeItemUpdate(node: Node, latIndex: Int, longIndex: Int, item: GeoTrie): Node =
    node.copy(
      children=node.children.updated(
        latIndex,
        node.children(latIndex).updated(longIndex, item),
      )
    )

  // Descends the given path (creates it if necessary), and applies the given function to the Leaf
  // at the end of that path (or the Leaf it created there, if the path didn't exist fully).
  private def updateAtPath(fn: Array[PointOfInterest] => Array[PointOfInterest])
                          (path: Array[(Int, Int)], node: Node): Node =
    if (path.length === 1) {
      val (latIndex, longIndex) = path.head
      val leaf = {
        if (children(latIndex).isDefinedAt(longIndex))
          node.children(latIndex)(longIndex) match {
            case leaf: Leaf => leaf
            case _          => Leaf()
          }
        else Leaf()
      }
      nodeItemUpdate(node, latIndex, longIndex, leaf.copy(fn(leaf.locations)))
    } else {
      val (latIndex, longIndex) = path.head
      val subNode = {
        if (node.children(latIndex).isDefinedAt(longIndex))
          node.children(latIndex)(longIndex) match {
            case node: Node => node
            case _          => Node()
          }
        else Node()
      }
      nodeItemUpdate(node, latIndex, longIndex, updateAtPath(fn)(path.tail, subNode))
    }

  def insertAtPath(item: PointOfInterest, path: Array[(Int, Int)], node: Node = this): Node =
    updateAtPath(_ :+ item)(path, node)

  def removeAtPath(item: PointOfInterest, path: Array[(Int, Int)], node: Node = this): Node =
    updateAtPath(_.filter(_ != item))(path, node)
}
