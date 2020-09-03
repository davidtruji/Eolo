package com.dtsoftware.paraglidinggps;

import android.location.Location;
import android.util.Log;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class Converters {
    @TypeConverter
    public static ArrayList<FlightLocation> fromString(String value) {
        Type listType = new TypeToken<ArrayList<FlightLocation>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<FlightLocation> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}
