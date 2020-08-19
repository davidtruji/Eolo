package com.dtsoftware.paraglidinggps;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface FlightDAO {

    // allowing the insert of the same word multiple times by passing a
    // conflict resolution strategy
    @Insert(onConflict = OnConflictStrategy.IGNORE)
     void insert(Flight flight);

    @Query("DELETE FROM flight_table")
    void deleteAll();

    @Query("SELECT * from flight_table ORDER BY id ASC")
    LiveData<List<Flight>> getFlights();

}
