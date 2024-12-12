package com.project.atlas.services

import GeocodeResponse
import GeocodeService
import com.project.atlas.apisRequest.RequestDataForRute
import com.project.atlas.apisRequest.ResponseDataForRute
import com.project.atlas.exceptions.InvalidRuteException
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
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


    fun fetchGeocode(apiKey: String, location: String, onResult: (String) -> Unit) {
        val call = geocodeService.getGeocode(apiKey, location)

        call.enqueue(object : Callback<GeocodeResponse> {
            override fun onResponse(
                call: Call<GeocodeResponse>,
                response: Response<GeocodeResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val geocodeResponse = response.body()
                    onResult(geocodeResponse.toString()) // O adapta según lo que necesites mostrar

                } else {
                    onResult("Error: ${response.code()} ${response.message()}")
                }
            }

            override fun onFailure(call: Call<GeocodeResponse>, t: Throwable) {
                onResult("Error: ${t.message}")
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

    suspend fun fetchRute(coordinates: List<List<Double>>, ruteType: String, vehicleType: String): ResponseDataForRute {
        val body = RequestDataForRute(coordinates, ruteType)
        return suspendCancellableCoroutine { continuation ->
            val call = geocodeService.getRute(vehicleType, body)

            call.enqueue(object : Callback<ResponseDataForRute> {
                override fun onResponse(call: Call<ResponseDataForRute>, response: Response<ResponseDataForRute>) {
                    if (response.isSuccessful && response.body() != null) {
                        continuation.resume(response.body()!!)
                    } else {
                        continuation.resumeWithException(InvalidRuteException("Error: ${response.code()} ${response.message()}"))
                    }
                }

                override fun onFailure(call: Call<ResponseDataForRute>, t: Throwable) {
                    continuation.resumeWithException(InvalidRuteException("Error: ${t.message ?: "Unknown error"}"))
                }
            })

            continuation.invokeOnCancellation {
                call.cancel()
            }
        }
    }
}
