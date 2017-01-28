package org.team401.robot;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class MPCalculator {
	public static void main(String[] args) throws FileNotFoundException {

		//******************************
		//Add what paths you want here
		//******************************
		//DONT TOUCH THIS ONE!
		double[][] path = new double[][]{{0, 0}};

		//ADD YOUR PATHS HERE:
		double[][][] paths = new double[][][]{
				AutoPaths.RIGHT_GEAR_PEG_TO_SHOOTING_POSITION_REVERSE,
				AutoPaths.RIGHT_GEAR_PEG_TO_SHOOTING_POSITION_2
		};

		//add the different paths we are using here
		FalconPathPlanner falcon = new FalconPathPlanner(path);
		falcon.calculate(15, 0.02, 2.16666);


		//test mecanum mode
		FalconPathPlanner mecaFalcon = new FalconPathPlanner(AutoPaths.TEST_MECANUM, true);
		mecaFalcon.calculate(15, 0.02, 2.16666);

		//adds the velocity graphs
		Velocities(paths);

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
		fig2.addData(Field.AIRSHIP, Color.black);
		fig2.addData(Field.BASELINE, Color.blue);
		fig2.addData(Field.NEUTRAL_ZONE, Color.green);
		fig2.addData(Field.KEY_BLU, Color.black);
		fig2.addData(Field.RETRIEVAL_ZONE_BLU, Color.black);

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
		fig3.addData(Field.AIRSHIP, Color.black);
		fig3.addData(Field.BASELINE, Color.blue);
		fig3.addData(Field.NEUTRAL_ZONE, Color.green);
		fig3.addData(Field.KEY_RED, Color.black);
		fig3.addData(Field.RETRIEVEAL_ZONE_RED, Color.black);

		//adds the data to our graph
		AddMotionProfile(fig3, falcon);


		//Exports raw speed controller instructions as 6 .csv spreadsheets.

		mecaFalcon.exportCSV();
		Export(paths);


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

		for (double[] u : path.smoothCenterVelocity)
			if (time >= u[0])
				return u[1];
		return result;
	}

	/**
	 * Adds the data for the motion profile paths to the figure specified
	 *
	 * @param fig    what figure to add the data to
	 * @param falcon what path we are adding
	 */
	public static void AddMotionProfile(FalconLinePlot fig, FalconPathPlanner falcon) {

		fig.addData(falcon.smoothPath, Color.red, Color.blue);
		fig.addData(falcon.leftPath, Color.magenta);
		fig.addData(falcon.rightPath, Color.magenta);
	}

	/**
	 * Plots our velocity graph
	 *
	 * @param fig    what figure to add the data to
	 * @param falcon what path we are adding
	 */
	public static void AddVelocityProfile(FalconLinePlot fig, FalconPathPlanner falcon) {

		fig.addData(falcon.smoothCenterVelocity, Color.red, Color.blue);
		fig.addData(falcon.smoothLeftVelocity, Color.red);
		fig.addData(falcon.smoothRightVelocity, Color.magenta);
	}

	/**
	 * Takes a 3D array of the paths you want in your graph and calculates then adds them to the graph you specify
	 * <p>
	 * NOTE: Only works for the wheel paths, does not do velocity graphs
	 *
	 * @param listOfPaths the 3D array housing your paths
	 * @param figure      what graph you want the paths added to
	 */
	public static void AddPaths(double[][][] listOfPaths, FalconLinePlot figure) {
		for (double[][] u : listOfPaths) {
			FalconPathPlanner falconPathPlanner = new FalconPathPlanner(u);
			falconPathPlanner.calculate(15, 0.02, 2.16666);

			figure.addData(falconPathPlanner.smoothPath, Color.red, Color.blue);
			figure.addData(falconPathPlanner.leftPath, Color.magenta);
			figure.addData(falconPathPlanner.rightPath, Color.magenta);


		}
	}

	/**
	 * Exports the CSV files for each motion profile
	 * @param listOfPaths the paths of the robot
	 * @throws FileNotFoundException
	 */
	public static void Export(double[][][] listOfPaths) throws FileNotFoundException {
		for (double[][] u : listOfPaths) {
			FalconPathPlanner falconPathPlanner = new FalconPathPlanner(u);
			falconPathPlanner.calculate(15, 0.02, 2.16666);
			falconPathPlanner.exportCSV();
		}

	}

	/**
	 * Calculates the velocities of the paths, then creates the graphs and plots the velocities on them
	 * @param paths the paths of the robot
	 */
	public static void Velocities(double[][][] paths) {

        for(double[][] u:paths){
            
            String name = AutoPaths.getName(u);

            FalconPathPlanner falconPathPlanner = new FalconPathPlanner(u);
            falconPathPlanner.calculate(15, 0.02, 2.16666);

            FalconLinePlot fig1 = new FalconLinePlot(falconPathPlanner.smoothCenterVelocity, null, Color.green);
            fig1.xGridOn();
            fig1.yGridOn();
            fig1.setTitle("Velocities (" + name + ") \n Center = blue, Left = red, Right = magenta");
            fig1.setXLabel("Time (seconds)");
            fig1.setYLabel("Velocity (ft/sec)");


			//adds the data to the graph
			fig1.addData(falconPathPlanner.smoothCenterVelocity, Color.red, Color.blue);
			fig1.addData(falconPathPlanner.smoothLeftVelocity, Color.red);
			fig1.addData(falconPathPlanner.smoothRightVelocity, Color.magenta);
		}
	}
}
