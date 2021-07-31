package com.dtsoftware.paraglidinggps.ui.waypoints;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.dtsoftware.paraglidinggps.R;
import com.dtsoftware.paraglidinggps.Utils;
import com.dtsoftware.paraglidinggps.Waypoint;
import com.google.android.material.textfield.TextInputLayout;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

public class EditWaypointFragment extends Fragment implements MapboxMap.OnMapClickListener {

    private MapView mapView;
    private MapboxMap mapboxMap;
    private LatLng currentPosition = new LatLng(0, 0);
    private GeoJsonSource geoJsonSource;
    private ValueAnimator animator;
    private OnMapReadyCallback onMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull MapboxMap mapboxMap) {
            EditWaypointFragment.this.mapboxMap = mapboxMap;

            geoJsonSource = new GeoJsonSource("source-id",
                    Feature.fromGeometry(Point.fromLngLat(currentPosition.getLongitude(),
                            currentPosition.getLatitude())));


            mapboxMap.setStyle(Style.SATELLITE_STREETS, style -> {

                style.addImage(("marker_icon"), BitmapFactory.decodeResource(
                        getResources(), R.drawable.mapbox_marker_icon_default));

                style.addSource(geoJsonSource);

                style.addLayer(new SymbolLayer("layer-id", "source-id")
                        .withProperties(
                                PropertyFactory.iconImage("marker_icon"),
                                PropertyFactory.iconIgnorePlacement(true),
                                PropertyFactory.iconAllowOverlap(true)
                        ));

                mapboxMap.addOnMapClickListener(EditWaypointFragment.this);

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(currentPosition)
                        .zoom(12.0)
                        .build();

                mapboxMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition), 5000);

            });
        }
    };

    private Waypoint waypoint;

    private TextView tvLat, tvLng;
    private EditText etName;
    private TextInputLayout tilName;
    private Toolbar toolbar;
    private WaypointViewModel waypointViewModel;


    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() < 1)
                tilName.setError("Name required");
            else
                tilName.setErrorEnabled(false);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Mapbox.getInstance(getContext(), getString(R.string.mapbox_access_token));

        View root = inflater.inflate(R.layout.fragment_add_waypoint, container, false);

        setHasOptionsMenu(true);

        toolbar = root.findViewById(R.id.aw_toolbar);
        toolbar.setSubtitle("Tap to edit the position");
        toolbar.inflateMenu(R.menu.ew_toolbar_menu);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setOnMenuItemClickListener(item -> {

            switch (item.getItemId()) {

                case R.id.action_save:
                    if (validateWaypoint())
                        updateWaypoint();
                    break;

                case R.id.action_delete:
                    showDeleteDialog();
                    break;

                default:
                    break;
            }

            return true;
        });
        toolbar.setNavigationOnClickListener(view -> getParentFragmentManager().popBackStack());

        setHasOptionsMenu(true);


        mapView = root.findViewById(R.id.mv_aw_map);
        tvLat = root.findViewById(R.id.tv_aw_lat);
        tvLng = root.findViewById(R.id.tv_aw_lng);
        etName = root.findViewById(R.id.et_aw_name);
        tilName = root.findViewById(R.id.textInputLayout);

        etName.addTextChangedListener(textWatcher);

        mapView.onCreate(savedInstanceState);


        waypointViewModel = new ViewModelProvider(getActivity()).get(WaypointViewModel.class);
        waypointViewModel.getSelectedWaypoint().observe(getViewLifecycleOwner(), this::bindWaypoint);

        return root;
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


        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(currentPosition)
                .build();

        mapboxMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition), 5000);


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

    public void bindWaypoint(Waypoint waypoint) {

        this.waypoint = waypoint;

        tvLat.setText("Lat. " + String.format(getString(R.string.coordinates_format), waypoint.getLatitude()));
        tvLng.setText("Long. " + String.format(getString(R.string.coordinates_format), waypoint.getLongitude()));


        currentPosition.setLatitude(waypoint.getLatitude());
        currentPosition.setLongitude(waypoint.getLongitude());


        toolbar.setTitle(waypoint.getWaypointName());
        etName.setText(waypoint.getWaypointName());


        mapView.getMapAsync(onMapReadyCallback);

    }


    private void updateWaypoint() {

        waypoint.setLatitude(currentPosition.getLatitude());
        waypoint.setLongitude(currentPosition.getLongitude());
        waypoint.setWaypointName(etName.getText().toString());

        waypointViewModel.updateWaypoint(waypoint);

        getParentFragmentManager().popBackStack();
    }

    public void showDeleteDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage("Are you sure of delete this Waypoint?")
                .setTitle("Delete")
                .setPositiveButton("YES", (dialogInterface, i) -> {
                    waypointViewModel.deleteWaypointById(waypoint.getId());
                    getParentFragmentManager().popBackStack();
                })
                .setNegativeButton("NO", (dialogInterface, i) -> {
                    //Do nothing
                });


        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private boolean validateWaypoint() {

        boolean validation = (etName.getText().toString().length() > 0);

        if (!validation)
            tilName.setError("Name required");

        return validation;
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
        // Prevent leaks
        mapView.onDestroy();
    }

}
