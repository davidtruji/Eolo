package com.dtsoftware.paraglidinggps;

import android.annotation.SuppressLint;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.Locale;


@Entity(tableName = "flight_table")
public class Flight {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private float distance; // Distancia en metros

    private long date; // TimeStamp con la fecha del vuelo dd/mm/aaaa

    private ArrayList<FlightLocation> route; // Lista de todas las ubicaciones

    private long duration; // Duración del vuelo milisegundos

    private int maxAltitude; // Altitud máxima en metros

    private int minAltitude; // Altitud mínima en metros

    private int minSpeed; // Minima velocidad m/s

    private int avgSpeed; // Velocidad media m/s

    private int maxSpeed; // Maxima velocidad m/s

    private String locationName; // Nombre de la localización del vuelo

    public Flight() {
        // Constructor vacio
    }


    @Ignore
    public Flight(String locationName, ArrayList<FlightLocation> route) {
        this.route = route;
        this.locationName = locationName;
        this.distance = Utils.getRouteDistance(route);

        if (route.size() > 0)
            this.date = route.get(0).getTime();
        else
            this.date = 0L;

        this.duration = Utils.getRouteDuration(route);
        obtainAltitudeValues();
        obtainSpeedValues();

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


    public int getMinSpeed() {
        return minSpeed;
    }

    public void setMinSpeed(int minSpeed) {
        this.minSpeed = minSpeed;
    }

    public int getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(int avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public String getDistanceString() {
        return String.format(Locale.US, Utils.DISTANCE_FORMAT, this.distance / 1000);
    }

    public String getDateString() {
        return Utils.DateToString(date);
    }

    public String getDurationString() {
        return Utils.DurationToString(duration);
    }

    @SuppressLint("DefaultLocale")
    public String getMaxAltitudeString() {
        return String.valueOf(this.maxAltitude);
    }

    @SuppressLint("DefaultLocale")
    public String getMinAltitudeString() {
        return String.valueOf(this.minAltitude);
    }

    private void obtainAltitudeValues() {

        Double minAltitude, maxAltitude;

        if (route.size() > 0) {

            minAltitude = route.get(0).getAltitude();
            maxAltitude = route.get(0).getAltitude();

            for (FlightLocation location : route) {

                if (location.getAltitude() < minAltitude)
                    minAltitude = location.getAltitude();
                else if (location.getAltitude() > maxAltitude)
                    maxAltitude = location.getAltitude();

            }

            this.minAltitude = minAltitude.intValue();
            this.maxAltitude = maxAltitude.intValue();

        }

    }

    private void obtainSpeedValues() {

        Double minSpeed, avgSpeed, maxSpeed;

        if (route.size() > 0) {

            minSpeed = route.get(0).getSpeed();
            avgSpeed = 0D;
            maxSpeed = route.get(0).getSpeed();

            for (FlightLocation location : route) {

                if (location.getSpeed() < minSpeed)
                    minSpeed = location.getSpeed();
                else if (location.getSpeed() > maxSpeed)
                    maxSpeed = location.getSpeed();

                avgSpeed += location.getSpeed();
            }

            this.minSpeed = minSpeed.intValue();
            this.maxSpeed = maxSpeed.intValue();
            this.avgSpeed = (int) (avgSpeed / route.size());

        }

    }


}
