package damysos

case class Coordinates(
  latitude: Double,
  longitude: Double,
)

case class PointOfInterest(
  name: String,
  coordinates: Coordinates,
)

object PointOfInterest {

  private def arrayToPIO(arr: Array[String]): PointOfInterest = {
    val Array(name, latitude, longitude) = arr
    PointOfInterest(name, Coordinates(latitude.toDouble, longitude.toDouble))
  }

  def loadFromCSV(filename: String): Iterator[PointOfInterest] =
    scala.io.Source.fromResource(filename)
      .getLines
      .map(_.split(";"))
      .map(arrayToPIO)
}
