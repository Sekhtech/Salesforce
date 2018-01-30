package com.salesforce.nvisio.salesforce.network;

import com.salesforce.nvisio.salesforce.Model.DistanceApiResponse;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by USER on 10-Jan-18.
 */

public interface RetrofitClient {
    @GET("json?{remaining}")
    Single<DistanceApiResponse>getDistance(
            @Path("remaining") String remaining
    );

    @GET
    Observable<DistanceApiResponse> getD(@Url String url);
}
