import org.scalatest.FlatSpec
import utils.PerfUtils
import damysos.{Coordinates, PointOfInterst, Damysos}
import damysos.PointOfInterst

class DamysosSpec extends FlatSpec {

  import org.scalatest.Matchers._

  // Cities of France Damysos
  val damysosFrance = PerfUtils.time[Damysos](s"Populating a Damysos with French cities") {
    Damysos() ++ PointOfInterst.loadFromCSV("france_cities.csv")
  }

  // World cities Damysos
  val damysosWorld = PerfUtils.time[Damysos](s"Populating a Damysos with world cities") {
    Damysos() ++ PointOfInterst.loadFromCSV("world_cities.csv")
  }

  PerfUtils.time[Array[PointOfInterst]]("toArray") {
    damysosWorld.toArray
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

    val franceMatches = PerfUtils.time[Array[PointOfInterst]]("Searching in France dataset") {
      val parisCoordinates = Coordinates(2.3522219, 48.856614)
      damysosFrance.findSurrounding(parisCoordinates)
    }
    assert(franceMatches.size == 91)

    val worldMatches = PerfUtils.time[Array[PointOfInterst]]("Searching in World dataset") {
      val singaporeCoordinates = Coordinates(1.28967, 103.85007)
      damysosWorld.findSurrounding(singaporeCoordinates, 5)
    }
    assert(worldMatches.size == 13)
  }

  it should "return a list of all the contents" in {

    val paris = PointOfInterst("Paris", Coordinates(2.3522219, 48.856614))
    val singapore = PointOfInterst("Singapore", Coordinates(1.28967, 103.85007))
    val data = Seq(paris, singapore)
    val tempDamysos = Damysos() ++ data
    tempDamysos.toArray should contain theSameElementsAs data
  }

  it should "return the correct size" in {

    val damysosFranceSize = PerfUtils.time[Int]("Counting the elements in France dataset") {
      damysosFrance.size
    }
    assert(damysosFranceSize == 36318)

    val damysosWorldSize = PerfUtils.time[Int]("Counting the elements in World dataset") {
      damysosWorld.size
    }
    assert(damysosWorldSize == 128769)
  }

  it should "remove an element" in {
    val paris = PointOfInterst("Paris", Coordinates(2.3522219, 48.856614))
    val tempDamysos = Damysos() + paris
    assert(tempDamysos.size == 1)
    val emptyDamysos = tempDamysos - paris
    assert(emptyDamysos.size == 0)
  }

  it should "remove multiple elements" in {
    val paris = PointOfInterst("Paris", Coordinates(2.3522219, 48.856614))
    val singapore = PointOfInterst("Singapore", Coordinates(1.28967, 103.85007))
    val data = Seq(paris, singapore)
    val tempDamysos = Damysos() ++ data
    assert(tempDamysos.size == 2)
    val emptyDamysos = tempDamysos -- data
    assert(emptyDamysos.size == 0)
  }
}
