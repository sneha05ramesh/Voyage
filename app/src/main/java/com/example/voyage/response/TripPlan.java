package com.example.voyage.response;

import java.io.Serializable;
import java.util.List;

public class TripPlan implements Serializable {
    public int days;
    public String destination;
    public String budget;
    public String fromCity;
    public String travelMode;
    public List<String> interests;
    public List<TripDay> itinerary;

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
