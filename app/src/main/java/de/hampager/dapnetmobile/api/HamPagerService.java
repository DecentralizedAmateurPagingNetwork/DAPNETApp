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
    //Call<HamnetCalls> postHamnetCall(String text, List<String> callSignNames,List<String> transmitterGroupNames, boolean emergency);
    Call<HamnetCall> postHamnetCall(@Body HamnetCall hamnetcall);

    @GET("calls")
    //List<Call<HamnetCall>> getAllHamnetCalls(@Body HamnetCall hamnetcall);
    Call<ArrayList<HamnetCall>> getAllHamnetCalls();
    // ?ownername=

    @GET("calls")
    Call<ArrayList<HamnetCall>> getOwnerHamnetCalls(@Query("ownerName") String ownerName);

    @GET("users/{name}")
    Call<UserResource> getUserResource(@Path("name") String username);
    //Served via ServiceGenerator
}
