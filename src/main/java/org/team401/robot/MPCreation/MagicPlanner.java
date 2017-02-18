package org.team401.robot.MPCreation;

import org.strongback.util.Values;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class MagicPlanner {
	//Is this using mecanum logic?
	public final boolean mecanum;

	//Original and smoothed paths
	public double[][] origPath,
			leftPath,
			rightPath;

	//Original and smoothed velocity
	public double[][] origCenterVelocity,
			origRightVelocity,
			origLeftVelocity;

	//Various doubles.
	public double numFinalPoints;//How many points are in the smooth paths/velocities;

	//Mecanum defaults to false
	public MagicPlanner(double[][] path) {
		this(path, false);
	}

	/**
	 * Constructor.  Takes a 2D array of cartesian x/y coordinates(feet) and sets up the object for processing.
	 *
	 * @param path Waypoints the robot will travel to
	 * @param mecanum Specifies if we are in mecanum or tank drive
	 */
	public MagicPlanner(double[][] path, boolean mecanum) {
		//Save instance data
		origPath = doubleArrayCopy(path);
		this.mecanum = mecanum;

		//Cut angles to a 0-359 range if applicable
		if(mecanum)
			for(double[] u:path) {
				while (u[2] < 0)
					u[2] += 360;
				while (u[2] > 359)
					u[2] -=360;
			}
	}

	/**
	 * Performs a deep copy of a 2D Array
	 *
	 * @param arr Array to copy
	 * @return Reference to separate but equal array
	 */
	private static double[][] doubleArrayCopy(double[][] arr) {
		//size first dimension of array
		double[][] temp = new double[arr.length][arr[0].length];

		for (int i = 0; i < arr.length; i++) {
			//Resize second dimension of array
			temp[i] = new double[arr[i].length];

			//Copy Contents
			System.arraycopy(arr[i], 0, temp[i], 0, arr[i].length);
		}
		return temp;
	}

	/**
	 * Inverts the velocities of a path so that the robot will know to go backwards in it
	 *
	 * @param vel Original velocity array
	 * @return All velocities are *='d by -1
	 */
	private double[][] invertVelocity(double[][] vel) {
		double[][] result = doubleArrayCopy(vel);
		for (double[] x:result)
			x[1] *= -1;
		return result;
	}

	//ratio defaults to the result of getRatio
	private double[] tankProfile(boolean left){
		return tankProfile(left, getRatio());
	}

	/**
	 * Transforms the tank drive data to a readable format by CTRE's example project and our 2017 code.
	 *
	 * @param left  Do we return the motion profile for left or right side of robot?
	 * @param ratio Transforms feet/second into RPM
	 * @return 2D array.  3 columns: Position(rotations), Velocity(RPM), Duration(ms)
	 */
	private double[] tankProfile(boolean left, double ratio) {
		//Declare sources and result.  Switch depending on wheel.
		double[][] velocity = left ? origLeftVelocity : origRightVelocity,
				path = left ? leftPath : rightPath;
		double[] result = new double[velocity.length];

		//Encoder should be 0 at start.  If it isn't, reset the encoder when starting each MP.
		double dist = 0, x, y;

		//First point is zeroed
		result[0] = 0;

		//Construct each entry in the result
		for (int i = 1; i < velocity.length; i++) {
			result[i] = 0;//please write some code to actually handle this//new double[]{dist, velocity[i][1] * ratio, (velocity[i][0] - velocity[i - 1][0])*1000};

			//Increment distance through pythag
			x = path[i][0] - path[i - 1][0];
			y = path[i][1] - path[i - 1][1];
			dist += ratio * Math.sqrt(Math.abs(x * x + y * y));//Add distance between last and current points using Pythag.  Math.abs ensures no errors.
		}

		//Round times to make saved data smaller
		roundall(result);
		return result;
	}

	/**
	 * Stores in an easily modifiable manner the robot's gear ratios so we can convert units.
	 * @return Single scale factor between feet traveled and rotations
	 */
	private double getRatio(){
		double[] ratios = new double[]{
				3.0 / Math.PI,//Reciprocal of wheel circumference(feet)
				mecanum ? 1.0 : 0.5//Divide by 2 if in traction drive
		};
		double result = 1;
		for (double x:ratios)
			result*=x;
		return result;
	}

	//ratio defaults to the result of getRatio
	private double[][] mecanumProfile(){
		return mecanumProfile(getRatio());//Default to the ratio defined above
	}

	/**
	 * Transforms data into 4 motion profiles, each for a different mecanum wheel using polar drive.
	 *
	 * @param ratio Transforms feet/second into RPM
	 * @return Front left, front right, rear left, and rear right motion profiles in the same format as tankProfile().
	 */
	private double[][] mecanumProfile(double ratio) {
		//Declare sources and result.
		double[][] result = new double[4][(int) numFinalPoints];
		double[][] path = doubleArrayCopy(origPath),
				vel = doubleArrayCopy(origCenterVelocity);

		for (int h = 0; h < 4; h++) {
			//For each wheel, zero distance traveled, first point data, time interval, and final velocity.
			double dist = 0.0;
			result[h][0] = 0.0;
			double velocity = 0.0,
					interval = 0.0;

			//For each entry in the profile, produce:
			for (int i = 1; i < numFinalPoints; i++) {
				//the total distance traveled by that point(rotations)
				dist += velocity*interval / 60;

				//the current velocity(RPM)
				velocity = polarMecanum(vel[i][1]*ratio, Math.atan(path[i][0] / path[i][1]), path[i][2])[h];

				//and change in time(s)
				interval = (vel[i][0]-vel[i-1][0]);

				//pack it in for export
				result[h][i] = dist;
			}
		}
		//Round times to make saved data smaller
		for(double[] u:result)
			roundall(u);
		return result;
	}

	/**
	 * Modified slightly from the Strongback source code.
	 *
	 * @param mag The magnitude to move at
	 * @param dir The direction the robot will move in
	 * @param rot What direction the robot is trying to face
	 * @return Array of each wheel's output
	 */
	private double[] polarMecanum(double mag, double dir, double rot) {
		// Normalized for full power along the Cartesian axes.
		mag = Values.symmetricLimiter(0.02, 1.0).applyAsDouble(mag) * Math.sqrt(2.0);

		// The rollers are at 45 degree angles.
		double dirInRad = Math.toRadians(dir + 45.0);
		double cosD = Math.cos(dirInRad);
		double sinD = Math.sin(dirInRad);

		//Return the vectors for each wheel
		return new double[]{
				(sinD * mag + rot),//LEFT FRONT
				(cosD * mag - rot),//RIGHT FRONT
				(cosD * mag + rot),//LEFT REAR
				(sinD * mag - rot)//RIGHT REAR
		};
	}

	/**
	 * Creates and saves a .csv file containing the input.
	 *
	 * @param fileName Name of the file
	 * @param arr Array to be saved
	 */
	private static void buildCSV(String fileName, double[] arr){
		try {
			PrintWriter pw = new PrintWriter(new File(fileName + ".csv"));

			//If you haven't heard of StringBuilder, it concatenates strings far more efficiently.
			StringBuilder sb = new StringBuilder();
			for (double u : arr) {
					sb.append(u);
					sb.append('\n');
			}
			pw.write(sb.toString());
			pw.close();
		}catch(FileNotFoundException e){
			//Error message.  Probably not the only possible cause of the exception, but it's the only one I've encountered.
			System.out.println("File \""+fileName+".csv\" is being used by another program!  Close the other program and restart Motion Profile Generator.");
		}
	}

	//prefix and suffix default to empty strings(suffix first)
	public void exportCSV(){
		exportCSV("");
	}
	public void exportCSV(String prefix){
		exportCSV(prefix, "");
	}

	/**
	 * Exports the data from this path planner as 6 .csv files, all fully capable of being used as motion profile instructions.
	 *
	 * @param prefix Prefix to add to the start of the filename
	 * @param suffix Suffix to add to the end of the filename
	 */
	public void exportCSV(String prefix, String suffix){
		if(mecanum) {
			//Export 4 wheels if in mecanum drive.
			double[][] temp = mecanumProfile();
			buildCSV(prefix+" FL"+suffix, temp[0]);
			buildCSV(prefix+" FR"+suffix, temp[1]);
			buildCSV(prefix+" RL"+suffix, temp[2]);
			buildCSV(prefix+" RR"+suffix, temp[3]);
		}else {
			//Each side can use the same profile in tank drive.
			buildCSV(prefix+" L"+suffix, tankProfile(true));
			buildCSV(prefix+" R"+suffix, tankProfile(false));
		}
	}

	/**
	 * Rounds every value in the third column of a 2D array.
	 *
	 * @param x The array to round
	 */
	private void roundall(double[] x) {
		//This method really doesn't need to exist.  I guess I just felt like extra work.
		double[] result = new double[x.length];
		for(int i = 0; i < x.length; i++)
			result[i] = Math.round(x[i]);
	}
}
