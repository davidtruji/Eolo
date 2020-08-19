package com.dtsoftware.paraglidinggps;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "flight_table")
public class Flight {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private Float distance;

   // private Date date;

    private Integer duration;


    public Flight(Float distance, Integer duration) {
        this.distance = distance;
        //this.date = date;
        this.duration = duration;
    }


    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Flight{" +
                "id=" + id +
                ", distance=" + distance +
                ", duration=" + duration +
                '}';
    }

}
