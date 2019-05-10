import org.scalatest.FlatSpec
import utils.PerfUtils
import damysos.{Coordinates, PointOfInterst, Damysos}
import damysos.PointOfInterst

class DamysosSpec extends FlatSpec {

  // Cities of France Damysos
  lazy val damysosFrance = PerfUtils.time[Damysos](s"Populating a Damysos with French cities") {
    Damysos() ++ PointOfInterst.loadFromCSV("france_cities.csv")
  }

  // World cities Damysos
  lazy val damysosWorld = PerfUtils.time[Damysos](s"Populating a Damysos with world cities") {
    Damysos() ++ PointOfInterst.loadFromCSV("world_cities.csv")
  }

  behavior of "The Damysos"

  it should "store points correctly" in {

    PerfUtils.time[Unit]("Checking French Damysos integirty") {
      val fake = PointOfInterst("fake city", Coordinates(2.3522219, 48.856614))
      assert(!(damysosFrance contains fake)) // The coordinates exist, but with a different name
      val nonExistant = PointOfInterst("I do not exist", Coordinates(-0.380752, 47.501715))
      assert(!(damysosFrance contains nonExistant)) // All properties of this PIO are inexistant
      PointOfInterst.loadFromCSV("france_cities.csv").foreach(item => {
        assert(damysosFrance contains item)
      })
    }

    PerfUtils.time[Unit]("Checking World Damysos integirty") {
      PointOfInterst.loadFromCSV("world_cities.csv").foreach(item => {
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
