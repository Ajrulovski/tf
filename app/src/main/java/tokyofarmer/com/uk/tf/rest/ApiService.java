package tokyofarmer.com.uk.tf.rest;


import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.mime.TypedInput;

/**
 * Created by Gazmend on 6/3/2016.
 */
public interface ApiService {
    @POST("/mobileapi")
    void getDocument(@Header("Authorization") String authorization, @Body TypedInput body, Callback<Response> cb);

    @POST("/api/auth/login")
    void startLoginSession(@Body TypedInput body, Callback<Response> cb);

    @POST("/api/rpc")
    void getAllDocIds(@Body TypedInput body, Callback<Response> cb);

}
