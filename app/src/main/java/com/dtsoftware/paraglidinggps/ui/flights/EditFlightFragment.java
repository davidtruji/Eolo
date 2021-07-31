package com.dtsoftware.paraglidinggps.ui.flights;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import com.dtsoftware.paraglidinggps.Flight;
import com.dtsoftware.paraglidinggps.R;
import com.dtsoftware.paraglidinggps.Utils;


public class EditFlightFragment extends Fragment {

    private EditText et_name, et_date, et_hour, et_duration, et_distance, et_min_alt, et_max_alt, et_min_speed, et_avg_speed, et_max_speed;
    private SharedFlightViewModel sharedFlightViewModel;
    private Flight flight;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_edit_flight, container, false);

        sharedFlightViewModel = new ViewModelProvider(getActivity()).get(SharedFlightViewModel.class);


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
                        uptateFlight();
                        getParentFragmentManager().popBackStack();
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
        et_hour = root.findViewById(R.id.et_ef_hour);
        et_min_speed = root.findViewById(R.id.et_ef_minSpeed);
        et_avg_speed = root.findViewById(R.id.et_AvgSpeed);
        et_max_speed = root.findViewById(R.id.et_maxSpeed);

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


        et_hour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHourDialog();
            }
        });


        bindFlight(flight);


        return root;
    }

    private void uptateFlight() {
        flight.setLocationName(et_name.getText().toString());
        flight.setDate(Utils.StringDateToTimestamp(et_date.getText().toString()));
        flight.setDuration(Utils.StringToDuration(et_duration.getText().toString()));
        flight.setDistance(Float.parseFloat(et_distance.getText().toString()) * 1000); // todo: Convertir unidades según ajustes
        flight.setMaxAltitude(Integer.parseInt(et_max_alt.getText().toString()));
        flight.setMinAltitude(Integer.parseInt(et_min_alt.getText().toString()));
        flight.setMinSpeed(Integer.parseInt(et_min_speed.getText().toString()));
        flight.setAvgSpeed(Integer.parseInt(et_avg_speed.getText().toString()));
        flight.setMaxSpeed(Integer.parseInt(et_max_speed.getText().toString()));

        sharedFlightViewModel.updateFlight(flight);
    }

    private void bindFlight(Flight flight) {
        et_name.setText(flight.getLocationName());
        et_date.setText(flight.getDateString());
        et_duration.setText(flight.getDurationString());
        et_distance.setText(flight.getDistanceString());
        et_min_alt.setText(flight.getMinAltitudeString());
        et_max_alt.setText(flight.getMaxAltitudeString());
        et_min_speed.setText(String.valueOf(flight.getMinSpeed()));
        et_avg_speed.setText(String.valueOf(flight.getAvgSpeed()));
        et_max_speed.setText(String.valueOf(flight.getMaxSpeed()));
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

    private void showHourDialog() {

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                et_hour.setText(hourOfDay + ":" + minute);
            }
        }, 19, 0, true);
        timePickerDialog.show();

    }

    private void showDurationPicker() {
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.number_picker, null);

        final NumberPicker npHours = dialogView.findViewById(R.id.np_hours);
        final NumberPicker npMinutes = dialogView.findViewById(R.id.np_minutes);

        npHours.setMaxValue(999);
        npHours.setMinValue(0);
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