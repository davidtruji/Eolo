package com.dtsoftware.paraglidinggps;

import android.location.Location;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class Converters {
    @TypeConverter
    public static ArrayList<Location> fromString(String value) {
        Type listType = new TypeToken<ArrayList<Location>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<Location> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}
