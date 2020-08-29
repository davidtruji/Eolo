package com.dtsoftware.paraglidinggps.ui.flights;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.dtsoftware.paraglidinggps.AppRepository;
import com.dtsoftware.paraglidinggps.Flight;

import java.util.List;

public class FlightsViewModel extends AndroidViewModel {

    private AppRepository mRepository;

    private LiveData<List<Flight>> mAllFlights;

    public FlightsViewModel (Application application) {
        super(application);
        mRepository = new AppRepository(application);
        mAllFlights = mRepository.getAllFlights();
    }

    LiveData<List<Flight>> getAllFlights() { return mAllFlights; }

    public void insert(Flight flight) { mRepository.insert(flight); }
}