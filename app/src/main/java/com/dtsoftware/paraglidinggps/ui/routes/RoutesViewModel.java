package com.dtsoftware.paraglidinggps.ui.routes;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.dtsoftware.paraglidinggps.AppRepository;
import com.dtsoftware.paraglidinggps.Flight;
import com.dtsoftware.paraglidinggps.Route;

import java.util.List;

public class RoutesViewModel extends AndroidViewModel {

    private AppRepository mRepository;

    private LiveData<List<Route>> allRoutes;

    public RoutesViewModel(Application application) {
        super(application);
        mRepository = new AppRepository(application);
        allRoutes = mRepository.getAllRoutes();
    }

    public LiveData<List<Route>> getAllRoutes() {
        return allRoutes;
    }

    public void insert(Route route) {
        mRepository.insert(route);
    }

    public void deleteRouteByID(int id) {
        mRepository.deleteRouteByID(id);
    }


}