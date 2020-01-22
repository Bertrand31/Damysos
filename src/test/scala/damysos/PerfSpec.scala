import org.scalatest.FlatSpec
import utils.PerfUtils
import scala.util.Random
import damysos.{Coordinates, PointOfInterest, Damysos}
import damysos.PointOfInterest

class PerfSpec extends FlatSpec {

  import org.scalatest.Matchers._

  def linearSearch(list: List[PointOfInterest], coordinates: Coordinates): List[PointOfInterest] =
    list.filter(poi =>
      (Math.abs(poi.coordinates.longitude - coordinates.longitude) < 0.25) &&
        (Math.abs(poi.coordinates.latitude - coordinates.latitude) < 0.25)
    )

  "Damysos search" should "be orders or magnitude faster then a naive linear search" in {
    val cities = PointOfInterest.loadFromCSV("world_cities.csv").toList
    val augmentedData = cities ++ (0 to 3).flatMap(i =>
      cities.map(poi =>
        PointOfInterest(
          poi.name + i,
          Coordinates(
            Random.between(-90000, 90000) / 1000d,
            Random.between(-180000, 180000) / 1000d,
          ),
        )
      )
    )
    println(s"Items in dataset: ${augmentedData.length}")

    val singapore = Coordinates(1.28967, 103.85007)

    val damysos = Damysos(maxPrecision=Some(6)) ++ augmentedData

    var res2 = Array[PointOfInterest]()
    val damysosTime = PerfUtils.profile("Damysos search") {
      res2 = damysos.findSurrounding(singapore)
    }
    println(res2.map(_.name).toList)

    var res1 = List[PointOfInterest]()
    val augmentedDataList = augmentedData.toList
    val linearTime = PerfUtils.profile("Linear search") {
      res1 = linearSearch(augmentedDataList, singapore)
    }
    println(res1.map(_.name).toList)
    val timesFaster = linearTime / damysosTime
    println(s"$timesFaster times faster")
    assert(timesFaster > 1000)
  }
}
