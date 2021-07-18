package com.dtsoftware.paraglidinggps;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RouteDAO {

    // Se permite añadir la misma multiples veces
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Route route);

    @Update
    void updateRoute(Route route);

    @Query("SELECT * FROM route_table WHERE id = :idRoute")
    LiveData<Route> getRouteByID(int idRoute);

    @Query("DELETE FROM route_table")
    void deleteAll();

    @Query("DELETE FROM route_table WHERE id = :idRoute")
    void deleteRouteById(int idRoute);

    @Query("SELECT * FROM route_table ORDER BY id DESC")
    LiveData<List<Route>> getRoutes();

}
