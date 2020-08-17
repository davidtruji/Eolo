package com.dtsoftware.paraglidinggps;

public class Utils {


    public static final String NORTH = "N"; // [337.5° - 22.5°)
    public static final String NORTH_EAST = "NE"; // [22.5° - 67.5°)
    public static final String EAST = "E"; // [67.5° - 112.5°)
    public static final String SOUTH_EAST = "SE"; // [112.5° - 157.5°)
    public static final String SOUTH = "S"; // [157.5° - 202.5°)
    public static final String SOUTH_WEST = "SW"; // [202.5° - 247.5°)
    public static final String WEST = "W"; // [247.5° - 292.5°)
    public static final String NORTH_WEST = "NW"; // [292.5° - 337.5°)


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


}
