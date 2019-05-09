import org.scalatest.FlatSpec
import scala.io.Source
import utils.PerfUtils
import proximus.{Coordinates, PointOfInterst, Proximus}

class ProximusSpec extends FlatSpec {

  private def loadPIOsFromCSV(filename: String): Iterator[PointOfInterst] =
    Source.fromFile(s"/home/bertrand/Code/Proximus/src/main/ressources/$filename")
      .getLines
      .map(_.split(";"))
      .map(arr => PointOfInterst(arr(0), Coordinates(arr(1).toDouble, arr(2).toDouble)))

  // Cities of France dataset
  val proximusFrance = PerfUtils.time[Proximus](s"Populating a Proximus with French cities") {
    Proximus() ++ loadPIOsFromCSV("france_cities.csv")
  }

  // World cities dataset
  val proximusWorld = PerfUtils.time[Proximus](s"Populating a Proximus with world cities") {
    Proximus() ++ loadPIOsFromCSV("world_cities.csv")
  }

  behavior of "The Proximus"

  it should "store points correctly" in {

    PerfUtils.time[Unit]("Checking French Proximus integirty") {
      val fake = PointOfInterst("fake city", Coordinates(2.3522219, 48.856614))
      assert(!(proximusFrance contains fake)) // The coordinates exist, but with a different name
      val nonExistant = PointOfInterst("I do not exist", Coordinates(-0.380752, 47.501715))
      assert(!(proximusFrance contains nonExistant)) // All properties of this PIO are inexistant
      loadPIOsFromCSV("france_cities.csv").foreach(item => {
        assert(proximusFrance contains item)
      })
    }

    PerfUtils.time[Unit]("Checking World Proximus integirty") {
      loadPIOsFromCSV("world_cities.csv").foreach(item => {
        assert(proximusWorld contains item)
      })
    }
  }

  it should "find the neighbouring points" in {

    val franceMatches = PerfUtils.time[List[PointOfInterst]]("Searching in France dataset") {
      val paris = Coordinates(2.3522219, 48.856614)
      proximusFrance.findSurrounding(paris)
    }
    assert(franceMatches.length == 91)

    val worldMatches = PerfUtils.time[List[PointOfInterst]]("Searching in World dataset") {
      val singapore = Coordinates(1.28967, 103.85007)
      proximusWorld.findSurrounding(singapore, 5)
    }
    assert(worldMatches.length == 13)
  }
}
