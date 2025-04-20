package com.example.voyage;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.voyage.response.TripPlan;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ItineraryDetailsActivity extends AppCompatActivity {

    private TripPlan tripPlan;
    private TextView textSummary;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private TabAdapter tabAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itinerary_details);

        tripPlan = (TripPlan) getIntent().getSerializableExtra("trip_plan");

        textSummary = findViewById(R.id.textSummary);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        // Set summary
        if (tripPlan != null) {
            String summary = "Destination: " + tripPlan.destination + "\n" +
                    "Days: " + tripPlan.days + "\n" +
                    "Budget: " + tripPlan.budget + "\n" +
                    "Interests: " + String.join(", ", tripPlan.interests);
            textSummary.setText(summary);
        }

        // Setup ViewPager adapter
        tabAdapter = new TabAdapter(this, tripPlan);
        viewPager.setAdapter(tabAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Itinerary"); break;
                case 1: tab.setText("Flights"); break;
                case 2: tab.setText("Hotels"); break;
                case 3: tab.setText("Recs"); break;
                case 4: tab.setText("Expenses"); break;
                case 5: tab.setText("Updates"); break;
            }
        }).attach();
    }
}
