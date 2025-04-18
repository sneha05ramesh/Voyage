package com.example.voyage.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HotelsFragment extends Fragment {

    private static final String TAG = "HotelsFragment";
    private static final String GOOGLE_PLACES_API_KEY = "AIzaSyBrd7nhHlb6Xoy743A3-nq8AkN2R62TzoE"; // Replace with your key
    private static final String ARG_DEST = "destination";

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private String destination;

    public static HotelsFragment newInstance(String destination) {
        Log.d(TAG, "Creating new instance with destination: " + destination);
        HotelsFragment fragment = new HotelsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DEST, destination);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        return inflater.inflate(R.layout.fragment_hotels, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated called");
        recyclerView = view.findViewById(R.id.hotels_recycler_view);
        progressBar = view.findViewById(R.id.hotels_loading_indicator);

        if (recyclerView == null) {
            Log.e(TAG, "RecyclerView not found in layout!");
            return;
        }
        if (progressBar == null) {
            Log.e(TAG, "ProgressBar not found in layout!");
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new HotelAdapter(requireContext(), hotel -> {
            Log.d(TAG, "Hotel clicked: " + hotel.getName());
            // TODO: Implement Book Now logic
        }));

        destination = getArguments() != null ? getArguments().getString(ARG_DEST, "London") : "London";
        Log.d(TAG, "Searching hotels for destination: " + destination);

        new FetchHotelsTask().execute(destination);
    }

    private class FetchHotelsTask extends AsyncTask<String, Void, List<Hotel>> {
        private String errorMessage = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "Starting to fetch hotels");
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Hotel> doInBackground(String... params) {
            String location = params[0];
            List<Hotel> hotelList = new ArrayList<>();

            try {
                Log.d(TAG, "Preparing API request for location: " + location);

                // URL encode the location to handle spaces and special characters
                String encodedLocation = URLEncoder.encode(location, StandardCharsets.UTF_8.toString());
                String apiUrl = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=hotels+in+"
                        + encodedLocation + "&type=lodging&key=" + GOOGLE_PLACES_API_KEY;

                Log.d(TAG, "API URL: " + apiUrl);

                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000); // 10 seconds
                conn.setReadTimeout(15000);    // 15 seconds

                Log.d(TAG, "Connection response code: " + conn.getResponseCode());

                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    errorMessage = "API returned error code: " + conn.getResponseCode();
                    Log.e(TAG, errorMessage);
                    return hotelList;
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                Log.d(TAG, "API Response: " + response.toString());

                JSONObject json = new JSONObject(response.toString());

                // Check if API returned an error
                if (json.has("error_message")) {
                    errorMessage = "API Error: " + json.getString("error_message");
                    Log.e(TAG, errorMessage);
                    return hotelList;
                }

                String status = json.getString("status");
                Log.d(TAG, "API Status: " + status);

                if (!status.equals("OK") && !status.equals("ZERO_RESULTS")) {
                    errorMessage = "API returned status: " + status;
                    Log.e(TAG, errorMessage);
                    return hotelList;
                }

                JSONArray results = json.getJSONArray("results");
                Log.d(TAG, "Found " + results.length() + " hotels");

                for (int i = 0; i < results.length(); i++) {
                    JSONObject obj = results.getJSONObject(i);
                    String name = obj.optString("name");
                    String address = obj.optString("formatted_address");
                    double rating = obj.optDouble("rating", 0.0);
                    String placeId = obj.optString("place_id");

                    Hotel hotel = new Hotel(name, address, rating, placeId);
                    hotelList.add(hotel);

                    Log.d(TAG, "Added hotel: " + name + ", Rating: " + rating);
                }

            } catch (IOException e) {
                errorMessage = "Network error: " + e.getMessage();
                Log.e(TAG, errorMessage, e);
            } catch (JSONException e) {
                errorMessage = "JSON parsing error: " + e.getMessage();
                Log.e(TAG, errorMessage, e);
            } catch (Exception e) {
                errorMessage = "Unexpected error: " + e.getMessage();
                Log.e(TAG, errorMessage, e);
            }

            Log.d(TAG, "Returning " + hotelList.size() + " hotels");
            return hotelList;
        }

        @Override
        protected void onPostExecute(List<Hotel> hotels) {
            super.onPostExecute(hotels);
            Log.d(TAG, "onPostExecute with " + hotels.size() + " hotels");

            if (getActivity() == null || !isAdded()) {
                Log.e(TAG, "Fragment not attached to activity");
                return;
            }

            progressBar.setVisibility(View.GONE);

            if (errorMessage != null) {
                Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_LONG).show();
            }

            HotelAdapter adapter = (HotelAdapter) recyclerView.getAdapter();
            if (adapter != null) {
                adapter.setHotels(hotels);
                Log.d(TAG, "Hotel adapter updated with " + hotels.size() + " hotels");
            } else {
                Log.e(TAG, "RecyclerView adapter is null!");
            }

            if (hotels.isEmpty()) {
                Toast.makeText(getContext(), "No hotels found for " + destination, Toast.LENGTH_SHORT).show();
            }
        }
    }
}