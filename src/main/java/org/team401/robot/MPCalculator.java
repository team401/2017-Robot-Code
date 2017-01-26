package org.team401.robot;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class MPCalculator {
    public static void main(String[] args) throws FileNotFoundException{

        //numbers are in feet


        //draws the near airship:
        double[][] airship = new double[][]{
                {11.831875, 9.325},
                {15.1681225, 9.325},
                {16.83625, 12.26625},
                {15.1681225, 15.2075},
                {11.831875, 15.2075},
                {10.16375, 12.26625},
                {11.831875, 9.325},
        };
        double[][] baseline = new double[][]{
                {0, 7.775},
                {27, 7.775}
        };
        double[][] neutralZone = new double[][]{
                {0, 15.1681225},
                {27, 15.1681225},
                {27, 54 - 15.1681225},
                {0, 54 - 15.1681225},
                {0, 15.1681225}
        };

        //calculates our path
        //******************************
        //Add what paths you want here
        //******************************
        FalconPathPlanner falcon = new FalconPathPlanner(AutoPaths.START_MID_TO_L_LIFT);
        falcon.calculate(15, 0.02, 2.16666);
        //in feet


        //test mecanum mode
        FalconPathPlanner mecaFalcon = new FalconPathPlanner(AutoPaths.TEST_MECANUM, true);
        mecaFalcon.calculate(15, 0.02, 2.16666);

        //creates the velocity graph
        FalconLinePlot fig1 = new FalconLinePlot(falcon.smoothCenterVelocity, null, Color.blue);
        fig1.xGridOn();
        fig1.yGridOn();
        fig1.setTitle("Veloccities of the wheels and the center \n Center = blue, Left = magenta, Right = cyan");
        fig1.setXLabel("Time (seconds)");
        fig1.setYLabel("Velocity (ft/sec)");

        //adds the data to the graph
        AddVelocityProfile(fig1, falcon);


        //Field map
        FalconLinePlot fig2 = new FalconLinePlot(AutoPaths.START_MID_TO_L_LIFT);
        fig2.xGridOn();
        fig2.yGridOn();
        fig2.setTitle("2017 Field Map\nNote: Size may be distorted slightly");
        fig2.setXLabel("Width of the Field (feet)");
        fig2.setYLabel("Length of the Field (feet)");
        //filed size: x: 54 ft y: 27 ft
        fig2.setXTic(0, 27, 1);
        fig2.setYTic(0, 39, 1);


        fig2.addData(airship, Color.black);
        fig2.addData(baseline, Color.blue);
        fig2.addData(neutralZone, Color.green);

        //adds the data to our graph
        AddMotionProfile(fig2, falcon);

        //Exports raw speed controller instructions as 6 .csv spreadsheets.
        if(false) {
            falcon.exportCSV();

        }

    }

    /**
     * Gives you the velocity of the robots center at a given time.
     *
     * @param path the centerpath of the robot
     * @param time the time you want to know the velocity at in seconds
     * @return returns the velocity of the center at the time requested
     */
    public static double InstantVelocity(FalconPathPlanner path, double time) {
        double result = 0;

        for (int i = 0; i < path.smoothCenterVelocity.length; i++)
            if (time >= path.smoothCenterVelocity[i][0])
                return path.smoothCenterVelocity[i][1];
        return result;
    }
    public static void AddMotionProfile(FalconLinePlot fig, FalconPathPlanner falcon){

        fig.addData(falcon.smoothPath, Color.red, Color.blue);
        fig.addData(falcon.leftPath, Color.magenta);
        fig.addData(falcon.rightPath, Color.magenta);
    }
    public static void AddVelocityProfile(FalconLinePlot fig, FalconPathPlanner falcon){

        fig.addData(falcon.smoothCenterVelocity, Color.red, Color.blue);
        fig.addData(falcon.smoothLeftVelocity, Color.red);
        fig.addData(falcon.smoothRightVelocity, Color.magenta);
    }
}
