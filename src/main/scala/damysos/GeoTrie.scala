package damysos

import scala.annotation.tailrec

sealed trait GeoTrie
final case class Leaf(locations: Set[PointOfInterst] = Set()) extends GeoTrie
final case class Node(children: Array[Array[GeoTrie]] = Array.fill(4)(Array.ofDim(4))) extends GeoTrie {

  def toSet: Set[PointOfInterst] =
    children.foldLeft(Set[PointOfInterst]())((acc, arr) => {
      acc ++ {
        arr.foldLeft(Set[PointOfInterst]())((acc2, kv) =>
          kv match {
            case node: Node      => acc2 ++ node.toSet
            case Leaf(locations) => acc2 ++ locations
            case _ => acc2
          }
        )
      }
    })

  lazy val size: Int =
    children.foldLeft(0)((acc, arr) => {
      acc + arr.foldLeft(0)((acc2, kv) => {
        kv match {
          case node: Node => acc2 + node.size
          case Leaf(location) => acc2 + location.size
          case _ => acc2
        }
      })
    })

  @tailrec
  def findLeaf(path: List[(Char, Char)], trie: GeoTrie = this): Option[GeoTrie] =
    path match {
      case head +: Nil => {
        val latIndex = head._1.toString.toInt
        val longIndex = head._2.toString.toInt
        trie match {
          case Node(children) => {
            if (children.isDefinedAt(latIndex) && children(latIndex).isDefinedAt(longIndex)) {
              Some(children(latIndex)(longIndex))
            }
            else None
          }
          case _ => None
        }
      }
      case head +: tail => {
        val latIndex = head._1.toString.toInt
        val longIndex = head._2.toString.toInt
        trie match {
          case Node(children) => {
            if (children.isDefinedAt(latIndex) && children(latIndex).isDefinedAt(longIndex)) {
              findLeaf(tail, children(latIndex)(longIndex))
            }
            else None
          }
          case _ => None
        }
      }
    }

  private def pathLocationsL(update: Set[PointOfInterst] => Set[PointOfInterst])
                            (path: List[(Char, Char)], node: Node): Node =
    path match {
      case head +: Nil => {
        val latIndex = head._1.toString.toInt
        val longIndex = head._2.toString.toInt
        val leaf = {
          if (node.children.isDefinedAt(latIndex) && children(latIndex).isDefinedAt(longIndex)) {
            node.children(latIndex)(longIndex) match {
              case leaf: Leaf => leaf
              case _ => Leaf()
            }
          }
          else Leaf()
        }
        node.copy(
          children=node.children.updated(latIndex, node.children(latIndex).updated(longIndex, Leaf(update(leaf.locations))))
        )
      }
      case head +: tail => {
        val latIndex = head._1.toString.toInt
        val longIndex = head._2.toString.toInt
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

  def insertAtPath(item: PointOfInterst, path: List[(Char, Char)], node: Node = this): Node =
    pathLocationsL(_ + item)(path, node)

  def removeAtPath(item: PointOfInterst, path: List[(Char, Char)], node: Node = this): Node =
    pathLocationsL(_ - item)(path, node)
}
