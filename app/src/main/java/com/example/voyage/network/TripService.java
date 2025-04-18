package com.example.voyage.network;

import com.example.voyage.response.ItineraryResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface TripService {

    @Headers({
            "Content-Type: application/json",
            "X-RapidAPI-Key: 8705b47712msh6f4737ae9615c82p1b2c5djsn1bff1ad9abae",  // Replace with your actual key
            "X-RapidAPI-Host: ai-trip-planner.p.rapidapi.com"
    })
    @POST("detailed-plan")
    Call<ItineraryResponse> getItinerary(@Body TripRequest request);
}
