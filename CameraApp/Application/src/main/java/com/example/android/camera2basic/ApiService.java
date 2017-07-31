package com.example.android.camera2basic;

import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.QueryMap;

public interface ApiService {
    /* Methods */
    @Multipart
    @POST("post_image")
    Call<Result> uploadImage(@Part MultipartBody.Part file);

    @POST("/post_message")
    @FormUrlEncoded
    Call<Post> PostMessage(@Field("title") String title,
                        @Field("body") String body,
                        @Field("userId") long userId);

    @GET("get_cam_state")
    Call<Result> getCamState(@QueryMap Map<String, String> params);

}
