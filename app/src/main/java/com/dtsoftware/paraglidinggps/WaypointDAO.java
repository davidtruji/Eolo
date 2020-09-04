package com.dtsoftware.paraglidinggps;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;


@Dao
public interface WaypointDAO {

    // Se permite añadir el mismo waypoint multiples veces
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Waypoint waypoint);

    @Update
    void updateWaypoint(Waypoint waypoint);

    @Query("SELECT * FROM waypoint_table WHERE id = :idWaypoint")
    LiveData<Waypoint> getWaypointByID(int idWaypoint);

    @Query("DELETE FROM waypoint_table")
    void deleteAll();

    @Query("SELECT * FROM waypoint_table ORDER BY id DESC")
    LiveData<List<Waypoint>> getWaypoints();

}

