package org.team401.robot;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

public class GeneratedMotionProfiles {
	// Data format: Position (rotations)	Velocity (RPM)	Duration (ms)
	public static double[][][] getProfile(String start, String tgt, boolean mecanum) {
		return mecanum ?
			new double[][][]{//Mecanum drive
				scanCSV(start+tgt+" FL"),
				scanCSV(start+tgt+" FR"),
				scanCSV(start+tgt+" RL"),
				scanCSV(start+tgt+" RR")
			}: new double[][][]{//Tank drive
				scanCSV(start+tgt+" L"),
				scanCSV(start+tgt+" R")
			};
	}

	public static double[][] scanCSV(String fileName){
		BufferedReader br = null;
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