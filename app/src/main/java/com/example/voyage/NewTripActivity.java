package com.example.voyage;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewTripActivity extends AppCompatActivity {

    EditText destinationInput, fromCityInput, startDateInput, endDateInput;
    TextView interestsPreview;
    RadioGroup budgetGroup;
    Button generateButton;
    ProgressBar loadingSpinner;

    String selectedStartDate = "";
    String selectedEndDate = "";

    final String[] interestOptions = {
            "Art", "Theater", "Museums", "History", "Architecture", "Cultural events",
            "Hiking", "Wildlife", "Beaches", "National parks", "Adventure sports",
            "Cuisine", "Street food", "Wine tasting", "Breweries", "Fine dining",
            "Spa", "Yoga retreats", "Relaxation", "Resorts",
            "Shopping", "Luxury brands", "Local markets",
            "Theme parks", "Zoos", "Kid-friendly activities",
            "Bars", "Clubs", "Live music", "Theater shows",
            "Sports events", "Fitness", "Cycling",
            "Tech", "Innovation", "Conventions",
            "Photography", "Scenic views"
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
        loadingSpinner = findViewById(R.id.loadingSpinner); // make sure this exists in XML

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

            int numDays = 3;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date start = sdf.parse(selectedStartDate);
                Date end = sdf.parse(selectedEndDate);
                long diffInMillis = end.getTime() - start.getTime();
                numDays = (int) TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS) + 1;
            } catch (Exception e) {
                e.printStackTrace(); // Keep default 3 if error
            }

            // Convert interests to lowercase for backend
            List<String> interestsLower = new ArrayList<>();
            for (String interest : selectedList) {
                interestsLower.add(interest.toLowerCase());
            }

            String budget = "medium"; // default
            int budgetId = budgetGroup.getCheckedRadioButtonId();
            if (budgetId == R.id.budgetLow) budget = "low";
            else if (budgetId == R.id.budgetHigh) budget = "high";

            TripRequest request = new TripRequest(
                    numDays,
                    destination,
                    interestsLower,
                    budget,
                    "public transport"
            );

            loadingSpinner.setVisibility(View.VISIBLE);

            TripService service = ApiClient.getClient().create(TripService.class);
            service.getItinerary(request).enqueue(new Callback<ItineraryResponse>() {
                @Override
                public void onResponse(Call<ItineraryResponse> call, Response<ItineraryResponse> response) {
                    loadingSpinner.setVisibility(View.GONE);

                    if (response.isSuccessful() && response.body() != null) {
                        TripPlan tripPlan = response.body().plan;
                        tripPlan.fromCity = fromCity;

                        Intent intent = new Intent(NewTripActivity.this, ReviewItineraryActivity.class);
                        intent.putExtra("trip_plan", tripPlan);
                        startActivity(intent);

                    } else {
                        Toast.makeText(NewTripActivity.this, "API Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ItineraryResponse> call, Throwable t) {
                    loadingSpinner.setVisibility(View.GONE);
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

        // Disable past dates
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }
}
