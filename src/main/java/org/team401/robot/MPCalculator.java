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
        double[][] key_blue = new double[][]{
                {3.036, 0},
                {9.525, 0},
                {0, 9.525},
                {0, 3.036},
                {3.036, 0}
        };
        double[][] retrevalZone_blue = new double[][]{
                {27, 5.735},
                {27, 13.79583},
                {27 - 7.30325, 0},
                {27 - 3.036, 0},
                {27, 5.735}
        };
        double[][] key_red = new double[][]{
                {27 - 3.036, 0},
                {27 - 9.525, 0},
                {27, 9.525},
                {27, 3.036},
                {27 - 3.036, 0}
        };
        double[][] retrevalZone_red = new double[][]{
                {0, 5.735},
                {0, 13.79583},
                {7.30325, 0},
                {3.036, 0},
                {0, 5.735}
        };

        //******************************
        //Add what paths you want here
        //******************************
        //DONT TOUCH THIS ONE!
        double[][] path = new double[][]{{0,0}};

        //ADD YOUR PATHS HERE:
        double[][][] paths = new double[][][]{
                AutoPaths.LEFT_GEAR_PEG_TO_SHOOTING_POSITION_REVERSE,
                AutoPaths.LEFT_GEAR_PEG_TO_SHOOTING_POSITION_2
        };

        //add the different paths we are using here
        FalconPathPlanner falcon = new FalconPathPlanner(path);
        falcon.calculate(15, 0.02, 2.16666);




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
        //Field map from the blue alliance's perspective
        FalconLinePlot fig2 = new FalconLinePlot(path);
        fig2.xGridOn();
        fig2.yGridOn();
        fig2.setTitle("2017 Field Map (From the blue alliance's perspective)\nNote: Size may be distorted slightly");
        fig2.setXLabel("Width of the Field (feet)");
        fig2.setYLabel("Length of the Field (feet)");
        //filed size: x: 54 ft y: 27 ft
        fig2.setXTic(0, 27, 1);
        fig2.setYTic(0, 39, 1);

        //adds the field elements the field
        fig2.addData(airship, Color.black);
        fig2.addData(baseline, Color.blue);
        fig2.addData(neutralZone, Color.green);
        fig2.addData(key_blue, Color.black);
        fig2.addData(retrevalZone_blue, Color.black);

        //adds the data to our graph
AddPaths(paths, fig2);
        //Field map from the red alliance's perspective
        FalconLinePlot fig3 = new FalconLinePlot(path);
        fig3.xGridOn();
        fig3.yGridOn();
        fig3.setTitle("2017 Field Map (From the red alliance's perspective)\nNote: Size may be distorted slightly");
        fig3.setXLabel("Width of the Field (feet)");
        fig3.setYLabel("Length of the Field (feet)");
        //filed size: x: 54 ft y: 27 ft
        fig3.setXTic(0, 27, 1);
        fig3.setYTic(0, 39, 1);

        //adds the field elements the field
        fig3.addData(airship, Color.black);
        fig3.addData(baseline, Color.blue);
        fig3.addData(neutralZone, Color.green);
        fig3.addData(key_red, Color.black);
        fig3.addData(retrevalZone_red, Color.black);

        //adds the data to our graph
        AddMotionProfile(fig3, falcon);


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

    /**
     * Adds the data for the motion profile paths to the figure specified
     * @param fig what figure to add the data to
     * @param falcon what path we are adding
     */
    public static void AddMotionProfile(FalconLinePlot fig, FalconPathPlanner falcon){

        fig.addData(falcon.smoothPath, Color.red, Color.blue);
        fig.addData(falcon.leftPath, Color.magenta);
        fig.addData(falcon.rightPath, Color.magenta);
    }

    /**
     * Plots our velocity graph
     * @param fig what figure to add the data to
     * @param falcon what path we are adding
     */
    public static void AddVelocityProfile(FalconLinePlot fig, FalconPathPlanner falcon){

        fig.addData(falcon.smoothCenterVelocity, Color.red, Color.blue);
        fig.addData(falcon.smoothLeftVelocity, Color.red);
        fig.addData(falcon.smoothRightVelocity, Color.magenta);
    }

    /**
     * Takes a 3D array of the paths you want in your graph and calculates then adds them to the graph you specify
     *
     * NOTE: Only works for the wheen paths, does not do velocity graphs
     *
     * @param listOfPaths the 3D array housing your paths
     * @param figure what graph you want the paths added to
     */
    public static void AddPaths(double[][][] listOfPaths, FalconLinePlot figure){
        for(int i = 0;i<listOfPaths.length;i++){
            FalconPathPlanner falconPathPlanner = new FalconPathPlanner(listOfPaths[i]);
            falconPathPlanner.calculate(15, 0.02, 2.16666);

            figure.addData(falconPathPlanner.smoothPath, Color.red, Color.blue);
            figure.addData(falconPathPlanner.leftPath, Color.magenta);
            figure.addData(falconPathPlanner.rightPath, Color.magenta);

        }
    }
}
