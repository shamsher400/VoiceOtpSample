package com.bng.bngvoiceotp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

 class ApiClient {
     static String BASE_URL = "";
     static String authenticationHeader = "";
     static boolean isHttpsRequest = false;
    private static Retrofit retrofit;
    static long connectionTimeout = 45;
    static long requestTimeout = 30;

     private static Retrofit getRetrofitInstance() {
         if (retrofit == null) {
             Gson gson = new GsonBuilder()
                     .setLenient()
                     .create();
             if (isHttpsRequest) {
                 OkHttpClient.Builder unsafeOkHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient().newBuilder().addInterceptor(new Interceptor() {
                     @Override
                     public Response intercept(Chain chain) throws IOException {
                         Request original = chain.request();
                         Request request = original.newBuilder()
                                 .header("Content-type", "application/json")
                                 .header("Authorization", ApiClient.authenticationHeader)
                                 .header("Content-type", "application/x-www-form-urlencoded;charset=utf-8")
                                 .build();
                         return chain.proceed(request);
                     }
                 });
                 OkHttpClient client = unsafeOkHttpClient
                         .connectTimeout(connectionTimeout, TimeUnit.SECONDS)
                         .readTimeout(requestTimeout, TimeUnit.SECONDS)
                         .writeTimeout(requestTimeout, TimeUnit.SECONDS)
                         .retryOnConnectionFailure(true)
                         .build();
                 retrofit = new Retrofit.Builder()
                         .baseUrl(BASE_URL)
                         .addConverterFactory(GsonConverterFactory.create(gson))
                         .client(client)
                         .build();
             } else {
                 retrofit = new retrofit2.Retrofit.Builder()
                         .baseUrl(BASE_URL)
                         .addConverterFactory(GsonConverterFactory.create())
                         .build();
             }
         }
         return retrofit;
     }


     public static ApiInterface getApiService() {
         return getRetrofitInstance().create(ApiInterface.class);
     }
}
