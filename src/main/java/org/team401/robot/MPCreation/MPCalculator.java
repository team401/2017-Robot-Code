package org.team401.robot.MPCreation;

import java.awt.*;
import java.io.FileNotFoundException;

public class MPCalculator {
	public static void main(String[] args){

		//******************************
		//Add what paths you want here
		//******************************
		//DONT TOUCH THIS ONE!
		double[][] path = {{0, 0}};

		//ADD YOUR PATHS HERE:
		double[][][] paths = {
				AutoPaths.START_LEFT_TO_LIFT,
				AutoPaths.START_LEFT_TO_L_LIFT,
				AutoPaths.START_LEFT_TO_R_LIFT,
				AutoPaths.START_MID_TO_LIFT,
				AutoPaths.START_MID_TO_L_LIFT,
				AutoPaths.START_MID_TO_R_LIFT,
				AutoPaths.START_RIGHT_TO_LIFT,
				AutoPaths.START_RIGHT_TO_L_LIFT,
				AutoPaths.START_RIGHT_TO_R_LIFT,
				AutoPaths.LEFT_GEAR_PEG_TO_LEFT_HOPPER_REVERSE_R,
				AutoPaths.RIGHT_GEAR_PEG_TO_RIGHT_HOPPER_REVERSE_R,
				AutoPaths.LEFT_GEAR_PEG_TO_LEFT_HOPPER_REVERSE_B,
				AutoPaths.RIGHT_GEAR_PEG_TO_RIGHT_HOPPER_REVERSE_B,
				AutoPaths.CENTER_GEAR_PEG_TO_LEFT_HOPPER_REVERSE_R,
				AutoPaths.CENTER_GEAR_PEG_TO_RIGHT_HOPPER_REVERSE_B
		};
		mecanumInject(paths);


		//System.out.println(AutoPaths.perpendicular_To_Airship(AutoPaths.CENTER_GEAR_PEG, 2));
		//System.out.println(AutoPaths.perpendicular_To_Airship(AutoPaths.CENTER_GEAR_PEG, 1));
		//add the different paths we are using here
		FalconPathPlanner falcon = new FalconPathPlanner(path);
		falcon.calculate(15, 0.02, 2.16666);
		
		//adds the velocity graphs
		//velocities(paths);

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
		fig2.addData(Field.LEFTHOPPERS_B, Color.black);
		fig2.addData(Field.RIGHTHOPPERS_B, Color.black);

		//adds the data to our graph
		addPaths(paths, fig2, true);
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
		fig3.addData(Field.LEFTHOPPERS_R, Color.black);
		fig3.addData(Field.RIGHTHOPPERS_R, Color.black);


		//adds the data to our graph
		addPaths(paths, fig3, true);



		//Exports raw speed controller instructions as 6 .csv spreadsheets.

		export(paths, true);


	}

	/**
	 * Gives you the velocity of the robots center at a given time.
	 *
	 * @param path the centerpath of the robot
	 * @param time the time you want to know the velocity at in seconds
	 * @return returns the velocity of the center at the time requested
	 */
	public static double instantVelocity(FalconPathPlanner path, double time) {
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
	public static void addMotionProfile(FalconLinePlot fig, FalconPathPlanner falcon) {

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
	public static void addVelocityProfile(FalconLinePlot fig, FalconPathPlanner falcon) {

		fig.addData(falcon.smoothCenterVelocity, Color.red, Color.blue);
		fig.addData(falcon.smoothLeftVelocity, Color.red);
		fig.addData(falcon.smoothRightVelocity, Color.magenta);
	}

	/**
	 * Takes a 3D array of the paths you want in your graph and calculates then adds them to the graph you specify
	 *
	 * NOTE: Only works for the wheel paths, does not do velocity graphs
	 *
	 * @param listOfPaths the 3D array housing your paths
	 * @param figure      what graph you want the paths added to
	 */
	public static void addPaths(double[][][] listOfPaths, FalconLinePlot figure, boolean mecanum) {
		for (double[][] u : listOfPaths) {
			FalconPathPlanner falconPathPlanner = new FalconPathPlanner(u);
			falconPathPlanner.calculate(15, 0.02, 2.16666, mecanum);

			figure.addData(falconPathPlanner.smoothPath, Color.red, Color.blue);
			figure.addData(falconPathPlanner.leftPath, Color.magenta);
			figure.addData(falconPathPlanner.rightPath, Color.magenta);
		}
	}

	/**
	 * Exports the CSV files for each motion profile
	 *
	 * @param listOfPaths the paths of the robot
	 * @throws FileNotFoundException
	 */
	public static void export(double[][][] listOfPaths, boolean braces){
		for (double[][] u : listOfPaths) {
			String name = AutoPaths.getName(u);
			FalconPathPlanner falconPathPlannerMech = new FalconPathPlanner(u, true);
			falconPathPlannerMech.calculate(15, 0.02, 2.16666);
			falconPathPlannerMech.exportCSV(name, "", braces);

			FalconPathPlanner falconPathPlanner = new FalconPathPlanner(u, false);
			falconPathPlanner.calculate(15, 0.02, 2.16666);
			falconPathPlanner.exportCSV(name, "", braces);

		}

	}

	/**
	 * Calculates the velocities of the paths, then creates the graphs and plots the velocities on them
	 *
	 * @param paths the paths of the robot
	 */
	public static void velocities(double[][][] paths) {

		for (double[][] u : paths) {

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

	/**
	 * Addes the mecanum direction value to the paths. Run immediately after the paths array is initialized.
	 *
	 * @param paths the different paths you're running
	 */
	public static void mecanumInject(double[][][] paths){

		for(int i = 0;i<paths.length;i++){
			double finalRotate = paths[i][paths[i].length - 1][2];
			double rotation = 0;
			String name = AutoPaths.getName(paths[i]);
			if(!(name.equals("LHCB")) || !(name.equals("LHCR")) || !(name.equals("RHCB")) || !(name.equals("RHCR"))) {
				for (int j = 0; j < paths[i].length - 3; j++) {
					rotation += finalRotate / paths[i].length - 3;
					paths[i][j + 1][2] = rotation;
				}
			}
		}
	}
}
