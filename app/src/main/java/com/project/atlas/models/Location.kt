package com.project.atlas.models

class Location() {
    var lat: Double = 0.0
    var lon: Double = 0.0
    var alias: String = ""
    var toponym: String = ""
    var isFavourite: Boolean = false

    constructor (lat: Double, lon: Double, alias: String, toponym: String, favourite: Boolean) : this() {
        this.lat = lat
        this.lon = lon
        this.alias = alias
        this.toponym = toponym
        this.isFavourite = favourite
    }

    constructor (lat: Double, lon: Double, alias: String, toponym: String) : this() {
        this.lat = lat
        this.lon = lon
        this.alias = alias
        this.toponym = toponym
    }

    fun changeFavourite(){
        isFavourite = !isFavourite
    }
}