package proximus

import scala.io.Source
import utils.PerfUtils

case class Coordinates(
  latitude: Double,
  longitude: Double
) {

  private def toBase(base: Int, number: Long): String =
    BigInt(Math.round(number)).toString(base)

  private val Breadth = 4
  private val TreeDepth = toBase(Breadth, 360 * 1000000).length

  // Returns an 7-characters-long base-32 number as a String
  private def toPaddedBase4(number: Double): String =
    toBase(Breadth, (number * 1000000).toLong)
      .reverse
      .padTo(TreeDepth, "0") // A base 4 number comprised between 0 and 360 is 26 chars long maximmum
      .reverse
      .mkString

  def latitudePath(): Seq[Char] =
    toPaddedBase4(latitude + 90)
      .toCharArray
      .toList

  def longitudePath(): Seq[Char] =
    toPaddedBase4(longitude + 180)
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
    println("--------------------------------------------------")
    val proximusFrance = PerfUtils.time[Proximus](s"Populating a Proximus with French cities") {
      Proximus() ++ loadPIOsFromCSV("france_cities.csv")
    }
    PerfUtils.time[Unit]("Checking French Proximus integirty") {
      // Cities of France dataset
      val fake = PointOfInterst("fake city", Coordinates(2.3522219, 48.856614))
      assert(!(proximusFrance contains fake)) // The coordinates exist, but with a different name
      val nonExistant = PointOfInterst("I do not exist", Coordinates(-0.380752, 47.501715))
      assert(!(proximusFrance contains nonExistant)) // All properties of this PIO are inexistant
      loadPIOsFromCSV("france_cities.csv").foreach(item => {
        assert(proximusFrance contains item)
      })
    }
    val francheMatches = PerfUtils.time[List[PointOfInterst]]("Searching in France dataset") {
      val paris = Coordinates(2.3522219, 48.856614)
      proximusFrance.findSurrounding(paris)
    }
    println(s"=> Found ${francheMatches.length} matches")

    println("--------------------------------------------------")

    // World cities dataset
    val proximusWorld = PerfUtils.time[Proximus](s"Populating a Proximus with world cities") {
      Proximus() ++ loadPIOsFromCSV("world_cities.csv")
    }
    PerfUtils.time[Unit]("Checking World Proximus integirty") {
      loadPIOsFromCSV("world_cities.csv").foreach(item => {
        assert(proximusWorld contains item)
      })
    }
    val worldMatches = PerfUtils.time[List[PointOfInterst]]("Searching in World dataset") {
      val singapore = Coordinates(1.28967, 103.85007)
      proximusWorld.findSurrounding(singapore, 5)
    }
    println(s"=> Found ${worldMatches.length} matches")
    println("--------------------------------------------------")
  }
}
