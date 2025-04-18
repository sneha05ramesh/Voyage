package com.example.voyage;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.voyage.network.ApiClient;
import com.example.voyage.network.TripRequest;
import com.example.voyage.network.TripService;
import com.example.voyage.response.ItineraryResponse;
import com.example.voyage.response.TripActivity;
import com.example.voyage.response.TripDay;
import com.example.voyage.response.TripPlan;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewTripActivity extends AppCompatActivity {

    EditText destinationInput, fromCityInput, startDateInput, endDateInput;
    TextView interestsPreview;
    RadioGroup budgetGroup;
    Button generateButton;

    String selectedStartDate = "";
    String selectedEndDate = "";

    final String[] interestOptions = {
            "art", "theater", "museums", "history", "architecture", "cultural events",
            "hiking", "wildlife", "beaches", "national parks", "adventure sports",
            "cuisine", "street food", "wine tasting", "breweries", "fine dining",
            "spa", "yoga retreats", "relaxation", "resorts",
            "shopping", "luxury brands", "local markets",
            "theme parks", "zoos", "kid-friendly activities",
            "bars", "clubs", "live music", "theater shows",
            "sports events", "fitness", "cycling",
            "tech", "innovation", "conventions",
            "photography", "scenic views"
    };
    boolean[] selectedInterests = new boolean[interestOptions.length];
    List<String> selectedList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip);

        destinationInput = findViewById(R.id.destinationInput);
        fromCityInput = findViewById(R.id.fromCityInput);
        startDateInput = findViewById(R.id.startDateInput);
        endDateInput = findViewById(R.id.endDateInput);
        interestsPreview = findViewById(R.id.interestsPreview);
        budgetGroup = findViewById(R.id.budgetGroup);
        generateButton = findViewById(R.id.generateButton);

        // Date pickers
        startDateInput.setOnClickListener(v -> showDatePicker(true));
        endDateInput.setOnClickListener(v -> showDatePicker(false));

        // Interests dialog
        interestsPreview.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Interests");
            builder.setMultiChoiceItems(interestOptions, selectedInterests, (dialog, which, isChecked) -> {
                if (isChecked) {
                    selectedList.add(interestOptions[which]);
                } else {
                    selectedList.remove(interestOptions[which]);
                }
            });

            builder.setPositiveButton("Done", (dialog, which) -> {
                if (selectedList.isEmpty()) {
                    interestsPreview.setText("Tap to select interests");
                } else {
                    interestsPreview.setText(String.join(", ", selectedList));
                }
            });

            builder.setNegativeButton("Cancel", null);
            builder.show();
        });

        generateButton.setOnClickListener(v -> {
            String destination = destinationInput.getText().toString().trim();

            if (destination.isEmpty() || selectedStartDate.isEmpty() || selectedEndDate.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String fromCity = fromCityInput.getText().toString().trim();
            if (fromCity.isEmpty()) {
                Toast.makeText(this, "Please enter your departure city", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedList.isEmpty()) {
                Toast.makeText(this, "Select at least one interest", Toast.LENGTH_SHORT).show();
                return;
            }

            int numDays = 3; // You can calculate from start-end date if needed

            String budget = "medium"; // default
            int budgetId = budgetGroup.getCheckedRadioButtonId();
            if (budgetId == R.id.budgetLow) budget = "low";
            else if (budgetId == R.id.budgetHigh) budget = "high";

            TripRequest request = new TripRequest(
                    numDays,
                    destination,
                    selectedList,
                    budget,
                    "public transport"
            );

            TripService service = ApiClient.getClient().create(TripService.class);
            service.getItinerary(request).enqueue(new Callback<ItineraryResponse>() {
                @Override
                public void onResponse(Call<ItineraryResponse> call, Response<ItineraryResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        TripPlan tripPlan = response.body().plan;
                        tripPlan.fromCity = fromCity;
                        StringBuilder sb = new StringBuilder();

                        for (TripDay tripDay : tripPlan.itinerary) {
                            sb.append("Day ").append(tripDay.day).append(":\n");
                            for (TripActivity act : tripDay.activities) {
                                sb.append("• ").append(act.time)
                                        .append(" - ").append(act.activity)
                                        .append(" @ ").append(act.location)
                                        .append("\n");
                            }
                            sb.append("\n");
                        }


                        Intent intent = new Intent(NewTripActivity.this, ReviewItineraryActivity.class);
                        intent.putExtra("trip_plan", tripPlan);// Pass the plan object
                        startActivity(intent);

                    } else {
                        Toast.makeText(NewTripActivity.this, "API Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ItineraryResponse> call, Throwable t) {
                    Toast.makeText(NewTripActivity.this, "Call failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void showDatePicker(boolean isStartDate) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String formattedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                    if (isStartDate) {
                        selectedStartDate = formattedDate;
                        startDateInput.setText(formattedDate);
                    } else {
                        selectedEndDate = formattedDate;
                        endDateInput.setText(formattedDate);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }
}
