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
import com.example.voyage.models.Hotel;
import com.example.voyage.response.TripPlan;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HotelsFragment extends Fragment {

    private static final String ARG_TRIP = "trip_plan";
    private TripPlan tripPlan;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

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
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + Uri.encode(hotel.getName() + " " + hotel.getAddress())));
                        startActivity(intent);
                    });
        });
        recyclerView.setAdapter(adapter);

        viewBookedBtn.setOnClickListener(v -> {
            BookedHotelsBottomSheet sheet = BookedHotelsBottomSheet.newInstance(tripPlan);
            sheet.show(getParentFragmentManager(), "BookedHotelsBottomSheet");
        });

        // ✅ Replaces dummy data with Google Places API
        if (tripPlan != null && tripPlan.destination != null) {
            String destination = tripPlan.destination;
            String query = "hotels in " + destination;

            String apiKey = "AIzaSyBrd7nhHlb6Xoy743A3-nq8AkN2R62TzoE"; // Replace this with your actual key
            String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + Uri.encode(query) + "&key=" + apiKey;

            progressBar.setVisibility(View.VISIBLE);

            new Thread(() -> {
                try {
                    URL apiUrl = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();

                    InputStream is = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    JSONObject jsonResponse = new JSONObject(result.toString());
                    JSONArray results = jsonResponse.getJSONArray("results");

                    List<Hotel> hotels = new ArrayList<>();
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject item = results.getJSONObject(i);
                        String name = item.getString("name");
                        String address = item.getString("formatted_address");
                        double rating = item.has("rating") ? item.getDouble("rating") : 0.0;
                        String placeId = item.getString("place_id");

                        hotels.add(new Hotel(name, address, rating, placeId));
                    }

                    requireActivity().runOnUiThread(() -> {
                        adapter.setHotels(hotels);
                        progressBar.setVisibility(View.GONE);
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Failed to load hotels", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    });
                }
            }).start();
        }
    }
}
