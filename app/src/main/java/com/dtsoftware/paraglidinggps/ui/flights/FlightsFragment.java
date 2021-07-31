package com.dtsoftware.paraglidinggps.ui.flights;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.dtsoftware.paraglidinggps.R;
import com.dtsoftware.paraglidinggps.Utils;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class FlightsFragment extends Fragment {

    private TextView tvHoursCount, tvNumberOfFlights;
    private FloatingActionButton fabAdd;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_flights_beta, container, false);
        FragmentManager fragmentManager = getParentFragmentManager();

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) root.findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbar.setTitle(getString(R.string.title_flights));


        FlightsViewModel flightsViewModel = new ViewModelProvider(getActivity()).get(FlightsViewModel.class);

        RecyclerView recyclerView = root.findViewById(R.id.rvWaypointsList);

        final FlightListAdapter adapter = new FlightListAdapter(getContext(), flight -> {


            SharedFlightViewModel sharedFlightViewModel = new ViewModelProvider(getActivity()).get(SharedFlightViewModel.class);
            sharedFlightViewModel.setSelected(flight);


            FragmentTransaction transaction = fragmentManager.beginTransaction();
            FlightDetailFragment flightDetailFragment = new FlightDetailFragment();

            transaction.hide(FlightsFragment.this);

            if (flightDetailFragment.isAdded()) {
                transaction.show(flightDetailFragment);//TODO: transiciones de fragments
            } else {
                transaction.add(R.id.nav_host_fragment, flightDetailFragment);
            }

            transaction.addToBackStack(null);
            transaction.commit();

        });


        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(root.getContext());
        recyclerView.setLayoutManager(layoutManager);

        tvHoursCount = root.findViewById(R.id.tvHoursCount);
        tvNumberOfFlights = root.findViewById(R.id.tvFlightsCount);

        flightsViewModel.getAllFlights().observe(getViewLifecycleOwner(), flights -> {
            adapter.setFlights(flights);
            tvHoursCount.setText(String.format(Utils.FLIGHT_HOURS_FORMAT, Utils.getTotalFlightHours(flights)));
            tvNumberOfFlights.setText(String.valueOf(flights.size()));
        });


        return root;
    }


}
