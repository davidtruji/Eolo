package com.dtsoftware.paraglidinggps.ui.flights;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dtsoftware.paraglidinggps.R;
import com.dtsoftware.paraglidinggps.Utils;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.Locale;


public class FlightsFragment extends Fragment {

    private TextView tvHoursCount, tvNumberOfFlights;
    private String distanceUnit;


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_flights, container, false);
        FragmentManager fragmentManager = getParentFragmentManager();

        CollapsingToolbarLayout collapsingToolbar =
                root.findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbar.setTitle(getString(R.string.title_flights));


        FlightsViewModel flightsViewModel = new ViewModelProvider(getActivity()).get(FlightsViewModel.class);

        RecyclerView recyclerView = root.findViewById(R.id.rvWaypointsList);

        setupSharedPreferences();

        final FlightListAdapter adapter = new FlightListAdapter(getContext(), distanceUnit, flight -> {


            SharedFlightViewModel sharedFlightViewModel = new ViewModelProvider(getActivity()).get(SharedFlightViewModel.class);
            sharedFlightViewModel.setSelected(flight);


            FragmentTransaction transaction = fragmentManager.beginTransaction();
            FlightDetailFragment flightDetailFragment = new FlightDetailFragment();

            transaction.hide(FlightsFragment.this);

            if (flightDetailFragment.isAdded()) {
                transaction.show(flightDetailFragment);
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
            tvHoursCount.setText(String.format(Locale.US, Utils.FLIGHT_HOURS_FORMAT, Utils.getTotalFlightHours(flights)));
            tvNumberOfFlights.setText(String.valueOf(flights.size()));
        });


        return root;
    }


    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        String value = sharedPreferences.getString(getString(R.string.distance_unit_key), "NULL");
        distanceUnit = value;
    }


}
