package com.dtsoftware.paraglidinggps;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class AppRepository {

    private FlightDAO mFlightDAO;
    private LiveData<List<Flight>> mAllFlights;

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public AppRepository(Application application) {
        AppRoomDatabase db = AppRoomDatabase.getDatabase(application);
        mFlightDAO = db.flightDAO();
        mAllFlights = mFlightDAO.getFlights();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<Flight>> getAllFlights() {
        return mAllFlights;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void insert(Flight flight) {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> mFlightDAO.insert(flight));
    }

    public LiveData<Flight> getFlightByID(int id) {
        return mFlightDAO.getFlightByID(id);
    }

    public void updateFlight(Flight flight) {
        mFlightDAO.updateFlight(flight);
    }


}