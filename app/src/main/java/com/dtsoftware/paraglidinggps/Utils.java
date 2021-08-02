package com.dtsoftware.paraglidinggps;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.google.android.material.snackbar.Snackbar;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
    public static final String BEARING_FORMAT = "%.0f";
    public static final String COORDINATES_FORMAT = "%1$.4f";
    public static final String FLIGHT_HOURS_FORMAT = "%05d";
    public static final String ALTITUDE_FORMAT = "%.0f";
    public static final String SPEED_FORMAT = "%.0f";
    public static final String DURATION_FORMAT = "%02d:%02d:%02d";
    public static final String HOUR_DATE_FORMAT = "HH:mmdd/MM/yyyy";
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String HOUR_FORMAT = "HH:mm";
    public static final String FLIGHT_DATE_FORMAT = "ddMMyyyy";


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
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        Date date = null;
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }


    public static String DateToString(long dateTimestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        return dateFormat.format(new Date(dateTimestamp));
    }

    public static String timestampToHourString(long hourTimestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(HOUR_FORMAT, Locale.getDefault());
        return dateFormat.format(new Date(hourTimestamp));
    }

    public static long hourDateStringToTimestamp(String hourDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(HOUR_DATE_FORMAT, Locale.getDefault());
        Date date = null;
        try {
            date = dateFormat.parse(hourDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }


    public static Integer getTotalFlightHours(List<Flight> vuelos) {
        long hours = 0L;

        for (Flight f : vuelos) {

            hours += f.getDuration();

        }


        hours = TimeUnit.MILLISECONDS.toHours(hours);

        return (int) hours;
    }


    public static GeoJsonSource getGeoJsonSourceFromRoute(ArrayList<FlightLocation> route) {
        List<Point> linePoints = new ArrayList<>();

        for (FlightLocation location : route) {
            linePoints.add(Point.fromLngLat(location.getLongitude(), location.getLatitude()));
        }

        return new GeoJsonSource(GEO_JSON_ID,
                Feature.fromGeometry(LineString.fromLngLats(linePoints)));
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

    /**
     * Genera nombres para los vuelos basandose en la fecha y hora actual
     *
     * @return string nombre del vuelo
     */
    public static String generateFlightName() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat(FLIGHT_DATE_FORMAT, Locale.getDefault());
        return "Flight_" + sdf.format(date);
    }

    public static void rotateImage(ImageView img, float startDegree, float endDegree) {
        RotateAnimation ra = new RotateAnimation(startDegree,
                endDegree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        ra.setFillAfter(true);

        // set how long the animation for the compass image will take place
        ra.setDuration(500);
        // Start animation of compass image
        img.startAnimation(ra);
    }

    public static float metersToKm(float meters) {
        return meters * 0.001f;
    }

    public static float kmToMeters(float km) {
        return km * 1000;
    }

    public static float metersToMi(float meters) {
        return meters * 0.0006213712f;
    }

    public static float miToMeters(float mi) {
        return mi * 1609.344f;
    }

    public static float metersToNm(float meters) {
        return meters * 0.0005399565f;
    }

    public static float nmToMeters(float nm) {
        return nm * 1852.001f;
    }

    public static float metersToFt(float meters) {
        return meters * 3.28084f;
    }

    public static float ftToMeters(float ft) {
        return ft * 0.30480003f;
    }

    public static float metersPerSecondToKmh(int metersPerSecond) {
        return metersPerSecond * 3.6f;
    }

    public static float kmhToMetersPerSecond(int kmh) {
        return kmh * 0.2777778f;
    }

    public static float metersPerSecondToMph(int metersPerSecond) {
        return metersPerSecond * 2.236936f;
    }

    public static float mphToMetersPerSecond(int mph) {
        return mph * 0.44704f;
    }

    public static float metersPerSecondToKt(int metersPerSecond) {
        return metersPerSecond * 1.943844f;
    }

    public static float ktToMetersPerSecond(int kt) {
        return kt * 0.5144447f;
    }


}
