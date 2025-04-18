// 📄 ReviewItineraryActivity.java
package com.example.voyage;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.voyage.adapters.DashboardActivity;
import com.example.voyage.response.TripActivity;
import com.example.voyage.response.TripDay;
import com.example.voyage.response.TripPlan;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ReviewItineraryActivity extends AppCompatActivity {

    TextView reviewText;
    Button saveButton, cancelButton;
    TripPlan tripPlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_itinerary);

        reviewText = findViewById(R.id.reviewText);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);

        tripPlan = (TripPlan) getIntent().getSerializableExtra("trip_plan");

        if (tripPlan != null) {
            StringBuilder sb = new StringBuilder();
            for (TripDay tripDay : tripPlan.itinerary) {
                sb.append("\uD83D\uDCC5 Day ").append(tripDay.day).append("\n\n");
                for (TripActivity act : tripDay.activities) {
                    sb.append("\uD83D\uDD52 ").append(act.time).append(" - ")
                            .append(act.activity).append("\n")
                            .append("\uD83D\uDCCD ").append(act.location).append("\n\n");
                }
            }
            reviewText.setText(sb.toString());
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
