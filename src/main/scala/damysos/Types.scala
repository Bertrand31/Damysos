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

  private def arrayToPIO(arr: Array[String]): PointOfInterest =
    PointOfInterest(
      name=arr(0),
      coordinates=Coordinates(
        latitude=arr(1).toDouble,
        longitude=arr(2).toDouble,
      ),
    )

  def loadFromCSV(filename: String): Iterator[PointOfInterest] =
    scala.io.Source.fromResource(filename)
      .getLines
      .map(_.split(";"))
      .map(arrayToPIO)
}
