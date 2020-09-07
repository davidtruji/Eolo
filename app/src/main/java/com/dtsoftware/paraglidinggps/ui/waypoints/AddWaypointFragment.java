package com.dtsoftware.paraglidinggps.ui.waypoints;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dtsoftware.paraglidinggps.MainActivity;
import com.dtsoftware.paraglidinggps.R;
import com.dtsoftware.paraglidinggps.Waypoint;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;


public class AddWaypointFragment extends Fragment implements OnMapReadyCallback, MapboxMap.OnMapClickListener {

    private MapView mapView;
    private MapboxMap mapboxMap;
    private LatLng currentPosition = new LatLng(0, 0);
    private GeoJsonSource geoJsonSource;
    private ValueAnimator animator;

    private TextView tvLat, tvLng;
    private EditText etName;

    WaypointsViewModel waypointsViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Mapbox.getInstance(getContext(), getString(R.string.mapbox_access_token));

        View root = inflater.inflate(R.layout.fragment_add_waypoint, container, false);

        setHasOptionsMenu(true);

        waypointsViewModel = new ViewModelProvider(getActivity()).get(WaypointsViewModel.class);

        mapView = root.findViewById(R.id.mv_aw_map);
        tvLat = root.findViewById(R.id.tv_aw_lat);
        tvLng = root.findViewById(R.id.tv_aw_lng);
        etName = root.findViewById(R.id.et_aw_name);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.aw_toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            waypointsViewModel.insert(new Waypoint(etName.getText().toString(), currentPosition.getLatitude(), currentPosition.getLongitude()));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;

        geoJsonSource = new GeoJsonSource("source-id",
                Feature.fromGeometry(Point.fromLngLat(currentPosition.getLongitude(),
                        currentPosition.getLatitude())));


        mapboxMap.setStyle(Style.SATELLITE_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {

                style.addImage(("marker_icon"), BitmapFactory.decodeResource(
                        getResources(), R.drawable.mapbox_marker_icon_default));

                style.addSource(geoJsonSource);

                style.addLayer(new SymbolLayer("layer-id", "source-id")
                        .withProperties(
                                PropertyFactory.iconImage("marker_icon"),
                                PropertyFactory.iconIgnorePlacement(true),
                                PropertyFactory.iconAllowOverlap(true)
                        ));

                mapboxMap.addOnMapClickListener(AddWaypointFragment.this);

            }
        });
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        // When the user clicks on the map, we want to animate the marker to that
        // location.
        if (animator != null && animator.isStarted()) {
            currentPosition = (LatLng) animator.getAnimatedValue();
            animator.cancel();
        }

        animator = ObjectAnimator
                .ofObject(latLngEvaluator, currentPosition, point)
                .setDuration(500);
        animator.addUpdateListener(animatorUpdateListener);
        animator.start();

        currentPosition = point;

        tvLat.setText("Lat. " + String.format(getString(R.string.coordinates_format), point.getLatitude()));
        tvLng.setText("Long. " + String.format(getString(R.string.coordinates_format), point.getLongitude()));


        return true;
    }

    private final ValueAnimator.AnimatorUpdateListener animatorUpdateListener =
            new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    LatLng animatedPosition = (LatLng) valueAnimator.getAnimatedValue();
                    geoJsonSource.setGeoJson(Point.fromLngLat(animatedPosition.getLongitude(), animatedPosition.getLatitude()));
                }
            };

    // Class is used to interpolate the marker animation.
    private static final TypeEvaluator<LatLng> latLngEvaluator = new TypeEvaluator<LatLng>() {

        private final LatLng latLng = new LatLng();

        @Override
        public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
            latLng.setLatitude(startValue.getLatitude()
                    + ((endValue.getLatitude() - startValue.getLatitude()) * fraction));
            latLng.setLongitude(startValue.getLongitude()
                    + ((endValue.getLongitude() - startValue.getLongitude()) * fraction));
            return latLng;
        }
    };


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
        // Prevent leaks
        mapView.onDestroy();
    }


}