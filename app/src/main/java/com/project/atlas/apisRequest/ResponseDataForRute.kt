package com.project.atlas.apisRequest

data class ResponseDataForRute(
    val bbox: List<Double>,
    val metadata: Metadata,
    val routes: List<Route>
){
    fun getDistance(): Double {
        return routes[0].summary.distance
    }
    fun getDuration(): Double{
        return routes[0].summary.duration
    }
    fun getRute(): String{
        return routes[0].geometry
    }
}

enum class RuteType {
    FASTER,
    RECOMMENDED,
    SHORTER;
    fun getPreference(): String {
        return when (this) {
            FASTER -> "fastest"
            RECOMMENDED -> "recommended"
            SHORTER -> "shortest"
        }
    }
}


data class Engine(
    val build_date: String,
    val graph_date: String,
    val version: String
)

data class Metadata(
    val attribution: String,
    val engine: Engine,
    val query: Query,
    val service: String,
    val timestamp: Long
)

data class Query(
    val coordinates: List<List<Double>>,
    val format: String,
    val preference: String,
    val profile: String
)

data class Route(
    val bbox: List<Double>,
    val geometry: String,
    val segments: List<Segment>,
    val summary: Summary,
    val way_points: List<Int>
)

data class Segment(
    val distance: Double,
    val duration: Double,
    val steps: List<Step>
)

data class Step(
    val distance: Double,
    val duration: Double,
    val instruction: String,
    val name: String,
    val type: Int,
    val way_points: List<Int>
)

data class Summary(
    val distance: Double,
    val duration: Double
)