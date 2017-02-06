package org.team401.robot.MPCreation;

/**
 * Simple wrapper class for an array and string.
 */

public class NamedPath {
	//Data to store
	private double[][] arr;
	private String name;
	private boolean invert;

	//invert defaults to false
	public NamedPath(String name, double[][] arr){
		this.name = name;
		this.arr = arr;
		invert = false;
	}

	/**
	 * Constructor
	 * @param name Instance data
	 * @param invert Instance data
	 * @param arr Instance data
	 */
	public NamedPath(String name,  boolean invert, double[][] arr){
		this.name = name;
		this.arr = arr;
		this.invert = invert;
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
	public boolean getInvert(){
		return invert;
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
	public void setInvert(boolean invert){
		this.invert = invert;
	}
}