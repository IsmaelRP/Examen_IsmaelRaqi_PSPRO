package com.practica.ismael.examen_ismaelraqi_pspro.data.remote;

import com.practica.ismael.examen_ismaelraqi_pspro.data.model.Data;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api {
    @GET("weather")
    Call<Data> getWeather(@Query("appid") String key, @Query("q") String city, @Query("units") String units,
                          @Query("lang") String lang);

}
