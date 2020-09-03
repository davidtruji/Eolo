package com.dtsoftware.paraglidinggps;

import android.location.Location;

public class FlightLocation {


    private double latitude;
    private double longitude;
    private double altitude;
    private double speed;
    private double bearing;
    private long time;


    public FlightLocation(Location location) {

        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.altitude = location.getAltitude();
        this.speed = location.getSpeed();
        this.bearing = location.getBearing();
        this.time = location.getTime();

    }


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getBearing() {
        return bearing;
    }

    public void setBearing(double bearing) {
        this.bearing = bearing;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public float distanceTo(FlightLocation location) {
        Location thisLocation = new Location("thisLocation");
        thisLocation.setLatitude(this.getLatitude());
        thisLocation.setLongitude(this.getLongitude());


        Location otherLocation = new Location("OtherLocation");
        otherLocation.setLatitude(location.getLatitude());
        otherLocation.setLongitude(location.getLongitude());


        return thisLocation.distanceTo(otherLocation);

    }

}
