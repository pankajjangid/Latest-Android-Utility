package latest.pankaj.utility.google_map.networking;



import latest.pankaj.utility.google_map.directions.DirectionResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by navneet on 17/7/16.
 */
public interface RetrofitMaps {

    /*
     * Retrofit get annotation with our URL
     * And our method that will return us details of student.
     */
    @GET("place/nearbysearch/json?sensor=true&key=AIzaSyDN7RJFmImYAca96elyZlE5s_fhX-MMuhk")
    Call<Example> getNearbyPlaces(@Query(value = "type", encoded = true) String type,
                                  @Query(value = "location", encoded = true) String location,
                                  @Query(value = "radius", encoded = true) int radius,
                                  @Query(value = "opennow", encoded = true) boolean opennow);


    @GET("place/search/json?")
    Call<Example> getGeoLocation(@Query(value = "location", encoded = true) String location, @Query(value = "key", encoded = true) String API_KEY,
                                 @Query(value = "radius", encoded = true) int radius, @Query(value = "sensor", encoded = true) boolean sensor);


    @GET("/maps/api/directions/json")
    public void getDirections(@Query("origin") String origin, @Query("destination") String destination, Callback<DirectionResults> callback);

}