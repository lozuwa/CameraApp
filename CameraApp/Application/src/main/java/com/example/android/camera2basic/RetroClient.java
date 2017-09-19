package com.example.android.camera2basic;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroClient {
    /** URL of the upload folder*/
    public static String IP = "http://192.168.0.104:5000/";
    private static final String ROOT_URL = IP;

    public RetroClient() {}

    /*** Get Retro Client
     * @return JSON Object*/
    private static Retrofit getRetroClient() {
        return new Retrofit.Builder()
                            .baseUrl(ROOT_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
    }

    /*** Api service */
    public static ApiService getApiService() {
        return getRetroClient().create(ApiService.class);
    }
}
