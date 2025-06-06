package com.example.voyage;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.voyage.fragments.ExpensesFragment;
import com.example.voyage.fragments.FlightsFragment;
import com.example.voyage.fragments.HotelsFragment;
import com.example.voyage.fragments.ItineraryFragment;
import com.example.voyage.fragments.RecommendationsFragment;
import com.example.voyage.fragments.TravelUpdatesFragment;
import com.example.voyage.response.TripPlan;

import java.util.ArrayList;

public class TabAdapter extends FragmentStateAdapter {

    private TripPlan tripPlan;

    public TabAdapter(@NonNull FragmentActivity fragmentActivity, TripPlan tripPlan) {
        super(fragmentActivity);
        this.tripPlan = tripPlan;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return ItineraryFragment.newInstance(tripPlan);
            case 1:
                return FlightsFragment.newInstance(tripPlan);
            case 2:
                return HotelsFragment.newInstance(tripPlan);
            case 3:
                return RecommendationsFragment.newInstance(
                        tripPlan.destination,
                        new ArrayList<>(tripPlan.interests));
            case 4:
                return ExpensesFragment.newInstance(tripPlan);
            case 5:
                return TravelUpdatesFragment.newInstance(tripPlan); //
            default:
                return new Fragment();
        }
    }

    @Override
    public int getItemCount() {
        return 6; //
    }
}
