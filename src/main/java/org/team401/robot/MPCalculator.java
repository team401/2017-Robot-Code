package org.team401.robot;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Brian Jameson on 1/16/2017.
 */
public class MPCalculator {
    public static void main(String[] args){
        //numbers are in feet
        double[][] path = new double[][]{
                {20, 0},
                {20, 10},
                {10, 10},
                {10, 5},
                {10, 0}
        };
        double[][] path2 = new double[][]{
                {1,1},
                {1,1},
                {1,1},
                {1,1},
                {1,1},
        };
        FalconLinePlot fig1 = new FalconLinePlot(path);
        fig1.xGridOn();
        fig1.yGridOn();
        fig1.setTitle("Figure 1");
        fig1.setXLabel("X (feet)");
        fig1.setYLabel("Y (feet)");
        //Field map
        FalconLinePlot fig2 = new FalconLinePlot(path2);
        fig2.xGridOn();
        fig2.yGridOn();
        fig2.setTitle("2017 Field Map");
        fig2.setXLabel("Width of the Field (feet)");
        fig2.setYLabel("Length of the Field (feet)");
        fig2.setXTic(0, 54 , 1);
        fig2.setYTic(0, 27, 1);
        


        FalconPathPlanner falcon = new FalconPathPlanner(path);
        //in feet
        falcon.calculate(20, 0.02, 2.16666);
        fig1.addData(falcon.smoothPath, Color.red, Color.blue);
        fig1.addData(falcon.leftPath, Color.magenta);
        fig1.addData(falcon.rightPath, Color.magenta);
        while(fig1.isFocusable()){

        }

    }
    public static void write(String filename, double[][] arr) throws IOException{
        BufferedWriter output = new BufferedWriter(new FileWriter(filename));
        for(int i = 0; i < arr.length; i++){
            for(int j = 0; j < arr[i].length; j++) {
                output.write("" + arr[i][j] + ",");
            }
        }
        output.flush();
        output.close();
    }
}
