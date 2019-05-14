import org.scalatest.FlatSpec
import utils.PerfUtils
import damysos.{Coordinates, PointOfInterst, Damysos}
import damysos.PointOfInterst

class PerfSpec extends FlatSpec {

  import org.scalatest.Matchers._

  def linearSearch(list: List[PointOfInterst], coordinates: Coordinates): List[PointOfInterst] =
    list.filter(poi =>
      (Math.abs(poi.coordinates.longitude - coordinates.longitude) < 0.3) &&
        (Math.abs(poi.coordinates.latitude - coordinates.latitude) < 0.3)
    )

  behavior of "The Damysos' performance"

  it should "be much faster then a naive, linear search" in {
    val cities = PointOfInterst.loadFromCSV("world_cities.csv").toList
    val augmentedData = (1 to 2).foldLeft(cities)((acc, item) => {
      acc ++ cities.map(poi => {
        poi.copy(
          name = poi.name + item,
          coordinates = Coordinates(poi.coordinates.latitude + item, poi.coordinates.longitude + item)
        )
      })
    })
    println(augmentedData.length)
    val damysos = Damysos() ++ augmentedData
    val singapore = Coordinates(1.28967, 103.85007)

    var res2: List[PointOfInterst] = List()
    val damysosTime = PerfUtils.profile("Damysos search") {
      res2 = damysos.findSurrounding(singapore)
    }
    println(res2.map(_.name))

    var res1: List[PointOfInterst] = List()
    val linearTime = PerfUtils.profile("Linear search") {
      res1 = linearSearch(augmentedData, singapore)
    }
    println(res1.map(_.name))

    assert(damysosTime < (linearTime * 0.2))
  }
}
