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

	public static final double[][][] mecanumMidCenterLift = new double[][][]{
			{},//Front left wheel
			{},//Front right wheel
			{},//Rear left wheel
			{}//Rear right wheel
	};
	public static final double[][][] mecanumMidLeftLift = new double[][][]{
			{},//Front left wheel
			{},//Front right wheel
			{},//Rear left wheel
			{}//Rear right wheel
	};
	public static final double[][][] mecanumMidRightLift = new double[][][]{
			{},//Front left wheel
			{},//Front right wheel
			{},//Rear left wheel
			{}//Rear right wheel
	};
	public static final double[][][] mecanumMidLeftHopper = new double[][][]{
			{},//Front left wheel
			{},//Front right wheel
			{},//Rear left wheel
			{}//Rear right wheel
	};
	public static final double[][][] mecanumMidRightHopper = new double[][][]{
			{},//Front left wheel
			{},//Front right wheel
			{},//Rear left wheel
			{}//Rear right wheel
	};

	public static final double[][][] mecanumLeftCenterLift = new double[][][]{
			{},//Front left wheel
			{},//Front right wheel
			{},//Rear left wheel
			{}//Rear right wheel
	};
	public static final double[][][] mecanumLeftLeftLift = new double[][][]{
			{},//Front left wheel
			{},//Front right wheel
			{},//Rear left wheel
			{}//Rear right wheel
	};
	public static final double[][][] mecanumLeftRightLift = new double[][][]{
			{},//Front left wheel
			{},//Front right wheel
			{},//Rear left wheel
			{}//Rear right wheel
	};
	public static final double[][][] mecanumLeftLeftHopper = new double[][][]{
			{},//Front left wheel
			{},//Front right wheel
			{},//Rear left wheel
			{}//Rear right wheel
	};
	public static final double[][][] mecanumLeftRightHopper = new double[][][]{
			{},//Front left wheel
			{},//Front right wheel
			{},//Rear left wheel
			{}//Rear right wheel
	};

	public static final double[][][] mecanumRightCenterLift = new double[][][]{
			{},//Front left wheel
			{},//Front right wheel
			{},//Rear left wheel
			{}//Rear right wheel
	};
	public static final double[][][] mecanumRightLeftLift = new double[][][]{
			{},//Front left wheel
			{},//Front right wheel
			{},//Rear left wheel
			{}//Rear right wheel
	};
	public static final double[][][] mecanumRightRightLift = new double[][][]{
			{},//Front left wheel
			{},//Front right wheel
			{},//Rear left wheel
			{}//Rear right wheel
	};
	public static final double[][][] mecanumRightLeftHopper = new double[][][]{
			{},//Front left wheel
			{},//Front right wheel
			{},//Rear left wheel
			{}//Rear right wheel
	};
	public static final double[][][] mecanumRightRightHopper = new double[][][]{
			{},//Front left wheel
			{},//Front right wheel
			{},//Rear left wheel
			{}//Rear right wheel
	};

	public static final double[][][] tractionMidCenterLift = new double[][][]{
			{},//Left profile, should be sent to both left wheels
			{}//Right profile, should be sent to both right wheels
	};
	public static final double[][][] tractionMidLeftLift = new double[][][]{
			{},//Left profile, should be sent to both left wheels
			{}//Right profile, should be sent to both right wheels
	};
	public static final double[][][] tractionMidRightLift = new double[][][]{
			{},//Left profile, should be sent to both left wheels
			{}//Right profile, should be sent to both right wheels
	};
	public static final double[][][] tractionMidLeftHopper = new double[][][]{
			{},//Left profile, should be sent to both left wheels
			{}//Right profile, should be sent to both right wheels
	};
	public static final double[][][] tractionMidRightHopper = new double[][][]{
			{},//Left profile, should be sent to both left wheels
			{}//Right profile, should be sent to both right wheels
	};

	public static final double[][][] tractionLeftCenterLift = new double[][][]{
			{},//Left profile, should be sent to both left wheels
			{}//Right profile, should be sent to both right wheels
	};
	public static final double[][][] tractionLeftLeftLift = new double[][][]{
			{},//Left profile, should be sent to both left wheels
			{}//Right profile, should be sent to both right wheels
	};
	public static final double[][][] tractionLeftRightLift = new double[][][]{
			{},//Left profile, should be sent to both left wheels
			{}//Right profile, should be sent to both right wheels
	};
	public static final double[][][] tractionLeftLeftHopper = new double[][][]{
			{},//Left profile, should be sent to both left wheels
			{}//Right profile, should be sent to both right wheels
	};
	public static final double[][][] tractionLeftRightHopper = new double[][][]{
			{},//Left profile, should be sent to both left wheels
			{}//Right profile, should be sent to both right wheels
	};

	public static final double[][][] tractionRightCenterLift = new double[][][]{
			{},//Left profile, should be sent to both left wheels
			{}//Right profile, should be sent to both right wheels
	};
	public static final double[][][] tractionRightLeftLift = new double[][][]{
			{},//Left profile, should be sent to both left wheels
			{}//Right profile, should be sent to both right wheels
	};
	public static final double[][][] tractionRightRightLift = new double[][][]{
			{},//Left profile, should be sent to both left wheels
			{}//Right profile, should be sent to both right wheels
	};
	public static final double[][][] tractionRightLeftHopper = new double[][][]{
			{},//Left profile, should be sent to both left wheels
			{}//Right profile, should be sent to both right wheels
	};
	public static final double[][][] tractionRightRightHopper = new double[][][]{
			{},//Left profile, should be sent to both left wheels
			{}//Right profile, should be sent to both right wheels
	};
}