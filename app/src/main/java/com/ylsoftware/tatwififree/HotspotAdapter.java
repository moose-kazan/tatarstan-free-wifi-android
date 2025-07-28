package com.ylsoftware.tatwififree;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Comparator;
import java.util.List;


public class HotspotAdapter extends RecyclerView.Adapter<HotspotAdapter.ViewHolder>{
    public final List<Hotspot> hotspots;
    private OnItemClickListener listener = null;

    public HotspotAdapter(List<Hotspot> hotspots) {
        this.hotspots = hotspots;
    }

    public HotspotAdapter(List<Hotspot> hotspots, OnItemClickListener listener) {
        this.hotspots = hotspots;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Hotspot item);
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
        if (this.listener != null) {
            holder.itemContainer.setOnClickListener(view -> listener.onItemClick(hotspot));
        }
    }

    private String formatDistance(Context context, float meters) {
        if (meters < 0) {
            return context.getString(R.string.distance_unknown);
        } else if (meters < 1000) {
            return  context.getString(R.string.distance_1000, Math.round(meters));
        } else if (meters < 3000) {
            return  context.getString(R.string.distance_3000, (meters / 1000));
        }
        return context.getString(R.string.distance_max, Math.round(meters/1000));
    }

    @Override
    public int getItemCount() {
        return this.hotspots.size();
    }

    public void setCurrentLocation(Location location) {
        Location tmpLocation = new Location(location);
        for (int i = 0; i < this.hotspots.size(); i++) {
            Hotspot tmpHotspot = this.hotspots.get(i);
            tmpLocation.setLatitude(tmpHotspot.lat);
            tmpLocation.setLongitude(tmpHotspot.lon);
            tmpHotspot.distance = location.distanceTo(tmpLocation);
            this.hotspots.set(i, tmpHotspot);
            this.notifyItemChanged(i);
        }
        this.hotspots.sort(new HotspotComparator());
        this.notifyDataSetChanged();
    }

    public class HotspotComparator implements Comparator<Hotspot> {

        @Override
        public int compare(Hotspot t1, Hotspot t2) {
            if (t1.distance == t2.distance) {
                return 0;
            } else if (t1.distance > t2.distance) {
                return 1;
            }
            return -1;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView addrView, distanceView;
        final LinearLayout itemContainer;
        ViewHolder(View view) {
            super(view);
            addrView = view.findViewById(R.id.hotspot_addr);
            distanceView = view.findViewById(R.id.hotspot_distance);
            itemContainer = view.findViewById(R.id.hotspot_item_container);
        }
    }
}
