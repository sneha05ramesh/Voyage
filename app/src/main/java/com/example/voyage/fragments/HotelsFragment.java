package com.example.voyage.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voyage.R;
import com.example.voyage.adapters.HotelAdapter;
import com.example.voyage.fragments.BookedHotelsBottomSheet;
import com.example.voyage.models.Hotel;
import com.example.voyage.response.TripPlan;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HotelsFragment extends Fragment {

    private static final String ARG_TRIP = "trip_plan";
    private TripPlan tripPlan;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private List<Hotel> hotelList = new ArrayList<>();

    public static HotelsFragment newInstance(TripPlan tripPlan) {
        HotelsFragment fragment = new HotelsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TRIP, tripPlan);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hotels, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            tripPlan = (TripPlan) getArguments().getSerializable(ARG_TRIP);
        }

        recyclerView = view.findViewById(R.id.hotels_recycler_view);
        progressBar = view.findViewById(R.id.hotels_loading_indicator);
        Button viewBookedBtn = view.findViewById(R.id.btn_view_booked_hotels);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        HotelAdapter adapter = new HotelAdapter(getContext(), hotel -> {
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(FirebaseAuth.getInstance().getUid())
                    .collection("itineraries")
                    .document(tripPlan.destination)
                    .collection("hotels")
                    .add(hotel)
                    .addOnSuccessListener(docRef -> {
                        Toast.makeText(getContext(), "Hotel booked!", Toast.LENGTH_SHORT).show();
                        // Open Google search for the hotel
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + Uri.encode(hotel.getName())));
                        startActivity(intent);
                    });
        });
        recyclerView.setAdapter(adapter);

        viewBookedBtn.setOnClickListener(v -> {
            BookedHotelsBottomSheet sheet = BookedHotelsBottomSheet.newInstance(tripPlan);
            sheet.show(getParentFragmentManager(), "BookedHotelsBottomSheet");
        });

        progressBar.setVisibility(View.GONE);

        // Dummy hotel list (you can replace this with API later)
        hotelList.add(new Hotel("Grand Palace Hotel", "123 Main St, New York", 4.5, "place123"));
        hotelList.add(new Hotel("Seaside Resort", "456 Ocean View, Miami", 4.2, "place456"));
        hotelList.add(new Hotel("Mountain Lodge", "789 Hilltop, Denver", 4.7, "place789"));

        adapter.setHotels(hotelList);
    }
}
