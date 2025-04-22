package com.example.voyage.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voyage.R;
import com.example.voyage.models.Hotel;
import com.example.voyage.response.TripPlan;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.HotelViewHolder> {

    private List<Hotel> hotelList;
    private final Context context;
    private final HotelClickListener clickListener;
    private final TripPlan tripPlan;
    private boolean bookedMode = false;

    public interface HotelClickListener {
        void onHotelDetailsClick(Hotel hotel);
    }

    public HotelAdapter(Context context, HotelClickListener clickListener, TripPlan tripPlan) {
        this.context = context;
        this.clickListener = clickListener;
        this.tripPlan = tripPlan;
        this.hotelList = new ArrayList<>();
    }

    public void setHotelList(List<Hotel> hotelList) {
        this.hotelList = hotelList;
        notifyDataSetChanged();
    }

    public void setBookedMode(boolean mode) {
        this.bookedMode = mode;
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
        holder.name.setText(hotel.getName());
        holder.address.setText(hotel.getAddress());
        holder.ratingBar.setRating((float) hotel.getRating());

        int estimated;
        int priceLevel = hotel.getPriceLevel();
        if (priceLevel == -1) {
            double rating = hotel.getRating();
            if (rating >= 4.5) {
                estimated = 250;
            } else if (rating >= 3.5) {
                estimated = 150;
            } else {
                estimated = 80;
            }
            holder.price.setText("Est. $" + estimated);
        } else {
            estimated = mapPriceLevelToMaxDollar(priceLevel);
            holder.price.setText("Est. up to $" + estimated);
        }

        if (bookedMode) {
            holder.bookButton.setText("Cancel");
        } else {
            holder.bookButton.setText("Book Now");
        }

        int finalEstimated = estimated;

        holder.bookButton.setOnClickListener(v -> {
            if (bookedMode) {
                // Cancel hotel
                clickListener.onHotelDetailsClick(hotel); // will trigger deletion in BookedHotelsBottomSheet
            } else {
                // Book hotel
                clickListener.onHotelDetailsClick(hotel); // optional callback
                String uid = FirebaseAuth.getInstance().getUid();
                String itineraryId = tripPlan.getId();

                Map<String, Object> hotelData = new HashMap<>();
                hotelData.put("name", hotel.getName());
                hotelData.put("address", hotel.getAddress());
                hotelData.put("rating", hotel.getRating());
                hotelData.put("placeId", hotel.getPlaceId());
                hotelData.put("priceLevel", hotel.getPriceLevel());
                hotelData.put("price", finalEstimated);

                FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(uid)
                        .collection("itineraries")
                        .document(itineraryId)
                        .collection("hotels")
                        .add(hotelData)
                        .addOnSuccessListener(docRef ->
                                Toast.makeText(context, "Hotel booked!", Toast.LENGTH_SHORT).show()
                        );
            }
        });


        holder.itemView.setOnClickListener(v -> {
            if (!bookedMode && hotel.getPlaceId() != null) {
                String url = "https://www.google.com/maps/search/?api=1&query=Google&query_place_id=" + hotel.getPlaceId();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return hotelList.size();
    }

    public static class HotelViewHolder extends RecyclerView.ViewHolder {
        TextView name, address, price;
        RatingBar ratingBar;
        Button bookButton;

        public HotelViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.hotel_name);
            address = itemView.findViewById(R.id.hotel_location);
            ratingBar = itemView.findViewById(R.id.hotel_rating);
            price = itemView.findViewById(R.id.hotel_price);
            bookButton = itemView.findViewById(R.id.book_hotel_button);
        }
    }

    private int mapPriceLevelToMaxDollar(int priceLevel) {
        switch (priceLevel) {
            case 1: return 50;
            case 2: return 150;
            case 3: return 300;
            case 4: return 500;
            default: return 100;
        }
    }
}
