package com.example.voyage.adapters;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voyage.DashboardAdapter;
import com.example.voyage.NewTripActivity;
import com.example.voyage.R;
import com.example.voyage.response.TripPlan;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    FloatingActionButton fabCreateItinerary;
    RecyclerView recyclerView;
    List<TripPlan> itineraryList;
    DashboardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // FAB setup
        fabCreateItinerary = findViewById(R.id.fabCreateItinerary);
        fabCreateItinerary.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, NewTripActivity.class));
        });

        // RecyclerView setup
        recyclerView = findViewById(R.id.recyclerViewItineraries);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        itineraryList = new ArrayList<>();
        adapter = new DashboardAdapter(itineraryList, this);
        recyclerView.setAdapter(adapter);

        loadItinerariesFromFirestore();
    }

    private void loadItinerariesFromFirestore() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("itineraries")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    itineraryList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        TripPlan plan = doc.toObject(TripPlan.class);
                        itineraryList.add(plan);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load trips.", Toast.LENGTH_SHORT).show();
                });
    }
}
