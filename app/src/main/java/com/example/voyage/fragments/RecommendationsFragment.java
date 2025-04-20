package com.example.voyage.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.voyage.R;
import com.example.voyage.adapters.PlaceAdapter;
import com.example.voyage.models.Place;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RecommendationsFragment extends Fragment {

    private static final String TAG = "Recommendations";
    private static final String ARG_DESTINATION = "destination";
    private static final String ARG_INTERESTS = "interests";

    private static final String GOOGLE_PLACES_API_KEY = "AIzaSyBrd7nhHlb6Xoy743A3-nq8AkN2R62TzoE"; //
    private static final String STATIC_MAPS_API_KEY = "AIzaSyBrd7nhHlb6Xoy743A3-nq8AkN2R62TzoE";  //

    private String destination = "Paris";
    private List<String> userInterests = List.of("food", "culture");

    private RecyclerView attractionsRecycler, restaurantsRecycler, activitiesRecycler;
    private ImageView mapAttractions, mapRestaurants, mapActivities;

    private View sectionAttractions, sectionRestaurants, sectionActivities;

    private OkHttpClient client = new OkHttpClient();

    public static RecommendationsFragment newInstance(String destination, ArrayList<String> interests) {
        RecommendationsFragment fragment = new RecommendationsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DESTINATION, destination);
        args.putStringArrayList(ARG_INTERESTS, interests);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            destination = getArguments().getString(ARG_DESTINATION, "Paris");
            List<String> received = getArguments().getStringArrayList(ARG_INTERESTS);
            if (received != null && !received.isEmpty()) {
                userInterests = received;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recommendations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        sectionAttractions = view.findViewById(R.id.section_attractions);
        sectionRestaurants = view.findViewById(R.id.section_restaurants);
        sectionActivities = view.findViewById(R.id.section_activities);

        attractionsRecycler = view.findViewById(R.id.recycler_attractions);
        restaurantsRecycler = view.findViewById(R.id.recycler_restaurants);
        activitiesRecycler = view.findViewById(R.id.recycler_activities);

        mapAttractions = view.findViewById(R.id.map_attractions);
        mapRestaurants = view.findViewById(R.id.map_restaurants);
        mapActivities = view.findViewById(R.id.map_activities);

        fetchPlaces("tourist_attraction", attractionsRecycler, mapAttractions, sectionAttractions);
        fetchPlaces("restaurant", restaurantsRecycler, mapRestaurants, sectionRestaurants);
        fetchPlaces("activity", activitiesRecycler, mapActivities, sectionActivities);
    }

    private void fetchPlaces(String type, RecyclerView recyclerView, ImageView mapView, View sectionView) {
        try {
            String query = type + " in " + destination;
            String encodedQuery = URLEncoder.encode(query, "UTF-8");
            String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=" +
                    encodedQuery + "&key=" + GOOGLE_PLACES_API_KEY;

            Request request = new Request.Builder().url(url).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e(TAG, "Failed to fetch " + type + ": " + e.getMessage());
                    requireActivity().runOnUiThread(() -> sectionView.setVisibility(View.GONE));
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    List<Place> places = new ArrayList<>();
                    try {
                        String json = response.body().string();
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray results = jsonObject.getJSONArray("results");

                        for (int i = 0; i < results.length(); i++) {
                            JSONObject obj = results.getJSONObject(i);
                            String name = obj.getString("name");
                            String address = obj.getString("formatted_address");

                            JSONObject geometry = obj.getJSONObject("geometry").getJSONObject("location");
                            double lat = geometry.getDouble("lat");
                            double lng = geometry.getDouble("lng");

                            places.add(new Place(name, address, lat, lng));
                        }

                        requireActivity().runOnUiThread(() -> {
                            if (places.isEmpty()) {
                                sectionView.setVisibility(View.GONE);
                            } else {
                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                recyclerView.setAdapter(new PlaceAdapter(places));

                                // Static Map
                                try {
                                    String encodedDest = URLEncoder.encode(destination, "UTF-8");
                                    String mapUrl = "https://maps.googleapis.com/maps/api/staticmap?" +
                                            "center=" + encodedDest +
                                            "&zoom=13&size=600x300&maptype=roadmap&markers=color:red|" +
                                            encodedDest +
                                            "&key=" + STATIC_MAPS_API_KEY;

                                    Glide.with(requireContext()).load(mapUrl).into(mapView);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    mapView.setVisibility(View.GONE);
                                }
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        requireActivity().runOnUiThread(() -> sectionView.setVisibility(View.GONE));
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            sectionView.setVisibility(View.GONE);
        }
    }
}
