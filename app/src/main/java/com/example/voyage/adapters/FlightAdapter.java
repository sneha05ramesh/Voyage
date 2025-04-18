package com.example.voyage.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voyage.R;
import com.example.voyage.models.Flight;

import java.util.List;

public class FlightAdapter extends RecyclerView.Adapter<FlightAdapter.FlightViewHolder> {

    private List<Flight> flightList;
    private OnFlightBookListener bookListener;

    public interface OnFlightBookListener {
        void onBook(Flight flight);
    }

    public FlightAdapter(List<Flight> flightList, OnFlightBookListener bookListener) {
        this.flightList = flightList;
        this.bookListener = bookListener;
    }

    @NonNull
    @Override
    public FlightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_flight, parent, false);
        return new FlightViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlightViewHolder holder, int position) {
        Flight flight = flightList.get(position);
        holder.airlineText.setText(flight.airline);
        holder.timeText.setText(flight.departTime + " → " + flight.arriveTime);
        holder.durationText.setText("Duration: " + flight.duration);
        holder.bookButton.setOnClickListener(v -> bookListener.onBook(flight));
    }

    @Override
    public int getItemCount() {
        return flightList.size();
    }

    static class FlightViewHolder extends RecyclerView.ViewHolder {
        TextView airlineText, timeText, durationText;
        Button bookButton;

        public FlightViewHolder(@NonNull View itemView) {
            super(itemView);
            airlineText = itemView.findViewById(R.id.textAirline);
            timeText = itemView.findViewById(R.id.textTime);
            durationText = itemView.findViewById(R.id.textDuration);
            bookButton = itemView.findViewById(R.id.buttonBook);
        }
    }
}
