package org.team401.robot;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Math;

/**
 * Created by Brian Jameson on 1/16/2017.
 */
public class MPCalculator {
    private static double[][] path;
    public static void main(String[] args){
        //numbers are in feet

        double[][] path2 = new double[][]{
                {1,1},
                {1,2},
                {1,3},
                {1,4},
                {1,5},
        };
        //draws the airships:

        double [][] airship = new double [][]{
                {11.831875, 9.325},
                {15.1681225, 9.325},
                {16.83625, 12.26625},
                {15.1681225, 15.2075},
                {11.831875, 15.2075},
                {10.16375, 12.26625},
                {11.831875, 9.325},


        };
        double [][] airship2 = new double [][]{
                {11.831875, 54 - 9.325},
                {15.1681225, 54 - 9.325},
                {16.83625, 54 - 12.26625},
                {15.1681225, 54 - 15.2075},
                {11.831875, 54 - 15.2075},
                {10.16375, 54 - 12.26625},
                {11.831875, 54 - 9.325},

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





        FalconPathPlanner falcon = new FalconPathPlanner(path2);
        //in feet
        falcon.calculate(20, 0.02, 2.16666);


        //Field map
        FalconLinePlot fig2 = new FalconLinePlot(path2);
        fig2.xGridOn();
        fig2.yGridOn();
        fig2.setTitle("2017 Field Map Note: Size may be distorted slightly");
        fig2.setXLabel("Width of the Field (feet)");
        fig2.setYLabel("Length of the Field (feet)");
        //filed size: x: 54 ft y: 27 ft
        fig2.setXTic(0, 27 , 1);
        fig2.setYTic(0, 54, 1);

        fig2.addData(falcon.smoothPath, Color.red, Color.blue);
        fig2.addData(falcon.leftPath, Color.magenta);
        fig2.addData(falcon.rightPath, Color.magenta);


        fig2.addData(airship, Color.black);
        fig2.addData(airship2, Color.black);
        fig2.addData(baseline, Color.blue);
        fig2.addData(neutralZone, Color.green);







    }

    public static void write(String filename, double[][] arr) throws IOException{
        BufferedWriter output = new BufferedWriter(new FileWriter(filename));
        for(double[] u:arr){
            for(int j = 0; j < u.length; j++) {
                output.write("" + u[j] + ",");
            }
        }
        output.flush();
        output.close();
    }
}
