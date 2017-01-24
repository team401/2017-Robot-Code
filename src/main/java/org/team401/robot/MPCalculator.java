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
        //IF YOU CHANGE ANY OF THE VALUES MAKE SURE TO CHANGE THEM IN FIG2 AS WELL!!!!
        //******************************
        FalconPathPlanner falcon = new FalconPathPlanner(AutoPaths.START_MID_TO_R_LIFT);
        //in feet
        falcon.calculate(15, 0.02, 2.16666);

        //test mecanum mode
        FalconPathPlanner mecaFalcon = new FalconPathPlanner(AutoPaths.TEST_MECANUM, true);
        mecaFalcon.calculate(15, 0.02, 2.16666);

        FalconLinePlot fig1 = new FalconLinePlot(falcon.smoothCenterVelocity, null, Color.blue);
        fig1.xGridOn();
        fig1.yGridOn();
        fig1.setTitle("Veloccities of the wheels and the center \n Center = blue, Left = magenta, Right = cyan");
        fig1.setXLabel("Time (seconds)");
        fig1.setYLabel("Velocity (ft/sec)");

        fig1.addData(falcon.smoothLeftVelocity, Color.magenta);
        fig1.addData(falcon.smoothRightVelocity, Color.cyan);
        fig1.addData(falcon.smoothCenterVelocity, Color.blue);



        //Field map
        FalconLinePlot fig2 = new FalconLinePlot(AutoPaths.START_MID_TO_R_LIFT);
        fig2.xGridOn();
        fig2.yGridOn();
        fig2.setTitle("2017 Field Map\nNote: Size may be distorted slightly");
        fig2.setXLabel("Width of the Field (feet)");
        fig2.setYLabel("Length of the Field (feet)");
        //filed size: x: 54 ft y: 27 ft
        fig2.setXTic(0, 27, 1);
        fig2.setYTic(0, 39, 1);

        fig2.addData(falcon.smoothPath, Color.red, Color.blue);
        fig2.addData(falcon.leftPath, Color.magenta);
        fig2.addData(falcon.rightPath, Color.magenta);



        fig2.addData(airship, Color.black);
        fig2.addData(baseline, Color.blue);
        fig2.addData(neutralZone, Color.green);

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
}
