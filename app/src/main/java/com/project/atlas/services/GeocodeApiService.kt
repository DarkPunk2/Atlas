package com.project.atlas.services

import GeocodeResponse
import GeocodeService
import android.util.Log
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object GeocodeApiService {
    private const val BASE_URL = "https://api.openrouteservice.org/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val geocodeService: GeocodeService by lazy {
        retrofit.create(GeocodeService::class.java)
    }


    fun fetchGeocode(apiKey: String, location: String, onResult: (Double, Double, String) -> Unit) {
        val call = geocodeService.getGeocode(apiKey, location)

        call.enqueue(object : Callback<GeocodeResponse> {
            override fun onResponse(
                call: Call<GeocodeResponse>,
                response: Response<GeocodeResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val geocodeResponse = response.body()

                    val toponym = geocodeResponse?.features?.get(0)?.properties?.label ?: "Topónimo no encontrado"
                    val lat = geocodeResponse?.features?.get(0)?.geometry?.coordinates?.get(1) ?: 0.0
                    val lon = geocodeResponse?.features?.get(0)?.geometry?.coordinates?.get(0) ?: 0.0

                    onResult(lat, lon, toponym)
                }
            }

            override fun onFailure(call: Call<GeocodeResponse>, t: Throwable) {
                Log.e("GeocodeApiViewModel", "Error en FetchGeocode")
            }
        })
    }

    fun fetchRegion(apiKey: String, latitude: String, longitude: String, onResult: (String) -> Unit) {
        val call = geocodeService.getGeocodeByLatLong(apiKey, latitude, longitude)

        call.enqueue(object : Callback<GeocodeResponse> {
            override fun onResponse(
                call: Call<GeocodeResponse>,
                response: Response<GeocodeResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val geocodeResponse = response.body()

                    // Extraer el campo `region` de la primera característica
                    val region = geocodeResponse?.features?.get(0)?.properties?.region ?: "Región no encontrada"

                    onResult(region)
                } else {
                    Log.e("GeocodeApiViewModel", "Respuesta fallida o cuerpo vacío")
                    onResult("Región no encontrada")
                }
            }

            override fun onFailure(call: Call<GeocodeResponse>, t: Throwable) {
                Log.e("GeocodeApiViewModel", "Error en FetchRegionByCoordinates", t)
                onResult("Error de conexión")
            }
        })
    }




    suspend fun fetchToponymByLatLong(apiKey: String, latitude: String, longitude: String): String {
        return suspendCoroutine { continuation ->
            val call = geocodeService.getGeocodeByLatLong(apiKey, latitude, longitude)

            call.enqueue(object : Callback<GeocodeResponse> {
                override fun onResponse(call: Call<GeocodeResponse>, response: Response<GeocodeResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val geocodeResponse = response.body()
                        val toponym = geocodeResponse?.features?.get(0)?.properties?.label ?: "Topónimo no encontrado"
                        continuation.resume(toponym)
                    } else {
                        continuation.resume("Error: ${response.code()} ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<GeocodeResponse>, t: Throwable) {
                    continuation.resume("Error: ${t.message}")
                }
            })
        }
    }


}
