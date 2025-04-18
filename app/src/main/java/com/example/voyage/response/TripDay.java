package com.example.voyage.response;

import java.io.Serializable;
import java.util.List;

public class TripDay implements Serializable {
    public int day;
    public List<TripActivity> activities;
}
