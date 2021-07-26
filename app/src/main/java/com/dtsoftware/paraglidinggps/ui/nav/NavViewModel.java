package com.dtsoftware.paraglidinggps.ui.nav;

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.mapbox.mapboxsdk.geometry.LatLng;


public class NavViewModel extends AndroidViewModel {

    private Boolean isSelectedWaypoint;
    private MutableLiveData<LatLng> selectedWaypoint = new MutableLiveData<>();
    private MutableLiveData<Location> lastLocation = new MutableLiveData<>();

    public NavViewModel(@NonNull Application application) {
        super(application);
        isSelectedWaypoint = false;
    }

    public MutableLiveData<Location> getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(Location lastLocation) {
        this.lastLocation.setValue(lastLocation);
    }

    public Boolean isWaypointSelected() {
        return isSelectedWaypoint;
    }

    public void setIsSelectedWaypoint(Boolean isSelected) {
        isSelectedWaypoint = isSelected;
    }

    public MutableLiveData<LatLng> getSelectedWaypoint() {
        return selectedWaypoint;
    }

    public void setSelectedWaypoint(LatLng selectedWaypoint) {
        this.selectedWaypoint.setValue(selectedWaypoint);
    }
}