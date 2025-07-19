package com.ylsoftware.tatwififree;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class HotspotAdapter extends RecyclerView.Adapter<HotspotAdapter.ViewHolder>{
    private final List<Hotspot> hotspots;

    public HotspotAdapter(List<Hotspot> hotspots) {
        this.hotspots = hotspots;
    }

    @NonNull
    @Override
    public HotspotAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_home_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HotspotAdapter.ViewHolder holder, int position) {
        Hotspot hotspot = this.hotspots.get(position);
        holder.addrView.setText(hotspot.address);
        holder.distanceView.setText(formatDistance(holder.itemView.getContext(), hotspot.distance));
    }

    private String formatDistance(Context context, int meters) {
        if (meters < 0) {
            return context.getString(R.string.distance_unknown);
        } else if (meters < 1000) {
            return  context.getString(R.string.distance_1000, meters);
        } else if (meters < 3000) {
            return  context.getString(R.string.distance_3000, ((float)meters / 1000));
        }
        return context.getString(R.string.distance_max, Math.round(meters/1000));
    }

    @Override
    public int getItemCount() {
        return this.hotspots.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView addrView, distanceView;
        ViewHolder(View view) {
            super(view);
            addrView = view.findViewById(R.id.hotspot_addr);
            distanceView = view.findViewById(R.id.hotspot_distance);
        }
    }
}
