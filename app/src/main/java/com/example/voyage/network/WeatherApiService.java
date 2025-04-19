package com.example.voyage.network;

import com.example.voyage.response.WeatherApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface WeatherApiService {

    @Headers({
            "x-rapidapi-host: weatherapi-com.p.rapidapi.com",
            "x-rapidapi-key: 8705b47712msh6f4737ae9615c82p1b2c5djsn1bff1ad9abae"
    })
    @GET("forecast.json")
    Call<WeatherApiResponse> getWeatherForecast(
            @Query("q") String location,
            @Query("days") int days
    );
}
