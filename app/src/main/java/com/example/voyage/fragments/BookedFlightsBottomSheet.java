package com.example.voyage.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voyage.R;
import com.example.voyage.adapters.FlightAdapter;
import com.example.voyage.models.Flight;
import com.example.voyage.response.TripPlan;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class BookedFlightsBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_TRIP = "trip_plan";
    private TripPlan tripPlan;

    public static BookedFlightsBottomSheet newInstance(TripPlan tripPlan) {
        BookedFlightsBottomSheet sheet = new BookedFlightsBottomSheet();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TRIP, tripPlan);
        sheet.setArguments(args);
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_bottom_sheet_booked_flights, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView recycler = view.findViewById(R.id.recyclerBookedFlights);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() != null) {
            tripPlan = (TripPlan) getArguments().getSerializable(ARG_TRIP);
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("itineraries")
                .document(tripPlan.getId())
                .collection("flights")
                .get()
                .addOnSuccessListener(query -> {
                    List<Flight> flights = new ArrayList<>();
                    List<String> docIds = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : query) {
                        Flight f = doc.toObject(Flight.class);
                        flights.add(f);
                        docIds.add(doc.getId());
                    }

                    FlightAdapter adapter = new FlightAdapter(flights, flight -> {
                        int index = flights.indexOf(flight);
                        if (index != -1 && index < docIds.size()) {
                            String docId = docIds.get(index);

                            FirebaseFirestore.getInstance()
                                    .collection("users")
                                    .document(uid)
                                    .collection("itineraries")
                                    .document(tripPlan.getId())
                                    .collection("flights")
                                    .document(docId)
                                    .delete()
                                    .addOnSuccessListener(aVoid -> dismiss());
                        }
                    });

                    adapter.setBookedMode(true);
                    recycler.setAdapter(adapter);
                });
    }
}
