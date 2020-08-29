package com.dtsoftware.paraglidinggps.ui.flights;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.dtsoftware.paraglidinggps.Flight;

public class SharedFlightViewModel {


    private final MutableLiveData<Flight> selected = new MutableLiveData<Flight>();

    public void selectFlight(Flight flight) {
        selected.setValue(flight);
    }

    public LiveData<Flight> getSelectedFlight() {
        return selected;
    }
    

}
