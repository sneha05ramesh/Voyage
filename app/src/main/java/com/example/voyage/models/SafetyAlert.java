package com.example.voyage.models;

public class SafetyAlert {
    public String title;
    public String description;
    public String date;
    public String source;

    public SafetyAlert(String title, String description, String date, String source) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.source = source;
    }
}
