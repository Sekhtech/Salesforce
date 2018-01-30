package com.salesforce.nvisio.salesforce.network;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by USER on 10-Jan-18.
 */

public class RetrofitInstance {

    private static final String BASE_URL="https://maps.googleapis.com/maps/api/distancematrix/";
    private static OkHttpClient httpClient=new OkHttpClient.Builder().build();
    private static Retrofit.Builder builder=new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
    private static Retrofit retrofit=builder.build();

    public RetrofitInstance() {
    }
    public static <S> S createService(Class<S> serviceClass){
        return retrofit.create(serviceClass);
    }
}
