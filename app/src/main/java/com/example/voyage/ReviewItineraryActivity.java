package com.example.voyage;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.voyage.response.TripActivity;
import com.example.voyage.response.TripDay;
import com.example.voyage.response.TripPlan;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ReviewItineraryActivity extends AppCompatActivity {

    Button saveButton, cancelButton;
    TripPlan tripPlan;
    LinearLayout dayContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_itinerary);

        dayContainer = findViewById(R.id.dayContainer);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);

        tripPlan = (TripPlan) getIntent().getSerializableExtra("trip_plan");

        if (tripPlan != null) {
            LayoutInflater inflater = LayoutInflater.from(this);

            for (TripDay tripDay : tripPlan.itinerary) {
                View dayView = inflater.inflate(R.layout.item_day_block, dayContainer, false);

                TextView dayTitle = dayView.findViewById(R.id.dayTitle);
                TextView dayContent = dayView.findViewById(R.id.dayContent);
                ImageView dayImage = dayView.findViewById(R.id.dayImage);

                dayTitle.setText("Day " + tripDay.day);
                dayImage.setImageResource(R.drawable.sample_day_image); // Use your own image here

                StringBuilder content = new StringBuilder();
                for (TripActivity act : tripDay.activities) {
                    content.append("🕗 ").append(act.time).append(" - ").append(act.activity).append("\n")
                            .append("📍 ").append(act.location).append("\n\n");
                }

                dayContent.setText(content.toString().trim());
                dayContainer.addView(dayView);
            }
        }

        cancelButton.setOnClickListener(v -> finish());

        saveButton.setOnClickListener(v -> {
            if (tripPlan != null) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                Map<String, Object> data = new HashMap<>();
                data.put("destination", tripPlan.destination);
                data.put("fromCity", tripPlan.fromCity);
                data.put("budget", tripPlan.budget);
                data.put("interests", tripPlan.interests);
                data.put("travelMode", tripPlan.travelMode);
                data.put("itinerary", tripPlan.itinerary);
                data.put("days", tripPlan.days);
                data.put("timestamp", System.currentTimeMillis());

                db.collection("users")
                        .document(uid)
                        .collection("itineraries")
                        .add(data)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(this, "Trip saved!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, DashboardActivity.class));
                            finish();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Error saving trip.", Toast.LENGTH_SHORT).show());
            }
        });
    }
}
