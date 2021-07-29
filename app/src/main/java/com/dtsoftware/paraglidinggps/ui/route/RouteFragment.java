package com.dtsoftware.paraglidinggps.ui.route;

import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dtsoftware.paraglidinggps.R;
import com.dtsoftware.paraglidinggps.Utils;
import com.dtsoftware.paraglidinggps.ui.nav.NavViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import static com.dtsoftware.paraglidinggps.Utils.GEO_JSON_ID;

public class RouteFragment extends Fragment implements OnMapReadyCallback, MapboxMap.OnMapClickListener {

    private MapView mapView;
    private MapboxMap mapboxMap;
    private LatLng currentPosition;
    private Button btnSetRoute, btnRemoveRoute;
    private NavViewModel navViewModel;
    private RouteViewModel routeViewModel;
    private RecyclerView recyclerView;
    private RouteWaypointsAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        Mapbox.getInstance(getContext(), getString(R.string.mapbox_access_token));

        View root = inflater.inflate(R.layout.fragment_route, container, false);

        routeViewModel = new ViewModelProvider(getActivity()).get(RouteViewModel.class);
        navViewModel = new ViewModelProvider(getActivity()).get(NavViewModel.class);

        Toolbar toolbar = root.findViewById(R.id.routes_toolbar);
        toolbar.setTitle(getString(R.string.title_route));
        toolbar.setSubtitle("Select a waypoint or tap the map");
        toolbar.inflateMenu(R.menu.routes_toolbar_menu);

        btnSetRoute = root.findViewById(R.id.btn_set);
        btnRemoveRoute = root.findViewById(R.id.bnt_remove);
        mapView = root.findViewById(R.id.mv_route_map);

        btnSetRoute.setOnClickListener(view -> {
            routeViewModel.setSelectedWaypoint(currentPosition);
            Utils.showSnakcbar(getView().findViewById(R.id.coordinatorLy), "Established route");
        });

        btnRemoveRoute.setOnClickListener(view -> {
            routeViewModel.setSelectedWaypoint(null);
            Utils.showSnakcbar(getView().findViewById(R.id.coordinatorLy), "The route has been deleted");
        });


        recyclerView = root.findViewById(R.id.rvWaypointsList);

        adapter = new RouteWaypointsAdapter(getContext(), (waypoint, position) -> {
            adapter.setSelectedItem(position);
            LatLng point = new LatLng(waypoint.getLatitude(), waypoint.getLongitude());
            currentPosition = point;
            setWaypointLayer();
            setCameraPosition(point, 15);
            btnSetRoute.setEnabled(true);
        }


        );

        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(root.getContext());
        recyclerView.setLayoutManager(layoutManager);

        routeViewModel.getWaypoints().observe(getViewLifecycleOwner(), waypoints -> adapter.setWaypoints(waypoints));
        routeViewModel.getSelectedWaypoint().observe(getViewLifecycleOwner(), latLng -> {

            if (latLng != null) {
                currentPosition = latLng;
                btnRemoveRoute.setEnabled(true);
            } else {
                btnRemoveRoute.setEnabled(false);
                btnSetRoute.setEnabled(false);
                if (currentPosition != null) {
                    currentPosition = null;
                    removeRoute();
                }

            }

        });


        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return root;
    }


    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.addOnMapClickListener(RouteFragment.this);
        mapboxMap.setStyle(Style.SATELLITE_STREETS);

        if (currentPosition != null) {
            setWaypointLayer();
            setCameraPosition(currentPosition, 10);
        } else {
            Location location = navViewModel.getLastLocation().getValue();
            if (location != null)
                setCameraPosition(new LatLng(location.getLatitude(), location.getLongitude()), 10);
        }
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        btnSetRoute.setEnabled(true);
        currentPosition = point;
        setWaypointLayer();
        setCameraPosition(point, mapboxMap.getCameraPosition().zoom);
        return true;
    }

    private void setWaypointLayer() {

        GeoJsonSource geoJsonSource = new GeoJsonSource(GEO_JSON_ID,
                Feature.fromGeometry(Point.fromLngLat(currentPosition.getLongitude(),
                        currentPosition.getLatitude())));

        mapboxMap.setStyle(Style.SATELLITE_STREETS, style -> {

            style.addImage("marker_icon", BitmapFactory.decodeResource(
                    getResources(), R.drawable.flag));

            style.addSource(geoJsonSource);

            style.addLayer(new SymbolLayer("layer-id", GEO_JSON_ID)
                    .withProperties(
                            PropertyFactory.iconImage("marker_icon"),
                            PropertyFactory.iconIgnorePlacement(true),
                            PropertyFactory.iconAllowOverlap(true),
                            PropertyFactory.iconAnchor(Property.ICON_ANCHOR_BOTTOM_LEFT)
                    ));
        });
    }

    private void setCameraPosition(LatLng position, double zoom) {

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(position)
                .zoom(zoom)
                .build();

        mapboxMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition), 3000);
    }

    private void removeRoute() {
        if (mapboxMap != null) {
            mapboxMap.setStyle(Style.SATELLITE_STREETS);
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
        mapView.onDestroy();
    }

}