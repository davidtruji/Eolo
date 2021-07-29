package com.dtsoftware.paraglidinggps.ui.route;

import android.app.Application;
import android.location.Location;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dtsoftware.paraglidinggps.AppRepository;
import com.dtsoftware.paraglidinggps.Waypoint;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.List;

public class RouteViewModel extends AndroidViewModel {

    private AppRepository mRepository;
    private MutableLiveData<LatLng> selectedWaypoint = new MutableLiveData<>();
    private LiveData<List<Waypoint>> waypoints;

    public RouteViewModel(Application application) {
        super(application);
        mRepository = new AppRepository(application);
        waypoints = mRepository.getAllWaypoints();
    }

    public MutableLiveData<LatLng> getSelectedWaypoint() {
        return selectedWaypoint;
    }

    public void setSelectedWaypoint(LatLng waypoint) {
        selectedWaypoint.setValue(waypoint);
    }

    public LiveData<List<Waypoint>> getWaypoints() {
        return waypoints;
    }

}