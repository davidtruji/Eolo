package com.dtsoftware.paraglidinggps.ui.flights;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.dtsoftware.paraglidinggps.Flight;

public class SharedFlightViewModel extends AndroidViewModel {


    private final MutableLiveData<Flight> selected = new MutableLiveData<Flight>();

    public SharedFlightViewModel(@NonNull Application application) {
        super(application);
    }

    public void selectFlight(Flight flight) {
        selected.setValue(flight);
    }

    public LiveData<Flight> getSelectedFlight() {
        return selected;
    }
    

}
