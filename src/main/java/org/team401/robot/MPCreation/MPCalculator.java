package org.team401.robot.MPCreation;

import java.awt.Color;
import java.awt.GraphicsEnvironment;

/**
 * Run this class as an executable.
 * It will produce graphs of where the robot will travel during Autonomous and .csv files
 * with motor instructions to move along those paths.
 */

public class MPCalculator {
	//Width of the robot for Traction mode
	private static double robotWidth = 1.9375;

	/**
	 * Set this as the main method for execution.
	 * @param args Input to this is useless.
	 */
	public static void main(String[] args) {
        //abort if the graphs can't run (needs a display)
        if(GraphicsEnvironment.isHeadless()) {
            System.out.println("No display! Aborting...");
            return;
        }

		//Constant that starts line plots correctly.
		final double[][] path = {{0, 0}};

		//Add all desired paths to this array.
		NamedPath[] paths = {
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
			AutoPaths.CENTER_GEAR_PEG_TO_RIGHT_HOPPER_REVERSE_B,
			AutoPaths.STARTING_LEFT_TO_LEFT_HOPPER_R,
			AutoPaths.STARTING_LEFT_TO_LEFT_HOPPER_B,
			AutoPaths.STARTING_RIGHT_TO_RIGHT_HOPPER_R,
			AutoPaths.STARTING_RIGHT_TO_RIGHT_HOPPER_B,
			AutoPaths.STARTING_MID_TO_LEFT_HOPPER_R,
			AutoPaths.STARTING_MID_TO_LEFT_HOPPER_B,
			AutoPaths.STARTING_MID_TO_RIGHT_HOPPER_R,
			AutoPaths.STARTING_MID_TO_RIGHT_HOPPER_B,
			AutoPaths.LEFT_HOPPER_COLLECTION_R
		};

		//add the direction value for mecanum drive
		mecanumInject(paths);

		//adds the velocity graphs
		velocities(paths);

		//Field map from the blue alliance's perspective
		FalconLinePlot bluePlot = new FalconLinePlot(path);
		bluePlot.xGridOn();
		bluePlot.yGridOn();
		bluePlot.setTitle("2017 Field Map (From the blue alliance's perspective)\nNote: Size may be distorted slightly");
		bluePlot.setXLabel("Width of the Field (feet)");
		bluePlot.setYLabel("Length of the Field (feet)");

		//set field size
		bluePlot.setXTic(0, 27, 1);
		bluePlot.setYTic(0, 39, 1);

		//add the field elements
		bluePlot.addData(Field.AIRSHIP, Color.black);
		bluePlot.addData(Field.BASELINE, Color.blue);
		bluePlot.addData(Field.NEUTRAL_ZONE, Color.green);
		bluePlot.addData(Field.KEY_BLU, Color.black);
	    bluePlot.addData(Field.RETRIEVAL_ZONE_BLU, Color.black);
		bluePlot.addData(Field.LEFT_HOPPERS_B, Color.black);
		bluePlot.addData(Field.RIGHT_HOPPERS_B, Color.black);

		//adds the path data to our graph
		addPaths(paths, bluePlot, true);

		//Field map from the red alliance's perspective
		FalconLinePlot redPlot = new FalconLinePlot(path);
		redPlot.xGridOn();
		redPlot.yGridOn();
		redPlot.setTitle("2017 Field Map (From the red alliance's perspective)\nNote: Size may be distorted slightly");
		redPlot.setXLabel("Width of the Field (feet)");
		redPlot.setYLabel("Length of the Field (feet)");

		//set field size
        redPlot.setXTic(0, 27, 1);
		redPlot.setYTic(0, 39, 1);

		//add the field elements
		redPlot.addData(Field.AIRSHIP, Color.black);
		redPlot.addData(Field.BASELINE, Color.blue);
		redPlot.addData(Field.NEUTRAL_ZONE, Color.green);
		redPlot.addData(Field.KEY_RED, Color.black);
		redPlot.addData(Field.RETRIEVEAL_ZONE_RED, Color.black);
		redPlot.addData(Field.LEFT_HOPPERS_R, Color.black);
		redPlot.addData(Field.RIGHT_HOPPERS_R, Color.black);

		//adds the data to our graph
		addPaths(paths, redPlot, true);

		//Export raw speed controller instructions as .csv spreadsheets.
		export(paths);
	}

	/**
	 * Gives you the velocity of the robot's center at a given time.
	 *
	 * @param path the path of the robot's center
	 * @param time the time you want to know the velocity at(seconds)
	 * @return Velocity at the time requested
	 */
	public static double centerVelocity(FalconPathPlanner path, double time) {
		//Run through the path, testing each time against the desired.
		for (double[] u : path.smoothCenterVelocity)
			if (time >= u[0])
				return u[1];

		//Return 0 if the correct time wasn't found.
		return 0;
	}

	/**
	 * Takes a 3D array of the paths you want in your graph and calculates then adds them to the graph you specify
	 * Only works for the wheel paths, does not do velocity graphs
	 *
	 * @param listOfPaths the 3D array housing your paths
	 * @param figure what graph you want the paths added to
	 */
	public static void addPaths(NamedPath[] listOfPaths, FalconLinePlot figure, boolean mecanum) {
		for (NamedPath u : listOfPaths) {
		    //Create a planner for each path
			FalconPathPlanner falconPathPlanner = new FalconPathPlanner(u.getArr());
			falconPathPlanner.calculate(15, 0.02, robotWidth, mecanum);

			//Add to the path
			figure.addData(falconPathPlanner.smoothPath, Color.red, Color.blue);
			figure.addData(falconPathPlanner.leftPath, Color.magenta);
			figure.addData(falconPathPlanner.rightPath, Color.magenta);
		}
	}

	/**
	 * Exports the CSV files for each motion profile
	 *
	 * @param listOfPaths the paths of the robot
	 */
	public static void export(NamedPath[] listOfPaths) {
		for (NamedPath u : listOfPaths) {
			//exports the motion profile for each of the 4 motors for mecanum mode
			FalconPathPlanner fpp = new FalconPathPlanner(u.getArr(), true);
			fpp.calculate(u.getTime(), 0.02, robotWidth, u.getInvert());
			fpp.exportCSV("profilecsv\\mecanum\\" + u.getName());

			//exports the motion profile for each of the 2 motors for traction mode
			fpp = new FalconPathPlanner(u.getArr());
			fpp.calculate(u.getTime(), 0.02, robotWidth, u.getInvert());
			fpp.exportCSV("profilecsv\\tank\\" + u.getName());
		}
	}

	/**
	 * Calculates the velocities of the paths, then creates the graphs and plots the velocities on them
	 *
	 * @param paths the paths of the robot
	 */
	public static void velocities(NamedPath[] paths) {
		for (NamedPath u : paths) {
			//finds the name of the path
			String name = u.getName();

			//creates the object and does the calculations
			FalconPathPlanner falconPathPlanner = new FalconPathPlanner(u.getArr());
			falconPathPlanner.calculate(15, 0.02, robotWidth);

			//makes the graph for the motion profile
			FalconLinePlot fig = new FalconLinePlot(falconPathPlanner.smoothCenterVelocity, null, Color.green);
			fig.xGridOn();
			fig.yGridOn();
			fig.setTitle("Velocities (" + name + ") \n Center = blue, Left = red, Right = magenta");
			fig.setXLabel("Time (seconds)");
			fig.setYLabel("Velocity (ft/sec)");

			//adds the data to the graph
			fig.addData(falconPathPlanner.smoothCenterVelocity, Color.red, Color.blue);
			fig.addData(falconPathPlanner.smoothLeftVelocity, Color.red);
			fig.addData(falconPathPlanner.smoothRightVelocity, Color.magenta);
		}
	}

	/**
	 * Adds the mecanum direction value to the paths. Run immediately after the paths array is initialized.
	 *
	 * @param paths the different paths you're running
	 */
	public static void mecanumInject(NamedPath[] paths) {
		for (NamedPath u:paths) {
			//finds the end direction
			double finalRotate = u.getArr()[u.getArr().length - 1][2];

			//robot starts out facing 0 degrees
			double rotation = 0;

			//finds the name of the motion profile
			String name = u.getName();

			//check for the hopper collection paths, for they have their own directions
			if (!(name.equals("LHCB")) || !(name.equals("LHCR")) || !(name.equals("RHCB")) || !(name.equals("RHCR"))) {
				//add an equal amount of turning to each point for smooth rotation
				for (int j = 0; j < u.getArr().length - 3; j++) {
					rotation += finalRotate / u.getArr().length - 3;
					u.getArr()[j + 1][2] = rotation;
				}
			}
		}
	}
}