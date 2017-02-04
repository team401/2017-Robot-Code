package org.team401.robot;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import edu.wpi.first.wpilibj.DriverStation;

public class MotionProfiles {
	// Data format: Position (rotations)	Velocity (RPM)	Duration (ms)
	public static double[][][] get(String start, String tgt, boolean mecanum){
		return get(start, tgt, mecanum, false);
	}
	public static double[][][] get(String start, String tgt, boolean mecanum, boolean alliance) {
		//Format for first profile names:
		//STARTPOS{L, C, R}+DESTINATION{LH, LL, CL, RL, RH}+IFHOPPER{ALLIANCE{R, B}}+space+WHEEL{FL, FR, RL, RR, L, R}
		//Second profile names:
		//STARTPOS{LH, LL, CL, RL, RH}+ALLIANCE{R, B}+space+WHEEL{FL, FR, RL, RR, L, R}

		String all = alliance||tgt.charAt(1)=='H' ? DriverStation.getInstance().getAlliance().name().substring(0, 1) : "";
		//Add on an alliance identifier(R, B, or I) if our paths are alliance-dependent.
		//Should return nothing if alliance is Invalid
		return mecanum ?
			new double[][][]{//Mecanum drive
				scanCSV(start+tgt+all+" FL"),
				scanCSV(start+tgt+all+" FR"),
				scanCSV(start+tgt+all+" RL"),
				scanCSV(start+tgt+all+" RR")
			}: new double[][][]{//Tank drive
				scanCSV(start+tgt+all+" L"),
				scanCSV(start+tgt+all+" R")
			};
	}

	public static double[][] scanCSV(String fileName){
		BufferedReader br;
		String line;
		fileName = fileName + ".csv";
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
}