package com.example.voyage;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CountryCodeMapper {
    private static final Map<String, String> cityMap = new HashMap<>();

    static {
        // United States
        cityMap.put("new york", "JFK");
        cityMap.put("los angeles", "LAX");
        cityMap.put("chicago", "ORD");
        cityMap.put("houston", "IAH");
        cityMap.put("dallas", "DFW");
        cityMap.put("san francisco", "SFO");
        cityMap.put("atlanta", "ATL");
        cityMap.put("miami", "MIA");
        cityMap.put("seattle", "SEA");
        cityMap.put("boston", "BOS");
        cityMap.put("washington dc", "IAD");

        // Europe
        cityMap.put("paris", "CDG");
        cityMap.put("london", "LHR");
        cityMap.put("rome", "FCO");
        cityMap.put("madrid", "MAD");
        cityMap.put("berlin", "BER");
        cityMap.put("vienna", "VIE");
        cityMap.put("amsterdam", "AMS");
        cityMap.put("brussels", "BRU");
        cityMap.put("zurich", "ZRH");
        cityMap.put("prague", "PRG");
        cityMap.put("budapest", "BUD");

        // Asia
        cityMap.put("tokyo", "HND");
        cityMap.put("osaka", "KIX");
        cityMap.put("seoul", "ICN");
        cityMap.put("bangkok", "BKK");
        cityMap.put("singapore", "SIN");
        cityMap.put("beijing", "PEK");
        cityMap.put("shanghai", "PVG");
        cityMap.put("mumbai", "BOM");
        cityMap.put("delhi", "DEL");
        cityMap.put("dubai", "DXB");

        // Oceania
        cityMap.put("sydney", "SYD");
        cityMap.put("melbourne", "MEL");
        cityMap.put("auckland", "AKL");

        // Americas
        cityMap.put("toronto", "YYZ");
        cityMap.put("vancouver", "YVR");
        cityMap.put("mexico city", "MEX");
        cityMap.put("sao paulo", "GRU");
        cityMap.put("buenos aires", "EZE");

        // Africa
        cityMap.put("cairo", "CAI");
        cityMap.put("casablanca", "CMN");
        cityMap.put("nairobi", "NBO");
        cityMap.put("johannesburg", "JNB");
    }

    public static String getAirportCode(String userInput) {
        String cleaned = userInput.toLowerCase(Locale.ROOT).trim();
        return cityMap.getOrDefault(cleaned, null);
    }
}
