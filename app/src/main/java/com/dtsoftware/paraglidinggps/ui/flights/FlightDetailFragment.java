package com.dtsoftware.paraglidinggps.ui.flights;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.dtsoftware.paraglidinggps.R;


public class FlightDetailFragment extends Fragment {


    public FlightDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_flight_detail, container, false);


        TextView tv_flightName = root.findViewById(R.id.tv_flight_detail_name);

        //FragmentManager fm = getActivity().getSupportFragmentManager();
        SharedFlightViewModel sharedFlightViewModel = new ViewModelProvider(getActivity()).get(SharedFlightViewModel.class);
        tv_flightName.setText(sharedFlightViewModel.getSelectedFlight().getValue().getLocationName());


        return root;
    }









}