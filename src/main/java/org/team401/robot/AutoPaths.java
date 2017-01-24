package org.team401.robot;


/**
 * Created by EJ on 1/23/2017.
 * This is a list off all the possible paths will may want to take in auto
 */
public class AutoPaths {


    //points for final waypoints
//if the points vary depending on the size of our robot
    public static final double[] CenterGearPeg = new double[]{
            13.5, 8
    };
    public static final double[] LeftGearPeg = new double[]{
            11, 14
    };
    public static final double[] RightGearPeg = new double[]{
            16, 8
    };
    public static final double[] StartingMid = new double[]{
            13.5, 2
    };
    public static final double[] StartingLeft = new double[]{
            5, 2
    };
    public static final double[] StartingRight = new double[]{
            22, 2
    };

/*
*******************************
Waypoint Paths:

*******************************
 */


    //goes from the middle starting position to the center gear lift
    public static final double[][] StartMidToLift = new double[][]{
            StartingMid,
            CenterGearPeg,
    };
    //goes from the middle starting position to the right gear lift
    public static final double[][] StartMidToRLift = new double[][]{
            StartingRight
            //RightGearPeg,
    };
    //goes from the Middle starting position to the left gear lift
    public static final double[][] StartMidToLLift = new double[][]{
            StartingMid,
            {10, 4},
            {7, 8},
            {7, 12},
            {7, 14},
            {9, 16},
            LeftGearPeg,
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
        return new double[]{
                (coords[0] + coords[2]) / 2.0 + (coords[1] - coords[3]) / 2.0 * factor,
                (coords[1] + coords[3]) / 2.0 + (coords[2] - coords[0]) / 2.0 * factor
        };
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