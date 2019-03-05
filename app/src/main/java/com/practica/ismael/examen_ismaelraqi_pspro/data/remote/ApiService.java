package com.practica.ismael.examen_ismaelraqi_pspro.data.remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiService {
    private static ApiService instance;
    private Api api;

    public static ApiService getInstance() {
        if(instance == null)
            instance = new ApiService();
        return instance;
    }

    private ApiService(){
        buildApService();
    }

    private void buildApService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);
    }

    public Api getApi(){
        return api;
    }
}
