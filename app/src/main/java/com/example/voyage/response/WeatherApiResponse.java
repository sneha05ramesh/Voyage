package com.example.voyage.response;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class WeatherApiResponse implements Serializable {
    public Location location;
    public Forecast forecast;

    public static class Location {
        public String name;
        public String region;
        public String country;
    }

    public static class Forecast {
        @SerializedName("forecastday")
        public List<ForecastDay> forecastday;
    }

    public static class ForecastDay {
        public String date;
        public Day day;
    }

    public static class Day {
        @SerializedName("avgtemp_c")
        public float avgtempC;

        public Condition condition;
    }

    public static class Condition {
        public String text;
        public String icon; // Example: "//cdn.weatherapi.com/..."
    }
}
