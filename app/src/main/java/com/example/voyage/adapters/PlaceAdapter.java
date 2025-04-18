package com.example.voyage.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voyage.R;
import com.example.voyage.models.Place;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {

    private final List<Place> placeList;

    public PlaceAdapter(List<Place> placeList) {
        this.placeList = placeList;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        Place place = placeList.get(position);
        holder.nameText.setText(place.getName());
        holder.addressText.setText(place.getAddress());
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    public static class PlaceViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, addressText;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.place_name);
            addressText = itemView.findViewById(R.id.place_address);
        }
    }
}
