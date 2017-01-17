package org.team401.robot;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by Brian Jameson on 1/16/2017.
 */
public class MPCalculator {
    private static double[][] path;
    public static void main(String[] args){
        Scanner scan = new Scanner(System.in);
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

        System.out.println("Ready for commands.  Type help for help.");
        while(fig1.isFocusable()){
            if(scan.hasNext())
                takeInput(scan.nextLine());
        }

    }
    public static void takeInput(String input){
        switch(input.substring(0, input.indexOf(" "))){
            case "set":
                try {
                    input = input.substring(input.indexOf(" "));
                    int i = Integer.parseInt(input.substring(0, input.indexOf(" ")));
                    input = input.substring(input.indexOf(" "));
                    double x = Double.parseDouble(input.substring(0, input.indexOf(" ")));
                    input = input.substring((input.indexOf(" ")));
                    double y = Double.parseDouble(input);
                    path[i][0] = x;
                    path[i][1] = y;
                }catch(Exception e){
                    System.out.println("Invalid invocation of set method!");
                }
                break;
            case "help":
                System.out.println("set i x y:");
                System.out.println("\t@param i: Integer specifying index of array to modify");
                System.out.println("\t@params x, y: Doubles specifying new position in feet of previously specified point\n");
                break;
            default:
                System.out.println("Invalid method call!");
        }
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
