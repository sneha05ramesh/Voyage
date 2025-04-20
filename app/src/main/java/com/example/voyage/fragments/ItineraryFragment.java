package com.example.voyage.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.voyage.R;
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
        return inflater.inflate(R.layout.fragment_itinerary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            tripPlan = (TripPlan) getArguments().getSerializable(ARG_TRIP);
        }

        LinearLayout itineraryContainer = view.findViewById(R.id.itineraryContainer);
        LayoutInflater inflater = LayoutInflater.from(getContext());

        if (tripPlan != null && tripPlan.itinerary != null) {
            for (TripDay tripDay : tripPlan.itinerary) {
                View dayView = inflater.inflate(R.layout.item_day_block, itineraryContainer, false);

                TextView dayTitle = dayView.findViewById(R.id.dayTitle);
                TextView dayContent = dayView.findViewById(R.id.dayContent);
                ImageView dayImage = dayView.findViewById(R.id.dayImage);

                dayTitle.setText("Day " + tripDay.day);
                dayImage.setImageResource(R.drawable.sample_day_image); // or logic-based image

                StringBuilder content = new StringBuilder();
                for (TripActivity act : tripDay.activities) {
                    content.append("⏰ ").append(act.time).append(" - ").append(act.activity).append("\n")
                            .append("📍 ").append(act.location).append("\n\n");
                }

                dayContent.setText(content.toString().trim());
                itineraryContainer.addView(dayView);
            }
        }
    }
}
