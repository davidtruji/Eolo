package com.dtsoftware.paraglidinggps.ui.routes;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.dtsoftware.paraglidinggps.R;
import com.dtsoftware.paraglidinggps.Route;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.List;

import static com.mapbox.mapboxsdk.style.expressions.Expression.eq;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;


public class AddRouteFragment extends Fragment implements OnMapReadyCallback, MapboxMap.OnMapClickListener {


    private static final String GEOJSON_SOURCE_ID = "source-id";
    private static final String POINT_LAYER_ID = "point-layer-id";
    private static final String LINE_LAYER_ID = "line-layer-id";


    private MapView mapView;
    private MapboxMap mapboxMap;
    private LatLng currentPosition = new LatLng(0, 0);
    private List<Point> route = new ArrayList<>();
    private FeatureCollection featureCollection;
    List<Feature> routeLayerFeatureList = new ArrayList<>();
    private GeoJsonSource geoJsonSource;
    private ValueAnimator animator;
    private Float distance = 0f;

    private TextView tvDistance;
    private EditText etName;
    private FloatingActionButton fabUndo, fabClean;

    RoutesViewModel routesViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Mapbox.getInstance(getContext(), getString(R.string.mapbox_access_token));

        View root = inflater.inflate(R.layout.fragment_add_route, container, false);

        Toolbar toolbar = root.findViewById(R.id.ar_toolbar);
        toolbar.setTitle(getString(R.string.title_ar));
        toolbar.setSubtitle("Tap to add a point to the route");
        toolbar.inflateMenu(R.menu.ar_toolbar_menu);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_save) {
                routesViewModel.insert(new Route(etName.getText().toString(), distance, (ArrayList<Point>) route));//TODO: Añadir bien la ruta
                getParentFragmentManager().popBackStack();
            }
            return true;
        });
        toolbar.setNavigationOnClickListener(view -> getParentFragmentManager().popBackStack());

        setHasOptionsMenu(true);

        routesViewModel = new ViewModelProvider(getActivity()).get(RoutesViewModel.class);

        mapView = root.findViewById(R.id.mv_ar_map);
        tvDistance = root.findViewById(R.id.tv_ar_distance);
        etName = root.findViewById(R.id.et_ar_name);
        fabClean = root.findViewById(R.id.fabClean);
        fabUndo = root.findViewById(R.id.fabUndo);

        fabClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                route.clear();//TODO: Confirmación mediante dialogo
                routeLayerFeatureList.clear();
                setRoute();
                resetDistance();
            }
        });

        fabUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (route.size() > 0) {
                    route.remove(route.size() - 1);
                    routeLayerFeatureList.remove(routeLayerFeatureList.size() - 1);
                    updateDistance();
                    setRoute();
                }
            }
        });

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return root;
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(Style.SATELLITE_STREETS, style -> {
            setRoute();
            mapboxMap.addOnMapClickListener(AddRouteFragment.this);
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

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(currentPosition)
                .build();

        mapboxMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition), 5000);


        addPointToRoute(Point.fromLngLat(point.getLongitude(), point.getLatitude()));

        return true;
    }

    private final ValueAnimator.AnimatorUpdateListener animatorUpdateListener =
            new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    setRoute();
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

    /**
     * Añade la capa de puntos al recurso GEOJSON
     *
     * @param loadedMapStyle
     */
    private void addPointsLayer(@NonNull Style loadedMapStyle) {
        // Create and style a CircleLayer that uses the Point Features' coordinates in the GeoJSON data
        CircleLayer individualCirclesLayer = new CircleLayer(POINT_LAYER_ID, GEOJSON_SOURCE_ID);
        individualCirclesLayer.setProperties(
                PropertyFactory.circleColor(Color.RED),
                PropertyFactory.circleRadius(5f),
                PropertyFactory.circleStrokeWidth(1f),
                PropertyFactory.circleStrokeColor(Color.WHITE));
        individualCirclesLayer.setFilter(eq(literal("$type"), literal("Point")));
        loadedMapStyle.addLayer(individualCirclesLayer);
    }

    /**
     * Añade la capa de lineas al recurso GEOJSON
     *
     * @param loadedMapStyle
     */
    private void addLinesLayer(@NonNull Style loadedMapStyle) {
        // Create and style a CircleLayer that uses the Point Features' coordinates in the GeoJSON data
        LineLayer individualLineLayer = new LineLayer(LINE_LAYER_ID, GEOJSON_SOURCE_ID);
        individualLineLayer.setProperties(
                PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                PropertyFactory.lineWidth(3f),
                PropertyFactory.lineColor(Color.RED));
        individualLineLayer.setFilter(eq(literal("$type"), literal("LineString")));
        loadedMapStyle.addLayer(individualLineLayer);
    }


    private void addPointToRoute(Point p) {
        route.add(p);

        routeLayerFeatureList.clear();

        //Añado la linea
        if (route.size() > 1)
            routeLayerFeatureList.add(Feature.fromGeometry(LineString.fromLngLats(route)));

        // Añado todos los puntos de la ruta
        for (Point point : route)
            routeLayerFeatureList.add(Feature.fromGeometry(point));

        updateDistance();


    }


    /**
     * Añade las capas al estilo del mapa y actualiza el recurso GEOJSON
     */
    private void setRoute() {

        featureCollection = FeatureCollection.fromFeatures(routeLayerFeatureList);

        geoJsonSource = new GeoJsonSource(GEOJSON_SOURCE_ID, featureCollection);

        mapboxMap.getStyle(style -> {

            if (style.getLayer(POINT_LAYER_ID) != null) {
                style.removeLayer(POINT_LAYER_ID);
                style.removeLayer(LINE_LAYER_ID);
                style.removeSource(GEOJSON_SOURCE_ID);
            }
            style.addSource(geoJsonSource);

            addLinesLayer(style);
            addPointsLayer(style);

        });


    }

    private void updateDistance() {
        if (route.size() > 1) {

            distance = 0f;
            float[] results = new float[1];
            Point prevPoint, currentPoint;

            for (int i = 0; i < route.size() - 1; i++) {

                prevPoint = route.get(i);
                currentPoint = route.get(i + 1);
                Location.distanceBetween(prevPoint.latitude(), prevPoint.longitude(), currentPoint.latitude(), currentPoint.longitude(), results);
                distance += results[0] / 1000;

            }

            tvDistance.setText(String.format(getString(R.string.distance_format), distance));
        }else{
            resetDistance();
        }

    }

    private void resetDistance() {
        distance = 0f;
        tvDistance.setText(String.format(getString(R.string.distance_format), distance));
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