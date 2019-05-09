package proximus

import scala.io.Source
import utils.PerfUtils

case class Coordinates(
  latitude: Double,
  longitude: Double
) {
  // Returns an 7-characters-long base-32 number as a String
  private def toPaddedBase16(number: Double): String =
    BigInt(Math.round(number * 10000000).toLong)
      .toString(32)
      .reverse
      .padTo(7, "0")
      .reverse
      .mkString

  def latitudePath(): Seq[Char] =
    toPaddedBase16(latitude + 90)
      .toCharArray
      .toList

  def longitudePath(): Seq[Char] =
    toPaddedBase16(longitude + 180)
      .toCharArray
      .toList
}

case class PointOfInterst(
  name: String,
  coordinates: Coordinates
)

object Main {

  private def loadPIOsFromCSV(filename: String): Iterator[PointOfInterst] =
    Source.fromFile(s"/home/bertrand/Code/Proximus/src/main/ressources/$filename")
      .getLines
      .map(_.split(";"))
      .map(arr => PointOfInterst(arr(0), Coordinates(arr(1).toDouble, arr(2).toDouble)))

  def main(args: Array[String]): Unit = {
    val proximus = PerfUtils.time[Proximus](s"Populating a Proximus lazily") {
      val dataIterable = loadPIOsFromCSV("pop_simplified.csv")
      dataIterable.foldLeft(Proximus(List()))(_ :+ _)
    }

    val coordinates = Coordinates(2.3522219, 48.856614)
    PerfUtils.time[List[PointOfInterst]](s"Searching around $coordinates") {
      proximus.findSurrounding(coordinates)
    }.foreach(println)

    val fake = PointOfInterst("fake city", Coordinates(2.3522219, 48.856614))
    assert(!(proximus contains fake))
    val nonExistant = PointOfInterst("I do not exist", Coordinates(-0.380752, 47.501715))
    assert(!(proximus contains nonExistant))
    // loadPIOsFromCSV("pop_simplified.csv").foreach(item => {
      // println(item)
      // assert(proximus contains item)
    // })
  }
}
