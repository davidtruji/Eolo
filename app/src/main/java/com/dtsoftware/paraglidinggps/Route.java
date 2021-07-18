package com.dtsoftware.paraglidinggps;


import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.mapbox.geojson.Point;

import java.util.ArrayList;


@Entity(tableName = "route_table")
public class Route {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String routeName; // Nombre de la ruta
    private float distance; // Distancia en metros
    private ArrayList<Point> route; // Lista de todos los puntos de la ruta

    public Route() {
        // Constructor vacio
    }

    @Ignore
    public Route(String routeName, float distance, ArrayList<Point> route) {
        this.distance = distance;
        this.route = route;
        this.routeName = routeName;
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

    public ArrayList<Point> getRoute() {
        return route;
    }

    public void setRoute(ArrayList<Point> route) {
        this.route = route;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }
}
