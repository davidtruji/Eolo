package com.dtsoftware.paraglidinggps.ui.nav;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


public class NavViewModel extends ViewModel {
    private MutableLiveData<Float> distance;

    public NavViewModel() {
        distance = new MutableLiveData<>();
    }

    public MutableLiveData<Float> getDistance() {
        return distance;
    }
}