data class GeocodeResponse(
    val geocoding: Geocoding,
    val type: String,
    val features: List<Feature>
)

data class Geocoding(
    val version: String,
    val query: Query,
    val warnings: List<String>,
    val engine: Engine,
    val timestamp: Long
)

data class Query(
    val text: String,
    val size: Int
)

data class Engine(
    val name: String,
    val author: String,
    val version: String
)

data class Feature(
    val type: String,
    val geometry: Geometry,
    val properties: Properties
)

data class Geometry(
    val type: String,
    val coordinates: List<Double>
)

data class Properties(
    val id: String,
    val gid: String,
    val layer: String,
    val name: String,
    val confidence: Double,
    val country: String,
    val region: String,
    val locality: String,
    val label: String
)

