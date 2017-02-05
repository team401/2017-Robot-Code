package org.team401.robot.MPCreation;

/**
 * Simple wrapper class for an array and string.
 */

public class NamedPath {
	//Data to store
	private double[][] arr;
	private String name;

	/**
	 * Constructor
	 * @param name Instance data
	 * @param arr Instance data
	 */
	public NamedPath(String name, double[][] arr){
		this.name = name;
		this.arr = arr;
	}

	/**
	 * Getters
	 */
	public String getName(){
		return name;
	}
	public double[][] getArr(){
		return arr;
	}

	/**
	 * Setters
	 */
	public void setName(String name){
		this.name = name;
	}
	public void setArr(double[][] arr){
		this.arr = arr;
	}
}