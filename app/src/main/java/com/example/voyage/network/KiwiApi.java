package com.example.voyage.network;

import com.example.voyage.response.KiwiFlightResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface KiwiApi {

    @Headers({
            "X-RapidAPI-Key: 8705b47712msh6f4737ae9615c82p1b2c5djsn1bff1ad9abae",
            "X-RapidAPI-Host: kiwi-com-cheap-flights.p.rapidapi.com"
    })
    @GET("one-way")
    Call<KiwiFlightResponse> getFlights(
            @Query("source") String source,       // "City:berlin_de"
            @Query("destination") String destination, // "City:dubrovnik_hr"
            @Query("currency") String currency,   // "usd"
            @Query("locale") String locale,       // "en"
            @Query("adults") int adults,          // 1
            @Query("cabinClass") String cabinClass, // "ECONOMY"
            @Query("outbound") String outboundDays, // "MONDAY,TUESDAY,..."
            @Query("transportTypes") String transportType, // "FLIGHT"
            @Query("limit") int limit             // 10
    );
}