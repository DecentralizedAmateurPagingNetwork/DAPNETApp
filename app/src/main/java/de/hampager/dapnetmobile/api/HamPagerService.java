package de.hampager.dapnetmobile.api;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface HamPagerService {

    @POST("calls")
    Call<HamnetCall> postHamnetCall(@Body HamnetCall hamnetcall);

    @GET("calls")
    Call<ArrayList<HamnetCall>> getAllHamnetCalls();

    @GET("calls")
    Call<ArrayList<HamnetCall>> getOwnerHamnetCalls(@Query("ownerName") String ownerName);

    @GET("users/{name}")
    Call<UserResource> getUserResource(@Path("name") String username);

    //Served via ServiceGenerator
    @GET("core/version")
    Call<Versions> getVersions();

    @GET("callsigns/{name}")
    Call<ArrayList<CallSignResource>> getAllCallSigns(@Path("name") String username);

    @GET("transmitterGroups")
    Call<ArrayList<TransmitterGroupResource>> getAllTransmitterGroups();

    @GET("stats")
    Call<StatsResource> getStats();
}
