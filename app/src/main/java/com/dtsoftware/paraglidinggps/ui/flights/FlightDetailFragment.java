package com.dtsoftware.paraglidinggps.ui.flights;

import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.expressions.Expression.match;
import static com.mapbox.mapboxsdk.style.expressions.Expression.rgb;
import static com.mapbox.mapboxsdk.style.expressions.Expression.stop;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleRadius;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleStrokeColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleStrokeWidth;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textSize;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.dtsoftware.paraglidinggps.Flight;
import com.dtsoftware.paraglidinggps.FlightLocation;
import com.dtsoftware.paraglidinggps.R;
import com.dtsoftware.paraglidinggps.Utils;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class FlightDetailFragment extends Fragment {

    private MapboxMap mapboxMap;
    private MapView mapView;
    private Flight mflight;
    private SharedFlightViewModel sharedFlightViewModel;
    private FlightsViewModel flightsViewModel;
    private TextView tvName, tvDate, tvDistance, tvDuration, tvMaxAltitude, tvMinAltitude, tvMinSpeed, tvAvgSpeed, tvMaxSpeed, tvDistanceLabel, tvMinAltitudeLabel, tvMaxAltitudeLabel, tvMinSpeedLabel, tvAvgSpeedLabel, tvMaxSpeedLabel;
    private Toolbar toolbar;
    private String distanceUnit, speedUnit, altitudeUnit;


    private final OnMapReadyCallback onMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull MapboxMap mapboxMap) {
            FlightDetailFragment.this.mapboxMap = mapboxMap;

            mapboxMap.setStyle(Style.SATELLITE_STREETS, style -> {

                if (mflight.getRoute() != null) {
                    setRouteLine(style);
                    setRoutePoints(style);
                }

            });

            CameraPosition position = mapboxMap.getCameraForLatLngBounds(Utils.getBoundsOfRoute(mflight.getRoute()), new int[]{50, 50, 50, 50});

            mapboxMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(position), 3000);


        }
    };


    @SuppressLint("NonConstantResourceId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Mapbox.getInstance(getContext(), getString(R.string.mapbox_access_token));

        View root = inflater.inflate(R.layout.fragment_flight_detail, container, false);

        sharedFlightViewModel = new ViewModelProvider(getActivity()).get(SharedFlightViewModel.class);
        flightsViewModel = new ViewModelProvider(getActivity()).get(FlightsViewModel.class);


        setHasOptionsMenu(true);

        mapView = root.findViewById(R.id.mv_route_map);
        mapView.onCreate(savedInstanceState);

        tvDate = root.findViewById(R.id.tvDate);
        tvName = root.findViewById(R.id.tvTitle);
        tvDuration = root.findViewById(R.id.tvDuration);

        tvDistance = root.findViewById(R.id.tvDistance);
        tvDistanceLabel = root.findViewById(R.id.tvDistanceLabel);


        tvMinAltitude = root.findViewById(R.id.tvMinAltitude);
        tvMinAltitudeLabel = root.findViewById(R.id.tvMinAltitudeLabel);

        tvMaxAltitude = root.findViewById(R.id.tvMaxAltitude);
        tvMaxAltitudeLabel = root.findViewById(R.id.tvMaxAltitudeLabel);


        tvMinSpeed = root.findViewById(R.id.tvMinSpeed);
        tvMinSpeedLabel = root.findViewById(R.id.tvMinSpeedLabel);

        tvAvgSpeed = root.findViewById(R.id.tvAvgSpeed);
        tvAvgSpeedLabel = root.findViewById(R.id.tvAvgSpeedLabel);

        tvMaxSpeed = root.findViewById(R.id.tvMaxSpeed);
        tvMaxSpeedLabel = root.findViewById(R.id.tvMaxSpeedLabel);


        // ACTUALIZA LA UI
        sharedFlightViewModel.getSelectedFlight().observe(getViewLifecycleOwner(), this::bindFlight);

        toolbar = root.findViewById(R.id.flight_toolbar);
        toolbar.setTitle("Details");
        toolbar.inflateMenu(R.menu.fd_toolbar_menu);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(view -> getParentFragmentManager().popBackStack());
        toolbar.setOnMenuItemClickListener(item -> {

            switch (item.getItemId()) {
                case R.id.action_delete:
                    showDeleteDialog();
                    break;
                case R.id.action_edit_flight:
                    showEditFlightFragment();
                    break;
                default:
                    break;
            }

            return true;
        });


        setupSharedPreferences();


        return root;
    }

    private void bindFlight(Flight flight) {
        mflight = flight;

        mapView.getMapAsync(onMapReadyCallback);

        toolbar.setSubtitle(flight.getLocationName());
        tvName.setText(flight.getLocationName());
        tvDate.setText(flight.getDateString());
        tvDuration.setText(flight.getDurationString());


        switch (distanceUnit) {
            // Distance
            case "km":
                tvDistance.setText(String.format(Locale.US, Utils.DISTANCE_FORMAT, Utils.metersToKm(flight.getDistance())));
                break;
            case "mi":
                tvDistance.setText(String.format(Locale.US, Utils.DISTANCE_FORMAT, Utils.metersToMi(flight.getDistance())));
                break;
            case "nm":
                tvDistance.setText(String.format(Locale.US, Utils.DISTANCE_FORMAT, Utils.metersToNm(flight.getDistance())));
                break;
        }


        switch (altitudeUnit) {
            // Altitude
            case "m":
                tvMinAltitude.setText(String.valueOf(flight.getMinAltitude()));
                tvMaxAltitude.setText(String.valueOf(flight.getMaxAltitude()));
                break;
            case "ft":
                tvMinAltitude.setText(String.format(Locale.US, Utils.ALTITUDE_FORMAT, Utils.metersToFt(flight.getMinAltitude())));
                tvMaxAltitude.setText(String.format(Locale.US, Utils.ALTITUDE_FORMAT, Utils.metersToFt(flight.getMaxAltitude())));
                break;
        }

        switch (speedUnit) {
            // Speed
            case "ms":
                tvMinSpeed.setText(String.valueOf(flight.getMinSpeed()));
                tvAvgSpeed.setText(String.valueOf(flight.getAvgSpeed()));
                tvMaxSpeed.setText(String.valueOf(flight.getMaxSpeed()));
                break;
            case "kmh":
                tvMinSpeed.setText(String.format(Locale.US, Utils.SPEED_FORMAT, Utils.metersPerSecondToKmh(flight.getMinSpeed())));
                tvAvgSpeed.setText(String.format(Locale.US, Utils.SPEED_FORMAT, Utils.metersPerSecondToKmh(flight.getAvgSpeed())));
                tvMaxSpeed.setText(String.format(Locale.US, Utils.SPEED_FORMAT, Utils.metersPerSecondToKmh(flight.getMaxSpeed())));
                break;
            case "mph":
                tvMinSpeed.setText(String.format(Locale.US, Utils.SPEED_FORMAT, Utils.metersPerSecondToMph(flight.getMinSpeed())));
                tvAvgSpeed.setText(String.format(Locale.US, Utils.SPEED_FORMAT, Utils.metersPerSecondToMph(flight.getAvgSpeed())));
                tvMaxSpeed.setText(String.format(Locale.US, Utils.SPEED_FORMAT, Utils.metersPerSecondToMph(flight.getMaxSpeed())));
                break;
            case "kt":
                tvMinSpeed.setText(String.format(Locale.US, Utils.SPEED_FORMAT, Utils.metersPerSecondToKt(flight.getMinSpeed())));
                tvAvgSpeed.setText(String.format(Locale.US, Utils.SPEED_FORMAT, Utils.metersPerSecondToKt(flight.getAvgSpeed())));
                tvMaxSpeed.setText(String.format(Locale.US, Utils.SPEED_FORMAT, Utils.metersPerSecondToKt(flight.getMaxSpeed())));
                break;
        }

    }


    public void showDeleteDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage("Are you sure of delete this flight?")
                .setTitle("Delete")
                .setPositiveButton("YES", (dialogInterface, i) -> {
                    flightsViewModel.deleteFlightByID(mflight.getId());
                    getParentFragmentManager().popBackStack();
                })
                .setNegativeButton("NO", (dialogInterface, i) -> {
                    //Do nothing
                });


        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showEditFlightFragment() {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        EditFlightFragment editFlightFragment = new EditFlightFragment();
        transaction.hide(FlightDetailFragment.this);
        transaction.add(R.id.nav_host_fragment, editFlightFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        String value = sharedPreferences.getString(getString(R.string.distance_unit_key), "NULL");
        distanceUnit = value;

        value = sharedPreferences.getString(getString(R.string.altitude_unit_key), "NULL");
        altitudeUnit = value;

        value = sharedPreferences.getString(getString(R.string.speed_unit_key), "NULL");
        speedUnit = value;

        setLabels();
    }


    private void setLabels() {

        switch (distanceUnit) {
            // Distance
            case "km":
                tvDistanceLabel.setText(getString(R.string.distance_km));
                break;
            case "mi":
                tvDistanceLabel.setText(getString(R.string.distance_mi));
                break;
            case "nm":
                tvDistanceLabel.setText(getString(R.string.distance_nm));
                break;
        }


        switch (altitudeUnit) {
            // Altitude
            case "m":
                tvMinAltitudeLabel.setText(R.string.min_altitude_m);
                tvMaxAltitudeLabel.setText(R.string.max_altitude_m);
                break;
            case "ft":
                tvMinAltitudeLabel.setText(R.string.min_altitude_ft);
                tvMaxAltitudeLabel.setText(R.string.max_altitude_ft);
                break;
        }

        switch (speedUnit) {
            // Speed
            case "ms":
                tvMinSpeedLabel.setText(R.string.min_speed_ms);
                tvAvgSpeedLabel.setText(R.string.avg_speed_ms);
                tvMaxSpeedLabel.setText(R.string.max_speed_ms);
                break;
            case "kmh":
                tvMinSpeedLabel.setText(R.string.min_speed_kmh);
                tvAvgSpeedLabel.setText(R.string.avg_speed_kmh);
                tvMaxSpeedLabel.setText(R.string.max_speed_kmh);
                break;
            case "mph":
                tvMinSpeedLabel.setText(R.string.min_speed_mph);
                tvAvgSpeedLabel.setText(R.string.avg_speed_mph);
                tvMaxSpeedLabel.setText(R.string.max_speed_mph);
                break;
            case "kt":
                tvMinSpeedLabel.setText(R.string.min_speed_kt);
                tvAvgSpeedLabel.setText(R.string.avg_speed_kt);
                tvMaxSpeedLabel.setText(R.string.max_speed_kt);
                break;
        }


    }


    private void setRouteLine(@NonNull Style style) {

        GeoJsonSource geoJsonSource = Utils.getGeoJsonSourceFromRoute(mflight.getRoute());


        style.addSource(geoJsonSource);

        // Add FillExtrusion layer to map using GeoJSON data
        style.addLayer(new LineLayer("line_layer", Utils.GEO_JSON_ID).withProperties(
                PropertyFactory.lineWidth(3f),
                PropertyFactory.lineColor(Color.rgb(41, 98, 255))
        ));


    }

    private void setRoutePoints(@NonNull Style style) {
        List<Feature> featureList = new ArrayList<>();

        FlightLocation firstFlightLocation = mflight.getRoute().get(0);
        FlightLocation lastFlightLocation = mflight.getRoute().get(mflight.getRoute().size() - 1);

        Feature initPoint = Feature.fromGeometry(Point.fromLngLat(firstFlightLocation.getLongitude(), firstFlightLocation.getLatitude()));
        Feature finalPoint = Feature.fromGeometry(Point.fromLngLat(lastFlightLocation.getLongitude(), lastFlightLocation.getLatitude()));

        initPoint.addNumberProperty("c", Color.GREEN);
        finalPoint.addNumberProperty("c", Color.RED);

        featureList.add(initPoint);
        featureList.add(finalPoint);

        GeoJsonSource geoJsonSource = new GeoJsonSource("points_source", FeatureCollection.fromFeatures(featureList));

        style.addSource(geoJsonSource);


        style.addLayer(new CircleLayer("points_layer", "points_source").withProperties(
                circleRadius(10f),
                circleStrokeColor(Color.WHITE),
                circleStrokeWidth(2f),
                circleColor(match(get("c"), rgb(0, 0, 0),
                        stop(Color.GREEN, rgb(67, 160, 71)),
                        stop(Color.RED, rgb(255, 0, 0))
                ))
        ));
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
        mapView.onDestroy();
    }


}