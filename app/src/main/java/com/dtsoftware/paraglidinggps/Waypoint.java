package com.dtsoftware.paraglidinggps;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "waypoint_table")
public class Waypoint {

    @PrimaryKey(autoGenerate = true)
    private int id;

    double latitude;
    double longitude;
    String waypointName;

    public Waypoint( String waypointName,double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.waypointName = waypointName;
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

    public String getWaypointName() {
        return waypointName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setWaypointName(String waypointName) {
        this.waypointName = waypointName;
    }
}
