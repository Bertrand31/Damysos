import org.scalatest.FlatSpec
import scala.io.Source
import utils.PerfUtils
import damysos.{Coordinates, PointOfInterst, Damysos}

class DamysosSpec extends FlatSpec {

  private def loadPIOsFromCSV(filename: String): Iterator[PointOfInterst] =
    Source.fromFile(s"/home/bertrand/Code/Damysos/src/main/ressources/$filename")
      .getLines
      .map(_.split(";"))
      .map(arr => PointOfInterst(arr(0), Coordinates(arr(1).toDouble, arr(2).toDouble)))

  // Cities of France dataset
  val damysosFrance = PerfUtils.time[Damysos](s"Populating a Damysos with French cities") {
    Damysos() ++ loadPIOsFromCSV("france_cities.csv")
  }

  // World cities dataset
  val damysosWorld = PerfUtils.time[Damysos](s"Populating a Damysos with world cities") {
    Damysos() ++ loadPIOsFromCSV("world_cities.csv")
  }

  behavior of "The Damysos"

  it should "store points correctly" in {

    PerfUtils.time[Unit]("Checking French Damysos integirty") {
      val fake = PointOfInterst("fake city", Coordinates(2.3522219, 48.856614))
      assert(!(damysosFrance contains fake)) // The coordinates exist, but with a different name
      val nonExistant = PointOfInterst("I do not exist", Coordinates(-0.380752, 47.501715))
      assert(!(damysosFrance contains nonExistant)) // All properties of this PIO are inexistant
      loadPIOsFromCSV("france_cities.csv").foreach(item => {
        assert(damysosFrance contains item)
      })
    }

    PerfUtils.time[Unit]("Checking World Damysos integirty") {
      loadPIOsFromCSV("world_cities.csv").foreach(item => {
        assert(damysosWorld contains item)
      })
    }
  }

  it should "find the neighboring points" in {

    val franceMatches = PerfUtils.time[List[PointOfInterst]]("Searching in France dataset") {
      val paris = Coordinates(2.3522219, 48.856614)
      damysosFrance.findSurrounding(paris)
    }
    assert(franceMatches.length == 91)

    val worldMatches = PerfUtils.time[List[PointOfInterst]]("Searching in World dataset") {
      val singapore = Coordinates(1.28967, 103.85007)
      damysosWorld.findSurrounding(singapore, 5)
    }
    assert(worldMatches.length == 13)
  }
}
