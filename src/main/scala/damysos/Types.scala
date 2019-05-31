package damysos

case class Coordinates(
  latitude: Double,
  longitude: Double,
)

case class PointOfInterst(
  name: String,
  coordinates: Coordinates,
)

object PointOfInterst {

  private def arrayToPIO(arr: Array[String]): PointOfInterst =
    PointOfInterst(
      name=arr(0),
      coordinates=Coordinates(
        latitude=arr(1).toDouble,
        longitude=arr(2).toDouble,
      )
    )

  def loadFromCSV(filename: String): Iterator[PointOfInterst] =
    scala.io.Source.fromResource(filename)
      .getLines
      .map(_.split(";"))
      .map(arrayToPIO)
}
