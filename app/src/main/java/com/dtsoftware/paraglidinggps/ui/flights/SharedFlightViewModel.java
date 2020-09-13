package com.dtsoftware.paraglidinggps.ui.flights;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dtsoftware.paraglidinggps.AppRepository;
import com.dtsoftware.paraglidinggps.Flight;

public class SharedFlightViewModel extends AndroidViewModel {


    private MutableLiveData<Flight> selected = new MutableLiveData<>();
    private AppRepository mRepository;


    public SharedFlightViewModel(@NonNull Application application) {
        super(application);
        mRepository = new AppRepository(application);
    }


    public MutableLiveData<Flight> getSelectedFlight() {
        return selected;
    }

    public void setSelected(Flight flight) {
        selected.setValue(flight);
    }

    public void updateFlight(Flight flight) {
        setSelected(flight);
        mRepository.updateFlight(flight);
    }


}
