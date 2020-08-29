package com.dtsoftware.paraglidinggps.ui.flights;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
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
import android.widget.TextView;

import com.dtsoftware.paraglidinggps.R;
import com.dtsoftware.paraglidinggps.Utils;


public class FlightsFragment extends Fragment {

    private TextView tvHoursCount;


    //TODO: Añadir fragment de vuelo al detalle

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.flights_fragment, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.rvFlightsList);

        final FlightListAdapter adapter = new FlightListAdapter(root.getContext(), (position, v) -> {

            Log.d(getString(R.string.debug_tag), "onItemClick position: " + position);
            FlightDetailFragment flightDetailFragment= new FlightDetailFragment();
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, flightDetailFragment).commit();

        });


        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(root.getContext());
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);


        tvHoursCount = root.findViewById(R.id.tvHoursCount);


        FlightsViewModel flightsViewModel = new ViewModelProvider(this).get(FlightsViewModel.class);

        flightsViewModel.getAllFlights().observe(getViewLifecycleOwner(), flights -> {
            adapter.setFlights(flights);
            tvHoursCount.setText(Utils.getTotalFlightHours(flights).toString());
        });


        return root;
    }


}