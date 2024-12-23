package com.project.atlas.apisRequest

data class RequestDataForRoute(
    val coordinates: List<List<Double>>,
    val preference: String
)
