package org.team401.robot;


/**
 * Created by EJ on 1/23/2017.
 * This is a list off all the possible paths will may want to take in auto
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
            16, 8
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
            {11,14},
            perpendicular(new double[]{10, 12, 12, 15}),

    };
    //goes from the Middle starting position to the left gear lift
    public static final double[][] START_MID_TO_L_LIFT = new double[][]{
            STARTING_MID,
            {10, 4},
            {7, 8},
            {7, 12},
            {7, 14},
            {9, 16},
            LEFT_GEAR_PEG,
    };
    //test mecanum path
    public static final double[][] TEST_MECANUM = new double[][]{
            {13.5, 2, -1},
            {13.5, 8, 180}
    };

    /**
     * This takes a point, the endpoints of the line that point is on and returns a point
     * perpendicular to that point either going up or down depending on the direction
     *
     * @param x1 first endpoint of the line
     * @param x2 second endpoint of the line
     * @param y1 first endpoint of the line
     * @param y2 second endpoint of the line
     * @param pointx the point you want the line based off x value
     *
     * @param direction which way you want the line
     * @return the new point
     */
/*//I commented out this method because I don't like it.  -Brian
    public static double[] perpendicular(double x1, double x2, double y1, double y2, double pointx, double pointy, int direction){
    double slope = (y2 - y1)/(x2 - x1);

    double PerpendicularSlope = ((x2 - x1)/(y2 - y1)) * -1;

        switch(direction){
            case 0:
                pointx += (x2 - x1) * -1;
                pointy += (y2 - y1);
                break;
            case 1:
                pointx += (x2 - x1);
                pointy+= (y2 - y1) * -1;
                break;
            default:

        }
        double[] newPoint = new double[]{
                pointx, pointy
        };
        return newPoint;
    }
*/

    /**
     * Returns the coordinates of C, where C is the vertex of a triangle and A and B are known points with angles of 45 degrees.
     * Slightly modified from http://math.stackexchange.com/questions/1842614/find-third-coordinate-for-a-right-triangle-with-45degree-angles
     *
     * @param coords The coordinates of the points.  Should be in the order [x1, y1, x2, y2].
     * @param factor Scales the result to the correct length
     * @return
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
}