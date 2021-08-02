package com.dtsoftware.paraglidinggps.ui.route;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.dtsoftware.paraglidinggps.R;
import com.dtsoftware.paraglidinggps.Utils;
import com.dtsoftware.paraglidinggps.Waypoint;
import com.dtsoftware.paraglidinggps.ui.nav.NavViewModel;

import java.util.List;
import java.util.Locale;

public class RouteWaypointsAdapter extends RecyclerView.Adapter<RouteWaypointsAdapter.RouteWaypointsViewHolder> {

    private final LayoutInflater mInflater;
    private List<Waypoint> waypoints; // Cached copy
    //private final Context context; // Para poder usar los recursos
    private static ClickListener itemClickListener;
    private final NavViewModel navViewModel;
    private int selectedItem;
    private final String distanceUnit;


    RouteWaypointsAdapter(Context context, String distanceUnit, ClickListener clickListener) {
        mInflater = LayoutInflater.from(context);
        //this.context = context;
        navViewModel = new ViewModelProvider((FragmentActivity) context).get(NavViewModel.class);
        RouteWaypointsAdapter.itemClickListener = clickListener;
        selectedItem = -1;
        this.distanceUnit = distanceUnit;
    }

    public static class RouteWaypointsViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvName, tvLat, tvLong, tvDistance;

        private RouteWaypointsViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvWaypointName);
            tvLat = itemView.findViewById(R.id.tvLatitude);
            tvLong = itemView.findViewById(R.id.tvLongitude);
            tvDistance = itemView.findViewById(R.id.tvWaypointDistance);
        }

        public void setClickListener(Waypoint waypoint, int position, ClickListener clickListener) {
            itemView.setOnClickListener(view -> clickListener.onItemClicked(waypoint, position));
        }

    }

    public interface ClickListener {
        void onItemClicked(Waypoint waypoint, int position);
    }

    @NonNull
    @Override
    public RouteWaypointsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.route_waypoint_list_item, parent, false);
        return new RouteWaypointsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteWaypointsViewHolder holder, int position) {
        if (waypoints != null) {
            Waypoint current = waypoints.get(position);


            holder.tvName.setText(current.getWaypointName());
            holder.tvLat.setText("Lat. " + String.format(Locale.US, Utils.COORDINATES_FORMAT, current.getLatitude()));
            holder.tvLong.setText("Long. " + String.format(Locale.US, Utils.COORDINATES_FORMAT, current.getLongitude()));
            holder.setClickListener(current, position, itemClickListener);

            String distance = getDistanceStringToWaypoint(current);


            holder.tvDistance.setText(distance);
            holder.tvDistance.setVisibility(View.VISIBLE);


            holder.itemView.setSelected(selectedItem == position);


        } // Covers the case of data not being ready yet.
        // holder.wordItemView.setText("No Word");

    }

    @SuppressLint("NotifyDataSetChanged")
    public void setWaypoints(List<Waypoint> waypoints) {
        this.waypoints = waypoints;
        notifyDataSetChanged();
    }

    private String getDistanceStringToWaypoint(Waypoint waypoint) {
        Location currentLocation = navViewModel.getLastLocation().getValue();
        Location waypointLocation = new Location("waypointLocation");

        waypointLocation.setLongitude(waypoint.getLongitude());
        waypointLocation.setLatitude(waypoint.getLatitude());

        float distance = currentLocation.distanceTo(waypointLocation);
        String distanceString = "-";

        switch (distanceUnit) {
            case "km":
                distanceString = "(" + (int) Utils.metersToKm(distance) + " km)";
                break;
            case "mi":
                distanceString = "(" + (int) Utils.metersToMi(distance) + " mi)";
                break;
            case "nm":
                distanceString = "(" + (int) Utils.metersToNm(distance) + " nm)";

                break;
        }


        return distanceString;

    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSelectedItem(int index) {
        selectedItem = index;
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

    @SuppressWarnings("unused")
    public Waypoint getItem(int index) {
        return waypoints.get(index);
    }


}