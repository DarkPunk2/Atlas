package com.project.auntetification.interfaces

import com.project.atlas.models.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query


interface ElectricityApiService {
    @GET("datos/mercados/precios-mercados-tiempo-real")
    suspend fun getElectricityPrices(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("time_trunc") timeTrunc: String = "hour"
    ): ApiResponse
}


