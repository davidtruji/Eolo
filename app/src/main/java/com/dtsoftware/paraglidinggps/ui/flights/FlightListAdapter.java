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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FlightListAdapter extends RecyclerView.Adapter<FlightListAdapter.FlightViewHolder> {

    private final LayoutInflater mInflater;
    private List<Flight> flights; // Cached copy
    private Context context; // Para poder usar los recursos
    private static ClickListener itemClickListener;


    FlightListAdapter(Context context, ClickListener clickListener) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        FlightListAdapter.itemClickListener = clickListener;
    }

    public static class FlightViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvflightName, tvDistance, tvTime, tvDate;

        private FlightViewHolder(View itemView) {
            super(itemView);
            tvflightName = itemView.findViewById(R.id.tvFlightName);
            tvDistance = itemView.findViewById(R.id.tvFlightDistance);
            tvTime = itemView.findViewById(R.id.tvFlightTime);
            tvDate = itemView.findViewById(R.id.tvFlightDate);
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
        View itemView = mInflater.inflate(R.layout.flight_list_item, parent, false);
        return new FlightViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FlightViewHolder holder, int position) {
        if (flights != null) {
            Flight current = flights.get(position);
            holder.tvflightName.setText(current.getLocationName());
            holder.tvDistance.setText(current.getDistanceString());
            holder.tvTime.setText(current.getDurationString());
            holder.tvDate.setText(current.getDateString());

            holder.setClickListener(current,itemClickListener);

        }  // Covers the case of data not being ready yet.
        // holder.wordItemView.setText("No Word");

    }

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

    @SuppressWarnings("unused")
    public Flight getItem(int index) {
        return flights.get(index);
    }


}