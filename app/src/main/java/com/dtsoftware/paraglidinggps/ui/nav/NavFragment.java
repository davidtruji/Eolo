package com.dtsoftware.paraglidinggps.ui.nav;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dtsoftware.paraglidinggps.MainActivity;
import com.dtsoftware.paraglidinggps.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import java.lang.ref.WeakReference;
import java.util.List;

import static android.os.Looper.getMainLooper;


public class NavFragment extends Fragment implements
        OnMapReadyCallback, PermissionsListener {

    private static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private static final long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    private MapboxMap mapboxMap;
    private MapView mapView;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationChangeListeningActivityLocationCallback callback =
            new LocationChangeListeningActivityLocationCallback(this);
    private TextView tvSpeed,tvCoodinates,tvAltitude,tvBearing,tvDistance;
    private FloatingActionButton fabCameraMode,fabLayers,fabPlay;
    private BottomNavigationView bottomNavigationView;


    private float distance=0.0f;
    private Location prevLocation=null;
    private boolean flying=false;



    public NavFragment(){

    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Mapbox.getInstance(getContext(),getString(R.string.mapbox_access_token));
        View root = inflater.inflate(R.layout.nav_fragment, container, false);


        mapView = (MapView) root.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        tvAltitude = (TextView) root.findViewById(R.id.tvAltitude);
        tvSpeed = (TextView) root.findViewById(R.id.tvSpeed);
        tvCoodinates = (TextView) root.findViewById(R.id.tvCoordinates);
        tvBearing = (TextView) root.findViewById(R.id.tvBearing);
        tvDistance = (TextView) root.findViewById(R.id.tvDistance);

        fabCameraMode = (FloatingActionButton) root.findViewById(R.id.fabCameraMode);
        fabCameraMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCameraMode();
            }
        });
        fabLayers = (FloatingActionButton) root.findViewById(R.id.fabLayers);
        fabLayers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setStyle();
            }
        });
        fabPlay = (FloatingActionButton) root.findViewById(R.id.fabPlay);
        fabPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playStopFly();
            }
        });

        return root;
    }

    private void playStopFly() {

        if(flying){// Usuario pulsó STOP
            fabPlay.setImageDrawable(getActivity().getDrawable(R.drawable.play));
            ((MainActivity) getActivity()).getSupportActionBar().show();
            flying=false;
            mapboxMap.getGesturesManager().getMoveGestureDetector().setEnabled(true);
            mapboxMap.getGesturesManager().getRotateGestureDetector().setEnabled(true);
        }else{// Usuario pulsó PLAY
            fabPlay.setImageDrawable(getActivity().getDrawable(R.drawable.stop));
            ((MainActivity) getActivity()).getSupportActionBar().hide();
            this.flying=true;
            mapboxMap.getGesturesManager().getMoveGestureDetector().setEnabled(false);
            mapboxMap.getGesturesManager().getRotateGestureDetector().setEnabled(false);
        }


    }

    private void setStyle() {
        String currentStyleUri=null;
        String newStyleUri=Style.OUTDOORS;

        if(mapboxMap.getStyle()!=null)
            currentStyleUri=mapboxMap.getStyle().getUri();

        if(currentStyleUri.equals(Style.OUTDOORS)){
            newStyleUri=Style.SATELLITE_STREETS;
        }else{
            newStyleUri=Style.OUTDOORS;
        }

        this.mapboxMap.setStyle(newStyleUri);

    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(Style.OUTDOORS,
                new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        enableLocationComponent(style);
                    }
                });

    }


    public void setCameraMode(){
        int currentCameraMode =  mapboxMap.getLocationComponent().getCameraMode();

        if(currentCameraMode==CameraMode.NONE){
            mapboxMap.getLocationComponent().setCameraMode(CameraMode.TRACKING_GPS_NORTH);
            fabCameraMode.setImageDrawable(getActivity().getDrawable(R.drawable.compass_off));
        }else if(currentCameraMode == CameraMode.TRACKING_GPS_NORTH){
            mapboxMap.getLocationComponent().setCameraMode(CameraMode.TRACKING_COMPASS);
            fabCameraMode.setImageDrawable(getActivity().getDrawable(R.drawable.compass_on));
        }else if(currentCameraMode == CameraMode.TRACKING_COMPASS){
            mapboxMap.getLocationComponent().setCameraMode(CameraMode.TRACKING_GPS_NORTH);
            fabCameraMode.setImageDrawable(getActivity().getDrawable(R.drawable.compass_off));
        }

    }




    /**
     * Initialize the Maps SDK's LocationComponent
     */
    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(getContext().getApplicationContext())) {

            // Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            // Set the LocationComponent activation options
            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(getContext().getApplicationContext(), loadedMapStyle)
                            .useDefaultLocationEngine(false)
                            .build();

            // Activate with the LocationComponentActivationOptions object
            locationComponent.activateLocationComponent(locationComponentActivationOptions);

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING_GPS_NORTH);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);


            initLocationEngine();
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
    }


    /**
     * Set up the LocationEngine and the parameters for querying the device's location
     */
    @SuppressLint("MissingPermission")
    private void initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(getContext().getApplicationContext());

        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();

        locationEngine.requestLocationUpdates(request, callback, getMainLooper());
        locationEngine.getLastLocation(callback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(getActivity(), R.string.user_location_permission_explanation,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(getActivity(), R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
        }
    }

    private static class LocationChangeListeningActivityLocationCallback
            implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<NavFragment> activityWeakReference;

        LocationChangeListeningActivityLocationCallback(NavFragment activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location has changed.
         *
         * @param result the LocationEngineResult object which has the last known location within it.
         */
        @Override
        public void onSuccess(LocationEngineResult result) {
            NavFragment activity = activityWeakReference.get();

            if (activity != null && activity.getContext()!=null) {
                Location location = result.getLastLocation();

                if (location == null) {
                    return;
                }

                // Información en pantalla
                activity.tvAltitude.setText(String.format(activity.getString(R.string.altitude_format),result.getLastLocation().getAltitude()));// Altitud en metros
                activity.tvSpeed.setText(String.format(activity.getString(R.string.speed_format),result.getLastLocation().getSpeed()*3.6));// Velocidad en m/s * 3.6 = Km/h
                activity.tvBearing.setText(String.format(activity.getString(R.string.bearing_format),result.getLastLocation().getBearing()));// rumbo en grados
                activity.tvCoodinates.setText(String.format(activity.getString(R.string.coordinates_format),result.getLastLocation().getLatitude(),result.getLastLocation().getLongitude()));// Latitud y longitud

                activity.updateDistance(result.getLastLocation());
                activity.tvDistance.setText(String.format(activity.getString(R.string.distance_format),activity.distance));
                activity.prevLocation=result.getLastLocation();

                // Pass the new location to the Maps SDK's LocationComponent
                if (activity.mapboxMap != null && result.getLastLocation() != null) {
                    activity.mapboxMap.getLocationComponent().forceLocationUpdate(result.getLastLocation());
                }
            }
        }



        /**
         * The LocationEngineCallback interface's method which fires when the device's location can't be captured
         *
         * @param exception the exception message
         */
        @Override
        public void onFailure(@NonNull Exception exception) {
            NavFragment activity = activityWeakReference.get();
            if (activity != null) {
                Toast.makeText(activity.getActivity(), exception.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void updateDistance(Location lastLocation){

        if(prevLocation == null){
            distance=0;
        }else{
            distance+=prevLocation.distanceTo(lastLocation)/1000;
        }

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
    public void onSaveInstanceState(Bundle outState) {
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