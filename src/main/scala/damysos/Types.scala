package damysos

case class Coordinates(
  latitude: Double,
  longitude: Double
)

case class PointOfInterst(
  name: String,
  coordinates: Coordinates
)

object PointOfInterst {

  private def arrayToPIO(arr: List[String]): PointOfInterst =
    PointOfInterst(arr(0), Coordinates(arr(1).toDouble, arr(2).toDouble))

  def loadFromCSV(filename: String): Iterator[PointOfInterst] =
    scala.io.Source.fromResource(filename)
      .getLines
      .map(_.split(";").toList)
      .map(arrayToPIO)
}
