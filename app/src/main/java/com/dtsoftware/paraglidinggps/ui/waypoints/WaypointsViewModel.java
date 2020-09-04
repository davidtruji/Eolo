package com.dtsoftware.paraglidinggps.ui.waypoints;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.dtsoftware.paraglidinggps.AppRepository;
import com.dtsoftware.paraglidinggps.Waypoint;

import java.util.List;

public class WaypointsViewModel extends AndroidViewModel {

    private AppRepository mRepository;

    private LiveData<List<Waypoint>> allWaypoints;

    public WaypointsViewModel(Application application) {
        super(application);
        mRepository = new AppRepository(application);
        allWaypoints = mRepository.getAllWaypoints();
    }

    public LiveData<List<Waypoint>> getAllWaypoints() {
        return allWaypoints;
    }

    public void insert(Waypoint waypoint) {
        mRepository.insert(waypoint);
    }

    public void deleteAll() {
        mRepository.deleteAllWaypoints();
    }
}