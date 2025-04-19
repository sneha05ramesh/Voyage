package com.example.voyage.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.voyage.R;
import com.example.voyage.models.SafetyAlert;

import java.util.List;

public class SafetyAlertAdapter extends RecyclerView.Adapter<SafetyAlertAdapter.ViewHolder> {

    private final List<SafetyAlert> alerts;

    public SafetyAlertAdapter(List<SafetyAlert> alerts) {
        this.alerts = alerts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_safety_alert, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SafetyAlert alert = alerts.get(position);
        holder.title.setText(alert.title);
        holder.description.setText(alert.description);
        holder.date.setText(alert.date);
        holder.source.setText("Source: " + alert.source);
    }

    @Override
    public int getItemCount() {
        return alerts.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, date, source;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.alertTitle);
            description = itemView.findViewById(R.id.alertDescription);
            date = itemView.findViewById(R.id.alertDate);
            source = itemView.findViewById(R.id.alertSource);
        }
    }
}
