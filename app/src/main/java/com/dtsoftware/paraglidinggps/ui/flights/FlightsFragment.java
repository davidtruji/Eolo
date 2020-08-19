package com.dtsoftware.paraglidinggps.ui.flights;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

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

import com.dtsoftware.paraglidinggps.Flight;
import com.dtsoftware.paraglidinggps.R;

import java.util.List;

public class FlightsFragment extends Fragment {

    private FlightsViewModel flightsViewModel;


    public static FlightsFragment newInstance() {
        return new FlightsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.flights_fragment, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.rvFlights);
        final FlightListAdapter adapter = new FlightListAdapter(root.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));

        flightsViewModel = new ViewModelProvider(this).get(FlightsViewModel.class);

        flightsViewModel.getAllWords().observe(getViewLifecycleOwner(), new Observer<List<Flight>>() {
            @Override
            public void onChanged(@Nullable final List<Flight> flights) {
                // Update the cached copy of the words in the adapter.
                adapter.setFlights(flights);//TODO: Añadir vuelitos
            }
        });


        return root;
    }


}