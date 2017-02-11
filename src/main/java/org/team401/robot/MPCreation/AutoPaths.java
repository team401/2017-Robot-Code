package org.team401.robot.MPCreation;

import java.util.Arrays;

/**
 * Provides waypoint paths for transformation into motion profiles.
 * All measurements are in feet.
 */

public class AutoPaths {
	//Starting and finishing positions of the first profile options.
	private static final double[] CENTER_GEAR_PEG = {
			(11.831875 + 15.168225)/2.0, 7.775, 0
		}, LEFT_GEAR_PEG = {
			(11.831875 + 10.16375)/2.0, (9.325 + 12.26625)/2.0, 0
		}, RIGHT_GEAR_PEG = {
			(16.83625 + 15.1681225)/2.0, (9.325 + 12.26625)/2.0, 0
		}, STARTING_MID = {
			13.5, 2, 0
		}, STARTING_LEFT = {
			5, 2, 0
		}, STARTING_RIGHT = {
			22, 2, 0
	};

	/**
	 * Actual paths
	 */
	public static final NamedPath START_MID_TO_LIFT = new NamedPath("CCL", new double[][]{
			STARTING_MID,
			perpendicular_To_Airship(CENTER_GEAR_PEG, 2),
			perpendicular_To_Airship(CENTER_GEAR_PEG, 1)

		}), START_MID_TO_R_LIFT = new NamedPath("CRL", new double[][]{
			STARTING_MID,
			{20, 3, 0},
			perpendicular_To_Airship(RIGHT_GEAR_PEG, 2),
			perpendicular_To_Airship(RIGHT_GEAR_PEG, 1)

		}), START_MID_TO_L_LIFT = new NamedPath("CLL", new double[][]{
			STARTING_MID,
			{7, 3, 0},
			perpendicular_To_Airship(LEFT_GEAR_PEG, 2),
			perpendicular_To_Airship(LEFT_GEAR_PEG, 1)

		}), START_LEFT_TO_LIFT = new NamedPath("LCL", new double[][]{
			STARTING_LEFT,
			perpendicular_To_Airship(CENTER_GEAR_PEG, 2),
			perpendicular_To_Airship(CENTER_GEAR_PEG, 1)

		}), START_LEFT_TO_R_LIFT = new NamedPath("LRL", new double[][]{
			STARTING_LEFT,
			{7, 5, 0},
			{19, 5, 0},
			{20, 7, 0},
			perpendicular_To_Airship(RIGHT_GEAR_PEG, 2),
			perpendicular_To_Airship(RIGHT_GEAR_PEG, 1)

		}), START_LEFT_TO_L_LIFT = new NamedPath("LLL", new double[][]{
			STARTING_LEFT,
			{5, 8, 0},
			perpendicular_To_Airship(LEFT_GEAR_PEG, 2),
			perpendicular_To_Airship(LEFT_GEAR_PEG, 1)

		}), START_RIGHT_TO_LIFT = new NamedPath("RCL", new double[][]{
			STARTING_RIGHT,
			perpendicular_To_Airship(CENTER_GEAR_PEG, 2),
			perpendicular_To_Airship(CENTER_GEAR_PEG, 1)

		}), START_RIGHT_TO_R_LIFT = new NamedPath("RRL", new double[][]{
			STARTING_RIGHT,
			{22, 8, 0},
			perpendicular_To_Airship(RIGHT_GEAR_PEG, 2),
			perpendicular_To_Airship(RIGHT_GEAR_PEG, 1)

		}), START_RIGHT_TO_L_LIFT = new NamedPath("RLL", new double[][]{
			STARTING_RIGHT,
			{20, 5, 0},
			{8, 5, 0},
			{7, 7, 0},
			perpendicular_To_Airship(LEFT_GEAR_PEG, 2),
			perpendicular_To_Airship(LEFT_GEAR_PEG, 1)

		}), RIGHT_GEAR_PEG_TO_SHOOTING_POSITION_REVERSE = new NamedPath("Right Gear Peg to Shooting Position (Reverse)", new double[][]{
			RIGHT_GEAR_PEG,
			{23, 18, 0}

		}), RIGHT_GEAR_PEG_TO_SHOOTING_POSITION_2 = new NamedPath("Right Gear Peg to Shooting Position 2", new double[][]{
			{23, 18, 0},
			{22, 17, 0},
			{22, 15, 0}

		}), LEFT_GEAR_PEG_TO_LEFT_HOPPER_REVERSE_R = new NamedPath("LLR", true, new double[][]{
			perpendicular_To_Airship(LEFT_GEAR_PEG, 1),
			perpendicular_To_Airship(LEFT_GEAR_PEG, 2),
			{1.5, 16.7916, 0},
			perpendicular(new double[]{0, 15.7916, 0, 17.7916})

		}), RIGHT_GEAR_PEG_TO_RIGHT_HOPPER_REVERSE_R = new NamedPath("RLR", true,  new double[][]{
			perpendicular_To_Airship(RIGHT_GEAR_PEG, 1),
			perpendicular_To_Airship(RIGHT_GEAR_PEG, 2),
			{23, 10, 0},
			{25.5, 9.7916, 0},
			perpendicular(new double[]{27, 10.7916, 27, 8.7916}, 1)

		}), LEFT_GEAR_PEG_TO_LEFT_HOPPER_REVERSE_B = new NamedPath("LLB", true, new double[][]{
			perpendicular_To_Airship(LEFT_GEAR_PEG, 1),
			perpendicular_To_Airship(LEFT_GEAR_PEG, 2),
			perpendicular(new double[]{0, 10.7916, 0, 8.7916}, -1)

		}), RIGHT_GEAR_PEG_TO_RIGHT_HOPPER_REVERSE_B = new NamedPath("RLB", true, new double[][]{
			perpendicular_To_Airship(RIGHT_GEAR_PEG, 1),
			perpendicular_To_Airship(RIGHT_GEAR_PEG, 2),
			{23, 10, 0},
			{24, 15, 0},
			perpendicular(new double[]{27, 15.7916, 27, 17.7916}, -2),
			perpendicular(new double[]{27, 15.7916, 27, 17.7916}, -1)

		}), CENTER_GEAR_PEG_TO_RIGHT_HOPPER_REVERSE_B = new NamedPath("CLB", true, new double[][]{
			CENTER_GEAR_PEG,
			{(11.831875 + 15.168225)/2.0, ((9.325 + 9.325)/2.0) - 2, 0},
			perpendicular(new double[]{27, 10.7916, 27, 8.7916}, 2),
			perpendicular(new double[]{27, 10.7916, 27, 8.7916}, 1)

		}), CENTER_GEAR_PEG_TO_LEFT_HOPPER_REVERSE_B = new NamedPath("DONOTUSE", true, new double[][]{
			CENTER_GEAR_PEG,
			{(11.831875 + 15.168225)/2.0, ((9.325 + 9.325)/2.0) - 2, 0},
			{10, 7, 0},
			{5, 16, 0},
			perpendicular(new double[]{0, 15.7916, 0, 17.7916}, 2),
			perpendicular(new double[]{0, 15.7916, 0, 17.7916}, 1)

		}), CENTER_GEAR_PEG_TO_RIGHT_HOPPER_REVERSE_R = new NamedPath("DONOTUSE", true, new double[][]{
			CENTER_GEAR_PEG,
			{(11.831875 + 15.168225)/2.0, ((9.325 + 9.325)/2.0)-2, 0},
			{20, 8, 0},
			{23, 15, 0},
			perpendicular(new double[]{27, 15.7916, 27, 17.7916}, -2),
			perpendicular(new double[]{27, 15.7916, 27, 17.7916}, -1)

		}), CENTER_GEAR_PEG_TO_LEFT_HOPPER_REVERSE_R = new NamedPath("CLR", true, new double[][]{
			CENTER_GEAR_PEG,
			{(11.831875 + 15.168225)/2.0, ((9.325 + 9.325)/2.0) - 2, 0},
			perpendicular(new double[]{0, 10.7916, 0, 8.7916}, -2),
			perpendicular(new double[]{0, 10.7916, 0, 8.7916}, -1)

		}), LEFT_HOPPER_COLLECTION_B = new NamedPath("LHB", new double[][]{
			perpendicular(new double[]{0, 15.7916, 0, 17.7916}, 1),
			perpendicular(new double[]{0, 15.7916, 0, 17.7916}, 2),
			{1, 16.7916, -90},
			{1, 20, -90},
			{1, 20, 90},
			{1, 10, 90}

		}), RIGHT_HOPPER_COLLECTION_B = new NamedPath("RHB", new double[][]{
			perpendicular(new double[]{27, 10.7916, 27, 8.7916}, 1),
			perpendicular(new double[]{27, 10.7916, 27, 8.7916}, 2),
			{26, 9.7916, 90},
			{26, 14, 90},
			{26, 14, -90},
			{26, 4, -90}

		}), LEFT_HOPPER_COLLECTION_R = new NamedPath("LHR", new double[][]{
			perpendicular(new double[]{0, 10.7916, 0, 8.7916}, -1),
			perpendicular(new double[]{0, 10.7916, 0, 8.7916}, -2),
			{1, 9.7916, -90},
			{1, 14, -90},
			{1, 14, 90},
			{1, 4, 90}

		}), RIGHT_HOPPER_COLLECTION_R = new NamedPath("RHR", new double[][]{
			perpendicular(new double[]{27, 15.7916, 27, 17.7916}, -1),
			perpendicular(new double[]{27, 15.7916, 27, 17.7916}, -2),
			{26, 16.7916, 90},
			{26, 20, 90},
			{26, 20, -90},
			{26, 10, -90}

		}), STARTING_LEFT_TO_LEFT_HOPPER_R = new NamedPath("LLHR", new double[][]{
			STARTING_LEFT,
			{5, 9.7916, 0},
			perpendicular(new double[]{0, 10.7916, 0, 8.7916}, -1),
			perpendicular(new double[]{0, 10.7916, 0, 8.7916}, -2)

		}), STARTING_LEFT_TO_LEFT_HOPPER_B = new NamedPath("LLHB", new double[][]{
			STARTING_LEFT,
			{5, 16.7916, 0},
			perpendicular(new double[]{0, 15.7916, 0, 17.7916}, 1),
			perpendicular(new double[]{0, 15.7916, 0, 17.7916}, 2)

		}), STARTING_RIGHT_TO_RIGHT_HOPPER_R = new NamedPath("RRHR", new double[][]{
			STARTING_RIGHT,
			{22, 16.7916, 0},
			perpendicular(new double[]{27, 15.7916, 27, 17.7916}, -1),
			perpendicular(new double[]{27, 15.7916, 27, 17.7916}, -2)

		}), STARTING_RIGHT_TO_RIGHT_HOPPER_B = new NamedPath("RRHB", new double[][]{
			STARTING_RIGHT,
			{22, 9.7916, 0},
			perpendicular(new double[]{27, 10.7916, 27, 8.7916}, 1),
			perpendicular(new double[]{27, 10.7916, 27, 8.7916}, 2)

		}), STARTING_MID_TO_LEFT_HOPPER_R = new NamedPath("CLHR", new double[][]{
			STARTING_MID,
			{5, 9.7916, 0},
			perpendicular(new double[]{0, 10.7916, 0, 8.7916}, -1),
			perpendicular(new double[]{0, 10.7916, 0, 8.7916}, -2)

		}), STARTING_MID_TO_LEFT_HOPPER_B = new NamedPath("CLHB", new double[][]{
			STARTING_MID,
			{5, 9.7916, 0},
			{5, 16.7916, 0},
			perpendicular(new double[]{0, 15.7916, 0, 17.7916}, 1),
			perpendicular(new double[]{0, 15.7916, 0, 17.7916}, 2)

		}), STARTING_MID_TO_RIGHT_HOPPER_R = new NamedPath("CRHR", new double[][]{
			STARTING_MID,
			{22, 9.7916, 0},
			{22, 16.7916, 0},
			perpendicular(new double[]{27, 15.7916, 27, 17.7916}, -1),
			perpendicular(new double[]{27, 15.7916, 27, 17.7916}, -2)

		}), STARTING_MID_TO_RIGHT_HOPPER_B = new NamedPath("CRHB", new double[][]{
			STARTING_MID,
			{22, 9.7916, 0},
			perpendicular(new double[]{27, 10.7916, 27, 8.7916}, 1),
			perpendicular(new double[]{27, 10.7916, 27, 8.7916}, 2)
	});

	//overloaded to accept a single value for b
	private static double[] concat(double[] a, double b){
		return concat(a, new double[]{b});
	}

	/**
	 * Concatenates one array onto the end of another.
	 * Not currently used, but I have a feeling we may need it.
	 *
	 * @param a Original array
	 * @param b The array to append
	 * @return Arrays combined end-to-end
	 */
	private static double[] concat(double[] a, double[] b) {
		//Make space for the result
		double[] result = new double[a.length + b.length];

		//Fill in data from first array, keeping track of location in result
		int index = 0;
		for (double u : a){
			result[index] = u;
			index++;
		}

		//Same for the second loop
		for(double u : b){
			result[index] = u;
			index++;
		}
		return result;
	}

	//factor defaults to 1.0
	private static double[] perpendicular(double[] coords) {
		return perpendicular(coords, 1.0);
	}

	/**
	 * Returns the coordinates of C, where C is the vertex of a triangle and A and B are known points with angles of 45 degrees, and the angle that the
	 * robot needs to be in to be perpendicular
	 * Slightly modified from http://math.stackexchange.com/questions/1842614/find-third-coordinate-for-a-right-triangle-with-45degree-angles
	 *
	 * Works no matter what order the points are entered
	 *
	 * @param coords The coordinates of the points.  Should be in the order [x1, y1, x2, y2].
	 * @param factor Scales the result to the correct length.  Should always be positive.
	 * @return Point C
	 */
	private static double[] perpendicular(double[] coords, double factor) {
		//Makes sure the result will always be outside the airship and not inside
		factor *= (coords[2] < coords[0] && (coords[3] < coords[1]) && !(coords[1] == coords[3])) ? 1 : -1;

		//Look on Stack Exchange if you want to understand this math.
		return new double[]{
				(coords[0] + coords[2]) / 2.0 + (coords[1] - coords[3]) / 2.0 * factor,
				(coords[1] + coords[3]) / 2.0 + (coords[2] - coords[0]) / 2.0 * factor,
				Math.toDegrees(Math.atan2(coords[0], coords[1]) + (Math.PI/2))
		};
	}

	/**
	 * Performs the perpendicular functions, but you only have to input the peg you are going to instead of a bunch of coordinates.
	 * @param peg What peg you are approaching
	 * @return the point right before the peg (is perpendicular to the face of the airship)
	 */
	private static double[] perpendicular_To_Airship (double[] peg, double factor){
		if(Arrays.equals(peg, LEFT_GEAR_PEG)){
			 return perpendicular(new double[]{10.16375, 12.26625, 11.831875, 9.325}, factor);
		}
		else if(Arrays.equals(peg, RIGHT_GEAR_PEG)){
			return perpendicular(new double[]{15.1681225, 9.325, 16.83625, 12.26625}, factor);
		}
		else if(Arrays.equals(peg, CENTER_GEAR_PEG)){
			return perpendicular(new double[]{11.831875, 9.325, 15.1681225, 9.325}, factor);
		}
		else{
			return peg;
		}
	}
}