package com.dtsoftware.paraglidinggps;


import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mapbox.geojson.Point;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Converters {
    @TypeConverter
    public static ArrayList<FlightLocation> flightListfromString(String value) {
        Type listType = new TypeToken<ArrayList<FlightLocation>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String stringFromFlightList(ArrayList<FlightLocation> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    @TypeConverter
    public static ArrayList<Point> pointListFromString(String value) {
        Type listType = new TypeToken<ArrayList<Point>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String stringFromPointList(ArrayList<Point> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }


}
