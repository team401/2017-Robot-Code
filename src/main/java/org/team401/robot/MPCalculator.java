package org.team401.robot;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Brian Jameson on 1/16/2017.
 */
public class MPCalculator {
    public static void main(String[] args){
        double[][] path = new double[][]{
                {0, 0},
                {5, 10},
                {10, 10},
                {20, 10},
                {50, 30}
        };
        FalconLinePlot gui = new FalconLinePlot(path);
        gui.xGridOn();
        gui.yGridOn();
        FalconPathPlanner falcon = new FalconPathPlanner(path);
        falcon.calculate(20, 0.02, 26);
        while(gui.isFocusable()){

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
