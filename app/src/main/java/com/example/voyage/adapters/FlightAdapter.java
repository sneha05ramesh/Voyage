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

public class FlightAdapter extends RecyclerView.Adapter<FlightAdapter.ViewHolder> {

    private List<Flight> flights;
    private boolean isBookedMode = false;
    private final FlightClickListener listener;

    public interface FlightClickListener {
        void onFlightClick(Flight flight);
    }

    public FlightAdapter(List<Flight> flights, FlightClickListener listener) {
        this.flights = flights;
        this.listener = listener;
    }

    public void setBookedMode(boolean bookedMode) {
        this.isBookedMode = bookedMode;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_flight, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Flight flight = flights.get(position);
        holder.airline.setText(flight.airline);
        //holder.time.setText(flight.departureTime + " → " + flight.arrivalTime);
        holder.duration.setText("Duration: " + flight.duration);
        holder.price.setText("Price: $" + flight.price);

        // Toggle button label and function
        holder.buttonBook.setText(isBookedMode ? "Cancel" : "Book");

        holder.buttonBook.setOnClickListener(v -> listener.onFlightClick(flight));
    }

    @Override
    public int getItemCount() {
        return flights.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView airline, time, duration, price;
        Button buttonBook;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            airline = itemView.findViewById(R.id.textAirline);
            time = itemView.findViewById(R.id.textTime);
            duration = itemView.findViewById(R.id.textDuration);
            price = itemView.findViewById(R.id.textPrice);
            buttonBook = itemView.findViewById(R.id.buttonBook);
        }
    }
}
