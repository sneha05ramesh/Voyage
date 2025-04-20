package com.example.voyage.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voyage.R;
import com.example.voyage.models.Hotel;

import java.util.ArrayList;
import java.util.List;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.HotelViewHolder> {
    private static final String TAG = "HotelAdapter";
    private List<Hotel> hotelList;
    private final Context context;
    private final HotelClickListener clickListener;
    private boolean bookedMode = false;

    public interface HotelClickListener {
        void onHotelDetailsClick(Hotel hotel);
    }

    public HotelAdapter(Context context, HotelClickListener clickListener) {
        this.context = context;
        this.hotelList = new ArrayList<>();
        this.clickListener = clickListener;
    }

    public void setBookedMode(boolean bookedMode) {
        this.bookedMode = bookedMode;
    }

    public void setHotels(List<Hotel> hotels) {
        this.hotelList = hotels;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HotelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hotel, parent, false);
        return new HotelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HotelViewHolder holder, int position) {
        Hotel hotel = hotelList.get(position);
        holder.hotelName.setText(hotel.getName());
        holder.hotelAddress.setText(hotel.getAddress());
        holder.hotelRating.setRating((float) hotel.getRating());

        holder.bookButton.setText(bookedMode ? "Cancel" : "Book Now");
        holder.bookButton.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onHotelDetailsClick(hotel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return hotelList.size();
    }

    public static class HotelViewHolder extends RecyclerView.ViewHolder {
        TextView hotelName, hotelAddress;
        RatingBar hotelRating;
        Button bookButton;

        public HotelViewHolder(@NonNull View itemView) {
            super(itemView);
            hotelName = itemView.findViewById(R.id.hotel_name);
            hotelAddress = itemView.findViewById(R.id.hotel_location);
            hotelRating = itemView.findViewById(R.id.hotel_rating);
            bookButton = itemView.findViewById(R.id.book_hotel_button);
        }
    }
}
