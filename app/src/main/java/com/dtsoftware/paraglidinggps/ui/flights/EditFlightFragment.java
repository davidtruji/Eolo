package com.dtsoftware.paraglidinggps.ui.flights;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.dtsoftware.paraglidinggps.Flight;
import com.dtsoftware.paraglidinggps.R;
import com.dtsoftware.paraglidinggps.Utils;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;


public class EditFlightFragment extends Fragment {

    private EditText et_name, et_date, et_hour, et_duration, et_distance, et_min_alt, et_max_alt, et_min_speed, et_avg_speed, et_max_speed;
    private TextInputLayout tilName, tilDistance, tilMinAltitude, tilMaxAltitude, tilMinSpeed, tilAvgSpeed, tilMaxSpeed;
    private SharedFlightViewModel sharedFlightViewModel;
    private Flight flight;
    private String distanceUnit, altitudeUnit, speedUnit;


    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() < 1)
                tilName.setError("Name required");
            else
                tilName.setErrorEnabled(false);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };


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
        toolbar.setNavigationOnClickListener(view -> getParentFragmentManager().popBackStack());
        toolbar.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.action_save && validateFlight()) {
                uptateFlight();
                getParentFragmentManager().popBackStack();
            }

            return true;
        });

        tilName = root.findViewById(R.id.tilName);
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

        tilMinAltitude = root.findViewById(R.id.tilMinAlt);
        tilMaxAltitude = root.findViewById(R.id.tilMaxAlt);
        tilDistance = root.findViewById(R.id.tilDistance);
        tilMinSpeed = root.findViewById(R.id.tilMinSpeed);
        tilAvgSpeed = root.findViewById(R.id.tilAvgSpeed);
        tilMaxSpeed = root.findViewById(R.id.tilMaxSpeed);

        et_name.addTextChangedListener(textWatcher);

        et_date.setOnClickListener(view -> showDateDialog());


        et_duration.setOnClickListener(view -> showDurationPicker());


        et_hour.setOnClickListener(v -> showHourDialog());

        setupSharedPreferences();

        bindFlight(flight);

        return root;
    }

    private void uptateFlight() {

        flight.setLocationName(et_name.getText().toString());

        String hourDate = et_hour.getText().toString() + et_date.getText().toString();
        flight.setDate(Utils.hourDateStringToTimestamp(hourDate));
        flight.setDuration(Utils.StringToDuration(et_duration.getText().toString()));

        if (et_distance.getText().length() > 0) {
            switch (distanceUnit) {
                // Distance
                case "km":
                    flight.setDistance(Utils.kmToMeters(Float.parseFloat(et_distance.getText().toString())));
                    break;
                case "mi":
                    flight.setDistance(Utils.miToMeters(Float.parseFloat(et_distance.getText().toString())));
                    break;
                case "nm":
                    flight.setDistance(Utils.nmToMeters(Float.parseFloat(et_distance.getText().toString())));
                    break;
            }
        } else
            flight.setDistance(0);


        if (et_max_alt.getText().length() > 0) {
            switch (altitudeUnit) {
                // Altitude
                case "m":
                    flight.setMaxAltitude(Integer.parseInt(et_max_alt.getText().toString()));
                    break;
                case "ft":
                    flight.setMaxAltitude((int) Utils.ftToMeters(Integer.parseInt(et_max_alt.getText().toString())));
                    break;
            }
        } else
            flight.setMaxAltitude(0);


        if (et_min_alt.getText().length() > 0) {
            switch (altitudeUnit) {
                // Altitude
                case "m":
                    flight.setMinAltitude(Integer.parseInt(et_min_alt.getText().toString()));
                    break;
                case "ft":
                    flight.setMinAltitude((int) Utils.ftToMeters(Integer.parseInt(et_min_alt.getText().toString())));
                    break;
            }
        } else
            flight.setMinAltitude(0);


        if (et_min_speed.getText().length() > 0) {
            switch (speedUnit) {
                // Speed
                case "ms":
                    flight.setMinSpeed(Integer.parseInt(et_min_speed.getText().toString()));
                    break;
                case "kmh":
                    flight.setMinSpeed((int) Utils.kmhToMetersPerSecond(Integer.parseInt(et_min_speed.getText().toString())));
                    break;
                case "mph":
                    flight.setMinSpeed((int) Utils.mphToMetersPerSecond(Integer.parseInt(et_min_speed.getText().toString())));
                    break;
                case "kt":
                    flight.setMinSpeed((int) Utils.ktToMetersPerSecond(Integer.parseInt(et_min_speed.getText().toString())));
                    break;
            }
        } else
            flight.setMinSpeed(0);


        if (et_avg_speed.getText().length() > 0) {
            switch (speedUnit) {
                // Speed
                case "ms":
                    flight.setAvgSpeed(Integer.parseInt(et_avg_speed.getText().toString()));
                    break;
                case "kmh":
                    flight.setAvgSpeed((int) Utils.kmhToMetersPerSecond(Integer.parseInt(et_avg_speed.getText().toString())));
                    break;
                case "mph":
                    flight.setAvgSpeed((int) Utils.mphToMetersPerSecond(Integer.parseInt(et_avg_speed.getText().toString())));
                    break;
                case "kt":
                    flight.setAvgSpeed((int) Utils.ktToMetersPerSecond(Integer.parseInt(et_avg_speed.getText().toString())));
                    break;
            }
        } else
            flight.setAvgSpeed(0);


        if (et_max_speed.getText().length() > 0) {
            switch (speedUnit) {
                // Speed
                case "ms":
                    flight.setMaxSpeed(Integer.parseInt(et_max_speed.getText().toString()));
                    break;
                case "kmh":
                    flight.setMaxSpeed((int) Utils.kmhToMetersPerSecond(Integer.parseInt(et_max_speed.getText().toString())));
                    break;
                case "mph":
                    flight.setMaxSpeed((int) Utils.mphToMetersPerSecond(Integer.parseInt(et_max_speed.getText().toString())));
                    break;
                case "kt":
                    flight.setMaxSpeed((int) Utils.ktToMetersPerSecond(Integer.parseInt(et_max_speed.getText().toString())));
                    break;
            }
        } else
            flight.setMaxSpeed(0);

        sharedFlightViewModel.updateFlight(flight);
    }

    private void bindFlight(Flight flight) {
        et_name.setText(flight.getLocationName());
        et_hour.setText(Utils.timestampToHourString(flight.getDate()));
        et_date.setText(flight.getDateString());
        et_duration.setText(flight.getDurationString());


        switch (distanceUnit) {
            // Distance
            case "km":
                et_distance.setText(String.format(Locale.US, Utils.DISTANCE_FORMAT, Utils.metersToKm(flight.getDistance())));
                break;
            case "mi":
                et_distance.setText(String.format(Locale.US, Utils.DISTANCE_FORMAT, Utils.metersToMi(flight.getDistance())));
                break;
            case "nm":
                et_distance.setText(String.format(Locale.US, Utils.DISTANCE_FORMAT, Utils.metersToNm(flight.getDistance())));
                break;
        }


        switch (altitudeUnit) {
            // Altitude
            case "m":
                et_min_alt.setText(String.valueOf(flight.getMinAltitude()));
                et_max_alt.setText(String.valueOf(flight.getMaxAltitude()));
                break;
            case "ft":
                et_min_alt.setText(String.format(Locale.US, Utils.ALTITUDE_FORMAT, Utils.metersToFt(flight.getMinAltitude())));
                et_max_alt.setText(String.format(Locale.US, Utils.ALTITUDE_FORMAT, Utils.metersToFt(flight.getMaxAltitude())));
                break;
        }

        switch (speedUnit) {
            // Speed
            case "ms":
                et_min_speed.setText(String.valueOf(flight.getMinSpeed()));
                et_avg_speed.setText(String.valueOf(flight.getAvgSpeed()));
                et_max_speed.setText(String.valueOf(flight.getMaxSpeed()));
                break;
            case "kmh":
                et_min_speed.setText(String.format(Locale.US, Utils.SPEED_FORMAT, Utils.metersPerSecondToKmh(flight.getMinSpeed())));
                et_avg_speed.setText(String.format(Locale.US, Utils.SPEED_FORMAT, Utils.metersPerSecondToKmh(flight.getAvgSpeed())));
                et_max_speed.setText(String.format(Locale.US, Utils.SPEED_FORMAT, Utils.metersPerSecondToKmh(flight.getMaxSpeed())));
                break;
            case "mph":
                et_min_speed.setText(String.format(Locale.US, Utils.SPEED_FORMAT, Utils.metersPerSecondToMph(flight.getMinSpeed())));
                et_avg_speed.setText(String.format(Locale.US, Utils.SPEED_FORMAT, Utils.metersPerSecondToMph(flight.getAvgSpeed())));
                et_max_speed.setText(String.format(Locale.US, Utils.SPEED_FORMAT, Utils.metersPerSecondToMph(flight.getMaxSpeed())));
                break;
            case "kt":
                et_min_speed.setText(String.format(Locale.US, Utils.SPEED_FORMAT, Utils.metersPerSecondToKt(flight.getMinSpeed())));
                et_avg_speed.setText(String.format(Locale.US, Utils.SPEED_FORMAT, Utils.metersPerSecondToKt(flight.getAvgSpeed())));
                et_max_speed.setText(String.format(Locale.US, Utils.SPEED_FORMAT, Utils.metersPerSecondToKt(flight.getMaxSpeed())));
                break;
        }


    }


    private void showDateDialog() {
        Calendar calendar = Calendar.getInstance();
        int yearNow = calendar.get(Calendar.YEAR);
        int monthNow = calendar.get(Calendar.MONTH);
        int dayNow = calendar.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (datePicker, year, month, day) -> et_date.setText(String.format(Locale.US, "%02d/%02d/%02d", day, month + 1, year)), yearNow, monthNow, dayNow);
        datePickerDialog.show();


    }

    private void showHourDialog() {

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> et_hour.setText(String.format(Locale.US, "%02d:%02d", hourOfDay, minute)), 19, 0, true);
        timePickerDialog.show();

    }

    private void showDurationPicker() {
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.number_picker, null);

        final NumberPicker npHours = dialogView.findViewById(R.id.np_hours);
        final NumberPicker npMinutes = dialogView.findViewById(R.id.np_minutes);

        npHours.setMaxValue(23);
        npHours.setMinValue(0);
        npMinutes.setMaxValue(59);
        npMinutes.setMinValue(0);


        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Duration");
        builder.setMessage("hh:mm");
        builder.setView(dialogView);
        builder.setPositiveButton("OK", (dialogInterface, i) -> et_duration.setText(String.format(Locale.US, Utils.DURATION_FORMAT, npHours.getValue(), npMinutes.getValue(), 0)));
        builder.setNegativeButton("CANCEL", (dialogInterface, i) -> {

        });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private boolean validateFlight() {

        boolean validation = (et_name.getText().toString().length() > 0);

        if (!validation)
            tilName.setError("Name required");

        return validation;
    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        String value = sharedPreferences.getString(getString(R.string.distance_unit_key), "NULL");
        distanceUnit = value;

        value = sharedPreferences.getString(getString(R.string.altitude_unit_key), "NULL");
        altitudeUnit = value;

        value = sharedPreferences.getString(getString(R.string.speed_unit_key), "NULL");
        speedUnit = value;

        setLabels();
    }

    private void setLabels() {

        switch (distanceUnit) {
            // Distance
            case "km":
                tilDistance.setHint(getString(R.string.distance_km));
                break;
            case "mi":
                tilDistance.setHint(getString(R.string.distance_mi));
                break;
            case "nm":
                tilDistance.setHint(getString(R.string.distance_nm));
                break;
        }

        switch (altitudeUnit) {
            // Altitude
            case "m":
                tilMinAltitude.setHint(R.string.min_altitude_m);
                tilMaxAltitude.setHint(R.string.max_altitude_m);
                break;
            case "ft":
                tilMinAltitude.setHint(R.string.min_altitude_ft);
                tilMaxAltitude.setHint(R.string.max_altitude_ft);
                break;
        }

        switch (speedUnit) {
            // Speed
            case "ms":
                tilMinSpeed.setHint(R.string.min_speed_ms);
                tilAvgSpeed.setHint(R.string.avg_speed_ms);
                tilMaxSpeed.setHint(R.string.max_speed_ms);
                break;
            case "kmh":
                tilMinSpeed.setHint(R.string.min_speed_kmh);
                tilAvgSpeed.setHint(R.string.avg_speed_kmh);
                tilMaxSpeed.setHint(R.string.max_speed_kmh);
                break;
            case "mph":
                tilMinSpeed.setHint(R.string.min_speed_mph);
                tilAvgSpeed.setHint(R.string.avg_speed_mph);
                tilMaxSpeed.setHint(R.string.max_speed_mph);
                break;
            case "kt":
                tilMinSpeed.setHint(R.string.min_speed_kt);
                tilAvgSpeed.setHint(R.string.avg_speed_kt);
                tilMaxSpeed.setHint(R.string.max_speed_kt);
                break;
        }


    }


}