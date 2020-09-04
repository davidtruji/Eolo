package com.dtsoftware.paraglidinggps.ui.waypoints;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
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

import com.dtsoftware.paraglidinggps.Flight;
import com.dtsoftware.paraglidinggps.R;
import com.dtsoftware.paraglidinggps.Waypoint;
import com.dtsoftware.paraglidinggps.ui.flights.FlightDetailFragment;
import com.dtsoftware.paraglidinggps.ui.flights.FlightListAdapter;
import com.dtsoftware.paraglidinggps.ui.flights.FlightsFragment;
import com.dtsoftware.paraglidinggps.ui.flights.FlightsViewModel;
import com.dtsoftware.paraglidinggps.ui.flights.SharedFlightViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class WaypointsFragment extends Fragment {

    private WaypointsViewModel mViewModel;
    private FloatingActionButton fabAddWaypoint;

    public static WaypointsFragment newInstance() {
        return new WaypointsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.waypoints_fragment, container, false);


        WaypointsViewModel waypointsViewModel = new ViewModelProvider(getActivity()).get(WaypointsViewModel.class);

        waypointsViewModel.deleteAll();

        waypointsViewModel.insert(new Waypoint("Badajoz", 38.8861, -6.9511));
        waypointsViewModel.insert(new Waypoint("Gévora", 38.9220, -6.9374));
        waypointsViewModel.insert(new Waypoint("La Parra", 38.5238, -6.6094));
        waypointsViewModel.insert(new Waypoint("Casemiro Patiño", 38.7045, -6.9945));


        RecyclerView recyclerView = root.findViewById(R.id.rv_waypoints);
        fabAddWaypoint = root.findViewById(R.id.fab_wp_add);

        fabAddWaypoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                AddWaypointFragment addWaypointFragment = new AddWaypointFragment();

                transaction.hide(WaypointsFragment.this);

                if (addWaypointFragment.isAdded()) {
                    transaction.show(addWaypointFragment);//TODO: transiciones de fragments
                } else {
                    transaction.add(R.id.nav_host_fragment, addWaypointFragment);
                }

                transaction.addToBackStack(null);
                transaction.commit();

            }
        });



        final WaypointListAdapter adapter = new WaypointListAdapter(getContext(), new WaypointListAdapter.ClickListener() {
            @Override
            public void onItemClicked(Waypoint waypoint) {
                //TODO: Implementar detalles de waypoint
            }
        });


        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(root.getContext());
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);


        waypointsViewModel.getAllWaypoints().observe(getViewLifecycleOwner(), new Observer<List<Waypoint>>() {
            @Override
            public void onChanged(List<Waypoint> waypoints) {
                adapter.setWaypoints(waypoints);
            }
        });


        return root;
    }

}