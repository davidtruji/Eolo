package com.dtsoftware.paraglidinggps.ui.waypoints;

import androidx.appcompat.widget.Toolbar;
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
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class WaypointsFragment extends Fragment {

    private WaypointsViewModel waypointsViewModel;
    private FloatingActionButton fabAddWaypoint;
    private FragmentManager fragmentManager;


    public static WaypointsFragment newInstance() {
        return new WaypointsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_waypoints, container, false);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) root.findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbar.setTitle(getString(R.string.title_waypoints));

        fragmentManager = getParentFragmentManager();

        waypointsViewModel = new ViewModelProvider(getActivity()).get(WaypointsViewModel.class);

        RecyclerView recyclerView = root.findViewById(R.id.rvWaypointsList);
        fabAddWaypoint = root.findViewById(R.id.fabAddWaypoint);

        fabAddWaypoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                WaypointViewModel waypointViewModel = new ViewModelProvider(getActivity()).get(WaypointViewModel.class);
                waypointViewModel.setSelected(waypoint);


                FragmentTransaction transaction = fragmentManager.beginTransaction();
                EditWaypointFragment flightDetailFragment = new EditWaypointFragment();

                transaction.hide(WaypointsFragment.this);
                transaction.add(R.id.nav_host_fragment, flightDetailFragment);
                transaction.addToBackStack(null);
                transaction.commit();


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