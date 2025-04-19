package com.example.voyage.response;

import java.util.List;

public class AlertResponse {
    public Location location;
    public AlertContainer alerts;

    public static class Location {
        public String name;
        public String region;
    }

    public static class AlertContainer {
        public List<Alert> alert;
    }

    public static class Alert {
        public String headline;
        public String desc;
        public String effective;
        public String event;
    }
}
