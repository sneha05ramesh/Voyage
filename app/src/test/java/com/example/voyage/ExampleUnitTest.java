
package com.example.voyage;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;
import java.util.regex.Pattern;

public class ExampleUnitTest {

    @Test
    public void email_isValidRegex() {
        String email = "test@example.com";
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        assertTrue(Pattern.matches(emailRegex, email));
    }

    @Test
    public void passwords_meetStrengthCriteria() {
        String password = "StrongPass123!";
        assertTrue(password.length() >= 8 && password.matches(".*[!@#$%^&*()].*"));
    }

    @Test
    public void interests_mapToKeywords() {
        List<String> interests = Arrays.asList("adventure", "culture");
        List<String> keywords = new ArrayList<>();
        for (String interest : interests) {
            if (interest.equals("adventure")) keywords.add("hiking");
            if (interest.equals("culture")) keywords.add("museums");
        }
        assertTrue(keywords.contains("hiking") && keywords.contains("museums"));
    }

    @Test
    public void budgetLevel_isCorrect() {
        int amount = 250;
        String level = (amount <= 200) ? "low" : (amount <= 800 ? "medium" : "high");
        assertEquals("medium", level);
    }

    @Test
    public void tripDuration_computedCorrectly() {
        int startDay = 1;
        int endDay = 5;
        int duration = endDay - startDay + 1;
        assertEquals(5, duration);
    }

    @Test
    public void itineraryFormatted_intoDays() {
        String raw = "Day 1: Visit Eiffel Tower\\nDay 2: Louvre Museum\\nDay 3: Seine River Cruise";
        String[] days = raw.split("Day \\d+:");
        assertEquals(4, days.length); // [ "", " Visit Eiffel Tower\n", " Louvre Museum\n", " Seine River Cruise" ]
    }


    @Test
    public void expenses_groupedAndTotaledCorrectly() {
        Map<String, Integer> expenses = new HashMap<>();
        expenses.put("2025-04-21", 120);
        expenses.put("2025-04-22", 200);
        int total = expenses.values().stream().mapToInt(i -> i).sum();
        assertEquals(320, total);
    }

    @Test
    public void flightData_parsesValidId() {
        String flightId = "FL1234-DEL-NYC";
        assertTrue(flightId.contains("DEL") && flightId.contains("NYC"));
    }

    @Test
    public void hotelResponse_isNotEmpty() {
        List<String> hotels = Arrays.asList("Taj Hotel", "Oberoi", "Le Meridien");
        assertFalse(hotels.isEmpty());
    }

    @Test
    public void genderSelection_isValidOption() {
        List<String> validGenders = Arrays.asList("male", "female", "other");
        String selected = "female";
        assertTrue(validGenders.contains(selected));
    }
}
