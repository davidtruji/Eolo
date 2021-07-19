package com.dtsoftware.paraglidinggps.ui.routes;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.dtsoftware.paraglidinggps.AppRepository;
import com.dtsoftware.paraglidinggps.Flight;
import com.dtsoftware.paraglidinggps.Route;

public class SharedRouteViewModel extends AndroidViewModel {


    private MutableLiveData<Route> selected = new MutableLiveData<>();
    private AppRepository mRepository;


    public SharedRouteViewModel(@NonNull Application application) {
        super(application);
        mRepository = new AppRepository(application);
    }


    public MutableLiveData<Route> getSelectedRoute() {
        return selected;
    }

    public void setSelected(Route route) {
        selected.setValue(route);
    }

    public void updateRoute(Route route) {
        setSelected(route);
        mRepository.updateRoute(route);
    }


}
