package org.team401.robot;

public class GeneratedMotionProfiles {
	// Position (rotations)	Velocity (RPM)	Duration (ms)
	public static double[][][] getProfile(int start, int tgt, boolean mecanum) {
		if (mecanum)//Mecanum drive
			switch (start) {
				case 0:
					switch (tgt) {
						case 0:
							return mecanumMidCenterLift;//Middle to Center Lift
						case 1:
							return mecanumMidLeftLift;//Middle to Left Lift
						case 2:
							return mecanumMidRightLift;//Middle to Right Lift
						case 3:
							return mecanumMidLeftHopper;//Middle to Left Hopper
						case 4:
							return mecanumMidRightHopper;//Middle to Right Hopper;
						default:
							return new double[0][0][0];//Return empty array to sit still if something bad happened
					}
				case 1:
					switch (tgt) {
						case 0:
							return mecanumLeftCenterLift;//Left to Center Lift
						case 1:
							return mecanumLeftLeftLift;//Left to Left Lift
						case 2:
							return mecanumLeftRightLift;//Left to Right Lift
						case 3:
							return mecanumLeftLeftHopper;//Left to Left Hopper
						case 4:
							return mecanumLeftRightHopper;//Left to Right Hopper;
						default:
							return new double[0][0][0];//Return empty array to sit still if something bad happened
					}
				case 2:
					switch (tgt) {
						case 0:
							return mecanumRightCenterLift;//Right to Center Lift
						case 1:
							return mecanumRightLeftLift;//Right to Left Lift
						case 2:
							return mecanumRightRightLift;//Right to Right Lift
						case 3:
							return mecanumRightLeftHopper;//Right to Left Hopper
						case 4:
							return mecanumRightRightHopper;//Right to Right Hopper;
						default:
							return new double[0][0][0];//Return empty array to sit still if something bad happened
					}
				default:
					return new double[0][0][0];//Return empty array to sit still if something bad happened
			}
		else//Tank drive
			switch (start) {
				case 0:
					switch (tgt) {
						case 0:
							return tractionMidCenterLift;//Middle to Center Lift
						case 1:
							return tractionMidLeftLift;//Middle to Left Lift
						case 2:
							return tractionMidRightLift;//Middle to Right Lift
						case 3:
							return tractionMidLeftHopper;//Middle to Left Hopper
						case 4:
							return tractionMidRightHopper;//Middle to Right Hopper;
						default:
							return new double[0][0][0];//Return empty array to sit still if something bad happened
					}
				case 1:
					switch (tgt) {
						case 0:
							return tractionLeftCenterLift;//Left to Center Lift
						case 1:
							return tractionLeftLeftLift;//Left to Left Lift
						case 2:
							return tractionLeftRightLift;//Left to Right Lift
						case 3:
							return tractionLeftLeftHopper;//Left to Left Hopper
						case 4:
							return tractionLeftRightHopper;//Left to Right Hopper;
						default:
							return new double[0][0][0];//Return empty array to sit still if something bad happened
					}
				case 2:
					switch (tgt) {
						case 0:
							return tractionRightCenterLift;//Right to Center Lift
						case 1:
							return tractionRightLeftLift;//Right to Left Lift
						case 2:
							return tractionRightRightLift;//Right to Right Lift
						case 3:
							return tractionRightLeftHopper;//Right to Left Hopper
						case 4:
							return tractionRightRightHopper;//Right to Right Hopper;
						default:
							return new double[0][0][0];//Return empty array to sit still if something bad happened
					}
				default:
					return new double[0][0][0];//Return empty array to sit still if something bad happened
			}
	}

	public static double[][][] mecanumMidCenterLift = new double[][][]{};
	public static double[][][] mecanumMidLeftLift = new double[][][]{};
	public static double[][][] mecanumMidRightLift = new double[][][]{};
	public static double[][][] mecanumMidLeftHopper = new double[][][]{};
	public static double[][][] mecanumMidRightHopper = new double[][][]{};

	public static double[][][] mecanumLeftCenterLift = new double[][][]{};
	public static double[][][] mecanumLeftLeftLift = new double[][][]{};
	public static double[][][] mecanumLeftRightLift = new double[][][]{};
	public static double[][][] mecanumLeftLeftHopper = new double[][][]{};
	public static double[][][] mecanumLeftRightHopper = new double[][][]{};

	public static double[][][] mecanumRightCenterLift = new double[][][]{};
	public static double[][][] mecanumRightLeftLift = new double[][][]{};
	public static double[][][] mecanumRightRightLift = new double[][][]{};
	public static double[][][] mecanumRightLeftHopper = new double[][][]{};
	public static double[][][] mecanumRightRightHopper = new double[][][]{};

	public static double[][][] tractionMidCenterLift = new double[][][]{};
	public static double[][][] tractionMidLeftLift = new double[][][]{};
	public static double[][][] tractionMidRightLift = new double[][][]{};
	public static double[][][] tractionMidLeftHopper = new double[][][]{};
	public static double[][][] tractionMidRightHopper = new double[][][]{};

	public static double[][][] tractionLeftCenterLift = new double[][][]{};
	public static double[][][] tractionLeftLeftLift = new double[][][]{};
	public static double[][][] tractionLeftRightLift = new double[][][]{};
	public static double[][][] tractionLeftLeftHopper = new double[][][]{};
	public static double[][][] tractionLeftRightHopper = new double[][][]{};

	public static double[][][] tractionRightCenterLift = new double[][][]{};
	public static double[][][] tractionRightLeftLift = new double[][][]{};
	public static double[][][] tractionRightRightLift = new double[][][]{};
	public static double[][][] tractionRightLeftHopper = new double[][][]{};
	public static double[][][] tractionRightRightHopper = new double[][][]{};
}