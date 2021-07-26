package com.dtsoftware.paraglidinggps.ui.route;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.dtsoftware.paraglidinggps.AppRepository;
import com.dtsoftware.paraglidinggps.Waypoint;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.List;

public class RouteViewModel extends AndroidViewModel {

    private AppRepository mRepository;
    private LiveData<List<Waypoint>> allWaypoints;
    private LatLng currentWpt;


    public RouteViewModel(Application application) {
        super(application);
        mRepository = new AppRepository(application);
        allWaypoints = mRepository.getAllWaypoints();
        currentWpt = null;
    }

    public LiveData<List<Waypoint>> getWaypoints() {
        return allWaypoints;
    }

    public LatLng getCurrentWpt() {
        return currentWpt;
    }

    public void setCurrentWpt(LatLng currentWpt) {
        this.currentWpt = currentWpt;
    }
}