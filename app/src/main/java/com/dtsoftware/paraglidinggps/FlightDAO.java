package com.dtsoftware.paraglidinggps;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FlightDAO {

    // Se permite añadir el mismo vuelo multiples veces
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Flight flight);

    @Update
    void updateFlight(Flight flight);

    @Query("SELECT * FROM flight_table WHERE id = :idFlight")
    LiveData<Flight> getFlightByID(int idFlight);

    @Query("DELETE FROM flight_table")
    void deleteAll();

    @Query("SELECT * FROM flight_table ORDER BY date DESC")
    LiveData<List<Flight>> getFlights();

}
