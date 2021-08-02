package com.dtsoftware.paraglidinggps.ui.flights;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dtsoftware.paraglidinggps.Flight;
import com.dtsoftware.paraglidinggps.R;
import com.dtsoftware.paraglidinggps.Utils;

import java.util.List;
import java.util.Locale;

public class FlightListAdapter extends RecyclerView.Adapter<FlightListAdapter.FlightViewHolder> {

    private final LayoutInflater mInflater;
    private List<Flight> flights; // Cached copy
    //private Context context; // Para poder usar los recursos
    private static ClickListener itemClickListener;
    private final String distanceUnit;


    FlightListAdapter(Context context, String distanceUnit, ClickListener clickListener) {
        mInflater = LayoutInflater.from(context);
        //this.context = context;
        FlightListAdapter.itemClickListener = clickListener;
        this.distanceUnit = distanceUnit;
    }

    public static class FlightViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvFlightTitle, tvDistance, tvDuration, tvDate;

        private FlightViewHolder(View itemView) {
            super(itemView);
            tvFlightTitle = itemView.findViewById(R.id.tvFlightTitle);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvDate = itemView.findViewById(R.id.tvDate);
        }

        public void setClickListener(Flight flight, ClickListener clickListener) {
            itemView.setOnClickListener(view -> clickListener.onItemClicked(flight));
        }

    }

    public interface ClickListener {
        void onItemClicked(Flight flight);
    }

    @NonNull
    @Override
    public FlightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.flight_list_item_beta, parent, false);
        return new FlightViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FlightViewHolder holder, int position) {
        if (flights != null) {
            Flight current = flights.get(position);

            holder.tvFlightTitle.setText(current.getLocationName());
            holder.tvDistance.setText(getDistanceString(current.getDistance()));
            holder.tvDuration.setText(current.getDurationString());
            holder.tvDate.setText(current.getDateString());

            holder.setClickListener(current, itemClickListener);

        }  // Covers the case of data not being ready yet.
        // holder.wordItemView.setText("No Word");

    }

    @SuppressLint("NotifyDataSetChanged")
    void setFlights(List<Flight> flightList) {
        flights = flightList;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (flights != null)
            return flights.size();
        else return 0;
    }

    private String getDistanceString(float distance) {
        String distanceString = "";
        switch (distanceUnit) {
            case "km":
                distanceString = String.format(Locale.US, Utils.DISTANCE_FORMAT, Utils.metersToKm(distance)) + " km";
                break;
            case "mi":
                distanceString = String.format(Locale.US, Utils.DISTANCE_FORMAT, Utils.metersToMi(distance)) + " mi";
                break;
            case "nm":
                distanceString = String.format(Locale.US, Utils.DISTANCE_FORMAT, Utils.metersToNm(distance)) + " nm";
                break;
        }
        return distanceString;
    }

    @SuppressWarnings("unused")
    public Flight getItem(int index) {
        return flights.get(index);
    }


}