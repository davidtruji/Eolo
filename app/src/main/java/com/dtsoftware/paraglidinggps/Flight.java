package com.dtsoftware.paraglidinggps;

import android.location.Location;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


@Entity(tableName = "flight_table")
public class Flight {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private Float distance; // Distancia en Km

    private Long time; // TimeStamp con la fecha y hora del vuelo

    private ArrayList<Location> route; // Lista de todas las ubicaciones

    private Long duration; //Duración en minutos del vuelo

    private Integer maxAltitude; // Altitud máxima en metros

    private Integer minAltitude; // Altitud mínima en metros

    private String locationName; // Nombre de la localización del vuelo

    //TODO: Añadir más campos útiles a cada vuelo guardado


    public Flight(String locationName,ArrayList<Location> route) {
        this.route = route;
        this.locationName = locationName;
        this.distance = Utils.getRouteDistance(route);
        this.time = route.get(0).getTime();
        this.duration = Utils.getRouteDuration(route);
        this.maxAltitude = Utils.getMaxAltitude(route).intValue();
        this.minAltitude = Utils.getMinAltitude(route).intValue();
        //TODO: Métodos para obtener la información en formato String
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public ArrayList<Location> getRoute() {
        return route;
    }

    public void setRoute(ArrayList<Location> route) {
        this.route = route;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Integer getMaxAltitude() {
        return maxAltitude;
    }

    public void setMaxAltitude(Integer maxAltitude) {
        this.maxAltitude = maxAltitude;
    }

    public Integer getMinAltitude() {
        return minAltitude;
    }

    public void setMinAltitude(Integer minAltitude) {
        this.minAltitude = minAltitude;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
}
