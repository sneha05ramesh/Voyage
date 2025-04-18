package com.example.voyage.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class KiwiFlightResponse {
    @SerializedName("itineraries")
    public List<ItineraryOneWay> data;

    public static class ItineraryOneWay {
        @SerializedName("id")
        public String id;

        @SerializedName("price")
        public Price price;

        @SerializedName("sector")
        public Sector sector;

        @SerializedName("provider")
        public Provider provider;

        @SerializedName("bookingOptions")
        public BookingOptions bookingOptions;
    }

    public static class Price {
        @SerializedName("amount")
        public String amount;
    }

    public static class Sector {
        @SerializedName("duration")
        public int duration;

        @SerializedName("sectorSegments")
        public List<SectorSegment> segments;
    }

    public static class SectorSegment {
        @SerializedName("segment")
        public Segment segment;
    }

    public static class Segment {
        @SerializedName("source")
        public Station source;

        @SerializedName("destination")
        public Station destination;

        @SerializedName("duration")
        public int duration;

        @SerializedName("carrier")
        public Carrier carrier;
    }

    public static class Station {
        @SerializedName("localTime")
        public String localTime;

        @SerializedName("utcTime")
        public String utcTime;

        @SerializedName("station")
        public Airport station;
    }

    public static class Airport {
        @SerializedName("name")
        public String name;

        @SerializedName("code")
        public String code;

        @SerializedName("city")
        public City city;
    }

    public static class City {
        @SerializedName("name")
        public String name;
    }

    public static class Carrier {
        @SerializedName("name")
        public String name;

        @SerializedName("code")
        public String code;
    }

    public static class Provider {
        @SerializedName("name")
        public String name;
    }

    public static class BookingOptions {
        @SerializedName("edges")
        public List<BookingEdge> edges;
    }

    public static class BookingEdge {
        @SerializedName("node")
        public BookingNode node;
    }

    public static class BookingNode {
        @SerializedName("token")
        public String token;

        @SerializedName("bookingUrl")
        public String bookingUrl;
    }
}