package org.team401.robot.MPCreation;

/**
 * Simple wrapper class for a 2D array , string, and boolean.
 */

public class NamedPath {
	//Data to store
	private double[][] arr;
	private String name;
	private boolean invert;

	//invert defaults to false
	public NamedPath(String name, double[][] arr){
		this(name, false, arr);
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

	/**
	 * Gets the X- or Y-values in the path
	 *
	 * @return First or second column of original parameter
	 */
	public double[] getX() {
		//Simple copy of every value in column 0
		double[] temp = new double[arr.length];
		for (int i = 0; i < temp.length; i++)
			temp[i] = arr[i][0];
		return temp;
	}
	public double[] getY() {
		//Simple copy of every value in column 1
		double[] temp = new double[arr.length];
		for (int i = 0; i < temp.length; i++)
			temp[i] = arr[i][1];
		return temp;
	}

	/**
	 * Transposes the array and returns it.
	 *
	 * @return Array, but first and second subscripts are switched
	 */
	public double[][] transpose() {
		//Copies each value, but j and i are swapped inside the loop
		for (int i = 0; i < arr.length; i++)
			for (int j = 0; j < arr[i].length; j++)
				arr[i][j] = arr[j][i];
		return arr;
	}
}