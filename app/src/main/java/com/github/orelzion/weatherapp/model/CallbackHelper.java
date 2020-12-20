package com.github.orelzion.weatherapp.model;

import android.util.Log;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallbackHelper implements Callback<List<ForecastDay>> {

    @Override
    public void onResponse(Call<List<ForecastDay>> call, Response<List<ForecastDay>> response) {
        Log.d("api", response.toString());
    }

    @Override
    public void onFailure(Call<List<ForecastDay>> call, Throwable t) {
        Log.e("api", "failed", t);
    }
}
