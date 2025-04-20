package com.example.voyage;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;


import com.example.voyage.SignupActivity;
import com.example.voyage.DashboardActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;

@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Rule
    public ActivityTestRule<SignupActivity> signUpRule = new ActivityTestRule<>(SignupActivity.class);

    @Test
    public void signUpFields_visible() {
        onView(withId(R.id.fullNameInput)).check(matches(isDisplayed()));
        onView(withId(R.id.emailInput)).check(matches(isDisplayed()));
        onView(withId(R.id.passwordInput)).check(matches(isDisplayed()));
        onView(withId(R.id.confirmPasswordInput)).check(matches(isDisplayed()));
    }

    @Test
    public void clickLoginButton_opensLoginScreen() {
        ActivityScenario.launch(WelcomeActivity.class);
        onView(withId(R.id.loginButton)).perform(click());
    }

    @Test
    public void welcomeScreen_showsBasicElements() {
        ActivityScenario.launch(WelcomeActivity.class);
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()));
        onView(withId(R.id.signupButton)).check(matches(isDisplayed()));
    }



}
