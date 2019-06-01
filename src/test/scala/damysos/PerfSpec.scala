import org.scalatest.FlatSpec
import utils.PerfUtils
import damysos.{Coordinates, PointOfInterst, Damysos}
import damysos.PointOfInterst

class PerfSpec extends FlatSpec {

  import org.scalatest.Matchers._

  def linearSearch(list: List[PointOfInterst], coordinates: Coordinates): List[PointOfInterst] =
    list.filter(poi =>
      (Math.abs(poi.coordinates.longitude - coordinates.longitude) < 0.25) &&
        (Math.abs(poi.coordinates.latitude - coordinates.latitude) < 0.25)
    )

  "Damysos search" should "be orders or magnitude faster then a naive linear search" in {
    val cities = PointOfInterst.loadFromCSV("world_cities.csv").toList
    val augmentedData = (0 to 12).flatMap(i =>
      cities.map(poi =>
        poi.copy(
          name = poi.name + i,
          coordinates = Coordinates(poi.coordinates.latitude + i, poi.coordinates.longitude + i)
        )
      )
    )
    println(s"Size of dataset: ${augmentedData.length}")

    val damysos = Damysos() ++ augmentedData
    val singapore = Coordinates(1.28967, 103.85007)

    var res2 = Array[PointOfInterst]()
    val damysosTime = PerfUtils.profile("Damysos search") {
      res2 = damysos.findSurrounding(singapore)
    }
    println(res2.map(_.name).mkString(", "))

    var res1 = List[PointOfInterst]()
    val augmentedDataList = augmentedData.toList
    val linearTime = PerfUtils.profile("Linear search") {
      res1 = linearSearch(augmentedDataList, singapore)
    }
    println(res1.map(_.name).mkString(", "))

    val timesFaster = linearTime / damysosTime
    println(s"$timesFaster times faster")
    assert(timesFaster > 1000)
  }
}
