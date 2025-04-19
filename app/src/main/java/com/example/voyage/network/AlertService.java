package com.example.voyage.network;

import com.example.voyage.response.AlertResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface AlertService {
    @Headers({
            "X-RapidAPI-Host: weatherapi-com.p.rapidapi.com",
            "X-RapidAPI-Key: 8705b47712msh6f4737ae9615c82p1b2c5djsn1bff1ad9abae"  // Replace with your actual key
    })
    @GET("alerts.json")
    Call<AlertResponse> getAlerts(@Query("q") String region);
}
