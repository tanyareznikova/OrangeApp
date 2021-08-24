package com.example.orange_app.notifications;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {

    private static Retrofit retrofit = null;

    public static Retrofit getRetrofit(String url){

        if(retrofit == null){

            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }//if

        return retrofit;

    }//Retrofit getRetrofit

}//Client
