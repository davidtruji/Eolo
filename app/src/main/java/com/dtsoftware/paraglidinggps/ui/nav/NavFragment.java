package com.dtsoftware.paraglidinggps.ui.nav;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.dtsoftware.paraglidinggps.MainActivity;
import com.dtsoftware.paraglidinggps.R;
import com.dtsoftware.paraglidinggps.Utils;
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

import static android.content.Context.SENSOR_SERVICE;
import static android.os.Looper.getMainLooper;


public class NavFragment extends Fragment implements
        OnMapReadyCallback, PermissionsListener, SensorEventListener {


    private static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000;
    private static final long DEFAULT_MAX_WAIT_TIME = 0;
    private MapboxMap mapboxMap;
    private MapView mapView;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationChangeListeningActivityLocationCallback callback =
            new LocationChangeListeningActivityLocationCallback(this);

    private TextView tvDistance, tvSpeed, tvBearing, tvBearingLet, tvAltitude;
    private Chronometer tvChronometer;
    private Location prevLocation = null;
    private float distance = 0;
    private FloatingActionButton fabStartFly;
    private boolean flying = false;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(getContext(), getString(R.string.mapbox_access_token));

        // This contains the MapView in XML and needs to be called after the access token is configured.
        View root = inflater.inflate(R.layout.nav_fragment, container, false);

        mapView = root.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        tvDistance = root.findViewById(R.id.tvDistance);
        tvBearing = root.findViewById(R.id.tvBearing);
        tvBearingLet = root.findViewById(R.id.tvBearingLetter);
        tvSpeed = root.findViewById(R.id.tvSpeed);
        tvAltitude = root.findViewById(R.id.tvAltitude);
        tvChronometer = root.findViewById(R.id.tvChronometer);


        fabStartFly = root.findViewById(R.id.fabPlay);


        fabStartFly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flying) {// Usuario pulsó STOP
                    stopFly();
                } else {// Usuario pulsó PLAY
                    startFly();
                }
            }
        });

        return root;
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        // TODO: Botón de capas del mapa
        mapboxMap.setStyle(Style.OUTDOORS,
                new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        enableLocationComponent(style);
                    }
                });
    }

    /**
     * Initialize the Maps SDK's LocationComponent
     */
    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(getContext())) {

            // Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            // Set the LocationComponent activation options
            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(getContext(), loadedMapStyle)
                            .useDefaultLocationEngine(false)
                            .build();

            // Activate with the LocationComponentActivationOptions object
            locationComponent.activateLocationComponent(locationComponentActivationOptions);

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING_COMPASS);

            // TODO: Cambiar angulo y zoom de camara cuando se empieza el vuelo
            // TODO: Botones para cambiar el modo de camara en tiempo de ejecución


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
        locationEngine = LocationEngineProvider.getBestLocationEngine(getContext());

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
        Toast.makeText(getContext(), R.string.user_location_permission_explanation,
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
            Toast.makeText(getContext(), R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //  tvBearingLet.setText(Float.toString(Math.round(sensorEvent.values[0])));

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

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

            if (activity != null) {
                Location location = result.getLastLocation();

                if (location == null) {
                    return;
                }


                // Actualiza la información del HUD, Velocidad, Altura etc
                activity.updateOnScreenInfo(result.getLastLocation());


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
                Toast.makeText(activity.getContext(), exception.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateOnScreenInfo(Location lastLocation) {

        Float heading = (mapboxMap.getLocationComponent().getCompassEngine().getLastHeading() + 360) % 360;

        tvBearingLet.setText(Utils.degreesToBearing(heading)); // Rumbo escrito en letras

        tvBearing.setText(String.format(getString(R.string.bearing_format), heading) + " " + getString(R.string.degrees_unit)); // Rumbo en grados

        tvAltitude.setText(String.format(getString(R.string.altitude_format), lastLocation.getAltitude())); // Altitud (m)

        tvSpeed.setText(String.format(getString(R.string.speed_format), lastLocation.getSpeed() * 3.6)); // Velocidad en Km/h (m/s * 3.6)

        if (flying) {
            updateDistance(lastLocation); // Distancia del vuelo (Km)
        }


    }

    private void resetOnScreenInfo() {
        distance = 0;
        tvDistance.setText(getString(R.string.default_distance));
        tvSpeed.setText(getString(R.string.default_speed));
        tvAltitude.setText(getString(R.string.default_altitude));
        tvBearing.setText(getString(R.string.default_bearing));
    }

    private void updateDistance(Location lastLocation) {

        if (prevLocation == null) {
            distance = 0;
        } else {
            distance += prevLocation.distanceTo(lastLocation) / 1000;
            tvDistance.setText(String.format(getString(R.string.distance_format), distance));
        }

        prevLocation = lastLocation;

    }

    private void startFly() {
        ((MainActivity) getActivity()).hideSystemUI();
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// Evitar que la pantalla se apague sola
        fabStartFly.setImageDrawable(getActivity().getDrawable(R.drawable.stop));
        flying = true;
        tvChronometer.setBase(SystemClock.elapsedRealtime());
        tvChronometer.start();
        Log.i(getString(R.string.debug_tag), "Vuelo Iniciado");
    }


    private void stopFly() {
        ((MainActivity) getActivity()).showSystemUI();
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// Permitir apagar la pantalla de nuevo
        fabStartFly.setImageDrawable(getActivity().getDrawable(R.drawable.play));
        flying = false;
        resetOnScreenInfo();
        tvChronometer.setBase(SystemClock.elapsedRealtime());
        tvChronometer.stop();
        Log.i(getString(R.string.debug_tag), "Vuelo finalizado");
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
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(callback);
        }
        mapView.onDestroy();
    }


}