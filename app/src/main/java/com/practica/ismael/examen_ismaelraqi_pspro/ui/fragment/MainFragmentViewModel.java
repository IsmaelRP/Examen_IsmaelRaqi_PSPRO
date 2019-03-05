package com.practica.ismael.examen_ismaelraqi_pspro.ui.fragment;

import android.annotation.SuppressLint;
import com.practica.ismael.examen_ismaelraqi_pspro.data.base.Event;
import com.practica.ismael.examen_ismaelraqi_pspro.data.model.Data;
import com.practica.ismael.examen_ismaelraqi_pspro.data.model.WeatherInfo;
import com.practica.ismael.examen_ismaelraqi_pspro.data.remote.Api;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class MainFragmentViewModel extends ViewModel {

    private MutableLiveData<WeatherInfo> weatherData = new MutableLiveData<>();
    private MutableLiveData<Event<String>> errorTrigger = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private Api api;

    public MainFragmentViewModel(Api api) {
        this.api = api;
    }

    @SuppressLint("SimpleDateFormat")

    void getWeather(String city) {
        loading.setValue(true);
        String key = "a51cf9d9a1c078b1830823a5cefb823e";
        String units = "metric";
        String lang = "sp";
        Call<Data> response = api.getWeather(key, city, units, lang);

        //noinspection NullableProblems
        response.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {

                if (response.body() != null && response.isSuccessful()) {
                    WeatherInfo weatherInfo;

                    Date date1 = new java.util.Date(response.body().getSys().getSunrise() * 1000L);
                    Date date2 = new java.util.Date(response.body().getSys().getSunset() * 1000L);

                    @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("HH:mm");
                    formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

                    String time1 = formatter.format(date1);
                    String time2 = formatter.format(date2);

                    weatherInfo = new WeatherInfo(response.body().getName(), response.body().getWeather().get(0).getDescription(),
                            response.body().getMain().getTemp(), response.body().getMain().getTempMax(), response.body().getMain().getTempMin(),
                            response.body().getRain() == null ? "-" : String.valueOf(response.body().getRain().get1h()), response.body().getMain().getHumidity(), response.body().getWind().getSpeed(), response.body().getWind().getDeg() == null ? 0 : response.body().getWind().getDeg(),
                            response.body().getClouds().getAll(), response.body().getWeather().get(0).getIcon(), time1,
                            time2, response.body().getSys().getCountry());
                    weatherData.setValue(weatherInfo);
                } else {
                    errorTrigger.setValue(new Event<>("Unable to find city"));
                }
                loading.setValue(false);
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {
                errorTrigger.setValue(new Event<>(t.toString()));
                loading.setValue(false);
            }
        });
    }

    MutableLiveData<WeatherInfo> getWeatherData() {
        return weatherData;
    }

    MutableLiveData<Event<String>> getErrorTrigger() {
        return errorTrigger;
    }

    MutableLiveData<Boolean> getLoading() {
        return loading;
    }
}
