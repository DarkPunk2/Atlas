package com.project.atlas.Models

import org.osmdroid.util.GeoPoint

data class MapState(
    var zoomTo: GeoPoint,
    var zoomValue: Double,
    var initialPoint: GeoPoint,
    var finalPoint: GeoPoint
)