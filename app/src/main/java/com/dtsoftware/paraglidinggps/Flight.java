package com.dtsoftware.paraglidinggps;

import android.annotation.SuppressLint;
import android.location.Location;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.ArrayList;


@Entity(tableName = "flight_table")
public class Flight {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private float distance; // Distancia en m

    private long date; // TimeStamp con la fecha y hora del vuelo

    private ArrayList<FlightLocation> route; // Lista de todas las ubicaciones

    private long duration; // Duración del vuelo

    private int maxAltitude; // Altitud máxima en metros

    private int minAltitude; // Altitud mínima en metros

    private String locationName; // Nombre de la localización del vuelo

    //TODO: Añadir más campos útiles a cada vuelo guardado


    public Flight(String locationName, ArrayList<FlightLocation> route) {
        this.route = route;
        this.locationName = locationName;
        this.distance = Utils.getRouteDistance(route);

        if (route.size() > 0)
            this.date = route.get(0).getTime();
        else
            this.date = 0L;

        this.duration = Utils.getRouteDuration(route);
        this.maxAltitude = Utils.getMaxAltitude(route).intValue();
        this.minAltitude = Utils.getMinAltitude(route).intValue();
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public ArrayList<FlightLocation> getRoute() {
        return route;
    }

    public void setRoute(ArrayList<FlightLocation> route) {
        this.route = route;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getMaxAltitude() {
        return maxAltitude;
    }

    public void setMaxAltitude(int maxAltitude) {
        this.maxAltitude = maxAltitude;
    }

    public int getMinAltitude() {
        return minAltitude;
    }

    public void setMinAltitude(int minAltitude) {
        this.minAltitude = minAltitude;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    @SuppressLint("DefaultLocale")
    public String getDistanceString() {
        return String.format(Utils.DISTANCE_FORMAT, this.distance/1000);
    }

    public String getDateString(){
        return Utils.DateToString(date);
    }

    public String getDurationString(){
        return Utils.DurationToString(duration);
    }

    @SuppressLint("DefaultLocale")
    public String getMaxAltitudeString(){
        return String.valueOf(this.maxAltitude);
    }

    @SuppressLint("DefaultLocale")
    public String getMinAltitudeString(){
        return String.valueOf(this.minAltitude);
    }


}
