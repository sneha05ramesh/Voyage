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

    public interface HotelClickListener {
        void onHotelDetailsClick(Hotel hotel);
    }

    public HotelAdapter(Context context, HotelClickListener clickListener) {
        this.context = context;
        this.hotelList = new ArrayList<>();
        this.clickListener = clickListener;
        Log.d(TAG, "Adapter created");
    }

    @NonNull
    @Override
    public HotelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "Creating new ViewHolder");
        View view = LayoutInflater.from(context).inflate(R.layout.item_hotel, parent, false);
        return new HotelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HotelViewHolder holder, int position) {
        Hotel hotel = hotelList.get(position);
        Log.d(TAG, "Binding hotel at position " + position + ": " + hotel.getName());

        holder.hotelName.setText(hotel.getName());
        holder.hotelAddress.setText(hotel.getAddress());

        // Check if rating bar is available before setting rating
        if (holder.hotelRating != null) {
            holder.hotelRating.setRating((float) hotel.getRating());
        } else {
            Log.w(TAG, "RatingBar is null for hotel: " + hotel.getName());
        }

        holder.bookButton.setOnClickListener(v -> {
            Log.d(TAG, "Book button clicked for hotel: " + hotel.getName());
            if (clickListener != null) {
                clickListener.onHotelDetailsClick(hotel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return hotelList.size();
    }

    public void setHotels(List<Hotel> hotels) {
        Log.d(TAG, "Setting " + hotels.size() + " hotels to adapter");
        this.hotelList = hotels;
        notifyDataSetChanged();
    }

    public static class HotelViewHolder extends RecyclerView.ViewHolder {
        TextView hotelName;
        TextView hotelAddress;
        RatingBar hotelRating;
        Button bookButton;

        public HotelViewHolder(@NonNull View itemView) {
            super(itemView);
            hotelName = itemView.findViewById(R.id.hotel_name);
            hotelAddress = itemView.findViewById(R.id.hotel_location);

            // Try to find the rating bar - might be null if not in layout
            try {
                hotelRating = itemView.findViewById(R.id.hotel_rating);
            } catch (Exception e) {
                Log.e("HotelAdapter", "Error finding hotel_rating view: " + e.getMessage());
            }

            bookButton = itemView.findViewById(R.id.book_hotel_button);

            // Log what was found/not found
            Log.d("HotelAdapter", "ViewHolder created: " +
                    "name=" + (hotelName != null) + ", " +
                    "address=" + (hotelAddress != null) + ", " +
                    "rating=" + (hotelRating != null) + ", " +
                    "button=" + (bookButton != null));
        }
    }
}