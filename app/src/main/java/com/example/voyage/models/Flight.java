package com.example.voyage.models;

import java.io.Serializable;

public class Flight implements Serializable {

    public String airline;
    public String departTime;
    public String arriveTime;
    public String duration;
    public String destination;
    public int price;  // ✅ price as int for Firestore compatibility
    public String flightId;
    public String bookingUrl;

    public Flight() {
        // Default constructor required for Firebase
    }

    public Flight(String airline, String departTime, String arriveTime,
                  String duration, String destination, int price,
                  String flightId, String bookingUrl) {
        this.airline = airline;
        this.departTime = departTime;
        this.arriveTime = arriveTime;
        this.duration = duration;
        this.destination = destination;
        this.price = price;
        this.flightId = flightId;
        this.bookingUrl = bookingUrl;
    }

    public String getAirline() {
        return airline;
    }

    public String getDepartTime() {
        return departTime;
    }

    public String getArriveTime() {
        return arriveTime;
    }

    public String getDuration() {
        return duration;
    }

    public String getDestination() {
        return destination;
    }

    public int getPrice() {
        return price;
    }

    public String getFlightId() {
        return flightId;
    }

    public String getBookingUrl() {
        return bookingUrl;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }

    public void setDepartTime(String departTime) {
        this.departTime = departTime;
    }

    public void setArriveTime(String arriveTime) {
        this.arriveTime = arriveTime;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public void setBookingUrl(String bookingUrl) {
        this.bookingUrl = bookingUrl;
    }
}
