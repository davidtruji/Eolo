package com.dtsoftware.paraglidinggps.ui.nav;

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;


public class NavViewModel extends AndroidViewModel {


    private MutableLiveData<Location> lastLocation = new MutableLiveData<>();

    public NavViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Location> getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(Location lastLocation) {
        this.lastLocation.setValue(lastLocation);
    }

}