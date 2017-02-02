package org.team401.robot;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

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

	public double[][] scanCSV(String fileName){
		BufferedReader br = null;
		String line;
		double[][] result;
		String[][] strs = new String[99999][3];
		try {
			br = new BufferedReader(new FileReader(fileName));
			int i = 1;
			while ((line = br.readLine()) != null) {

				// use comma as separator
				strs[i] = line.split(",");
				i++;
			}
			result = new double[i][3];
			for(int j = 0; j < i; j++)
				for(int k = 0; k < 3; k++)
					result[j][k] = Double.parseDouble(strs[j][k]);
			br.close();
			return result;
		} catch (FileNotFoundException e) {
			System.out.println("File " + fileName + "not found!");
		} catch (IOException e) {
			System.out.println("IOException in scanCSV while scanning " + fileName + "!");
		}
		return new double[0][0];

	}

	public static final double[][][] mecanumMidCenterLift = {
			{},//Front left wheel
			{},//Front right wheel
			{},//Rear left wheel
			{}//Rear right wheel
	};
	public static final double[][][] mecanumMidLeftLift = {
			{},//Front left wheel
			{},//Front right wheel
			{},//Rear left wheel
			{}//Rear right wheel
	};
	public static final double[][][] mecanumMidRightLift = {
			{},//Front left wheel
			{},//Front right wheel
			{},//Rear left wheel
			{}//Rear right wheel
	};
	public static final double[][][] mecanumMidLeftHopper = {
			{},//Front left wheel
			{},//Front right wheel
			{},//Rear left wheel
			{}//Rear right wheel
	};
	public static final double[][][] mecanumMidRightHopper = {
			{},//Front left wheel
			{},//Front right wheel
			{},//Rear left wheel
			{}//Rear right wheel
	};

	public static final double[][][] mecanumLeftCenterLift = {
			{},//Front left wheel
			{},//Front right wheel
			{},//Rear left wheel
			{}//Rear right wheel
	};
	public static final double[][][] mecanumLeftLeftLift = {
			{},//Front left wheel
			{},//Front right wheel
			{},//Rear left wheel
			{}//Rear right wheel
	};
	public static final double[][][] mecanumLeftRightLift = {
			{},//Front left wheel
			{},//Front right wheel
			{},//Rear left wheel
			{}//Rear right wheel
	};
	public static final double[][][] mecanumLeftLeftHopper = {
			{},//Front left wheel
			{},//Front right wheel
			{},//Rear left wheel
			{}//Rear right wheel
	};
	public static final double[][][] mecanumLeftRightHopper = {
			{},//Front left wheel
			{},//Front right wheel
			{},//Rear left wheel
			{}//Rear right wheel
	};

	public static final double[][][] mecanumRightCenterLift = {
			{},//Front left wheel
			{},//Front right wheel
			{},//Rear left wheel
			{}//Rear right wheel
	};
	public static final double[][][] mecanumRightLeftLift = {
			{},//Front left wheel
			{},//Front right wheel
			{},//Rear left wheel
			{}//Rear right wheel
	};
	public static final double[][][] mecanumRightRightLift = {
			{},//Front left wheel
			{},//Front right wheel
			{},//Rear left wheel
			{}//Rear right wheel
	};
	public static final double[][][] mecanumRightLeftHopper = {
			{},//Front left wheel
			{},//Front right wheel
			{},//Rear left wheel
			{}//Rear right wheel
	};
	public static final double[][][] mecanumRightRightHopper = {
			{},//Front left wheel
			{},//Front right wheel
			{},//Rear left wheel
			{}//Rear right wheel
	};

	public static final double[][][] tractionMidCenterLift = {

			};
	public static final double[][][] tractionMidLeftLift = {

	};
	public static final double[][][] tractionMidRightLift = {
			{},//Left profile, should be sent to both left wheels
			{}//Right profile, should be sent to both right wheels
	};
	public static final double[][][] tractionMidLeftHopper = {
			{},//Left profile, should be sent to both left wheels
			{}//Right profile, should be sent to both right wheels
	};
	public static final double[][][] tractionMidRightHopper = {
			{},//Left profile, should be sent to both left wheels
			{}//Right profile, should be sent to both right wheels
	};
	public static final double[][][] tractionLeftCenterLift = {
			{},//Left profile, should be sent to both left wheels
			{}//Right profile, should be sent to both right wheels
	};
	public static final double[][][] tractionLeftLeftLift = {
			{},//Left profile, should be sent to both left wheels
			{}//Right profile, should be sent to both right wheels
	};
	public static final double[][][] tractionLeftRightLift = {
			{},//Left profile, should be sent to both left wheels
			{}//Right profile, should be sent to both right wheels
	};
	public static final double[][][] tractionLeftLeftHopper = {
			{},//Left profile, should be sent to both left wheels
			{}//Right profile, should be sent to both right wheels
	};
	public static final double[][][] tractionLeftRightHopper = {
			{},//Left profile, should be sent to both left wheels
			{}//Right profile, should be sent to both right wheels
	};
	public static final double[][][] tractionRightCenterLift = {
			{},//Left profile, should be sent to both left wheels
			{}//Right profile, should be sent to both right wheels
	};
	public static final double[][][] tractionRightLeftLift = {
			{},//Left profile, should be sent to both left wheels
			{}//Right profile, should be sent to both right wheels
	};
	public static final double[][][] tractionRightRightLift = {
			{},//Left profile, should be sent to both left wheels
			{}//Right profile, should be sent to both right wheels
	};
	public static final double[][][] tractionRightLeftHopper = {
			{},//Left profile, should be sent to both left wheels
			{}//Right profile, should be sent to both right wheels
	};
	public static final double[][][] tractionRightRightHopper = {
			{},//Left profile, should be sent to both left wheels
			{}//Right profile, should be sent to both right wheels
	};
}