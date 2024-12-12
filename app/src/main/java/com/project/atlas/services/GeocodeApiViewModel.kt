package com.project.atlas.services

import GeocodeResponse
import GeocodeService
import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object ApiClient {
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
