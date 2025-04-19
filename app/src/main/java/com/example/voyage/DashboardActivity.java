package com.example.voyage;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voyage.response.TripPlan;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FloatingActionButton fabCreateItinerary;
    RecyclerView recyclerView;
    List<TripPlan> itineraryList;
    DashboardAdapter adapter;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = findViewById(R.id.dashboardToolbar);
        setSupportActionBar(toolbar);


        // Navigation Drawer setup
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);


        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(this);

        // FAB
        fabCreateItinerary = findViewById(R.id.fabCreateItinerary);
        fabCreateItinerary.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, NewTripActivity.class));
        });

        // RecyclerView
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_safety_alerts) {
            startActivity(new Intent(this, SafetyAlertsActivity.class));
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
