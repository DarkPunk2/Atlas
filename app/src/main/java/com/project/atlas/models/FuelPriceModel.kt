package com.project.atlas.models

import com.google.gson.annotations.SerializedName

data class FuelPriceModel(
    val Rótulo: String,
    val PrecioProducto: String,
    val Latitud: String,
    val Longitud: String
)

data class FuelPriceResponse(
    val ListaEESSPrecio: List<FuelStation> // Coincide con el JSON
)

data class FuelStation(
    val Rótulo: String,           // Coincide con el JSON
    val PrecioProducto: String,   // Coincide con el JSON
    val Latitud: String,          // Coincide con el JSON
    @SerializedName("Longitud (WGS84)") val Longitud: String
)

data class Municipio(
    val IDMunicipio: String,
    val IDProvincia: String,
    val IDCCAA: String,
    val Municipio: String,
    val Provincia: String,
    val CCAA: String
)

data class Provincia(
    val IDPovincia: String,
    val IDCCAA: String,
    val Provincia: String,
    val CCAA: String
)
