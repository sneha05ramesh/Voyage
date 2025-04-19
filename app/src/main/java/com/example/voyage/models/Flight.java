package com.example.voyage.models;

import java.io.Serializable;

public class Flight implements Serializable {
    public String airline;
    public String departTime;
    public String arriveTime;
    public String duration;
    public String destination;
    public String price;
    public String flightId;
    public String bookingUrl; // ✅ new

    public Flight() {}

    public Flight(String airline, String departTime, String arriveTime, String duration, String destination, String price, String flightId, String bookingUrl) {
        this.airline = airline;
        this.departTime = departTime;
        this.arriveTime = arriveTime;
        this.duration = duration;
        this.destination = destination;
        this.price = price;
        this.flightId = flightId;
        this.bookingUrl = bookingUrl;
    }
}
