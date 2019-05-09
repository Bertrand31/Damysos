package damysos

case class Coordinates(
  latitude: Double,
  longitude: Double
)

case class PointOfInterst(
  name: String,
  coordinates: Coordinates
)
