package com.dtsoftware.paraglidinggps.ui.waypoints;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.dtsoftware.paraglidinggps.AppRepository;
import com.dtsoftware.paraglidinggps.Waypoint;

public class WaypointViewModel extends AndroidViewModel {


    private MutableLiveData<Waypoint> selected = new MutableLiveData<>();
    private AppRepository mRepository;


    public WaypointViewModel(@NonNull Application application) {
        super(application);
        mRepository = new AppRepository(application);
    }


    public MutableLiveData<Waypoint> getSelectedWaypoint() {
        return selected;
    }

    public void setSelected(Waypoint waypoint) {
        selected.setValue(waypoint);
    }

    public void updateWaypoint(Waypoint waypoint) {
        setSelected(waypoint);
        mRepository.updateWaypoint(waypoint);
    }

    public void deleteWaypointById(int id) {
        mRepository.deleteWaypointByID(id);
    }


}
