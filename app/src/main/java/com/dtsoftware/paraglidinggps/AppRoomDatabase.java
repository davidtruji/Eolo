package com.dtsoftware.paraglidinggps;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Flight.class, Waypoint.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppRoomDatabase extends RoomDatabase {

    public abstract FlightDAO flightDAO();
    public abstract WaypointDAO waypointDAO();


    private static volatile AppRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static AppRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppRoomDatabase.class, "app_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
