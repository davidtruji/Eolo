package com.dtsoftware.paraglidinggps.ui.route;

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
import com.dtsoftware.paraglidinggps.Waypoint;
import com.dtsoftware.paraglidinggps.ui.nav.NavViewModel;

import java.util.List;

public class RouteWaypointsAdapter extends RecyclerView.Adapter<RouteWaypointsAdapter.RouteWaypointsViewHolder> {

    private final LayoutInflater mInflater;
    private List<Waypoint> waypoints; // Cached copy
    private Context context; // Para poder usar los recursos
    private static ClickListener itemClickListener;
    private NavViewModel navViewModel;
    private int selectedItem;

    RouteWaypointsAdapter(Context context, ClickListener clickListener) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        navViewModel = new ViewModelProvider((FragmentActivity) context).get(NavViewModel.class);
        RouteWaypointsAdapter.itemClickListener = clickListener;
        selectedItem = -1;
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
            holder.tvLat.setText("Lat. " + String.format(context.getString(R.string.coordinates_format), current.getLatitude()));
            holder.tvLong.setText("Long. " + String.format(context.getString(R.string.coordinates_format), current.getLongitude()));
            holder.setClickListener(current, position, itemClickListener);

            int distance = getDistanceToWaypoint(current);

            if (distance > 0) {
                holder.tvDistance.setText("(" + String.valueOf(distance) + " Km)");
                holder.tvDistance.setVisibility(View.VISIBLE);
            }

            if (selectedItem == position)
                holder.itemView.setSelected(true);
            else
                holder.itemView.setSelected(false);


        } // Covers the case of data not being ready yet.
        // holder.wordItemView.setText("No Word");

    }

    public void setWaypoints(List<Waypoint> waypoints) {
        this.waypoints = waypoints;
        notifyDataSetChanged();
    }

    private int getDistanceToWaypoint(Waypoint waypoint) {
        Location currentLocation = navViewModel.getLastLocation().getValue();
        Location waypointLocation = new Location("waypointLocation");

        waypointLocation.setLongitude(waypoint.getLongitude());
        waypointLocation.setLatitude(waypoint.getLatitude());

        if (currentLocation != null)
            return (int) (currentLocation.distanceTo(waypointLocation) / 1000);
        else
            return 0;

    }

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