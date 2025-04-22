package com.example.voyage.models;

import java.io.Serializable;

public class Hotel implements Serializable {
    private String name;
    private String address;
    private double rating;
    private String placeId;
    private int priceLevel;

    public Hotel() {}

    public Hotel(String name, String address, double rating, String placeId, int priceLevel) {
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.placeId = placeId;
        this.priceLevel = priceLevel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public int getPriceLevel() {
        return priceLevel;
    }

    public void setPriceLevel(int priceLevel) {
        this.priceLevel = priceLevel;
    }
}
