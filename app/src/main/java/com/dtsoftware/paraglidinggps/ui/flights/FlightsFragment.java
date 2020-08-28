package com.dtsoftware.paraglidinggps.ui.flights;

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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dtsoftware.paraglidinggps.Flight;
import com.dtsoftware.paraglidinggps.R;
import com.dtsoftware.paraglidinggps.Utils;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class FlightsFragment extends Fragment {

    private FlightsViewModel flightsViewModel;
private  TextView tvHoursCount;

    public static FlightsFragment newInstance() {
        return new FlightsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.flights_fragment, container, false);






        RecyclerView recyclerView = root.findViewById(R.id.rvFlightsList);
        final FlightListAdapter adapter = new FlightListAdapter(root.getContext());
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(root.getContext());
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);


        tvHoursCount = root.findViewById(R.id.tvHoursCount);


        flightsViewModel = new ViewModelProvider(this).get(FlightsViewModel.class);

        flightsViewModel.getAllWords().observe(getViewLifecycleOwner(), new Observer<List<Flight>>() {
            @Override
            public void onChanged(@Nullable final List<Flight> flights) {
                // Update the cached copy of the words in the adapter.
                adapter.setFlights(flights);//TODO: Añadir vuelitos
                 tvHoursCount.setText(Utils.getTotalFlightHours(flights).toString());
            }
        });


        return root;
    }


}