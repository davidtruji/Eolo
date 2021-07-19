package com.dtsoftware.paraglidinggps.ui.routes;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dtsoftware.paraglidinggps.R;
import com.dtsoftware.paraglidinggps.Route;
import com.dtsoftware.paraglidinggps.Utils;
import com.dtsoftware.paraglidinggps.ui.flights.SharedFlightViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.List;

import static com.dtsoftware.paraglidinggps.Utils.GEO_JSON_ID;

public class RoutesFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private MapboxMap mapboxMap;
    private RoutesViewModel mViewModel;
    private Route selectedRoute;
    private FloatingActionButton fabAdd,fabEdit;
    SharedRouteViewModel sharedRouteViewModel;


    public static RoutesFragment newInstance() {
        return new RoutesFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        Mapbox.getInstance(getContext(), getString(R.string.mapbox_access_token));

        View root = inflater.inflate(R.layout.fragment_routes_list, container, false);
        FragmentManager fragmentManager = getParentFragmentManager();

        SharedRouteViewModel sharedRouteViewModel = new ViewModelProvider(getActivity()).get(SharedRouteViewModel.class);
        RoutesViewModel routesViewModel = new ViewModelProvider(getActivity()).get(RoutesViewModel.class);

        Toolbar toolbar = root.findViewById(R.id.routes_toolbar);
        toolbar.setTitle(getString(R.string.title_routes));
        toolbar.setSubtitle("Tap a route to see in the map");

        fabAdd = root.findViewById(R.id.fab_add_route);
        fabEdit = root.findViewById(R.id.fab_edit_route);
        mapView = root.findViewById(R.id.mv_rd_map);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentTransaction transaction = fragmentManager.beginTransaction();
                AddRouteFragment addRouteFragment = new AddRouteFragment();
                transaction.hide(RoutesFragment.this);
                transaction.add(R.id.nav_host_fragment, addRouteFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sharedRouteViewModel.setSelected(selectedRoute);

                FragmentTransaction transaction = fragmentManager.beginTransaction();
                EditRouteFragment editRouteFragment = new EditRouteFragment();
                transaction.hide(RoutesFragment.this);
                transaction.add(R.id.nav_host_fragment, editRouteFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });



        RecyclerView recyclerView = root.findViewById(R.id.rvRoutesList);

        final RouteListAdapter adapter = new RouteListAdapter(getContext(), route -> {

            selectedRoute = route;
            toolbar.setSubtitle(route.getRouteName());
            mapView.getMapAsync(this);
            fabEdit.setVisibility(View.VISIBLE);

        });

        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(root.getContext());
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);


        routesViewModel.getAllRoutes().observe(getViewLifecycleOwner(), routes -> {
            adapter.setRoutes(routes);
        });


        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        return root;
    }


    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.SATELLITE_STREETS, style -> {
            if (selectedRoute != null) {
                setRoute();
                LatLng init = new LatLng(selectedRoute.getRoute().get(0).latitude(), selectedRoute.getRoute().get(0).longitude());
                mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(init, 10), 5000);
            }

        });
    }


    /**
     * Añade las capas al estilo del mapa y actualiza el recurso GEOJSON
     */
    private void setRoute() {

        List<Feature> routeLayerFeatureList = new ArrayList<>();

        //Añado la linea
        if (selectedRoute.getRoute().size() > 1)
            routeLayerFeatureList.add(Feature.fromGeometry(LineString.fromLngLats(selectedRoute.getRoute())));

        // Añado todos los puntos de la ruta
        for (Point point : selectedRoute.getRoute())
            routeLayerFeatureList.add(Feature.fromGeometry(point));


        FeatureCollection featureCollection = FeatureCollection.fromFeatures(routeLayerFeatureList);

        GeoJsonSource geoJsonSource = new GeoJsonSource(GEO_JSON_ID, featureCollection);

        mapboxMap.getStyle(style -> {

            if (style.getLayer(Utils.POINT_LAYER_ID) != null) {
                style.removeLayer(Utils.POINT_LAYER_ID);
                style.removeLayer(Utils.LINE_LAYER_ID);
                style.removeSource(GEO_JSON_ID);
            }
            style.addSource(geoJsonSource);

            Utils.addRouteLayersToMap(style);

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
        mapView.onDestroy();
    }
}