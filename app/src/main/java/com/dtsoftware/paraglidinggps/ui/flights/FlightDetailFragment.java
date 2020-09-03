package com.dtsoftware.paraglidinggps.ui.flights;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dtsoftware.paraglidinggps.Flight;
import com.dtsoftware.paraglidinggps.R;
import com.dtsoftware.paraglidinggps.Utils;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.FillExtrusionLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillExtrusionColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillExtrusionHeight;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillExtrusionOpacity;


public class FlightDetailFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private MapboxMap mapboxMap;
    private Flight flight;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        Mapbox.getInstance(getContext(), getString(R.string.mapbox_access_token));

        View root = inflater.inflate(R.layout.fragment_flight_detail, container, false);

        mapView = root.findViewById(R.id.mv_fd_map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        TextView tvName, tvDate, tvDistance, tvDuration, tvMaxAltitude, tvMinAltitude;

        tvDate = root.findViewById(R.id.tv_fd_date);
        tvName = root.findViewById(R.id.tv_fd_name);
        tvDistance = root.findViewById(R.id.tv_fd_distance);
        tvDuration = root.findViewById(R.id.tv_fd_duration);
        tvMaxAltitude = root.findViewById(R.id.tv_fd_max_altitude);
        tvMinAltitude = root.findViewById(R.id.tv_fd_min_altitude);

        SharedFlightViewModel sharedFlightViewModel = new ViewModelProvider(getActivity()).get(SharedFlightViewModel.class);
        flight = sharedFlightViewModel.getSelectedFlight().getValue();

        tvName.setText(flight.getLocationName());
        tvDate.setText(flight.getDateString());
        tvDistance.setText("Distance: " + flight.getDistanceString() + " km");
        tvDuration.setText("Duration: " + flight.getDurationString() + " (hh:mm:ss)");
        tvMaxAltitude.setText("Max. Altitude: " + flight.getMaxAltitudeString() + " m");
        tvMinAltitude.setText("Min. Altitude: " + flight.getMinAltitudeString() + " m");

        return root;
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        mapboxMap.setStyle(Style.SATELLITE_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {

                GeoJsonSource geoJsonSource = Utils.getGeoJsonSourceFromRoute(flight.getRoute());

                style.addSource(geoJsonSource);

                // Add FillExtrusion layer to map using GeoJSON data
                style.addLayer(new FillExtrusionLayer("course", Utils.GEO_JSON_ID).withProperties(
                        fillExtrusionColor(Color.YELLOW),
                        fillExtrusionOpacity(0.7f),
                        fillExtrusionHeight(get("e"))));


                double lat = flight.getRoute().get(0).getLatitude();
                double lng = flight.getRoute().get(0).getLongitude();


                CameraPosition position = new CameraPosition.Builder()
                        .target(new LatLng(lat,lng))
                        .zoom(16)
                        .tilt(40)
                        .bearing(0)
                        .build();

                mapboxMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(position), 3000);
            }
        });
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