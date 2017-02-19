package org.team401.robot.MPCreation;

import org.strongback.util.Values;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;


/**
 * Modified from Kevin Harrilal's work at Team 2168.
 * Transforms a set of waypoints into a smooth robot path.
 * After calculate()ing, you can export Talon SRX motion profile instructions in .csv files for both tank and mecanum drives.
 */

public class FalconPathPlanner {
	//Is this using mecanum logic?
	public final boolean mecanum;

	//Original and smoothed paths
	public double[][] origPath,
		nodeOnlyPath,
		smoothPath,
		leftPath,
		rightPath;

	//Original and smoothed velocity
	public double[][] origCenterVelocity,
		origRightVelocity,
		origLeftVelocity,
		smoothCenterVelocity,
		smoothRightVelocity,
		smoothLeftVelocity;

	//Various doubles.
	public double numFinalPoints,//How many points are in the smooth paths/velocities
		pathAlpha,//How much the smoothing sticks to original waypoints
		pathBeta,//How aggressive the smoothing is
		pathTolerance,//How precise the smooth path must be.  Creates an infinite loop if too low.
		velocityAlpha,//Same as above but for velocity
		velocityBeta,
		velocityTolerance;

	//Mecanum defaults to false
	public FalconPathPlanner(double[][] path) {
		this(path, false);
	}

	/**
	 * Constructor.  Takes a 2D array of cartesian x/y coordinates(feet) and sets up the object for processing.
	 *
	 * @param path Waypoints the robot will travel to
	 * @param mecanum Specifies if we are in mecanum or tank drive
	 */
	public FalconPathPlanner(double[][] path, boolean mecanum) {
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

		//Default values for double instance data.  See above for description.
		pathAlpha = 0.7;
		pathBeta = 0.3;
		pathTolerance = 0.0000001;
		velocityAlpha = 0.1;
		velocityBeta = 0.3;
		velocityTolerance = 0.0000001;
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
	 * Supersamples the path by linear injection.
	 *
	 * @param orig The original array
	 * @param numToInject How many points we inject between each waypoint
	 * @return Array with more points in it
	 */
	private double[][] inject(double[][] orig, int numToInject) {
		//Initialize result and index of loop's progress in result
		double morePoints[][] = new double[orig.length + ((numToInject) * (orig.length - 1))][orig[0].length];
		int index = 0;

		for (int i = 0; i < orig.length - 1; i++) {
			//Copy the newest point in the original array
			morePoints[index][0] = orig[i][0];
			morePoints[index][1] = orig[i][1];
			if (mecanum)
				morePoints[index][2] = orig[i][2];
			index++;

			//Calculate intermediate points between j and j+1  originals
			for (int j = 1; j < numToInject + 1; j++) {
				morePoints[index][0] = j * ((orig[i + 1][0] - orig[i][0]) / (numToInject + 1)) + orig[i][0];//x
				morePoints[index][1] = j * ((orig[i + 1][1] - orig[i][1]) / (numToInject + 1)) + orig[i][1];//y
				if (mecanum)
					morePoints[index][2] = j * ((orig[i + 1][2] - orig[i][2]) / (numToInject + 1)) + orig[i][2];//rotation
				index++;
			}
		}

		//Copy last point in original and return
		morePoints[index][0] = orig[orig.length - 1][0];
		morePoints[index][1] = orig[orig.length - 1][1];
		if (mecanum) {
			morePoints[index][2] = orig[orig.length - 1][2];
			double[][] origs = doubleArrayCopy(morePoints);
			for(int i = 1; i < origs.length; i++)
				morePoints[i][2] -= origs[i-1][2];
		}
		return morePoints;
	}

	/**
	 * Optimizes the data points in path to create a smooth trajectory using gradient descent.
	 * While unlikely, it is possible for this algorithm to never converge. If this happens, try increasing the tolerance level.
	 *
	 * @param path Path to optimize
	 * @param weight_data Increases/decreases the importance of the original data
	 * @param weight_smooth Increases/decreases the importance of smooth movement
	 * @param tolerance How precise the resultant path needs to be
	 * @return A smoothed-out path, neatly curving between waypoints.
	 */
	private double[][] smooth(double[][] path, double weight_data, double weight_smooth, double tolerance) {
		//copy array and set process variable
		double[][] newPath = doubleArrayCopy(path);
		double change = tolerance;

		//Reset change every loop until it gets small enough to be tolerable
		while (change >= tolerance) {
			change = 0.0;

			//Loops through every value in the path except first row
			for (int i = 1; i < path.length - 1; i++)
				for (int j = 0; j < path[i].length; j++) {
					double aux = newPath[i][j];
					newPath[i][j] += weight_data * (path[i][j] - newPath[i][j]) + weight_smooth * (newPath[i - 1][j] + newPath[i + 1][j] - (2.0 * newPath[i][j]));
					change += Math.abs(aux - newPath[i][j]);
				}
		}
		return newPath;
	}

	/**
	 * Reduces the path into only nodes which change direction.
	 *
	 * @param path Path to simplify
	 * @return Simplified path
	 */
	public double[][] nodeOnlyWayPoints(double[][] path) {
		//Don't do anything in Mecanum mode, as the robot may rotate no matter what direction it's moving in.
		if(mecanum)
			return path;

		//Linked list allows variable size, perfect for something like this
		List<double[]> li = new LinkedList<>();

		//save first value
		li.add(path[0]);

		//find intermediate nodes
		for (int i = 1; i < path.length - 1; i++) {
			//calculate direction
			double vector1 = Math.atan2((path[i][1] - path[i - 1][1]), path[i][0] - path[i - 1][0]);
			double vector2 = Math.atan2((path[i + 1][1] - path[i][1]), path[i + 1][0] - path[i][0]);

			//determine if both vectors have a change in direction
			if (Math.abs(vector2 - vector1) >= 0.01)
				li.add(path[i]);
		}

		//save last point
		li.add(path[path.length - 1]);

		//re-write nodes into new 2D Array, which is returned
		double[][] temp = new double[li.size()][path[0].length];
		for (int i = 0; i < li.size(); i++) {
			temp[i][0] = li.get(i)[0];
			temp[i][1] = li.get(i)[1];
		}
		return temp;
	}

	/**
	 * Gets the velocity at a series of times.
	 *
	 * @param smoothPath Path to calculate from
	 * @param timeStep Time between each point(s)
	 * @return 2D Array. Column 0 stores time since path started(s). Column 1 stores velocity at that time.
	 */
	private double[][] velocity(double[][] smoothPath, double timeStep) {
		//Create vars and result array
		double dxdt, dydt;
		double[][] velocity = new double[smoothPath.length][2];

		//set first to zero
		velocity[0][0] = 0;
		velocity[0][1] = 0;

		for (int i = 1; i < smoothPath.length; i++) {
			//Calculate horizontal and vertical velocities separately, then merge into overall
			dxdt = (smoothPath[i][0] - smoothPath[i - 1][0]) / timeStep;
			dydt = (smoothPath[i][1] - smoothPath[i - 1][1]) / timeStep;
			velocity[i][1] = Math.sqrt(Math.pow(dxdt, 2) + Math.pow(dydt, 2));

			//Add time since last entry
			velocity[i][0] = velocity[i - 1][0] + timeStep;
		}
		return velocity;
	}

	/**
	 * Optimizes velocity, since there may be errors during smoothing.  If the algorithm doesn't converge, increase tolerance.
	 *
	 * @param smoothVelocity Previously smoothed velocity array with distance error at end
	 * @param origVelocity Original velocity array
	 * @param tolerance Precision of the output
	 * @return Fixed array
	 */
	private double[][] velocityFix(double[][] smoothVelocity, double[][] origVelocity, double tolerance) {
		//calculate error difference
		double[] difference = errorSum(origVelocity, smoothVelocity);

		//result array starts as a copy
		double[][] fixVel = doubleArrayCopy(smoothVelocity);

		//Correct the error until threshold is reached.  May cause infinite loop.
		double increase;
		while (Math.abs(difference[difference.length - 1]) > tolerance) {
			increase = difference[difference.length - 1] / 50;
			for (int i = 1; i < fixVel.length - 1; i++)
				fixVel[i][1] -= increase;
			difference = errorSum(origVelocity, fixVel);
		}
		return fixVel;
	}

	/**
	 * This method calculates the integral of the smooth velocity term and compares it to the integral of the
	 * original velocity term. This makes sure that the distance traveled is the same between the two.
	 *
	 * @param origVelocity Original veloicty array
	 * @param smoothVelocity Array after smoothing
	 * @return Amount of error between the two
	 */
	private double[] errorSum(double[][] origVelocity, double[][] smoothVelocity) {
		//copy vectors
		double[] tempOrigDist = new double[origVelocity.length];
		double[] tempSmoothDist = new double[smoothVelocity.length];
		double[] difference = new double[smoothVelocity.length];

		//Get how long between points
		double timeStep = origVelocity[1][0] - origVelocity[0][0];

		//copy first elements
		tempOrigDist[0] = origVelocity[0][1];
		tempSmoothDist[0] = smoothVelocity[0][1];

		//calculate and return difference
		for (int i = 1; i < origVelocity.length; i++) {
			tempOrigDist[i] = origVelocity[i][1] * timeStep + tempOrigDist[i - 1];
			tempSmoothDist[i] = smoothVelocity[i][1] * timeStep + tempSmoothDist[i - 1];
			difference[i] = tempSmoothDist[i] - tempOrigDist[i];
		}
		return difference;
	}

	/**
	 * Calculates the optimal parameters for determining what amount of nodes to inject into the path
	 * to meet the time restraint.
	 *
	 * @param numNodeOnlyPoints How many waypoints we need to travel
	 * @param maxTimeToComplete How much time we have to move(s)
	 * @param timeStep How much time in each command loop
	 */
	private int[] injectionCounter2Steps(double numNodeOnlyPoints, double maxTimeToComplete, double timeStep) {
		//reset point count and create process variables
		numFinalPoints = 0;
		int first = 0, second = 0, third = 0;
		double oldPointsTotal = 0, totalPoints = maxTimeToComplete / timeStep, pointsFirst, pointsTotal;
		int[] ret;

		//I can't be bothered to read this part of Harrilal's code, and it seems to work right, so too bad for comment-readers.
		if (totalPoints < 100) {
			for (int i = 4; i <= 6; i++)
				for (int j = 1; j <= 8; j++) {
					pointsFirst = i * (numNodeOnlyPoints - 1) + numNodeOnlyPoints;
					pointsTotal = (j * (pointsFirst - 1) + pointsFirst);
					if (pointsTotal <= totalPoints && pointsTotal > oldPointsTotal) {
						first = i;
						second = j;
						numFinalPoints = pointsTotal;
						oldPointsTotal = pointsTotal;
					}
				}
		} else {
			double pointsSecond;
			for (int i = 1; i <= 5; i++)
				for (int j = 1; j <= 8; j++)
					for (int k = 1; k < 8; k++) {
						pointsFirst = i * (numNodeOnlyPoints - 1) + numNodeOnlyPoints;
						pointsSecond = (j * (pointsFirst - 1) + pointsFirst);
						pointsTotal = (k * (pointsSecond - 1) + pointsSecond);

						if (pointsTotal <= totalPoints) {
							first = i;
							second = j;
							third = k;
							numFinalPoints = pointsTotal;
						}
					}
		}
		ret = new int[]{first, second, third};
		return ret;
	}

	/**
	 * Calculates the left and right wheel paths based on robot track width
	 *
	 * @param smoothPath      - center smooth path of robot
	 * @param robotTrackWidth - width between left and right wheels of robot's chassis.
	 */
	private void leftRight(double[][] smoothPath, double robotTrackWidth) {
		//Create result arrays and storage for how much the robot turns
		double[][] left = new double[smoothPath.length][2];
		double[][] right = new double[smoothPath.length][2];
		double[] gradient = new double[smoothPath.length];

		//Construct the direction array
		for (int i = 0; i < smoothPath.length - 1; i++)
			gradient[i] = Math.atan2(smoothPath[i + 1][1] - smoothPath[i][1], smoothPath[i + 1][0] - smoothPath[i][0]);
		gradient[gradient.length - 1] = gradient[gradient.length - 2];

		for (int i = 0; i < gradient.length; i++) {
			//Beef of processing.  Transforms the left and right x/y coords.
			left[i][0] = robotTrackWidth / 2 * Math.cos(gradient[i] + Math.PI / 2) + smoothPath[i][0];
			left[i][1] = robotTrackWidth / 2 * Math.sin(gradient[i] + Math.PI / 2) + smoothPath[i][1];
			right[i][0] = robotTrackWidth / 2 * Math.cos(gradient[i] - Math.PI / 2) + smoothPath[i][0];
			right[i][1] = robotTrackWidth / 2 * Math.sin(gradient[i] - Math.PI / 2) + smoothPath[i][1];

			//convert to degrees 0 to 360 where 0 degrees is positive x-axis
			double deg = Math.toDegrees(gradient[i]);
			gradient[i] = deg;

			//Correct for excess angle change
			if (i > 0) {
				if ((deg - gradient[i - 1]) > 180)
					gradient[i] = -360 + deg;
				if ((deg - gradient[i - 1]) < -180)
					gradient[i] = 360 + deg;
			}
		}
		//Push results to path vars
		leftPath = left;
		rightPath = right;
	}

	//invert defaults to false
	public void calculate(double totalTime, double timeStep, double robotTrackWidth) {
		calculate(totalTime, timeStep, robotTrackWidth, false);
	}

	/**
	 * Calls other methods to make a smooth path based on the program parameters(with defaulting).
	 * Results are saved in public variables(yes, bad java practice, but we aren't doing anything stupid with it in 2017 robot code).
	 * For tank drive, simply pass .smoothRightVelocity[1] and .smoothLeftVelocity[1] to the corresponding speed controllers.
	 *
	 * @param totalTime time the user wishes to complete the path in seconds
	 * @param timeStep the frequency at which the robot controller is running on the robot
	 * @param robotTrackWidth distance between left and right side wheels of the chassis for tank drive
	 */
	public void calculate(double totalTime, double timeStep, double robotTrackWidth, boolean invert){
		//first find only direction changing nodes
		nodeOnlyPath = nodeOnlyWayPoints(origPath);

		//Figure out how many nodes to inject
		int[] inject = injectionCounter2Steps(nodeOnlyPath.length, totalTime, timeStep);

		//iteratively inject and smooth the path
		for (int i = 0; i < inject.length; i++) {
			if (i == 0) {
				smoothPath = inject(nodeOnlyPath, inject[0]);
				smoothPath = smooth(smoothPath, pathAlpha, pathBeta, pathTolerance);
			} else {
				smoothPath = inject(smoothPath, inject[i]);
				smoothPath = smooth(smoothPath, 0.1, 0.3, 0.0000001);
			}
		}

		//calculate left and right path based on center path
		leftRight(smoothPath, robotTrackWidth);

		//Original velocities calculated for fixing
		origCenterVelocity = velocity(smoothPath, timeStep);
		origLeftVelocity = velocity(leftPath, timeStep);
		origRightVelocity = velocity(rightPath, timeStep);

		//copy smooth velocities into fix Velocities
		smoothCenterVelocity = doubleArrayCopy(origCenterVelocity);
		smoothLeftVelocity = doubleArrayCopy(origLeftVelocity);
		smoothRightVelocity = doubleArrayCopy(origRightVelocity);

		//set final vel to zero
		smoothCenterVelocity[smoothCenterVelocity.length - 1][1] = 0.0;
		smoothLeftVelocity[smoothLeftVelocity.length - 1][1] = 0.0;
		smoothRightVelocity[smoothRightVelocity.length - 1][1] = 0.0;

		//Smooth velocity with zero at end
		smoothCenterVelocity = smooth(smoothCenterVelocity, velocityAlpha, velocityBeta, velocityTolerance);
		smoothLeftVelocity = smooth(smoothLeftVelocity, velocityAlpha, velocityBeta, velocityTolerance);
		smoothRightVelocity = smooth(smoothRightVelocity, velocityAlpha, velocityBeta, velocityTolerance);

		//fix velocity distance error
		smoothCenterVelocity = velocityFix(smoothCenterVelocity, origCenterVelocity, 0.0000001);
		smoothLeftVelocity = velocityFix(smoothLeftVelocity, origLeftVelocity, 0.0000001);
		smoothRightVelocity = velocityFix(smoothRightVelocity, origRightVelocity, 0.0000001);

		//invert if needed
		if (invert) {
			smoothCenterVelocity = invertVelocity(smoothCenterVelocity);
			smoothLeftVelocity = invertVelocity(smoothLeftVelocity);
			smoothRightVelocity = invertVelocity(smoothRightVelocity);
		}
		//smoothCenterVelocity = cullVelocity(smoothCenterVelocity);
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
	private double[][] tankProfile(boolean left){
		return tankProfile(left, getRatio());
	}

	/**
	 * Transforms the tank drive data to a readable format by CTRE's example project and our 2017 code.
	 *
	 * @param left  Do we return the motion profile for left or right side of robot?
	 * @param ratio Transforms feet/second into RPM
	 * @return 2D array.  3 columns: Position(rotations), Velocity(RPM), Duration(ms)
	 */
	private double[][] tankProfile(boolean left, double ratio) {
		//Declare sources and result.  Switch depending on wheel.
		double[][] velocity = left ? smoothLeftVelocity : smoothRightVelocity,
				result = new double[velocity.length][3],
				path = left ? leftPath : rightPath;

		//Encoder should be 0 at start.  If it isn't, reset the encoder when starting each MP.
		double dist = 0, x, y;

		//First point is zeroed
		result[0] = new double[]{0, velocity[0][1], velocity[0][0]*1000};

		//Construct each entry in the result
		for (int i = 1; i < velocity.length; i++) {
			result[i] = new double[]{dist, velocity[i][1] * ratio, (velocity[i][0] - velocity[i - 1][0])*1000};

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
				84/(4*Math.PI),
				mecanum?1.0:0.5
		};
		double result = 1;
		for (double x:ratios)
			result*=x;
		return result;
	}

	//ratio defaults to the result of getRatio
	private double[][][] mecanumProfile(){
		return mecanumProfile(getRatio());//Default to the ratio defined above
	}

	/**
	 * Transforms data into 4 motion profiles, each for a different mecanum wheel using polar drive.
	 *
	 * @param ratio Transforms feet/second into RPM
	 * @return Front left, front right, rear left, and rear right motion profiles in the same format as tankProfile().
	 */
	private double[][][] mecanumProfile(double ratio) {
		//Declare sources and result.
		double[][][] result = new double[4][(int) numFinalPoints][3];
		double[][] path = doubleArrayCopy(smoothPath),
			vel = doubleArrayCopy(smoothCenterVelocity);

		for (int h = 0; h < 4; h++) {
			//For each wheel, zero distance traveled, first point data, time interval, and final velocity.
			double dist = 0.0;
			result[h][0] = new double[3];
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
				result[h][i] = new double[]{dist,
					velocity,
					interval*1000.0
				};
			}
		}
		//Round times to make saved data smaller
		for(double[][] u:result)
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
	private static void buildCSV(String fileName, double[][] arr){
		try {
			PrintWriter pw = new PrintWriter(new File(fileName + ".csv"));

			//If you haven't heard of StringBuilder, it concatenates strings far more efficiently.
			StringBuilder sb = new StringBuilder();
			for (double[] u : arr) {
				for (double v : u) {
					//In case you haven't seen a .csv before, the values are separated by commas.  It's a .CommaSeparatedValue file.
					sb.append(v);
					sb.append(',');
				}
				sb.deleteCharAt(sb.length() - 1);
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
			double[][][] temp = mecanumProfile();
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
	private void roundall(double[][] x) {
		//This method really doesn't need to exist.  I guess I just felt like extra work.
		for(double[] v:x)
			v[2] = Math.round(v[2]);
	}
}