package com.dtsoftware.paraglidinggps.ui.flights;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.dtsoftware.paraglidinggps.Flight;
import com.dtsoftware.paraglidinggps.R;
import com.dtsoftware.paraglidinggps.Utils;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class EditFlightFragment extends Fragment {

    static EditText et_name, et_date, et_duration, et_distance, et_min_alt, et_max_alt;
    private SharedFlightViewModel sharedFlightViewModel;
    private FlightsViewModel flightsViewModel;
    private Flight flight;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_edit_flight, container, false);

        sharedFlightViewModel = new ViewModelProvider(getActivity()).get(SharedFlightViewModel.class);
        flightsViewModel = new ViewModelProvider(getActivity()).get(FlightsViewModel.class);


        flight = sharedFlightViewModel.getSelectedFlight().getValue();

        setHasOptionsMenu(true);

        Toolbar toolbar = root.findViewById(R.id.ef_toolbar);
        toolbar.setTitle("Edit Flight");
        toolbar.setSubtitle(flight.getLocationName());
        toolbar.inflateMenu(R.menu.ef_toolbar_menu);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.action_save:
                        //TODO: UPDATE FLIGHT
                        uptateFlight();
                        break;
                    default:
                        break;
                }

                return true;
            }
        });


        et_name = root.findViewById(R.id.et_ef_name);
        et_date = root.findViewById(R.id.et_ef_date);
        et_duration = root.findViewById(R.id.et_ef_duration);
        et_distance = root.findViewById(R.id.et_ef_distance);
        et_min_alt = root.findViewById(R.id.et_ef_min_alt);
        et_max_alt = root.findViewById(R.id.et_ef_max_alt);


        et_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog();
            }
        });


        et_duration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDurationPicker();
            }
        });


        bindFlight(flight);


        return root;
    }

    private void uptateFlight() {
        flight.setLocationName(et_name.getText().toString());
        flight.setDate(Utils.StringDateToTimestamp(et_date.getText().toString()));
        flight.setDuration(Utils.StringToDuration(et_duration.getText().toString()));
        flight.setDistance(Float.parseFloat(et_distance.getText().toString()) * 1000); // Paso de km del usuario a metros
        flight.setMaxAltitude(Integer.parseInt(et_max_alt.getText().toString()));
        flight.setMinAltitude(Integer.parseInt(et_min_alt.getText().toString()));
        flightsViewModel.updateFLight(flight);
    }

    private void bindFlight(Flight flight) {
        et_name.setText(flight.getLocationName());
        et_date.setText(flight.getDateString());
        et_duration.setText(flight.getDurationString());
        et_distance.setText(flight.getDistanceString());
        et_min_alt.setText(flight.getMinAltitudeString());
        et_max_alt.setText(flight.getMaxAltitudeString());
    }

    private void showDateDialog() {
        Calendar calendar = Calendar.getInstance();
        int yearNow = calendar.get(Calendar.YEAR);
        int monthNow = calendar.get(Calendar.MONTH);
        int dayNow = calendar.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                et_date.setText(String.format("%02d/%02d/%02d", day, month + 1, year));
            }
        }, yearNow, monthNow, dayNow);
        datePickerDialog.show();


    }

    private void showDurationPicker() {
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.number_picker, null);

        final NumberPicker npHours = dialogView.findViewById(R.id.np_hours);
        final NumberPicker npMinutes = dialogView.findViewById(R.id.np_minutes);

        npHours.setMaxValue(999);
        npHours.setMinValue(0);
        // npHours.canScrollVertically(NumberPicker.DOWN)
        npMinutes.setMaxValue(59);
        npMinutes.setMinValue(0);


        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Duration");
        builder.setMessage("hh:mm");
        builder.setView(dialogView);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                et_duration.setText(String.format(Utils.DURATION_FORMAT, npHours.getValue(), npMinutes.getValue(), 0));
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}