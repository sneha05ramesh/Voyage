package com.example.voyage.fragments;

import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
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
    private static final String GOOGLE_PLACES_API_KEY = "AIzaSyBrd7nhHlb6Xoy743A3-nq8AkN2R62TzoE";
    private static final String STATIC_MAPS_API_KEY = "AIzaSyBrd7nhHlb6Xoy743A3-nq8AkN2R62TzoE";

    private String destination = "Paris";
    private List<String> userInterests = List.of("food", "culture");

    private RecyclerView attractionsRecycler, restaurantsRecycler, activitiesRecycler;
    private ImageView mapAttractions, mapRestaurants, mapActivities;

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
        attractionsRecycler = view.findViewById(R.id.recycler_attractions);
        restaurantsRecycler = view.findViewById(R.id.recycler_restaurants);
        activitiesRecycler = view.findViewById(R.id.recycler_activities);

        mapAttractions = view.findViewById(R.id.map_attractions);
        mapRestaurants = view.findViewById(R.id.map_restaurants);
        mapActivities = view.findViewById(R.id.map_activities);

        fetchPlaces("tourist_attraction", "Local Attractions", userInterests, attractionsRecycler, mapAttractions);
        fetchPlaces("restaurant", "Restaurants", userInterests, restaurantsRecycler, mapRestaurants);
        fetchPlaces("park", "Activities", userInterests, activitiesRecycler, mapActivities);
    }

    private void fetchPlaces(String type, String categoryName, List<String> keywords, RecyclerView recyclerView, ImageView mapImageView) {
        try {
            String keywordQuery = TextUtils.join(" ", keywords);
            String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?" +
                    "query=" + URLEncoder.encode(type + " in " + destination + " " + keywordQuery, "UTF-8") +
                    "&key=" + GOOGLE_PLACES_API_KEY;

            Log.d(TAG, "🌍 " + categoryName + " → Querying for " + destination + " [" + keywordQuery + "]");

            Request request = new Request.Builder().url(url).build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e(TAG, categoryName + " fetch failed: " + e.getMessage());
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), categoryName + " fetch failed", Toast.LENGTH_SHORT).show()
                    );
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.e(TAG, "❌ API error for " + categoryName + ": " + response.message());
                        return;
                    }

                    String body = response.body().string();
                    try {
                        JSONObject json = new JSONObject(body);
                        JSONArray results = json.optJSONArray("results");
                        if (results == null || results.length() == 0) {
                            Log.w(TAG, "⚠️ No results found for " + categoryName);
                            return;
                        }

                        List<Place> placeList = new ArrayList<>();
                        List<LatLng> coordinates = new ArrayList<>();

                        for (int i = 0; i < Math.min(results.length(), 5); i++) {
                            JSONObject obj = results.getJSONObject(i);
                            String name = obj.optString("name");
                            String address = obj.optString("formatted_address");

                            if (!obj.has("geometry")) continue;

                            double lat = obj.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                            double lng = obj.getJSONObject("geometry").getJSONObject("location").getDouble("lng");

                            coordinates.add(new LatLng(lat, lng));
                            placeList.add(new Place(name, address, lat, lng));
                        }

                        requireActivity().runOnUiThread(() -> {
                            Log.d(TAG, "✅ " + categoryName + ": Showing " + placeList.size() + " places");
                            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                            recyclerView.setAdapter(new PlaceAdapter(placeList));

                            if (!coordinates.isEmpty()) {
                                String mapUrl = buildStaticMapUrl(coordinates);
                                Glide.with(requireContext()).load(mapUrl).into(mapImageView);
                            }
                        });

                    } catch (JSONException e) {
                        Log.e(TAG, "⚠️ JSON error in " + categoryName + ": " + e.getMessage());
                    }
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "🔥 Error building query URL for " + categoryName + ": " + e.getMessage());
        }
    }

    private String buildStaticMapUrl(List<LatLng> coords) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/staticmap?");
        url.append("size=600x300&zoom=13");

        for (int i = 0; i < coords.size(); i++) {
            LatLng coord = coords.get(i);
            url.append("&markers=color:red%7Clabel:").append((char) ('A' + i))
                    .append("%7C").append(coord.latitude).append(",").append(coord.longitude);
        }

        url.append("&key=").append(STATIC_MAPS_API_KEY);
        return url.toString();
    }
}
