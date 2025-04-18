package com.example.voyage;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voyage.response.TripPlan;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.ItineraryViewHolder> {

    private List<TripPlan> itineraryList;
    private Context context;

    public DashboardAdapter(List<TripPlan> itineraryList, Context context) {
        this.itineraryList = itineraryList;
        this.context = context;
    }

    @NonNull
    @Override
    public ItineraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_itinerary, parent, false);
        return new ItineraryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItineraryViewHolder holder, int position) {
        TripPlan trip = itineraryList.get(position);
        holder.destinationText.setText(trip.destination);

        // Optional: format the timestamp to readable dates (if you have start and end timestamps)
        String dateRange = "Days: " + trip.days;
        holder.dateText.setText(dateRange);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ItineraryDetailsActivity.class);
            intent.putExtra("trip_plan", trip);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return itineraryList.size();
    }

    public static class ItineraryViewHolder extends RecyclerView.ViewHolder {
        TextView destinationText, dateText;

        public ItineraryViewHolder(@NonNull View itemView) {
            super(itemView);
            destinationText = itemView.findViewById(R.id.textDestination);
            dateText = itemView.findViewById(R.id.textDates);
        }
    }
}
