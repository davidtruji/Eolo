package com.dtsoftware.paraglidinggps;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;


public class AppRepository {

    private FlightDAO mFlightDAO;
    private WaypointDAO waypointDAO;
    private RouteDAO routeDAO;

    private LiveData<List<Flight>> mAllFlights;
    private LiveData<List<Waypoint>> allWaypoints;
    private LiveData<List<Route>> allRoutes;


    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public AppRepository(Application application) {
        AppRoomDatabase db = AppRoomDatabase.getDatabase(application);
        mFlightDAO = db.flightDAO();
        waypointDAO = db.waypointDAO();
        routeDAO = db.routeDAO();

        mAllFlights = mFlightDAO.getFlights();
        allWaypoints = waypointDAO.getWaypoints();
        allRoutes = routeDAO.getRoutes();
    }



    /*
        Metodos de vuelos
     */

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<Flight>> getAllFlights() {
        return mAllFlights;
    }

    public LiveData<Flight> getFlightByID(int id) {
        return mFlightDAO.getFlightByID(id);
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void insert(Flight flight) {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> mFlightDAO.insert(flight));
    }

    public void updateFlight(Flight flight) {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> mFlightDAO.updateFlight(flight));
    }

    public void deleteFlightByID(int id) {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> mFlightDAO.deleteFlightById(id));
    }

    /*
        Metodos de waypoints
     */

    public LiveData<List<Waypoint>> getAllWaypoints() {
        return allWaypoints;
    }

    public void insert(Waypoint waypoint) {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> waypointDAO.insert(waypoint));
    }

    public void updateWaypoint(Waypoint waypoint) {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> waypointDAO.updateWaypoint(waypoint));
    }

    public void deleteWaypointByID(int id) {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> waypointDAO.deleteWaypointById(id));
    }

    public void deleteAllWaypoints() {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> waypointDAO.deleteAll());
    }

    /*
        Metodos de Rutas
     */

    public LiveData<List<Route>> getAllRoutes() {
        return allRoutes;
    }

    public void insert(Route route) {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> routeDAO.insert(route));
    }

    public void updateRoute(Route route) {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> routeDAO.updateRoute(route));
    }

    public void deleteRouteByID(int id) {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> routeDAO.deleteRouteById(id));
    }

    public void deleteAllRoutes() {
        AppRoomDatabase.databaseWriteExecutor.execute(() -> routeDAO.deleteAll());
    }

}