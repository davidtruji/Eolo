package com.dtsoftware.paraglidinggps.ui.nav;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.dtsoftware.paraglidinggps.AppRepository;
import com.dtsoftware.paraglidinggps.Route;


public class NavViewModel extends AndroidViewModel {

    private Boolean isRouteSelected;
    private MutableLiveData<Route> selectedRoute = new MutableLiveData<>();
    private AppRepository mRepository;

    public NavViewModel(@NonNull Application application) {
        super(application);
        isRouteSelected = false;
    }

    public Boolean getRouteSelected() {
        return isRouteSelected;
    }

    public void setRouteSelected(Boolean routeSelected) {
        isRouteSelected = routeSelected;
    }

    public MutableLiveData<Route> getSelectedRoute() {
        return selectedRoute;
    }

    public void setSelectedRoute(Route route) {
        selectedRoute.setValue(route);
    }


}