package com.dtsoftware.paraglidinggps.ui.flights;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class FlightsFragment extends Fragment {

    private TextView tvHoursCount, tvNumberOfFlights;
    private FloatingActionButton fabAdd;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_flights_list, container, false);
        FragmentManager fragmentManager = getParentFragmentManager();

        Toolbar toolbar = root.findViewById(R.id.flights_toolbar);
        toolbar.setTitle(getString(R.string.title_flights));

        fabAdd = root.findViewById(R.id.fab_fl_add_flight);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                AddFlightFragment addFlightFragment = new AddFlightFragment();

                transaction.hide(FlightsFragment.this);
                transaction.add(R.id.nav_host_fragment, addFlightFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        FlightsViewModel flightsViewModel = new ViewModelProvider(getActivity()).get(FlightsViewModel.class);

        RecyclerView recyclerView = root.findViewById(R.id.rvFlightsList);

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

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);


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