package com.dtsoftware.paraglidinggps.ui.flights;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dtsoftware.paraglidinggps.Flight;
import com.dtsoftware.paraglidinggps.R;


public class FlightDetailFragment extends Fragment {


    public FlightDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_flight_detail, container, false);

        TextView tvName, tvDate, tvDistance, tvDuration, tvMaxAltitude, tvMinAltitude;

        tvDate = root.findViewById(R.id.tv_fd_date);
        tvName = root.findViewById(R.id.tv_fd_name);
        tvDistance = root.findViewById(R.id.tv_fd_distance);
        tvDuration = root.findViewById(R.id.tv_fd_duration);
        tvMaxAltitude = root.findViewById(R.id.tv_fd_max_altitude);
        tvMinAltitude = root.findViewById(R.id.tv_fd_min_altitude);

        SharedFlightViewModel sharedFlightViewModel = new ViewModelProvider(getActivity()).get(SharedFlightViewModel.class);
        Flight flight = sharedFlightViewModel.getSelectedFlight().getValue();

        tvName.setText(flight.getLocationName());
        tvDate.setText(flight.getDateString());
        tvDistance.setText("Distance: " + flight.getDistanceString() + " km");
        tvDuration.setText("Duration: " + flight.getDurationString() + " (hh:mm:ss)");
        tvMaxAltitude.setText("Max. Altitude: " + flight.getMaxAltitudeString() + " m");
        tvMinAltitude.setText("Min. Altitude: " + flight.getMinAltitudeString() + " m");

        return root;
    }


}