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
import com.example.voyage.adapters.HotelAdapter;
import com.example.voyage.models.Hotel;
import com.example.voyage.response.TripPlan;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class BookedHotelsBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_TRIP = "trip_plan";
    private TripPlan tripPlan;

    public static BookedHotelsBottomSheet newInstance(TripPlan tripPlan) {
        BookedHotelsBottomSheet sheet = new BookedHotelsBottomSheet();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TRIP, tripPlan);
        sheet.setArguments(args);
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_bottom_sheet_booked_hotels, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            tripPlan = (TripPlan) getArguments().getSerializable(ARG_TRIP);
        }

        RecyclerView recycler = view.findViewById(R.id.recyclerBookedHotels);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Hotel> hotels = new ArrayList<>();
        List<String> docIds = new ArrayList<>();

        String uid = FirebaseAuth.getInstance().getUid();

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("itineraries")
                .document(tripPlan.getId()) // ✅ use correct itinerary ID
                .collection("hotels")
                .get()
                .addOnSuccessListener(query -> {
                    for (QueryDocumentSnapshot doc : query) {
                        Hotel hotel = doc.toObject(Hotel.class);
                        hotels.add(hotel);
                        docIds.add(doc.getId());
                    }

                    HotelAdapter adapter = new HotelAdapter(getContext(), hotel -> {
                        int index = hotels.indexOf(hotel);
                        if (index >= 0 && index < docIds.size()) {
                            String docId = docIds.get(index);
                            FirebaseFirestore.getInstance()
                                    .collection("users")
                                    .document(uid)
                                    .collection("itineraries")
                                    .document(tripPlan.getId())
                                    .collection("hotels")
                                    .document(docId)
                                    .delete()
                                    .addOnSuccessListener(aVoid -> dismiss());
                        }
                    }, tripPlan); // ✅ pass tripPlan to maintain structure

                    adapter.setBookedMode(true);
                    adapter.setHotelList(hotels); // ✅ correct method name
                    recycler.setAdapter(adapter);
                });
    }
}
