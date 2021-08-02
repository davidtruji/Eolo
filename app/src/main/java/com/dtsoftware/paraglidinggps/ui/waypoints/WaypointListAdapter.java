package com.dtsoftware.paraglidinggps.ui.waypoints;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dtsoftware.paraglidinggps.R;
import com.dtsoftware.paraglidinggps.Utils;
import com.dtsoftware.paraglidinggps.Waypoint;

import java.util.List;
import java.util.Locale;

public class WaypointListAdapter extends RecyclerView.Adapter<WaypointListAdapter.WaypointViewHolder> {

    private final LayoutInflater mInflater;
    private List<Waypoint> waypoints; // Cached copy
    private Context context; // Para poder usar los recursos
    private static ClickListener itemClickListener;


    WaypointListAdapter(Context context, ClickListener clickListener) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        WaypointListAdapter.itemClickListener = clickListener;
    }

    public static class WaypointViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvName, tvLat, tvLong;

        private WaypointViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvWaypointName);
            tvLat = itemView.findViewById(R.id.tvLatitude);
            tvLong = itemView.findViewById(R.id.tvLongitude);
        }

        public void setClickListener(Waypoint waypoint, ClickListener clickListener) {
            itemView.setOnClickListener(view -> clickListener.onItemClicked(waypoint));
        }

    }

    public interface ClickListener {
        void onItemClicked(Waypoint waypoint);
    }

    @NonNull
    @Override
    public WaypointViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.waypoint_list_item, parent, false);
        return new WaypointViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WaypointViewHolder holder, int position) {
        if (waypoints != null) {
            Waypoint current = waypoints.get(position);

            holder.tvName.setText(current.getWaypointName());
            holder.tvLat.setText("Lat. " + String.format(Locale.US, Utils.COORDINATES_FORMAT,current.getLatitude()));
            holder.tvLong.setText("Long. " + String.format(Locale.US, Utils.COORDINATES_FORMAT,current.getLongitude()));

            holder.setClickListener(current, itemClickListener);

        }  // Covers the case of data not being ready yet.
        // holder.wordItemView.setText("No Word");

    }

    void setWaypoints(List<Waypoint> waypointList) {
        waypoints = waypointList;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (waypoints != null)
            return waypoints.size();
        else return 0;
    }


}