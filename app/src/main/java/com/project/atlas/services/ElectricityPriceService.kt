package com.project.atlas.services


import com.project.auntetification.interfaces.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.LocalDateTime

class ElectricityPriceService {
    private val BASE_URL = "https://apidatos.ree.es/es/"

    private var pricesByHourMap: Map<String, Double> = emptyMap()

    private var lastUpdate: LocalDate = LocalDate.of(1999,12,31)

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    suspend fun fetchPricesByHour() {
        var currentDate = LocalDate.now()
        if (lastUpdate.isBefore(currentDate)){
        try {
            var startDate = LocalDateTime.now().minusHours(1)
            var endDate = startDate.plusDays(1)
            val response = apiService.getElectricityPrices(startDate.toString(), endDate.toString())

            // Accedemos a los precios de la respuesta, que están dentro de 'included'
            pricesByHourMap = response.included
                .firstOrNull { it.type == "PVPC" } // Filtramos el tipo "PVPC"
                ?.attributes
                ?.values
                ?.associate {
                    val hour = it.datetime.substring(11, 13)  // Extraemos solo la hora (HH)
                    hour to it.price  // Asociamos la hora con el valor del precio
                } ?: emptyMap()
            if (!pricesByHourMap.isEmpty()) lastUpdate = currentDate
        } catch (e: Exception) {
            println("Error al obtener los precios: ${e.message}")
        }
        }
    }

    fun getPriceByHour(): Double {
        val hour = String.format("%02d", LocalDateTime.now().hour)
        val price = pricesByHourMap[hour]
        return price ?: -1.0
    }
    fun getPricesMap(): Map<String, Double>{
        return pricesByHourMap
    }

}