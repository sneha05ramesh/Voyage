package com.example.voyage.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voyage.R;
import com.example.voyage.adapters.FlightAdapter;
import com.example.voyage.models.Flight;
import com.example.voyage.response.TripPlan;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FlightsFragment extends Fragment {

    private static final String ARG_DEST = "destination";
    private static final String ARG_TRIP = "trip_plan";

    private String destination;
    private TripPlan tripPlan;
    private RecyclerView recyclerView;
    private Button buttonViewBooked;

    public static FlightsFragment newInstance(TripPlan plan) {
        FlightsFragment fragment = new FlightsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TRIP, plan);
        args.putString(ARG_DEST, plan.destination);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_flights, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyclerFlights);
        buttonViewBooked = view.findViewById(R.id.buttonViewBooked);

        if (getArguments() != null) {
            destination = getArguments().getString(ARG_DEST);
            tripPlan = (TripPlan) getArguments().getSerializable(ARG_TRIP);
        }

        // Show mock flights
        List<Flight> dummyFlights = getDummyFlights(destination);
        FlightAdapter adapter = new FlightAdapter(dummyFlights, this::bookFlight);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Open bottom sheet for booked flights
        buttonViewBooked.setOnClickListener(v -> {
            BookedFlightsBottomSheet.newInstance(tripPlan)
                    .show(getParentFragmentManager(), "BookedFlightsBottomSheet");
        });
    }

    private List<Flight> getDummyFlights(String dest) {
        List<Flight> list = new ArrayList<>();
        list.add(new Flight("Delta", "9:00 AM", "12:00 PM", "3h", dest));
        list.add(new Flight("United", "1:30 PM", "4:20 PM", "2h 50m", dest));
        list.add(new Flight("American Airlines", "6:45 PM", "9:35 PM", "2h 50m", dest));
        return list;
    }

    private void bookFlight(Flight flight) {
        new AlertDialog.Builder(getContext())
                .setTitle("Confirm Booking")
                .setMessage("Add this flight to your trip?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(uid)
                            .collection("itineraries")
                            .document(tripPlan.destination) // Replace with itineraryId if needed
                            .collection("flights")
                            .add(flight)
                            .addOnSuccessListener(ref -> {
                                dialog.dismiss();
                            });
                })
                .setNegativeButton("No", null)
                .show();
    }
}
