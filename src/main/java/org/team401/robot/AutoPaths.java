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

    public static double[] Perpendicular(double x1, double x2, double y1, double y2, double pointx, double pointy, int direction){
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
}
