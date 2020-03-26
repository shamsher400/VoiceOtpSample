package com.bng.bngvoiceotp;


import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

interface ApiInterface {
    @POST("initiateAuthCall")
    Call<JsonObject> requestForCall(@Body JsonObject jsonObject);

    @POST("endAuthCall")
    Call<JsonObject> endCallRequest(@Body JsonObject jsonObject);

}
