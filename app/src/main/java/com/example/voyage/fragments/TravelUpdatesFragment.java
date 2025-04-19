package com.example.voyage.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.voyage.R;
import com.example.voyage.network.WeatherApiClient;
import com.example.voyage.network.WeatherApiService;
import com.example.voyage.response.TripPlan;
import com.example.voyage.response.WeatherApiResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TravelUpdatesFragment extends Fragment {

    private static final String ARG_TRIP_PLAN = "trip_plan";
    private TripPlan tripPlan;

    private LinearLayout weatherContainer;

    public static TravelUpdatesFragment newInstance(TripPlan tripPlan) {
        TravelUpdatesFragment fragment = new TravelUpdatesFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TRIP_PLAN, tripPlan);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_travel_updates, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        weatherContainer = view.findViewById(R.id.weatherContainer);

        if (getArguments() != null) {
            tripPlan = (TripPlan) getArguments().getSerializable(ARG_TRIP_PLAN);
            fetchWeather(tripPlan.destination);
        }
    }

    private void fetchWeather(String location) {
        WeatherApiService service = WeatherApiClient.getClient().create(WeatherApiService.class);

        // Log the request details
        Log.d("WeatherDebug", "Requesting weather for: " + location + ", days: 5");

        service.getWeatherForecast(location, 5).enqueue(new Callback<WeatherApiResponse>() {
            @Override
            public void onResponse(Call<WeatherApiResponse> call, Response<WeatherApiResponse> response) {
                // Log the raw response
                Log.d("WeatherDebug", "Response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    // Log successful response details
                    WeatherApiResponse weatherData = response.body();
                    Log.d("WeatherDebug", "API response successful. Forecast days received: " +
                            (weatherData.forecast != null && weatherData.forecast.forecastday != null ?
                                    weatherData.forecast.forecastday.size() : 0));

                    if (weatherData.forecast != null && weatherData.forecast.forecastday != null) {
                        for (WeatherApiResponse.ForecastDay day : weatherData.forecast.forecastday) {
                            // Log each day's data
                            Log.d("WeatherDebug", "Processing day: " + day.date +
                                    ", condition: " + (day.day != null && day.day.condition != null ?
                                    day.day.condition.text : "N/A"));

                            View itemView = LayoutInflater.from(getContext())
                                    .inflate(R.layout.item_weather_day, weatherContainer, false);

                            TextView date = itemView.findViewById(R.id.textDate);
                            TextView condition = itemView.findViewById(R.id.textCondition);
                            TextView temp = itemView.findViewById(R.id.textTemp);
                            ImageView icon = itemView.findViewById(R.id.imageIcon);

                            date.setText(day.date);
                            condition.setText(day.day.condition.text);
                            temp.setText(String.format("%.1f°C", day.day.avgtempC));

                            // Log image loading
                            String iconUrl = "https:" + day.day.condition.icon;
                            Log.d("WeatherDebug", "Loading icon from: " + iconUrl);

                            Glide.with(requireContext())
                                    .load(iconUrl)
                                    .into(icon);

                            weatherContainer.addView(itemView);
                        }
                    } else {
                        Log.e("WeatherDebug", "Forecast data is null or empty");
                    }
                } else {
                    // Log error details
                    Log.e("WeatherDebug", "Response unsuccessful. Error body: " +
                            (response.errorBody() != null ? response.errorBody().toString() : "null"));
                }
            }

            @Override
            public void onFailure(Call<WeatherApiResponse> call, Throwable t) {
                // Log failure
                Log.e("WeatherDebug", "API call failed", t);

                TextView error = new TextView(getContext());
                error.setText("Failed to load weather data.");
                weatherContainer.addView(error);
            }
        });
    }
}
