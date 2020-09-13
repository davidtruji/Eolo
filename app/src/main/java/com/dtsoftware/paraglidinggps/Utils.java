package com.dtsoftware.paraglidinggps;

import android.annotation.SuppressLint;
import android.location.Location;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.snackbar.Snackbar;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Utils {


    public static final String NORTH = "N"; // [337.5° - 22.5°)
    public static final String NORTH_EAST = "NE"; // [22.5° - 67.5°)
    public static final String EAST = "E"; // [67.5° - 112.5°)
    public static final String SOUTH_EAST = "SE"; // [112.5° - 157.5°)
    public static final String SOUTH = "S"; // [157.5° - 202.5°)
    public static final String SOUTH_WEST = "SW"; // [202.5° - 247.5°)
    public static final String WEST = "W"; // [247.5° - 292.5°)
    public static final String NORTH_WEST = "NW"; // [292.5° - 337.5°)

    public static final String DISTANCE_FORMAT = "%.1f";
    public static final String FLIGHT_HOURS_FORMAT = "%05d";
    public static final String ALTITUDE_FORMAT = "%.0f";
    public static final String DURATION_FORMAT = "%02d:%02d:%02d";
    public static final String DATE_FORMAT = "dd/MM/yyyy";

    public static final double POLYGON_SIZE = .000025;
    public static final String GEO_JSON_ID = "source-id";


    public static String degreesToBearing(Float degrees) {

        if (degrees >= 337.5 || degrees < 22.5) {
            //north
            return NORTH;
        } else if (degrees >= 22.5 && degrees < 67.5) {
            //north east
            return NORTH_EAST;
        } else if (degrees >= 67.5 && degrees < 112.5) {
            //east
            return EAST;
        } else if (degrees >= 112.5 && degrees < 157.5) {
            //south east
            return SOUTH_EAST;
        } else if (degrees >= 157.5 && degrees < 202.5) {
            //south
            return SOUTH;
        } else if (degrees >= 202.5 && degrees < 247.5) {
            //south west
            return SOUTH_WEST;
        } else if (degrees >= 247.5 && degrees < 292.5) {
            //west
            return WEST;
        } else {
            //north west
            return NORTH_WEST;
        }

    }


    public static void showSnakcbar(View coordinatorView, String text) {
        Snackbar snackbar;
        snackbar = Snackbar.make(coordinatorView, text, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }


    public static Float getRouteDistance(ArrayList<FlightLocation> route) {
        float distance = 0f; // Distancia en metros de la ruta
        FlightLocation prevLocation = null;

        for (FlightLocation location : route) {

            if (prevLocation != null)
                distance += prevLocation.distanceTo(location);

            prevLocation = location;
        }

        return distance;
    }


    public static Long getRouteDuration(ArrayList<FlightLocation> route) {
        long duration = 0L, initTime, finTime;

        if (route.size() > 0) {

            initTime = route.get(0).getTime();
            finTime = route.get(route.size() - 1).getTime();
            duration = finTime - initTime;

        }
        return duration;
    }


    public static Double getMaxAltitude(ArrayList<FlightLocation> route) {
        double maxAltitude = 0D;

        if (route.size() > 0) {

            maxAltitude = route.get(0).getAltitude();

            for (FlightLocation location : route) {

                if (location.getAltitude() > maxAltitude)
                    maxAltitude = location.getAltitude();


            }

        }
        return maxAltitude;
    }


    public static Double getMinAltitude(ArrayList<FlightLocation> route) {
        double minAltitude = 0D;

        if (route.size() > 0) {

            minAltitude = route.get(0).getAltitude();

            for (FlightLocation location : route) {

                if (location.getAltitude() < minAltitude)
                    minAltitude = location.getAltitude();


            }

        }

        return minAltitude;
    }


    @SuppressLint("DefaultLocale")
    public static String DurationToString(long durationTimestamp) {
        long secs = durationTimestamp / 1000;
        return String.format(DURATION_FORMAT, secs / 3600, (secs % 3600) / 60, secs % 60);
    }


    public static long StringToDuration(String durationString) {
        String[] durationStringSplit = durationString.split(":");
        long durationTimestamp = 0L;

        int hours = Integer.parseInt(durationStringSplit[0]);
        int minutes = Integer.parseInt(durationStringSplit[1]);
        int seconds = Integer.parseInt(durationStringSplit[2]);

        durationTimestamp += TimeUnit.HOURS.toMillis(hours);
        durationTimestamp += TimeUnit.MINUTES.toMillis(minutes);
        durationTimestamp += TimeUnit.SECONDS.toMillis(seconds);

        return durationTimestamp;
    }

    public static long StringDateToTimestamp(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Date date = null;
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }


    public static String DateToString(long dateTimestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        return dateFormat.format(new Date(dateTimestamp));
    }


    public static Integer getTotalFlightHours(List<Flight> vuelos) {
        long hours = 0L;

        for (Flight f : vuelos) {

            hours += f.getDuration();

        }


        hours = TimeUnit.MILLISECONDS.toHours(hours);

        return (int) hours;
    }


    public static void hideAllFragments(FragmentManager fragmentManager) {
        List<Fragment> fragmentList = fragmentManager.getFragments();
        for (Fragment f : fragmentList) {
            fragmentManager.beginTransaction().hide(f).commit();
        }
    }

    public static GeoJsonSource getGeoJsonSourceFromRoute(ArrayList<FlightLocation> route) {
        List<Feature> featureList = new ArrayList<>();
        Feature feature;
        FeatureCollection featureCollection;

        double groundAltitude = getMinAltitude(route);

        for (FlightLocation location : route) {
            feature = Feature.fromGeometry(getPolygonFromLocation(location));
            feature.addNumberProperty("e", location.getAltitude() - groundAltitude);
            featureList.add(feature);
        }

        featureCollection = FeatureCollection.fromFeatures(featureList);

        return new GeoJsonSource(GEO_JSON_ID, featureCollection);
    }


    private static Polygon getPolygonFromLocation(FlightLocation location) {

        List<Point> pointList = new ArrayList<>();
        List<List<Point>> coordinates = new ArrayList<>();

        double lat = location.getLatitude();
        double lng = location.getLongitude();
        double latN, latS, lngW, lngE;

        if (lat + POLYGON_SIZE > 90)
            latN = 90 - ((lat + POLYGON_SIZE) - 90);
        else
            latN = lat + POLYGON_SIZE;

        if (lat - POLYGON_SIZE < -90)
            latS = -90 + ((lat - POLYGON_SIZE) + 90);
        else
            latS = lat - POLYGON_SIZE;

        if (lng + POLYGON_SIZE > 180)
            lngE = -180 + ((lng + POLYGON_SIZE) - 180);
        else
            lngE = lng + POLYGON_SIZE;

        if (lng - POLYGON_SIZE < -180)
            lngW = 180 - ((lng - POLYGON_SIZE) + 180);
        else
            lngW = lng - POLYGON_SIZE;

        pointList.add(Point.fromLngLat(lngW, latN)); // Arriba izq
        pointList.add(Point.fromLngLat(lngE, latN)); // Arriba der
        pointList.add(Point.fromLngLat(lngE, latS)); // Abajo der
        pointList.add(Point.fromLngLat(lngW, latS)); // Abajo izq
        pointList.add(Point.fromLngLat(lngW, latN)); // Arriba izq (Repetido necesariamente)

        coordinates.add(pointList);

        return Polygon.fromLngLats(coordinates);
    }


    public static LatLngBounds getBoundsOfRoute(ArrayList<FlightLocation> flightLocations) {

        double northLatitude = -90;
        double southLatitude = 90;
        double eastLongitude = -180;
        double westLongitude = 180;

        double currentLat, currentLng;

        for (FlightLocation flightLocation : flightLocations) {

            currentLat = flightLocation.getLatitude();
            currentLng = flightLocation.getLongitude();

            if (currentLat > northLatitude)
                northLatitude = currentLat;

            if (currentLat < southLatitude)
                southLatitude = currentLat;

            if (currentLng > eastLongitude)
                eastLongitude = currentLng;

            if (currentLng < westLongitude)
                westLongitude = currentLng;

        }

        return LatLngBounds.from(northLatitude, eastLongitude, southLatitude, westLongitude);
    }


}
