package com.practica.ismael.examen_ismaelraqi_pspro.ui.fragment;

import android.app.Notification;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.practica.ismael.examen_ismaelraqi_pspro.R;
import com.practica.ismael.examen_ismaelraqi_pspro.data.base.Event;
import com.practica.ismael.examen_ismaelraqi_pspro.data.model.WeatherInfo;
import com.practica.ismael.examen_ismaelraqi_pspro.data.remote.ApiService;
import com.practica.ismael.examen_ismaelraqi_pspro.databinding.FragmentMainBinding;
import com.practica.ismael.examen_ismaelraqi_pspro.utils.Constants;
import com.practica.ismael.examen_ismaelraqi_pspro.utils.KeyboardUtils;
import com.practica.ismael.examen_ismaelraqi_pspro.utils.SnackbarUtils;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class MainFragment extends Fragment {

    private MainFragmentViewModel vm;
    private FragmentMainBinding b;
    private final Picasso picasso = Picasso.get();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        b = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        return b.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupViews();
    }

    private void setupViews() {
        requireActivity().setTitle(R.string.title);
        vm = ViewModelProviders.of(this,
                new MainFragmentViewModelFactory(ApiService.getInstance().getApi())).get(MainFragmentViewModel.class);
        b.fab.setOnClickListener(v -> queryWeather());
        observeError();
        observeData();
        observeLoading();
    }

    private void observeLoading() {
        vm.getLoading().observe(getViewLifecycleOwner(), this::showLoading);
    }

    private void showLoading(Boolean flag) {
        if (flag) {
            b.pbProgress.setVisibility(View.VISIBLE);
        } else {
            b.pbProgress.setVisibility(View.INVISIBLE);
        }
    }

    private void observeData() {
        vm.getWeatherData().observe(getViewLifecycleOwner(), this::restoreData);
    }

    private void restoreData(WeatherInfo weatherInfo) {
        b.lblTemp.setVisibility(View.VISIBLE);
        b.lblWind.setVisibility(View.VISIBLE);
        b.lblCityName.setText(getString(R.string.cityName, weatherInfo.getName(), weatherInfo.getCountry()));
        b.lblWeatherType.setText(weatherInfo.getDescription());
        b.lblTempMin.setText(getString(R.string.minTemp, weatherInfo.getMinTemp()));
        b.lblTempMax.setText(getString(R.string.maxTemp, weatherInfo.getMaxTemp()));
        b.lblTempAverage.setText(getString(R.string.mediaTemp, weatherInfo.getAverageTemp()));
        b.lblRain.setText(getString(R.string.rain, weatherInfo.getRain()));
        b.lblHumidity.setText(getString(R.string.porcent, weatherInfo.getHumidity()));
        b.lblWindSpeed.setText(getString(R.string.windSpeed, weatherInfo.getWindSpeed()));
        b.lblWindDirection.setText(getString(R.string.windDirection, weatherInfo.getWindDirection()));
        b.lblCloud.setText(getString(R.string.cloud, weatherInfo.getClouds()));
        b.lblSunriseDawn.setText(getString(R.string.sunsetdawn, weatherInfo.getDawn(), weatherInfo.getSunset()));
        String img = "http://openweathermap.org/img/w/";
        String imgType = ".png";
        picasso.load(getString(R.string.image, img, weatherInfo.getImgWeather(), imgType))
                .error(R.drawable.ic_wb_sunny_black_24dp)     //  Por si acaso
                .into(b.imgWeather);
        Notification notification = new NotificationCompat.Builder(requireContext(), Constants.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_wb_sunny_black_24dp)
                .setContentTitle("Weather")
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
                .setContentText(weatherInfo.getName() + ", " + weatherInfo.getDescription())
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
        notificationManager.notify(1, notification);
    }

    private void observeError() {
        vm.getErrorTrigger().observe(getViewLifecycleOwner(), this::showError);
    }

    private void showError(Event<String> stringEvent) {
        String error;
        if (!stringEvent.hasBeenHandled()) {
            error = stringEvent.getContentIfNotHandled();
            SnackbarUtils.snackbar(requireView(), error, Snackbar.LENGTH_SHORT);
        }
    }

    private void queryWeather() {
        if (Objects.requireNonNull(b.txtCity.getText()).toString().isEmpty()) {
            KeyboardUtils.hideSoftKeyboard(requireActivity());
            SnackbarUtils.snackbar(requireView(), getString(R.string.empty), Snackbar.LENGTH_SHORT);

        } else {
            KeyboardUtils.hideSoftKeyboard(requireActivity());
            vm.getWeather(b.txtCity.getText().toString());
        }
    }
}
