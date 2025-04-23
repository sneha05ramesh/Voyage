
package com.example.voyage;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.voyage.DashboardActivity;
import com.example.voyage.NewTripActivity;
import com.example.voyage.ReviewItineraryActivity;
import com.example.voyage.SafetyAlertsActivity;
import com.example.voyage.SignupActivity;
import com.example.voyage.WelcomeActivity;
import com.example.voyage.ItineraryDetailsActivity;
import com.example.voyage.response.TripPlan;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility.*;

import static org.mockito.Mockito.*;

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Rule
    public ActivityScenarioRule<SignupActivity> signupRule = new ActivityScenarioRule<>(SignupActivity.class);

    @Test
    public void signupForm_hasAllFields() {
        onView(withId(R.id.fullNameInput)).check(matches(isDisplayed()));
        onView(withId(R.id.emailInput)).check(matches(isDisplayed()));
        onView(withId(R.id.passwordInput)).check(matches(isDisplayed()));
        onView(withId(R.id.confirmPasswordInput)).check(matches(isDisplayed()));
        onView(withId(R.id.genderRadioGroup)).check(matches(isDisplayed()));
    }

    @Test
    public void welcomeScreen_buttonsVisible() {
        ActivityScenario.launch(WelcomeActivity.class);
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()));
        onView(withId(R.id.signupButton)).check(matches(isDisplayed()));
    }

    @Test
    public void welcomeScreen_loginNavigation() {
        ActivityScenario.launch(WelcomeActivity.class);
        onView(withId(R.id.loginButton)).perform(click());
    }


    @Test
    public void newTripActivity_inputsVisible() {
        ActivityScenario.launch(NewTripActivity.class);
        onView(withId(R.id.destinationInput)).check(matches(isDisplayed()));
        onView(withId(R.id.budgetGroup)).check(matches(isDisplayed()));
        onView(withId(R.id.interestsLabel)).check(matches(isDisplayed()));
        onView(withId(R.id.startDateInput)).check(matches(isDisplayed()));
        onView(withId(R.id.endDateInput)).check(matches(isDisplayed()));
    }

    @Test
    public void reviewItinerary_displayAndButtons() {
        ActivityScenario.launch(ReviewItineraryActivity.class);
        onView(withId(R.id.dayContainer)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.saveButton)).check(matches(isDisplayed()));
        onView(withId(R.id.cancelButton)).check(matches(isDisplayed()));
    }

    @Test
    public void itineraryDetails_tabsWorkWithTripPlan() {
        TripPlan dummy = new TripPlan();
        dummy.destination = "Paris";
        dummy.fromCity = "London";
        dummy.budget = "medium";
        dummy.travelMode = "plane";
        dummy.days = 5;
        dummy.itinerary = new ArrayList<>();
        dummy.interests = new ArrayList<>();

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ItineraryDetailsActivity.class);
        intent.putExtra("trip_plan", dummy); // ✅ matches activity key
        ActivityScenario.launch(intent);
        onView(withContentDescription("✈️ Flights")).perform(click());
        onView(withContentDescription("🏨 Hotels")).perform(click());
    }



    @Test
    public void hotelsFragment_buttonIsVisible() {
        // 🧪 Prepare dummy TripPlan with required fields to avoid crashes in FlightsFragment
        TripPlan dummy = new TripPlan();
        dummy.destination = "Paris";
        dummy.fromCity = "London"; // ✅ Required by FlightsFragment
        dummy.budget = "medium";
        dummy.travelMode = "plane";
        dummy.days = 5;
        dummy.itinerary = new ArrayList<>();
        dummy.interests = new ArrayList<>();

        // 📦 Launch ItineraryDetailsActivity with mock trip data
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ItineraryDetailsActivity.class);
        intent.putExtra("trip_plan", dummy); // ✅ Use the correct key
        ActivityScenario.launch(intent);

        // 🔍 Navigate to Hotels tab and check if the booked hotels button is visible
        onView(withContentDescription("🏨 Hotels")).perform(click());
        onView(withId(R.id.btn_view_booked_hotels)).check(matches(isDisplayed()));
    }


    @Test
    public void safetyAlerts_showsList() {
        ActivityScenario.launch(SafetyAlertsActivity.class);
        onView(withId(R.id.recyclerViewSafety)).check(matches(isDisplayed()));
    }

    @Test
    public void itineraryTab_switchesProperly() {
        ActivityScenario.launch(ItineraryDetailsActivity.class);
        onView(withContentDescription("🗓 Itinerary")).perform(click());
    }
}
