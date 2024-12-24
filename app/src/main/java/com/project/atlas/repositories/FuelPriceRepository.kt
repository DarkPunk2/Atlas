// FuelPriceRepository.kt
package com.project.atlas.repository

import com.project.atlas.models.FuelPriceModel
import com.project.atlas.models.FuelPriceResponse
import com.project.atlas.models.Municipio
import com.project.atlas.models.Provincia
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface FuelPriceApi {
    @GET("EstacionesTerrestres/FiltroProvinciaProducto/{IDProvincia}/{IDProducto}")
    suspend fun getFuelPrice(
        @Path("IDProvincia") IDProvincia: String,
        @Path("IDProducto") idProducto: Int
    ): FuelPriceResponse

    @GET("Listados/Provincias/")
    suspend fun obtenerMunicipios(): List<Provincia> // Cambiado a suspend
}

class FuelPriceRepository {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://sedeaplicaciones.minetur.gob.es/ServiciosRESTCarburantes/PreciosCarburantes/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val fuelPriceApi = retrofit.create(FuelPriceApi::class.java)

    suspend fun getFuelPrices(idRegion: String, idProducto: Int): List<FuelPriceModel> {
        // Obtenemos la respuesta de la API
        val response = fuelPriceApi.getFuelPrice(idRegion, idProducto)

        // Mapeamos cada FuelStation a FuelPriceModel
        return response.ListaEESSPrecio.map { fuelStation ->
            FuelPriceModel(
                Rótulo = fuelStation.Rótulo,
                PrecioProducto = fuelStation.PrecioProducto,
                Latitud = fuelStation.Latitud,
                Longitud = fuelStation.Longitud
            )
        }
    }

    suspend fun getRegions(): List<Provincia> {
        return try {
            fuelPriceApi.obtenerMunicipios().map { provincia ->
                Provincia(
                    IDPovincia = provincia.IDPovincia,
                    IDCCAA = provincia.IDCCAA,
                    Provincia = provincia.Provincia,
                    CCAA = provincia.CCAA
                )
            }
        } catch (e: Exception) {
            emptyList() // Manejo básico de errores, puedes ajustar según necesidades
        }
    }



}

