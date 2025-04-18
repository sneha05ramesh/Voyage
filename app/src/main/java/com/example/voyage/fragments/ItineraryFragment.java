package com.example.voyage.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.voyage.response.TripActivity;
import com.example.voyage.response.TripDay;
import com.example.voyage.response.TripPlan;

public class ItineraryFragment extends Fragment {

    private static final String ARG_TRIP = "trip_plan";
    private TripPlan tripPlan;

    public static ItineraryFragment newInstance(TripPlan plan) {
        ItineraryFragment fragment = new ItineraryFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TRIP, plan);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(android.R.layout.simple_list_item_1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            tripPlan = (TripPlan) getArguments().getSerializable(ARG_TRIP);
        }

        TextView textView = view.findViewById(android.R.id.text1);
        StringBuilder sb = new StringBuilder();

        if (tripPlan != null && tripPlan.itinerary != null) {
            for (TripDay day : tripPlan.itinerary) {
                sb.append("🗓 Day ").append(day.day).append("\n");
                for (TripActivity act : day.activities) {
                    sb.append("• ").append(act.time)
                            .append(" - ").append(act.activity)
                            .append(" @ ").append(act.location)
                            .append("\n");
                }
                sb.append("\n");
            }
        }

        textView.setText(sb.toString());
    }
}
