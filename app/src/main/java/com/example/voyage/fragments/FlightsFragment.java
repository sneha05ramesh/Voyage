package com.example.voyage.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voyage.CountryCodeMapper;
import com.example.voyage.R;
import com.example.voyage.adapters.FlightAdapter;
import com.example.voyage.models.Flight;
import com.example.voyage.network.ApiClientKiwi;
import com.example.voyage.network.KiwiApi;
import com.example.voyage.response.KiwiFlightResponse;
import com.example.voyage.response.TripPlan;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FlightsFragment extends Fragment {

    private static final String ARG_TRIP = "trip_plan";

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TripPlan tripPlan;

    public static FlightsFragment newInstance(TripPlan tripPlan) {
        FlightsFragment fragment = new FlightsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TRIP, tripPlan);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_flights, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyclerFlights);
        progressBar = view.findViewById(R.id.loadingSpinner);

        View bookedButton = view.findViewById(R.id.buttonViewBooked);
        bookedButton.setOnClickListener(v -> {
            BookedFlightsBottomSheet sheet = BookedFlightsBottomSheet.newInstance(tripPlan);
            sheet.show(getParentFragmentManager(), "BookedFlightsSheet");
        });


        if (getArguments() != null) {
            tripPlan = (TripPlan) getArguments().getSerializable(ARG_TRIP);
            fetchFlights();
        }
    }

    private void fetchFlights() {
        progressBar.setVisibility(View.VISIBLE);

        String fromCode = CountryCodeMapper.getAirportCode(tripPlan.fromCity.toLowerCase());
        String toCode = CountryCodeMapper.getAirportCode(tripPlan.destination.toLowerCase());


        if (fromCode == null || toCode == null) {
            showErrorDialog("Country not supported. Try a popular international destination.");
            return;
        }

        String from = "Country:" + fromCode;
        String to = "Country:" + toCode;


        KiwiApi api = ApiClientKiwi.getClient().create(KiwiApi.class);
        Call<KiwiFlightResponse> call = api.getFlights(
                from,                      // source
                to,                        // destination
                "usd",                     // currency
                "en",                      // locale
                1,                         // adults
                "ECONOMY",                 // cabinClass
                "MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY,SUNDAY", // outboundDays
                "FLIGHT",                  // transportTypes
                10                         // limit
        );

        call.enqueue(new Callback<KiwiFlightResponse>() {
            @Override
            public void onResponse(Call<KiwiFlightResponse> call, Response<KiwiFlightResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    Log.d("API Response", "Response received successfully");

                    if (response.body().data != null && !response.body().data.isEmpty()) {
                        List<Flight> flightList = new ArrayList<>();

                        for (KiwiFlightResponse.ItineraryOneWay itinerary : response.body().data) {
                            try {
                                // Get the main segment info
                                KiwiFlightResponse.SectorSegment mainSegment = itinerary.sector.segments.get(0);

                                // Extract airline name
                                String airline = mainSegment.segment.carrier.name;

                                // Extract times
                                String depTime = mainSegment.segment.source.localTime;
                                String arrTime = mainSegment.segment.destination.localTime;

                                // Format duration (comes in seconds)
                                int durationSecs = mainSegment.segment.duration;
                                String duration = formatDuration(durationSecs);

                                // Get price
                                int price = (int) Double.parseDouble(itinerary.price.amount);
                                String flightId = itinerary.id;
                                String bookingUrl = itinerary.bookingOptions.edges.get(0).node.bookingUrl;

                                // Get destination city name
                                String destination = mainSegment.segment.destination.station.city.name;

                                Log.d("Flight Data",
                                        "Airline: " + airline +
                                                ", DepTime: " + depTime +
                                                ", ArrTime: " + arrTime +
                                                ", Duration: " + duration +
                                                ", Price: $" + price);

                                flightList.add(new Flight(
                                        airline,
                                        formatApiDateTime(depTime),
                                        formatApiDateTime(arrTime),
                                        duration,
                                        destination,
                                        price,
                                        flightId,
                                        bookingUrl
                                ));
                            } catch (Exception e) {
                                Log.e("Flight Parsing", "Error parsing flight: " + e.getMessage());
                            }
                        }

                        if (!flightList.isEmpty()) {
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.setAdapter(new FlightAdapter(flightList, FlightsFragment.this::bookFlight));
                        } else {
                            showErrorDialog("Could not parse flight data correctly.");
                        }
                    } else {
                        Log.d("API Response", "No flights found in data.");
                        showErrorDialog("No flights found for this route.");
                    }
                } else {
                    Log.d("API Response", "Response unsuccessful or body is null.");
                    showErrorDialog("No flights found for this route.");
                }
            }

            @Override
            public void onFailure(Call<KiwiFlightResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                showErrorDialog("Error fetching flights: " + t.getMessage());
            }
        });
    }

    // Helper method to format duration from seconds to readable format
    private String formatDuration(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        return hours + "h " + minutes + "m";
    }

    // Helper method to format API date time (2025-05-11T06:35:00) to readable format
    private String formatApiDateTime(String apiDateTime) {
        try {
            // Parse the API date time format
            String[] parts = apiDateTime.split("T");
            String date = parts[0];
            String time = parts[1].substring(0, 5); // Get HH:MM

            return date + " " + time;
        } catch (Exception e) {
            return apiDateTime; // Return original if parsing fails
        }
    }

    private void showErrorDialog(String msg) {
        new AlertDialog.Builder(getContext())
                .setTitle("Oops!")
                .setMessage(msg)
                .setPositiveButton("OK", null)
                .show();
    }

    private void bookFlight(Flight flight) {
        // Open booking URL if available
        try {
            if (flight.bookingUrl != null && !flight.bookingUrl.isEmpty()) {
                String fullUrl = "https://www.kiwi.com" + flight.bookingUrl;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fullUrl));
                startActivity(browserIntent);
            }
        } catch (Exception e) {
            Log.e("Booking URL", "Invalid booking URL: " + e.getMessage());
        }

        // Save full flight data to Firestore
        String uid = FirebaseAuth.getInstance().getUid();
        String itineraryId = tripPlan.getId();

        Map<String, Object> flightData = new HashMap<>();
        flightData.put("airline", flight.airline);
        flightData.put("duration", flight.duration);
        flightData.put("destination", flight.destination);
        flightData.put("price", flight.price);
        flightData.put("flightId", flight.flightId);
        flightData.put("bookingUrl", flight.bookingUrl);

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("itineraries")
                .document(itineraryId)
                .collection("flights")
                .add(flightData)
                .addOnSuccessListener(docRef -> Log.d("Firestore", "Flight booked and saved"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error saving flight: " + e.getMessage()));
    }


}