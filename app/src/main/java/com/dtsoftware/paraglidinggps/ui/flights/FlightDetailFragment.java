package com.dtsoftware.paraglidinggps.ui.flights;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dtsoftware.paraglidinggps.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FlightDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FlightDetailFragment extends Fragment {


    private TextView tv_flightName;

    public FlightDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_flight_detail, container, false);


        tv_flightName = root.findViewById(R.id.tv_flight_detail_name);

        //FragmentManager fm = getActivity().getSupportFragmentManager();
        SharedFlightViewModel sharedFlightViewModel = new ViewModelProvider(getActivity()).get(SharedFlightViewModel.class);
        tv_flightName.setText(sharedFlightViewModel.getSelectedFlight().getValue().getLocationName());


        return root;
    }

}