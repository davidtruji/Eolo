package com.dtsoftware.paraglidinggps.ui.nav;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.dtsoftware.paraglidinggps.Flight;
import com.dtsoftware.paraglidinggps.FlightLocation;
import com.dtsoftware.paraglidinggps.MainActivity;
import com.dtsoftware.paraglidinggps.R;
import com.dtsoftware.paraglidinggps.Utils;
import com.dtsoftware.paraglidinggps.Waypoint;
import com.dtsoftware.paraglidinggps.ui.flights.FlightsViewModel;
import com.dtsoftware.paraglidinggps.ui.waypoints.WaypointsViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.OnCameraTrackingChangedListener;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import okhttp3.internal.Util;

import static android.os.Looper.getMainLooper;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;


@SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
public class NavFragment extends Fragment implements
        OnMapReadyCallback, PermissionsListener, OnCameraTrackingChangedListener, SaveDialogFragment.SaveDialogListener {

    // Constantes
    private static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000;
    private static final long DEFAULT_MAX_WAIT_TIME = 0;

    private static final String SOURCE_ID = "wpt_source";
    private static final String ICON_LAYER_ID = "icon_layer";
    private static final String IMAGE_NAME = "red_marker";


    // Mapbox variables
    private MapboxMap mapboxMap;
    private MapView mapView;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationChangeListeningActivityLocationCallback callback =
            new LocationChangeListeningActivityLocationCallback(this);
    private List<Waypoint> waypoints = new ArrayList<>();

    // Variables UI
    private TextView tvDistance, tvSpeed, tvBearing, tvBearingLet, tvAltitude;
    private Chronometer tvChronometer;
    private FloatingActionButton fabStartFly, fabLayers, fabCompass;

    // Variables de vuelo
    private boolean flying = false;
    private Location prevLocation = null;
    private float distance = 0;
    private ArrayList<FlightLocation> route = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(getContext(), getString(R.string.mapbox_access_token));

        // This contains the MapView in XML and needs to be called after the access token is configured.
        View root = inflater.inflate(R.layout.nav_fragment, container, false);
        //TODO: Pulsación larga para cambiar los bloques visibles
        //TODO: Bariometro primitivo con el GPS


        Toolbar toolbar = root.findViewById(R.id.nav_toolbar);
        toolbar.setTitle(getString(R.string.title_nav));

        tvDistance = root.findViewById(R.id.tvDistance);
        tvBearing = root.findViewById(R.id.tvBearing);
        tvBearingLet = root.findViewById(R.id.tvBearingLetter);
        tvSpeed = root.findViewById(R.id.tvSpeed);
        tvAltitude = root.findViewById(R.id.tvAltitude);
        tvChronometer = root.findViewById(R.id.tvChronometer);


        fabStartFly = root.findViewById(R.id.fabPlay);
        fabLayers = root.findViewById(R.id.fabLayers);
        fabCompass = root.findViewById(R.id.fabCompass);


        mapView = root.findViewById(R.id.mv_nav_map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        fabStartFly.setOnClickListener(view -> {
            if (flying) {   // Usuario pulsó STOP
                stopFly();
            } else {        // Usuario pulsó PLAY
                startFly();
            }
        });

        fabLayers.setOnClickListener(view -> changeCurrentMapLayer());

        fabCompass.setOnClickListener(view -> changeCameraMode());

        WaypointsViewModel waypointsViewModel = new ViewModelProvider(getActivity()).get(WaypointsViewModel.class);
        waypointsViewModel.getAllWaypoints().observe(getViewLifecycleOwner(), waypoints -> {
            NavFragment.this.waypoints = waypoints;

            if (mapboxMap != null)
                setWaypointsLayer();

        });


        return root;
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(Style.OUTDOORS,
                style -> {
                    enableLocationComponent(style);
                    setWaypointsLayer();
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

            // Modo por defecto de la cámara al iniciar la app
            locationComponent.setCameraMode(CameraMode.TRACKING_GPS_NORTH);
            locationComponent.zoomWhileTracking(14);
            locationComponent.tiltWhileTracking(0);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);

            // Añado el Listener que vigila el estado de la cámara
            locationComponent.addOnCameraTrackingChangedListener(this);

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
            mapboxMap.getStyle(this::enableLocationComponent);
        } else {
            Toast.makeText(getContext(), R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
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

            if (activity != null) {
                Location location = result.getLastLocation();

                if (location == null) {
                    return;
                }


                // Actualiza la información del HUD, Velocidad, Altura etc
                activity.updateFlightInfo(result.getLastLocation());


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

    private void updateFlightInfo(Location lastLocation) {

        Float heading = (mapboxMap.getLocationComponent().getCompassEngine().getLastHeading() + 360) % 360;

        tvBearingLet.setText(Utils.degreesToBearing(heading)); // Rumbo escrito en letras

        tvBearing.setText(String.format(getString(R.string.bearing_format), heading) + " " + getString(R.string.degrees_unit)); // Rumbo en grados

        tvAltitude.setText(String.format(getString(R.string.altitude_format), lastLocation.getAltitude())); // Altitud (m)

        tvSpeed.setText(String.format(getString(R.string.speed_format), lastLocation.getSpeed() * 3.6)); // Velocidad en Km/h (m/s * 3.6)

        if (flying) {
            updateDistance(lastLocation); // Distancia del vuelo (Km)
            route.add(new FlightLocation(lastLocation));
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
        route.clear();
        ((MainActivity) getActivity()).hideSystemUI();
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// Evitar que la pantalla se apague sola
        fabStartFly.setImageDrawable(getActivity().getDrawable(R.drawable.stop));
        flying = true;
        tvChronometer.setBase(SystemClock.elapsedRealtime());
        tvChronometer.start();
        Log.i(getString(R.string.debug_tag), "Vuelo Iniciado");
    }


    private void stopFly() {

        flying = false;
        resetOnScreenInfo();
        ((MainActivity) getActivity()).showSystemUI();
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// Permitir apagar la pantalla de nuevo
        fabStartFly.setImageDrawable(getActivity().getDrawable(R.drawable.play));
        tvChronometer.setBase(SystemClock.elapsedRealtime());
        tvChronometer.stop();

        Log.i(getString(R.string.debug_tag), "Vuelo finalizado: " + "distancia: " + Utils.getRouteDistance(route) + "m" + " duracion: " + Utils.getRouteDuration(route) / 1000 + "\"");
        showSaveFlightDialog();
    }


    private void showSaveFlightDialog() {
        DialogFragment newFragment = new SaveDialogFragment();
        newFragment.setTargetFragment(NavFragment.this, 0);
        newFragment.show(this.getParentFragmentManager(), "save");
    }


    private void changeCurrentMapLayer() {
        if (mapboxMap.getStyle().getUri().equalsIgnoreCase(Style.SATELLITE_STREETS))
            mapboxMap.setStyle(Style.OUTDOORS);
        else
            mapboxMap.setStyle(Style.SATELLITE_STREETS);
    }


    private void changeCameraMode() {
        LocationComponent locationComponent = mapboxMap.getLocationComponent();

        if (locationComponent.getCameraMode() == CameraMode.TRACKING_COMPASS) {
            locationComponent.setCameraMode(CameraMode.TRACKING_GPS_NORTH);
            Log.i(getString(R.string.debug_tag), "Modo de cámara cambia de TRACKING_COMPASS a TRACKING_GPS_NORTH");
            Utils.showSnakcbar(getView().findViewById(R.id.screenInfo_layout), getString(R.string.tracking_gps_north_snack));

        } else if (locationComponent.getCameraMode() == CameraMode.TRACKING_GPS_NORTH) {
            locationComponent.setCameraMode(CameraMode.TRACKING_COMPASS);
            Log.i(getString(R.string.debug_tag), "Modo de cámara cambia de TRACKING_GPS_NORTH a TRACKING_COMPASS");
            Utils.showSnakcbar(getView().findViewById(R.id.screenInfo_layout), getString(R.string.tracking_compass_snack));

        } else {
            locationComponent.setCameraMode(CameraMode.TRACKING_GPS_NORTH);
            Log.i(getString(R.string.debug_tag), "Modo de cámara cambia de NONE a TRACKING_GPS_NORTH");
            Utils.showSnakcbar(getView().findViewById(R.id.screenInfo_layout), getString(R.string.tracking_gps_north_snack));

        }


    }


    @Override
    public void onCameraTrackingDismissed() {
        fabCompass.setImageDrawable(getActivity().getDrawable(R.drawable.ic_baseline_my_location_24));
    }

    @SuppressLint("SwitchIntDef")
    @Override
    public void onCameraTrackingChanged(int currentMode) {

        switch (currentMode) {
            case CameraMode.TRACKING_COMPASS:
                fabCompass.setImageDrawable(getActivity().getDrawable(R.drawable.compass_on));
                break;

            case CameraMode.TRACKING_GPS_NORTH:
                fabCompass.setImageDrawable(getActivity().getDrawable(R.drawable.compass_off));
                break;

            default:
                break;
        }


    }


    @Override
    public void onDialogSaveClick(String flightName) {
        FlightsViewModel flightsViewModel = new ViewModelProvider(this).get(FlightsViewModel.class);
        flightsViewModel.insert(new Flight(flightName, route));
    }


    public void setWaypointsLayer() {

        List<Feature> symbolLayerIconFeatureList = new ArrayList<>();

        for (Waypoint waypoint : waypoints) {
            symbolLayerIconFeatureList.add(Feature.fromGeometry(Point.fromLngLat(waypoint.getLongitude(), waypoint.getLatitude())));
        }

        mapboxMap.getStyle(style -> {

            if (style.getLayer(ICON_LAYER_ID) != null) {
                style.removeLayer(ICON_LAYER_ID);
                style.removeSource(SOURCE_ID);

            } else {

                style.addImage(IMAGE_NAME, BitmapFactory.decodeResource(
                        NavFragment.this.getResources(), R.drawable.mapbox_marker_icon_default));

            }

            style.addSource(new GeoJsonSource(SOURCE_ID, FeatureCollection.fromFeatures(symbolLayerIconFeatureList)));

            style.addLayer(new SymbolLayer(ICON_LAYER_ID, SOURCE_ID)
                    .withProperties(
                            iconImage(IMAGE_NAME),
                            iconAllowOverlap(true),
                            iconIgnorePlacement(true)
                    ));
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
        // Prevent leaks
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(callback);
        }
        mapView.onDestroy();
    }


}