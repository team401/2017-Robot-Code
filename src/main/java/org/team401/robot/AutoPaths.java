package org.team401.robot;


/**
 * Created by EJ on 1/23/2017.
 * This is a list off all the possible paths will may want to take in auto
 * And some methods that help plot points
 */
public class AutoPaths {


    //points for final waypoints
//if the points vary depending on the size of our robot
    public static final double[] CENTER_GEAR_PEG = new double[]{
            13.5, 8
    };
    public static final double[] LEFT_GEAR_PEG = new double[]{
            11, 14
    };
    public static final double[] RIGHT_GEAR_PEG = new double[]{
            16, 14
    };
    public static final double[] STARTING_MID = new double[]{
            13.5, 2
    };
    public static final double[] STARTING_LEFT = new double[]{
            5, 2
    };
    public static final double[] STARTING_RIGHT = new double[]{
            22, 2
    };

/*
*******************************
Waypoint Paths:

*******************************
 */


    //goes from the middle starting position to the center gear lift
    public static final double[][] START_MID_TO_LIFT = new double[][]{
            STARTING_MID,
            CENTER_GEAR_PEG,
    };
    //goes from the middle starting position to the right gear lift
    public static final double[][] START_MID_TO_R_LIFT = new double[][]{
            STARTING_MID,


    };
    //goes from the Middle starting position to the left gear lift
    public static final double[][] START_MID_TO_L_LIFT = new double[][]{
            STARTING_MID,
            {10, 4},
            {7, 8},
            {7, 12},
            {7, 14},
            perpendicular_To_Airship(LEFT_GEAR_PEG),
            LEFT_GEAR_PEG,
    };
    //test mecanum path
    public static final double[][] TEST_MECANUM = new double[][]{
            {13.5, 2, -1},
            {13.5, 8, 180}
    };

   //NOTE: WE MAY WANT TO CHANGE THE FACTOR PART OF THIS. IT WOULD BE EASIER IN THE LONG RUN TO MAKE IT SO THAT
    //IT EXTENDS THE POINT ONLY AS FAR AS OUR ROBOT IS LONG


    /**
     * Returns the coordinates of C, where C is the vertex of a triangle and A and B are known points with angles of 45 degrees.
     * Slightly modified from http://math.stackexchange.com/questions/1842614/find-third-coordinate-for-a-right-triangle-with-45degree-angles
     *
     * Works no matter what order the points are entered
     *
     * @param coords The coordinates of the points.  Should be in the order [x1, y1, x2, y2].
     * @param factor Scales the result to the correct length
     * @return Point C
     */
    public static double[] perpendicular(double[] coords, double factor) {
        if(coords[0] < coords[2]){
            return new double[]{
                    (coords[0] + coords[2]) / 2.0 + (coords[1] - coords[3]) / 2.0 * factor,
                    (coords[1] + coords[3]) / 2.0 + (coords[2] - coords[0]) / 2.0 * factor
            };
        }else{
            return new double[]{
                    (coords[0] + coords[2]) / 2.0 - (coords[1] - coords[3]) / 2.0 * factor,
                    (coords[1] + coords[3]) / 2.0 - (coords[2] - coords[0]) / 2.0 * factor
            };

        }

    }

    public static double[] perpendicular(double x1, double y1, double x2, double y2, double factor) {
        return perpendicular(new double[]{x1, y1, x2, y2}, factor);
    }

    public static double[] perpendicular(double[] coords) {
        return perpendicular(coords, 1.0);
    }

    public static double[] perpendicular(double x1, double y1, double x2, double y2) {
        return perpendicular(new double[]{x1, y1, x2, y2});
    }

    /**
     * Performs the perpendicular functions, but you only have to input the peg you are going to instead of a bunch of coordinates.
     * @param peg What peg you are approaching
     * @return the point right before the peg (is perpendicular to the face of the airship)
     */
    public static double[] perpendicular_To_Airship (double[] peg){
        //Are you sure you wanted to use == instead of .equals?  With .equals, you have the option to input "new double[]{11, 14}" as peg and it would select the left peg option.
        if(peg.equals(LEFT_GEAR_PEG)){
             return perpendicular(new double[]{10.16375, 12.26625, 11.831875, 15.2075});
        }
        else if(peg.equals(RIGHT_GEAR_PEG)){
            return perpendicular(new double[]{16.83625, 12.26625, 15.1681225, 15.2075}, -1);
        }
        else if(peg.equals(CENTER_GEAR_PEG)){
            return perpendicular(new double[]{11.831875, 9.325, 15.1681225, 9.325}, -1);
        }
        else{
            return peg;
        }
    }
}