package com.example.voyage;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voyage.adapters.SafetyAlertAdapter;
import com.example.voyage.models.SafetyAlert;

import java.util.ArrayList;
import java.util.List;

public class SafetyAlertsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SafetyAlertAdapter adapter;
    List<SafetyAlert> alertList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_alerts);

        recyclerView = findViewById(R.id.recyclerViewSafety);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        alertList = getMockAlerts();
        adapter = new SafetyAlertAdapter(alertList);
        recyclerView.setAdapter(adapter);
    }

    private List<SafetyAlert> getMockAlerts() {
        List<SafetyAlert> alerts = new ArrayList<>();
        alerts.add(new SafetyAlert("Health Advisory", "COVID-19 cases are rising in London. Wear masks in crowded areas.", "2025-04-17", "WHO"));
        alerts.add(new SafetyAlert("Political Unrest", "Protests ongoing in central Paris. Avoid large gatherings.", "2025-04-16", "TravelGov"));
        alerts.add(new SafetyAlert("Weather Warning", "Heavy rainfall expected in Tokyo. Carry rain gear and check train schedules.", "2025-04-15", "Japan Meteorological Agency"));
        return alerts;
    }
}
