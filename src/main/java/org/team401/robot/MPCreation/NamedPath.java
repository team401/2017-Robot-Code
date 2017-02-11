package org.team401.robot.MPCreation;

/**
 * Simple wrapper class for a 2D array , string, and boolean.
 */

public class NamedPath {
	//Data to store
	private double[][] arr;
	private double time, avg;
	private String name;
	private boolean invert;

	//invert defaults to false, time defaults to 15 seconds
	public NamedPath(String name, double[][] arr){
		this(name, false, arr);
	}
	public NamedPath(String name, boolean invert, double[][] arr){
		this(name, invert, 15, arr);
	}
	public NamedPath(String name, double time, double[][] arr) {
		this(name, false, time, arr);
	}
	/**
	 * Constructor
	 * @param name Instance data
	 * @param invert Instance data
	 * @param time Instance data
	 * @param arr Instance data
	 */
	public NamedPath(String name,  boolean invert, double time, double[][] arr){
		this.name = name;
		this.arr = arr;
		this.invert = invert;
		this.time = time;
		this.avg = time/arr.length;
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
	public double getTime() {
		return time;
	}
	public double getAvg() {
		return avg;
	}

	/**
	 * Setters
	 */
	public void setName(String name){
		this.name = name;
	}
	public void setArr(double[][] arr){
		this.arr = arr;
		avg = time/arr.length;
	}
	public void setInvert(boolean invert){
		this.invert = invert;
	}
	public void setTime(double time){
		this.time = time;
		avg = time/arr.length;
	}

	/**
	 * Gets the X- or Y-values in the path
	 *
	 * @return First or second column of original parameter
	 */
	public double[] getX() {
		return getColumn(0);
	}
	public double[] getY() {
		return getColumn(1);
	}
	public double[] getColumn(int col) {
		//Simple copy of every value in the selected column
		double[] temp = new double[arr.length];
		for (int i = 0; i < temp.length; i++)
			temp[i] = arr[i][col];
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