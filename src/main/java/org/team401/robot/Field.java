package org.team401.robot;

/**
 * Created by Driver Station on 1/28/2017.
 */
public class Field {
	public static final double[][] AIRSHIP = new double[][]{
			{11.831875, 9.325},
			{15.1681225, 9.325},
			{16.83625, 12.26625},
			{15.1681225, 15.2075},
			{11.831875, 15.2075},
			{10.16375, 12.26625},
			{11.831875, 9.325},
	};
	public static final double[][] BASELINE = new double[][]{
			{0, 7.775},
			{27, 7.775}
	};
	public static final double[][] NEUTRAL_ZONE = new double[][]{
			{0, 15.1681225},
			{27, 15.1681225},
			{27, 54 - 15.1681225},
			{0, 54 - 15.1681225},
			{0, 15.1681225}
	};
	public static final double[][] KEY_BLU = new double[][]{
			{3.036, 0},
			{9.525, 0},
			{0, 9.525},
			{0, 3.036},
			{3.036, 0}
	};
	public static final double[][] RETRIEVAL_ZONE_BLU = new double[][]{
			{27, 5.735},
			{27, 13.79583},
			{27 - 7.30325, 0},
			{27 - 3.036, 0},
			{27, 5.735}
	};
	public static final double[][] KEY_RED = new double[][]{
			{27 - 3.036, 0},
			{27 - 9.525, 0},
			{27, 9.525},
			{27, 3.036},
			{27 - 3.036, 0}
	};
	public static final double[][] RETRIEVEAL_ZONE_RED = new double[][]{
			{0, 5.735},
			{0, 13.79583},
			{7.30325, 0},
			{3.036, 0},
			{0, 5.735}
	};
}
