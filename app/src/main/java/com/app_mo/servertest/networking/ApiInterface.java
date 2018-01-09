package com.app_mo.servertest.networking;

import com.app_mo.servertest.Model;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("items.php")
    Call<List<Model>> getItems(@Query("page") String page);
}
