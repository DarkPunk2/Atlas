import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodeService {
    @GET("geocode/search")
    fun getGeocode(
        @Query("api_key") apiKey: String,
        @Query("text") location: String
    ): Call<GeocodeResponse>

    @GET("geocode/reverse")
    fun getGeocodeByLatLong(
        @Query("api_key") apiKey: String,
        @Query("point.lat") latitude: String,
        @Query("point.lon") longitude: String
    ): Call<GeocodeResponse>
}

