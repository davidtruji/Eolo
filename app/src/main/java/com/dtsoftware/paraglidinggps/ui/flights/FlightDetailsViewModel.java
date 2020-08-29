package com.dtsoftware.paraglidinggps.ui.flights;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.dtsoftware.paraglidinggps.AppRepository;
import com.dtsoftware.paraglidinggps.Flight;

public class FlightDetailsViewModel extends AndroidViewModel {

    private AppRepository mRepository;
    private LiveData<Flight> mFlight;


    public FlightDetailsViewModel(@NonNull Application application, int id) {
        super(application);
        mRepository = new AppRepository(application);
        mFlight = mRepository.getFlightByID(id);
    }

    LiveData<Flight> getFlight() {
        return mFlight;
    }

    public void updateFlight(Flight flight) {
        mRepository.updateFlight(flight);
    }
}
