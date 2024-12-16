import com.project.atlas.apisRequest.RequestDataForRoute
import com.project.atlas.apisRequest.ResponseDataForRoute
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
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

    @Headers("Authorization: 5b3ce3597851110001cf62487f08fce4eb244c3fb214b1e26f965b9f", "Content-Type: application/json")
    @POST("v2/directions/{vehicle}")
    fun getRoute(
        @Path("vehicle") vehicle: String,
        @Body coordinates: RequestDataForRoute
    ): Call<ResponseDataForRoute>

}

