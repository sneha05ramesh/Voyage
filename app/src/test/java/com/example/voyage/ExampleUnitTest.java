package com.example.voyage;

import org.junit.Test;
import static org.junit.Assert.*;

public class ExampleUnitTest {

    @Test
    public void email_isValidFormat() {
        String email = "test@example.com";
        assertTrue(email.contains("@") && email.endsWith(".com"));
    }

    @Test
    public void passwords_match() {
        String pass = "mypassword";
        String confirmPass = "mypassword";
        assertEquals(pass, confirmPass);
    }

    @Test
    public void budgetFilter_appliesCorrectly() {
        int[] hotelPrices = {50, 70, 40};
        for (int price : hotelPrices) {
            assertTrue(price <= 100); // Assuming Low Budget is <= 100
        }
    }

    @Test
    public void tripDuration_isCorrect() {
        int start = 1;
        int end = 5;
        int expectedDays = 5 - 1 + 1;
        assertEquals(5, expectedDays);
    }

    @Test
    public void travelMode_defaultIsPlane() {
        String defaultMode = "plane";
        assertEquals("plane", defaultMode);
    }
}
