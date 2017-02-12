package org.team401.robot;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * Static class that reads motion profiles from .csv files.
 * Data expected is 3 columns:
 * Position(rotations), Velocity(RPM), Duration(ms)
 */

public class MotionProfiles {
	//alliance param defaults to false
	public static double[][][] get(String start, String tgt, boolean mecanum){
		return get(start, tgt, mecanum, false);
	}

	/**
	 * Scans multiple .csv files to get the correct motion profiles for any given start and end point.
	 *
	 * @param start Where the robot starts at the beginning of the match.  Valid inputs are "L", "C", and "R".
	 * @param tgt Where the robot will drive.  Valid inputs are "LH", "LL", "CL", "RL", and "RH".
	 * @param mecanum Whether to read spreadsheets for each gearbox or for left and right sets of gearboxes.
	 * @param alliance Whether or not the alliance we're on matters.
	 * @return Array with length 4 or 2 containing complete motion profiles.
	 */
	public static double[][][] get(String start, String tgt, boolean mecanum, boolean alliance) {
		//Adds an alliance identifier("R", "B", or "I") if the path is alliance-dependent. "I" should never occur.
		String all = alliance||tgt.endsWith("H") ? DriverStation.getInstance().getAlliance().name().substring(0, 1) : "";

		//Constructs the .csv names from parameters
		return mecanum ?
			new double[][][]{
				//Mecanum drive
				scanCSV("/home/mecanumprofiles/"+start+tgt+all+" FL"),
				scanCSV("/home/mecanumprofiles/"+start+tgt+all+" FR"),
				scanCSV("/home/mecanumprofiles/"+start+tgt+all+" RL"),
				scanCSV("/home/mecanumprofiles/"+start+tgt+all+" RR")
			}: new double[][][]{
				//Tank drive
				scanCSV("/home/tankprofiles/"+start+tgt+all+" L"),
				scanCSV("/home/tankprofiles/"+start+tgt+all+" R")
			};
	}

	/**
	 * Parses the contents of a .csv as a 2D array of doubles.
	 * @param fileName Name, without file extension, of the file to read from.
	 * @return Contents of the .csv
	 */
	private static double[][] scanCSV(String fileName){
		//Add file extension
		fileName = fileName + ".csv";

		//BufferedReader will read from the file
		BufferedReader br;

		//Gigantic String array to store as much data as needed
		String[][] strs = new String[99999][3];

		//Place to store the finished product
		double[][] result;

		//File I/O sometimes causes errors
		try {
			//Scan the file into Java
			br = new BufferedReader(new FileReader(fileName));

			//Read each line, and split it into 3 double strings
			int i = 0;
			for(String line; (line=br.readLine())!=null; i++)
				strs[i] = line.split(",");

			//Size the result to the length of the file
			result = new double[i][3];

			//Parse the string array into doubles
			for(int j = 0; j < i; j++)
				for(int k = 0; k < 3; k++)
					result[j][k] = Double.parseDouble(strs[j][k]);

			//Close the file reader and return the result
			br.close();
			return result;

		//Notify user of exceptions
		} catch (FileNotFoundException e) {
			System.out.println("File " + fileName + " not found!");
		} catch (IOException e) {
			System.out.println("IOException in scanCSV while scanning " + fileName + "!");
		}

		//Return empty array if there was an exception
		return new double[0][0];
	}
}