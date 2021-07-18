package com.dtsoftware.paraglidinggps.ui.routes;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.dtsoftware.paraglidinggps.Flight;
import com.dtsoftware.paraglidinggps.R;
import com.dtsoftware.paraglidinggps.Route;
import com.dtsoftware.paraglidinggps.Utils;
import com.dtsoftware.paraglidinggps.ui.flights.EditFlightFragment;
import com.dtsoftware.paraglidinggps.ui.flights.FlightsViewModel;
import com.dtsoftware.paraglidinggps.ui.flights.SharedFlightViewModel;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.FillExtrusionLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillExtrusionColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillExtrusionHeight;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillExtrusionOpacity;


public class RouteDetailFragment extends Fragment {
    //TODO: acabar detalles
    private MapboxMap mapboxMap;
    private MapView mapView;
    private Route route;
    private SharedRouteViewModel sharedRouteViewModel;
    private RoutesViewModel routesViewModel;
    private TextView  tvDistance;
    private Toolbar toolbar;

    private OnMapReadyCallback onMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull MapboxMap mapboxMap) {


            mapboxMap.setStyle(Style.SATELLITE_STREETS, new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {

//                    if (mflight.getRoute().size() > 0) {
//                        GeoJsonSource geoJsonSource = Utils.getGeoJsonSourceFromRoute(mflight.getRoute());
//
//                        style.addSource(geoJsonSource);
//
//                        // Add FillExtrusion layer to map using GeoJSON data
//                        style.addLayer(new FillExtrusionLayer("course", Utils.GEO_JSON_ID).withProperties(
//                                fillExtrusionColor(Color.YELLOW),
//                                fillExtrusionOpacity(0.7f),
//                                fillExtrusionHeight(get("e"))));
//
//                        CameraPosition position = mapboxMap.getCameraForLatLngBounds(Utils.getBoundsOfRoute(mflight.getRoute()), new int[]{50, 50, 50, 50});
//
//                        mapboxMap.animateCamera(CameraUpdateFactory
//                                .newCameraPosition(position), 5000);
//                    }
                }
            });


        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Mapbox.getInstance(getContext(), getString(R.string.mapbox_access_token));

        View root = inflater.inflate(R.layout.fragment_flight_detail, container, false);

        sharedRouteViewModel = new ViewModelProvider(getActivity()).get(SharedRouteViewModel.class);
        routesViewModel = new ViewModelProvider(getActivity()).get(RoutesViewModel.class);

        setHasOptionsMenu(true);

        mapView = root.findViewById(R.id.mv_rd_map);
        mapView.onCreate(savedInstanceState);


        tvDistance = root.findViewById(R.id.tv_fd_distance);


        sharedRouteViewModel.getSelectedFlight().observe(getViewLifecycleOwner(), new Observer<Flight>() {
            @Override
            public void onChanged(Flight flight) {
               // bindFlight(flight); // ACTUALIZA LA UI
            }
        });

        toolbar = root.findViewById(R.id.rd_toolbar);
        toolbar.setTitle("Route Detail");
        toolbar.inflateMenu(R.menu.fd_toolbar_menu);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.action_delete:
                       // showDeleteDialog();
                        break;
                    case R.id.action_edit_flight:
                       // showEditFlightFragment();
                        break;
                    default:
                        break;
                }


                return true;
            }
        });


        return root;
    }

    private void bindFlight(Route route) {
        this.route = route;

        mapView.getMapAsync(onMapReadyCallback);

        toolbar.setSubtitle(route.getRouteName());

        //tvDistance.setText("Distance: " + toString(route.getDistance()) + " km");

    }


//    public void showDeleteDialog() {
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//
//        builder.setMessage("Are you sure of delete this flight?")
//                .setTitle("Delete")
//                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        flightsViewModel.deleteFlightByID(mflight.getId());
//                        getParentFragmentManager().popBackStack();
//                    }
//                })
//                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        //Do nothing
//                    }
//                });
//
//
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }

//    private void showEditFlightFragment() {
//        FragmentManager fragmentManager = getParentFragmentManager();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        EditFlightFragment editFlightFragment = new EditFlightFragment();
//        transaction.hide(RouteDetailFragment.this);
//        transaction.add(R.id.nav_host_fragment, editFlightFragment);
//        transaction.addToBackStack(null);
//        transaction.commit();
//    }


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

    }


}