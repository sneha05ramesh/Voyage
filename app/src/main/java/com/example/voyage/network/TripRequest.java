package com.example.voyage.network;

import java.util.List;

public class TripRequest {
    public int days;
    public String destination;
    public List<String> interests;
    public String budget;
    public String travelMode;

    public TripRequest(int days, String destination, List<String> interests, String budget, String travelMode) {
        this.days = days;
        this.destination = destination;
        this.interests = interests;
        this.budget = budget;
        this.travelMode = travelMode;
    }
}
