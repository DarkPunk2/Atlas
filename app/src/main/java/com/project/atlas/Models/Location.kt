package com.project.atlas.Models

class Location() {
    var lat: Double = 0.0
    var lon: Double = 0.0
    var alias: String = ""

    constructor (lat: Double, lon: Double, alias: String) : this() {
        this.lat = lat
        this.lon = lon
        this.alias = alias
    }


}