package com.dtsoftware.paraglidinggps.ui.flights;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dtsoftware.paraglidinggps.Flight;
import com.dtsoftware.paraglidinggps.R;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;


public class FlightDetailFragment extends Fragment {

    private MapView mapView;
    private MapboxMap mapboxMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_flight_detail, container, false);

        mapView = root.findViewById(R.id.mv_fd_map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {


                    }
                });
            }
        });


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


    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


}