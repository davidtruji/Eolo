package com.dtsoftware.paraglidinggps.ui.nav;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.dtsoftware.paraglidinggps.Flight;
import com.dtsoftware.paraglidinggps.FlightLocation;
import com.dtsoftware.paraglidinggps.MainActivity;
import com.dtsoftware.paraglidinggps.R;
import com.dtsoftware.paraglidinggps.SettingsActivity;
import com.dtsoftware.paraglidinggps.TextViewOutline;
import com.dtsoftware.paraglidinggps.Utils;
import com.dtsoftware.paraglidinggps.Waypoint;
import com.dtsoftware.paraglidinggps.ui.flights.FlightsViewModel;
import com.dtsoftware.paraglidinggps.ui.waypoints.WaypointsViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.CompassListener;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.OnCameraTrackingChangedListener;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import static android.os.Looper.getMainLooper;
import static com.mapbox.mapboxsdk.style.expressions.Expression.eq;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.in;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.layers.Property.ICON_ANCHOR_BOTTOM;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAnchor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static java.lang.Math.abs;


@SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
public class NavFragment extends Fragment implements CompassListener, PermissionsListener, OnCameraTrackingChangedListener {


    // Constantes
    private final long DEFAULT_INTERVAL_IN_MILLISECONDS = 100L;
    private final long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    private static final String WPT_SOURCE_ID = "wpt_source";
    private static final String ROUTE_ICON_SOURCE_ID = "route_icon_source";
    private static final String ROUTE_LINE_SOURCE_ID = "route_line_source";
    private static final String ROUTE_LINE_LAYER_ID = "route_line_layer";
    private static final String ICON_LAYER_ID = "icon_layer";
    private static final String WINDOW_LAYER_ID = "window_layer";
    private static final String ROUTE_ICON_LAYER_ID = "route_icon_layer";
    private static final String IMAGE_NAME = "red_marker";
    private static final String PROPERTY_SELECTED = "selected";
    private static final String PROPERTY_NAME = "name";
    private static final String PARAGLIDING_MAP_STYLE = "mapbox://styles/davidtruji/ckr6jbe9q0vz018mxsqd9yk3a";


    // Mapbox variables
    private MapboxMap mapboxMap;
    private MapView mapView;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationChangeListeningActivityLocationCallback callback =
            new LocationChangeListeningActivityLocationCallback(this);
    private GeoJsonSource wptSource;
    private FeatureCollection wptFeatureCollection;

    @SuppressLint("WrongConstant")
    private OnMapReadyCallback onMapReadyCallback = mapboxMap -> {
        NavFragment.this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(PARAGLIDING_MAP_STYLE,
                style -> {
                    style.addImage("route_icon", BitmapFactory.decodeResource(
                            getResources(), R.drawable.flag));
                    enableLocationComponent(style);
                    setWaypointsLayer();
                });

        mapboxMap.addOnMapClickListener(point -> handleClickIcon(mapboxMap.getProjection().toScreenLocation(point)));

    };

    // Variables UI
    private NavViewModel navViewModel;
    private WaypointsViewModel waypointsViewModel;
    private TextView tvBlock1Label, tvBlock2Label, tvBlock3Label, tvBlock4Label;
    private TextViewOutline tvBlock1, tvBlock2, tvBlock3, tvBlock4;
    private Chronometer tvChronometer;
    private FloatingActionButton fabLayers, fabCompass;
    private ToggleButton tbStartFly;
    private ImageView ivCompass, ivRouteCompass;

    private ArrayList<Integer> bearingBlocks = new ArrayList<>();
    private ArrayList<Integer> speedBlocks = new ArrayList<>();
    private ArrayList<Integer> altitudeBlocks = new ArrayList<>();
    private ArrayList<Integer> distanceBlocks = new ArrayList<>();
    private ArrayList<Integer> distanceWPTBlocks = new ArrayList<>();
    private ArrayList<Integer> timeToArrivalBlocks = new ArrayList<>();


    // Variables de vuelo
    private boolean flying = false;
    private Location currentLocation = null;
    private Location prevLocation = null;
    private float distance = 0, distanceWPT = 0;
    private int minutesETA = 0;


    private ArrayList<FlightLocation> flight = new ArrayList<>();
    private float compassDegreeStart = 0f;
    private float routeCompassDegreeStart = 0f;
    private LatLng routeWaypoint;
    private List<Waypoint> waypoints = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(getContext(), getString(R.string.mapbox_access_token));

        // This contains the MapView in XML and needs to be called after the access token is configured.
        View root = inflater.inflate(R.layout.fragment_navigation, container, false);

        Toolbar toolbar = root.findViewById(R.id.nav_toolbar);
        toolbar.setTitle(getString(R.string.title_nav));
        toolbar.inflateMenu(R.menu.nav_toolbar_menu);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_settings) {
                Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(settingsIntent);
            }
            return true;
        });


        tvBlock1 = root.findViewById(R.id.tvBlock1);
        tvBlock1Label = root.findViewById(R.id.tvBlock1Label);

        tvBlock2 = root.findViewById(R.id.tvBlock2);
        tvBlock2Label = root.findViewById(R.id.tvBlock2Label);

        tvBlock3 = root.findViewById(R.id.tvBlock3);
        tvBlock3Label = root.findViewById(R.id.tvBlock3Label);

        tvBlock4 = root.findViewById(R.id.tvBlock4);
        tvBlock4Label = root.findViewById(R.id.tvBlock4Label);

        tvChronometer = root.findViewById(R.id.tvChronometer);

        ivCompass = root.findViewById(R.id.compass);
        ivRouteCompass = root.findViewById(R.id.route_compass);

        tbStartFly = root.findViewById(R.id.tbStart);
        fabLayers = root.findViewById(R.id.fabUndo);
        fabCompass = root.findViewById(R.id.fabClean);

        mapView = root.findViewById(R.id.mv_nav_map);
        mapView.onCreate(savedInstanceState);

        tbStartFly.setOnClickListener(view -> {
            if (flying) {   // Usuario pulsó STOP
                stopFly();
            } else {        // Usuario pulsó START
                startFly();
            }
        });

        fabLayers.setOnClickListener(view -> changeCurrentMapLayer());
        fabCompass.setOnClickListener(view -> changeCameraMode());


        waypointsViewModel = new ViewModelProvider(getActivity()).get(WaypointsViewModel.class);
        navViewModel = new ViewModelProvider(getActivity()).get(NavViewModel.class);

        waypointsViewModel.getAllWaypoints().observe(getViewLifecycleOwner(), waypoints -> {
            NavFragment.this.waypoints = waypoints;
            mapView.getMapAsync(onMapReadyCallback);
        });

        navViewModel.getSelectedWaypoint().observe(getViewLifecycleOwner(), waypoint -> {
            this.routeWaypoint = waypoint;
            resetRouteCompass();
            setRouteLine();
            setRoute();
            ivRouteCompass.setVisibility(View.VISIBLE);
        });

        setupNavigationPrecefences();


        return root;
    }


    /**
     * AsyncTask to generate Bitmap from Views to be used as iconImage in a SymbolLayer.
     * <p>
     * Call be optionally be called to update the underlying data source after execution.
     * <p>
     * <p>
     * Generating Views on background thread since we are not going to be adding them to the view hierarchy.
     */
    private static class GenerateViewIconTask extends AsyncTask<FeatureCollection, Void, HashMap<String, Bitmap>> {

        private final HashMap<String, View> viewMap = new HashMap<>();
        private final WeakReference<NavFragment> activityRef;
        private final boolean refreshSource;

        GenerateViewIconTask(NavFragment activity, boolean refreshSource) {
            this.activityRef = new WeakReference<>(activity);
            this.refreshSource = refreshSource;
        }

        GenerateViewIconTask(NavFragment activity) {
            this(activity, false);
        }

        @SuppressWarnings("WrongThread")
        @Override
        protected HashMap<String, Bitmap> doInBackground(FeatureCollection... params) {
            NavFragment activity = activityRef.get();
            if (activity != null) {
                HashMap<String, Bitmap> imagesMap = new HashMap<>();
                LayoutInflater inflater = LayoutInflater.from(activity.getContext());

                FeatureCollection featureCollection = params[0];

                for (Feature feature : featureCollection.features()) {

                    LinearLayout bubbleLayout = (LinearLayout) inflater.inflate(R.layout.symbol_layer_info_window, null);

                    String name = feature.getStringProperty(PROPERTY_NAME);
                    TextView titleTextView = bubbleLayout.findViewById(R.id.tv_iw_name);
                    titleTextView.setText(feature.getStringProperty(PROPERTY_NAME));


                    int measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                    bubbleLayout.measure(measureSpec, measureSpec);

                    Bitmap bitmap = SymbolGenerator.generate(bubbleLayout);
                    imagesMap.put(name, bitmap);
                    viewMap.put(name, bubbleLayout);
                }

                return imagesMap;
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(HashMap<String, Bitmap> bitmapHashMap) {
            super.onPostExecute(bitmapHashMap);
            NavFragment activity = activityRef.get();
            if (activity != null && bitmapHashMap != null) {
                activity.setImageGenResults(bitmapHashMap);
                if (refreshSource) {
                    activity.refreshSource();
                }
            }
        }
    }

    /**
     * Utility class to generate Bitmaps for Symbol.
     */
    private static class SymbolGenerator {

        /**
         * Generate a Bitmap from an Android SDK View.
         *
         * @param view the View to be drawn to a Bitmap
         * @return the generated bitmap
         */
        static Bitmap generate(@NonNull View view) {
            int measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            view.measure(measureSpec, measureSpec);

            int measuredWidth = view.getMeasuredWidth();
            int measuredHeight = view.getMeasuredHeight();

            view.layout(0, 0, measuredWidth, measuredHeight);
            Bitmap bitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
            bitmap.eraseColor(Color.TRANSPARENT);
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);
            return bitmap;
        }
    }


    /**
     * This method handles click events for SymbolLayer symbols.
     * <p>
     * When a SymbolLayer icon is clicked, we moved that feature to the selected state.
     * </p>
     *
     * @param screenPoint the point on screen clicked
     */
    private boolean handleClickIcon(PointF screenPoint) {
        List<Feature> features = mapboxMap.queryRenderedFeatures(screenPoint, ICON_LAYER_ID);
        if (!features.isEmpty()) {
            String name = features.get(0).getStringProperty(PROPERTY_NAME);
            List<Feature> featureList = wptFeatureCollection.features();
            if (featureList != null) {
                for (int i = 0; i < featureList.size(); i++) {
                    if (featureList.get(i).getStringProperty(PROPERTY_NAME).equals(name)) {
                        if (featureSelectStatus(i)) {
                            setFeatureSelectState(featureList.get(i), false);
                        } else {
                            setSelected(i);
                        }
                    }
                }
            }
            return true;
        } else {
            unselectAllWaypoints();
            return false;
        }
    }


    public void setWaypointsLayer() {

        List<Feature> symbolLayerIconFeatureList = new ArrayList<>();

        for (Waypoint waypoint : waypoints) {
            Feature feature = Feature.fromGeometry(Point.fromLngLat(waypoint.getLongitude(), waypoint.getLatitude()));
            feature.addStringProperty(PROPERTY_NAME, waypoint.getWaypointName());
            feature.addBooleanProperty(PROPERTY_SELECTED, false);
            symbolLayerIconFeatureList.add(feature);
        }

        wptFeatureCollection = FeatureCollection.fromFeatures(symbolLayerIconFeatureList);


        mapboxMap.getStyle(style -> {
            if (style.getLayer(ICON_LAYER_ID) != null) {
                style.removeLayer(ICON_LAYER_ID);
                style.removeSource(WPT_SOURCE_ID);

            } else {

                style.addImage(IMAGE_NAME, BitmapFactory.decodeResource(
                        NavFragment.this.getResources(), R.drawable.mapbox_marker_icon_default));

            }


            wptSource = new GeoJsonSource(WPT_SOURCE_ID, wptFeatureCollection);

            style.addSource(wptSource);

            style.addLayer(new SymbolLayer(ICON_LAYER_ID, WPT_SOURCE_ID)
                    .withProperties(
                            iconImage(IMAGE_NAME),
                            iconAllowOverlap(true),
                            iconIgnorePlacement(true)
                    ));

            style.addLayer(new SymbolLayer(WINDOW_LAYER_ID, WPT_SOURCE_ID)
                    .withProperties(
                            /* show image with id title based on the value of the name feature property */
                            iconImage("{name}"),

                            /* set anchor of icon to bottom-left */
                            iconAnchor(ICON_ANCHOR_BOTTOM),

                            /* all info window and marker image to appear at the same time*/
                            iconAllowOverlap(true),

                            /* offset the info window to be above the marker */
                            iconOffset(new Float[]{0f, -30f})
                    )
                    /* add a filter to show only when selected feature property is true */
                    .withFilter(eq((get(PROPERTY_SELECTED)), literal(true))));

            new GenerateViewIconTask(this).execute(wptFeatureCollection);


        });


    }

    /**
     * Set a feature selected state.
     *
     * @param index the index of selected feature
     */
    private void setSelected(int index) {
        if (wptFeatureCollection.features() != null) {
            Feature feature = wptFeatureCollection.features().get(index);
            setFeatureSelectState(feature, true);
            refreshSource();
        }
    }


    private void unselectAllWaypoints() {
        for (Feature feature : wptFeatureCollection.features()) {
            setFeatureSelectState(feature, false);
        }
        refreshSource();
    }


    /**
     * Selects the state of a feature
     *
     * @param feature the feature to be selected.
     */
    private void setFeatureSelectState(Feature feature, boolean selectedState) {
        if (feature.properties() != null) {
            feature.properties().addProperty(PROPERTY_SELECTED, selectedState);
            refreshSource();
        }
    }

    /**
     * Checks whether a Feature's boolean "selected" property is true or false
     *
     * @param index the specific Feature's index position in the FeatureCollection's list of Features.
     * @return true if "selected" is true. False if the boolean property is false.
     */
    private boolean featureSelectStatus(int index) {
        if (wptFeatureCollection == null) {
            return false;
        }
        return wptFeatureCollection.features().get(index).getBooleanProperty(PROPERTY_SELECTED);
    }

    /**
     * Invoked when the bitmaps have been generated from a view.
     */
    public void setImageGenResults(HashMap<String, Bitmap> imageMap) {
        if (mapboxMap != null) {
            mapboxMap.getStyle(style -> {
                // calling addImages is faster as separate addImage calls for each bitmap.
                style.addImages(imageMap);
            });
        }
    }


    /**
     * Updates the display of data on the map after the FeatureCollection has been modified
     */
    private void refreshSource() {
        if (wptSource != null && wptFeatureCollection != null) {
            wptSource.setGeoJson(wptFeatureCollection);
        }
    }


    /**
     * Initialize the Maps SDK's LocationComponent
     */
    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(getContext())) {


            LocationComponentOptions customLocationComponentOptions = LocationComponentOptions.builder(getContext())
                    .elevation(5)
                    .compassAnimationEnabled(true)
                    .bearingDrawable(R.drawable.location)
                    .build();

            // Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();


            // Set the LocationComponent activation options
            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(getContext(), loadedMapStyle)
                            .useDefaultLocationEngine(false)
                            .locationComponentOptions(customLocationComponentOptions)
                            .build();

            // Activate with the LocationComponentActivationOptions object
            locationComponent.activateLocationComponent(locationComponentActivationOptions);


            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Modo por defecto de la cámara al iniciar la app
            locationComponent.setCameraMode(CameraMode.TRACKING_GPS_NORTH);
            locationComponent.zoomWhileTracking(13);
            locationComponent.tiltWhileTracking(0);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);

            // Añado el Listener que vigila el estado de la cámara
            locationComponent.addOnCameraTrackingChangedListener(this);

            locationComponent.getCompassEngine().addCompassListener(this);


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

                if (location == null)
                    return;


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

        currentLocation = lastLocation;
        navViewModel.setLastLocation(lastLocation);


        if (flying) {
            updateDistance(lastLocation); // Distancia del vuelo (Km)
            flight.add(new FlightLocation(lastLocation));
        }

        updateAltitude((int) lastLocation.getAltitude());// meters
        updateSpeed((int) (lastLocation.getSpeed() * 3.6));// m/s to Km/h
        updateDistance(distance);// Km

        setRouteLine();
        updateRouteInfo();

        updateDistanceWPT(distanceWPT / 1000);
        updateTimeToArrival(minutesETA);


    }


    private void updateRouteInfo() {
        if (navViewModel.isWaypointSelected() && currentLocation != null) {

            Location waypointLocation = new Location("waypointLocation");

            waypointLocation.setLongitude(routeWaypoint.getLongitude());
            waypointLocation.setLatitude(routeWaypoint.getLatitude());

            distanceWPT = currentLocation.distanceTo(waypointLocation);
            minutesETA = (int) ((distanceWPT / currentLocation.getSpeed()) / 60);

        }
    }

    private void resetOnScreenInfo() {
        distance = 0;
//        tvDistance.setText(getString(R.string.default_distance));
//        tvSpeed.setText(getString(R.string.default_speed));
//        tvAltitude.setText(getString(R.string.default_altitude));
//        tvBearing.setText(getString(R.string.default_bearing));
    }

    private void updateDistance(Location lastLocation) {

        if (prevLocation == null) {
            distance = 0;
        } else {
            distance += prevLocation.distanceTo(lastLocation) / 1000;
        }
        prevLocation = lastLocation;
    }

    private void startFly() {
        flight.clear();
        mapboxMap.getGesturesManager().getMoveGestureDetector().setEnabled(false);
        mapboxMap.getLocationComponent().setCameraMode(CameraMode.TRACKING_COMPASS);
        ((MainActivity) getActivity()).hideSystemUI();
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// Evitar que la pantalla se apague sola
        flying = true;
        tvChronometer.setBase(SystemClock.elapsedRealtime());
        tvChronometer.start();
        Log.i(getString(R.string.debug_tag), "Vuelo Iniciado");
    }


    private void stopFly() {

        flying = false;
        mapboxMap.getGesturesManager().getMoveGestureDetector().setEnabled(true);
        resetOnScreenInfo();
        ((MainActivity) getActivity()).showSystemUI();
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// Permitir apagar la pantalla de nuevo
        tvChronometer.setBase(SystemClock.elapsedRealtime());
        tvChronometer.stop();

        Log.i(getString(R.string.debug_tag), "Vuelo finalizado: " + "distancia: " + Utils.getRouteDistance(flight) + "m" + " duracion: " + Utils.getRouteDuration(flight) / 1000 + "\"");
        saveFlight();
    }


    @SuppressLint("WrongConstant")
    private void changeCurrentMapLayer() {
        if (mapboxMap.getStyle().getUri().equalsIgnoreCase(Style.SATELLITE_STREETS))
            mapboxMap.setStyle(PARAGLIDING_MAP_STYLE);
        else
            mapboxMap.setStyle(Style.SATELLITE_STREETS);

        setWaypointsLayer();
        setRoute();

        mapboxMap.getStyle(style -> {
            for (Layer layer : style.getLayers())
                Log.i("LAYER", layer.getId());

        });
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


    public void saveFlight() {
        FlightsViewModel flightsViewModel = new ViewModelProvider(this).get(FlightsViewModel.class);
        flightsViewModel.insert(new Flight(Utils.generateFlightName(), flight));
        Utils.showSnakcbar(getView().findViewById(R.id.screenInfo_layout), "Saved Flight");
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
    public void onCompassChanged(float userHeading) {

        userHeading = (userHeading + 360) % 360;

        updateBearing(userHeading);
        updateRouteBearing(userHeading);

        // No se permite que la brujula gire mas de 180º
        if (abs(compassDegreeStart + userHeading) > 180) {
            if (compassDegreeStart < -userHeading)
                compassDegreeStart += 360;
            else
                compassDegreeStart -= 360;
        }

        Utils.rotateImage(ivCompass, compassDegreeStart, -userHeading);

        compassDegreeStart = -userHeading;

    }

    private void updateRouteBearing(float userHeading) {

        if (navViewModel.isWaypointSelected() && currentLocation != null) {

            Location dest = new Location("dest");
            dest.setLatitude(routeWaypoint.getLatitude());
            dest.setLongitude(routeWaypoint.getLongitude());
            float bearingToDest = currentLocation.bearingTo(dest);

            bearingToDest = (360 + ((bearingToDest + 360) % 360) - userHeading) % 360;

            // No se permite que la brujula gire mas de 180º
            if (abs(routeCompassDegreeStart - bearingToDest) > 180) {
                if (routeCompassDegreeStart < bearingToDest)
                    routeCompassDegreeStart += 360;
                else
                    routeCompassDegreeStart -= 360;
            }

            Utils.rotateImage(ivRouteCompass, routeCompassDegreeStart, bearingToDest);

            routeCompassDegreeStart = bearingToDest;

        }


    }

    private void resetRouteCompass() {
        ivRouteCompass.setRotation(0);
        routeCompassDegreeStart = 0f;
    }


    @Override
    public void onCompassAccuracyChange(int compassStatus) {

    }

    private void setRoute() {

        if (routeWaypoint != null && navViewModel.isWaypointSelected()) {


            GeoJsonSource geoJsonSource = new GeoJsonSource(ROUTE_ICON_SOURCE_ID,
                    Feature.fromGeometry(Point.fromLngLat(routeWaypoint.getLongitude(),
                            routeWaypoint.getLatitude())));

            mapboxMap.getStyle(style -> {

                if (style.getLayer(ROUTE_ICON_LAYER_ID) != null) {
                    style.removeLayer(ROUTE_ICON_LAYER_ID);
                    style.removeSource(ROUTE_ICON_SOURCE_ID);
                }

                style.addSource(geoJsonSource);
                style.addLayer(new SymbolLayer(ROUTE_ICON_LAYER_ID, ROUTE_ICON_SOURCE_ID)
                        .withProperties(
                                PropertyFactory.iconImage("route_icon"),
                                PropertyFactory.iconIgnorePlacement(true),
                                PropertyFactory.iconAllowOverlap(true),
                                PropertyFactory.iconAnchor(Property.ICON_ANCHOR_BOTTOM_LEFT)
                                //PropertyFactory.textField("PRUEBANDO BRO")
                                //PropertyFactory.iconSize(0.5f)
                                //iconOffset(new Float[]{0f, -30f})
                        ));
            });
        }
    }


    private void setRouteLine() {

        if (routeWaypoint != null && navViewModel.isWaypointSelected() && currentLocation != null) {

            List<Point> linePoints = new ArrayList<>();

            linePoints.add(Point.fromLngLat(currentLocation.getLongitude(), currentLocation.getLatitude()));
            linePoints.add(Point.fromLngLat(routeWaypoint.getLongitude(), routeWaypoint.getLatitude()));


            GeoJsonSource geoJsonSource = new GeoJsonSource(ROUTE_LINE_SOURCE_ID,
                    Feature.fromGeometry(LineString.fromLngLats(linePoints)));

            mapboxMap.getStyle(style -> {

                if (style.getLayer(ROUTE_LINE_LAYER_ID) != null) {
                    style.removeLayer(ROUTE_LINE_LAYER_ID);
                    style.removeSource(ROUTE_LINE_SOURCE_ID);
                }

                style.addSource(geoJsonSource);


                style.addLayerBelow(new LineLayer(ROUTE_LINE_LAYER_ID, ROUTE_LINE_SOURCE_ID)
                        .withProperties(
                                PropertyFactory.lineWidth(2f),
                                PropertyFactory.lineColor(Color.DKGRAY),
                                PropertyFactory.lineDasharray(new Float[]{3f, 1.5f})
                        ), "mapbox-location-shadow-layer");


            });
        }
    }


    private void updateBearing(float bearing) {


        String brg = Utils.degreesToBearing(bearing);
        String brg_degrees = String.format(getContext().getString(R.string.bearing_format), bearing) + "º";

        for (Integer block : bearingBlocks) {

            switch (block) {
                case 1:
                    tvBlock1.setText(brg);
                    tvBlock1Label.setText(brg_degrees);
                    break;
                case 2:
                    tvBlock2.setText(brg);
                    tvBlock2Label.setText(brg_degrees);
                    break;
                case 3:
                    tvBlock3.setText(brg);
                    tvBlock3Label.setText(brg_degrees);
                    break;
                case 4:
                    tvBlock4.setText(brg);
                    tvBlock4Label.setText(brg_degrees);
                    break;
                default:
                    break;
            }


        }

    }

    private void updateTimeToArrival(int minutes) {


        String min = String.valueOf(minutes);
        String label = getString(R.string.time_to_arrival_label);

        for (Integer block : timeToArrivalBlocks) {

            switch (block) {
                case 1:
                    tvBlock1.setText(min);
                    tvBlock1Label.setText(label);
                    break;
                case 2:
                    tvBlock2.setText(min);
                    tvBlock2Label.setText(label);
                    break;
                case 3:
                    tvBlock3.setText(min);
                    tvBlock3Label.setText(label);
                    break;
                case 4:
                    tvBlock4.setText(min);
                    tvBlock4Label.setText(label);
                    break;
                default:
                    break;
            }


        }

    }


    private void updateAltitude(int altitude) {

        String alt = String.valueOf(altitude);
        String label = getString(R.string.altitude_label);

        for (Integer block : altitudeBlocks) {
            switch (block) {
                case 1:
                    tvBlock1.setText(alt);
                    tvBlock1Label.setText(label);
                    break;
                case 2:
                    tvBlock2.setText(alt);
                    tvBlock2Label.setText(label);
                    break;
                case 3:
                    tvBlock3.setText(alt);
                    tvBlock3Label.setText(label);
                    break;
                case 4:
                    tvBlock4.setText(alt);
                    tvBlock4Label.setText(label);
                    break;
                default:
                    break;
            }

        }

    }

    private void updateSpeed(int speed) {

        String spd = String.valueOf(speed);
        String label = getString(R.string.speed_label);

        for (Integer block : speedBlocks) {
            switch (block) {
                case 1:
                    tvBlock1.setText(spd);
                    tvBlock1Label.setText(label);
                    break;
                case 2:
                    tvBlock2.setText(spd);
                    tvBlock2Label.setText(label);
                    break;
                case 3:
                    tvBlock3.setText(spd);
                    tvBlock3Label.setText(label);
                    break;
                case 4:
                    tvBlock4.setText(spd);
                    tvBlock4Label.setText(label);
                    break;
                default:
                    break;
            }

        }

    }

    private void updateDistance(float distance) {

        String dst = String.valueOf(distance);
        String label = getString(R.string.distance_label);

        for (Integer block : distanceBlocks) {
            switch (block) {
                case 1:
                    tvBlock1.setText(dst);
                    tvBlock1Label.setText(label);
                    break;
                case 2:
                    tvBlock2.setText(dst);
                    tvBlock2Label.setText(label);
                    break;
                case 3:
                    tvBlock3.setText(dst);
                    tvBlock3Label.setText(label);
                    break;
                case 4:
                    tvBlock4.setText(dst);
                    tvBlock4Label.setText(label);
                    break;
                default:
                    break;
            }

        }

    }

    private void updateDistanceWPT(float distance) {

        String dst = String.valueOf(distance);
        String label = getString(R.string.distanceWPT_label);

        for (Integer block : distanceWPTBlocks) {
            switch (block) {
                case 1:
                    tvBlock1.setText(dst);
                    tvBlock1Label.setText(label);
                    break;
                case 2:
                    tvBlock2.setText(dst);
                    tvBlock2Label.setText(label);
                    break;
                case 3:
                    tvBlock3.setText(dst);
                    tvBlock3Label.setText(label);
                    break;
                case 4:
                    tvBlock4.setText(dst);
                    tvBlock4Label.setText(label);
                    break;
                default:
                    break;
            }

        }

    }


    private void setupNavigationPrecefences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        String value = "null";

        value = sharedPreferences.getString("space1", "NULL");
        addBlockToArray(1, value);

        value = sharedPreferences.getString("space2", "NULL");
        addBlockToArray(2, value);

        value = sharedPreferences.getString("space3", "NULL");
        addBlockToArray(3, value);

        value = sharedPreferences.getString("space4", "NULL");
        addBlockToArray(4, value);

    }

    private void addBlockToArray(int block, String array) {

        switch (array) {

            case "bearing":
                bearingBlocks.add(block);
                break;
            case "altitude":
                altitudeBlocks.add(block);
                break;
            case "speed":
                speedBlocks.add(block);
                break;
            case "distance":
                distanceBlocks.add(block);
                break;
            case "distance_wpt":
                distanceWPTBlocks.add(block);
                break;
            case "time_to_arrival":
                timeToArrivalBlocks.add(block);
                break;
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
            mapboxMap.getLocationComponent().getCompassEngine().removeCompassListener(this);
        }
        mapView.onDestroy();
    }


}