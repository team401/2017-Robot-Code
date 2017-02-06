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

	//Accumulated heading
	public double[][] heading;

	//Various doubles.  UPDATE THESE COMMENTS AS YOU MOVE THROUGH THE CODE
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
		if (mecanum)
			morePoints[index][2] = orig[orig.length - 1][2];
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
		heading[0][1] = 0;

		for (int i = 1; i < smoothPath.length; i++) {
			//Calculate horizontal and vertical velocities separately, then merge into overall
			dxdt = (smoothPath[i][0] - smoothPath[i - 1][0]) / timeStep;
			dydt = (smoothPath[i][1] - smoothPath[i - 1][1]) / timeStep;
			velocity[i][1] = Math.sqrt(Math.pow(dxdt, 2) + Math.pow(dydt, 2));

			//Add time since last entry
			velocity[i][0] = velocity[i - 1][0] + timeStep;
			heading[i][0] = heading[i - 1][0] + timeStep;
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
	public int[] injectionCounter2Steps(double numNodeOnlyPoints, double maxTimeToComplete, double timeStep) {
		int first = 0, second = 0, third = 0;

		double oldPointsTotal = 0;

		numFinalPoints = 0;

		int[] ret;

		double totalPoints = maxTimeToComplete / timeStep;

		if (totalPoints < 100) {
			double pointsFirst;
			double pointsTotal;


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

			ret = new int[]{first, second, third};
		} else {

			double pointsFirst,
				pointsSecond,
				pointsTotal;

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

			ret = new int[]{first, second, third};
		}


		return ret;
	}

	/**
	 * Calculates the left and right wheel paths based on robot track width
	 *
	 * @param smoothPath      - center smooth path of robot
	 * @param robotTrackWidth - width between left and right wheels of robot of skid steer chassis.
	 */
	public void leftRight(double[][] smoothPath, double robotTrackWidth) {

		double[][] left = new double[smoothPath.length][2];
		double[][] right = new double[smoothPath.length][2];

		double[][] gradient = new double[smoothPath.length][2];

		for (int i = 0; i < smoothPath.length - 1; i++)
			gradient[i][1] = Math.atan2(smoothPath[i + 1][1] - smoothPath[i][1], smoothPath[i + 1][0] - smoothPath[i][0]);

		gradient[gradient.length - 1][1] = gradient[gradient.length - 2][1];


		for (int i = 0; i < gradient.length; i++) {
			left[i][0] = (robotTrackWidth / 2 * Math.cos(gradient[i][1] + Math.PI / 2)) + smoothPath[i][0];
			left[i][1] = (robotTrackWidth / 2 * Math.sin(gradient[i][1] + Math.PI / 2)) + smoothPath[i][1];

			right[i][0] = robotTrackWidth / 2 * Math.cos(gradient[i][1] - Math.PI / 2) + smoothPath[i][0];
			right[i][1] = robotTrackWidth / 2 * Math.sin(gradient[i][1] - Math.PI / 2) + smoothPath[i][1];

			//convert to degrees 0 to 360 where 0 degrees is +X - axis, accumulated to aline with WPI sensor
			double deg = Math.toDegrees(gradient[i][1]);

			gradient[i][1] = deg;

			if (i > 0) {
				if ((deg - gradient[i - 1][1]) > 180)
					gradient[i][1] = -360 + deg;

				if ((deg - gradient[i - 1][1]) < -180)
					gradient[i][1] = 360 + deg;
			}


		}

		heading = gradient;
		leftPath = left;
		rightPath = right;
	}

	/**
	 * Returns the first column of a 2D array of doubles
	 *
	 * @param arr 2D array of doubles
	 * @return array of doubles representing the 1st column of the initial parameter
	 */

	public static double[] getXVector(double[][] arr) {
		double[] temp = new double[arr.length];

		for (int i = 0; i < temp.length; i++)
			temp[i] = arr[i][0];

		return temp;
	}

	/**
	 * Returns the second column of a 2D array of doubles
	 *
	 * @param arr 2D array of doubles
	 * @return array of doubles representing the 1st column of the initial parameter
	 */
	public static double[] getYVector(double[][] arr) {
		double[] temp = new double[arr.length];

		for (int i = 0; i < temp.length; i++)
			temp[i] = arr[i][1];

		return temp;
	}

	public static double[][] transposeVector(double[][] arr) {
		double[][] temp = new double[arr[0].length][arr.length];

		for (int i = 0; i < temp.length; i++)
			for (int j = 0; j < temp[i].length; j++)
				temp[i][j] = arr[j][i];

		return temp;
	}

	public void setPathAlpha(double alpha) {
		pathAlpha = alpha;
	}

	public void setPathBeta(double beta) {
		pathBeta = beta;
	}

	public void setPathTolerance(double tolerance) {
		pathTolerance = tolerance;
	}

	/**
	 * This code will calculate a smooth path based on the program parameters. If the user doesn't set any parameters, the will use the defaults optimized for most cases. The results will be saved into the corresponding
	 * class members. The user can then access .smoothPath, .leftPath, .rightPath, .smoothCenterVelocity, .smoothRightVelocity, .smoothLeftVelocity as needed.
	 * <p>
	 * After calling this method, the user only needs to pass .smoothRightVelocity[1], .smoothLeftVelocity[1] to the corresponding speed controllers on the Robot, and step through each setPoint.
	 *
	 * @param totalTime       - time the user wishes to complete the path in seconds. (this is the maximum amount of time the robot is allowed to take to traverse the path.)
	 * @param timeStep        - the frequency at which the robot controller is running on the robot.
	 * @param robotTrackWidth - distance between left and right side wheels of a skid steer chassis. Known as the track width.
	 */
	public void calculate(double totalTime, double timeStep, double robotTrackWidth) {
		calculate(totalTime, timeStep, robotTrackWidth, false);
	}

	public void calculate(double totalTime, double timeStep, double robotTrackWidth, boolean invert) {
		/**
		 * pseudo code
		 *
		 * 1. Reduce input waypoints to only essential (direction changing) node points
		 * 2. Calculate how many total datapoints we need to satisfy the controller for "playback"
		 * 3. Simultaneously inject and smooth the path until we end up with a smooth path with required number
		 *    of datapoints, and which follows the waypoint path.
		 * 4. Calculate left and right wheel paths by calculating parallel points at each datapoint
		 */


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

		//Smooth velocity with zero final V
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
	}

	public double[][] invertVelocity(double[][] vel) {
		double[][] result = doubleArrayCopy(vel);
		for (int i = 0; i < result.length; i++)
			result[i][1] = result[i][1] * -1;
		return result;
	}

	/**
	 * Gives the data stored here in a usable format for the example Talon SRX motion profile project.
	 *
	 * @param left  Boolean determining whether to return the motion profile for left or right side of robot.
	 * @param ratio Double used to transform feet/second into RPM
	 * @return Array of array of 3 doubles: Position(rotations), Velocity(RPM), Duration(ms)
	 */
	private double[][] tankProfile(boolean left, double ratio) {
		double[][] source = left ? smoothLeftVelocity : smoothRightVelocity,//Switch depending on wheel
				result = new double[source.length][3],
				wheel = left ? leftPath : rightPath;

		double dist = 0, x, y;//Assume encoder position is 0 at start.  If it isn't, each point can simply be +='d at runtime.

		result[0] = new double[]{0, source[0][1], source[0][0]*1000};

		for (int i = 1; i < source.length; i++) {
			result[i] = new double[]{dist, source[i][1] * ratio, (source[i][0] - source[i - 1][0])*1000};

			x = wheel[i][0] - wheel[i - 1][0];//Get dX and dY
			y = wheel[i][1] - wheel[i - 1][1];
			dist += ratio * Math.sqrt(Math.abs(x * x + y * y));//Add distance between last and current points using Pythag.  Math.abs ensures no errors.
		}
		roundall(result);
		return result;
	}

	private double[][] tankProfile(boolean left){
		return tankProfile(left, getRatio());
	}

	public double getRatio(){
		double[] ratios = new double[]{
				3.0 / Math.PI,//Reciprocal of circumference, in FEET
				mecanum ? 1.0 : 0.5//Divide by 2 if in traction drive
		};
		double result = 1;
		for (double x:ratios)
			result*=x;
		return result;
	}
	/**
	 * @return Array of 4 motion profiles that control Front Left, Front Right, Rear Left, and Rear Right wheels respectively.
	 */
	private double[][][] mecanumProfile(){
		return mecanumProfile(getRatio());//Default to the ratio defined above
	}
	private double[][][] mecanumProfile(double ratio) {
		double[][][] result = new double[4][(int) numFinalPoints][3];
		double[][] path = doubleArrayCopy(smoothPath),
			vel = doubleArrayCopy(smoothCenterVelocity);

		for (int h = 0; h < 4; h++) {
			double dist = 0.0;
			result[h][0] = new double[3];
			double lastVel = 0.0,
				interval = 0.0;
			for (int i = 1; i < numFinalPoints; i++) {
				dist += lastVel*interval / 60;
				lastVel = polarMecanum(vel[i][1]*ratio, Math.atan(path[i][0] / path[i][1]), path[i][2])[h];
				interval = (vel[i][0]-vel[i-1][0]);
				result[h][i] = new double[]{dist,
					lastVel,
					interval*1000.0
				};
			}
		}
		return result;
	}

	/**
	 * Taken straight from the Strongback source code
	 */
	private double[] polarMecanum(double mag, double dir, double rot) {
		// Normalized for full power along the Cartesian axes.
		mag = Values.symmetricLimiter(0.02, 1.0).applyAsDouble(mag) * Math.sqrt(2.0);
		// The rollers are at 45 degree angles.
		double dirInRad = Math.toRadians(dir + 45.0);
		double cosD = Math.cos(dirInRad);
		double sinD = Math.sin(dirInRad);

		double wheelSpeeds[] = new double[]{
				(sinD * mag + rot),//LEFT FRONT
				(cosD * mag - rot),//RIGHT FRONT
				(cosD * mag + rot),//LEFT REAR
				(sinD * mag - rot)//RIGHT REAR
		};
		return wheelSpeeds;
	}

	private static void exportCSV(String fileName, double[][] arr){
		try {
			PrintWriter pw = new PrintWriter(new File(fileName + ".csv"));
			StringBuilder sb = new StringBuilder();
			for (double[] u : arr) {
				for (double v : u) {
					sb.append(v);
					sb.append(',');
				}
				sb.deleteCharAt(sb.length() - 1);
				sb.append('\n');
			}
			pw.write(sb.toString());
			pw.close();
		}catch(FileNotFoundException e){
			System.out.println("File \""+fileName+".csv\" is being used by another program!  Close the other program and restart Motion Profile Generator.");
		}
	}
	public void exportCSV(){
		exportCSV("");
	}
	public void exportCSV(String prefix){
		exportCSV(prefix, "");
	}
	public void exportCSV(String prefix, String suffix){
		if(mecanum) {
			double[][][] temp = mecanumProfile();

			//Sometimes we get floating-point errors with the time column, so just round to nearest millisecond.
			for(double[][] u:temp)
				roundall(u);

			exportCSV(prefix+" FL"+suffix, temp[0]);
			exportCSV(prefix+" FR"+suffix, temp[1]);
			exportCSV(prefix+" RL"+suffix, temp[2]);
			exportCSV(prefix+" RR"+suffix, temp[3]);
		}else {
			exportCSV(prefix+" L"+suffix, tankProfile(true));
			exportCSV(prefix+" R"+suffix, tankProfile(false));
		}
	}
	private void roundall(double[][] x) {
			for(double[] v:x)
				v[2] = Math.round(v[2]);
	}
}