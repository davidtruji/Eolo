package com.dtsoftware.paraglidinggps.ui.routes;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

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
import com.dtsoftware.paraglidinggps.Utils;
import com.dtsoftware.paraglidinggps.ui.flights.AddFlightFragment;
import com.dtsoftware.paraglidinggps.ui.flights.FlightDetailFragment;
import com.dtsoftware.paraglidinggps.ui.flights.FlightListAdapter;
import com.dtsoftware.paraglidinggps.ui.flights.FlightsFragment;
import com.dtsoftware.paraglidinggps.ui.flights.FlightsViewModel;
import com.dtsoftware.paraglidinggps.ui.flights.SharedFlightViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class RoutesFragment extends Fragment {

    private RoutesViewModel mViewModel;
    private FloatingActionButton fabAdd;

    public static RoutesFragment newInstance() {
        return new RoutesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.routes_fragment, container, false);
        FragmentManager fragmentManager = getParentFragmentManager();

        Toolbar toolbar = root.findViewById(R.id.routes_toolbar);
        toolbar.setTitle(getString(R.string.title_routes));

        fabAdd = root.findViewById(R.id.fab_add_route);
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


        RoutesViewModel routesViewModel = new ViewModelProvider(getActivity()).get(RoutesViewModel.class);

        RecyclerView recyclerView = root.findViewById(R.id.rvRoutesList);

        final RouteListAdapter adapter = new RouteListAdapter(getContext(), route -> {

            Log.d(getString(R.string.debug_tag), "onItemClick: " + route.getRouteName());

//            SharedFlightViewModel sharedFlightViewModel = new ViewModelProvider(getActivity()).get(SharedFlightViewModel.class);
////            sharedFlightViewModel.setSelected(flight);//TODO: SharedViewModel


            FragmentTransaction transaction = fragmentManager.beginTransaction();
            //FlightDetailFragment flightDetailFragment = new FlightDetailFragment();//TODO: RouteDetailFragment

            transaction.hide(RoutesFragment.this);

//            if (flightDetailFragment.isAdded()) {
//                transaction.show(flightDetailFragment);//TODO: transiciones de fragments
//            } else {
//                transaction.add(R.id.nav_host_fragment, flightDetailFragment);
//            }//TODO: Implementar navegación a detalles de ruta...

            transaction.addToBackStack(null);
            transaction.commit();

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

        return root;
    }


}