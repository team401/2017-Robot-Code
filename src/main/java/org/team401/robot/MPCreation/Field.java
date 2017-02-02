package org.team401.robot.MPCreation;

public class Field {
	public static final double[][] AIRSHIP = {
			{11.831875, 9.325},
			{15.1681225, 9.325},
			{16.83625, 12.26625},
			{15.1681225, 15.2075},
			{11.831875, 15.2075},
			{10.16375, 12.26625},
			{11.831875, 9.325},
	};
	public static final double[][] BASELINE = {
			{0, 7.775},
			{27, 7.775}
	};
	public static final double[][] NEUTRAL_ZONE = {
			{0, 15.1681225},
			{27, 15.1681225},
			{27, 54 - 15.1681225},
			{0, 54 - 15.1681225},
			{0, 15.1681225}
	};
	public static final double[][] KEY_BLU = {
			{3.036, 0},
			{9.525, 0},
			{0, 9.525},
			{0, 3.036},
			{3.036, 0}
	};
	public static final double[][] RETRIEVAL_ZONE_BLU = {
			{27, 5.735},
			{27, 13.79583},
			{27 - 7.30325, 0},
			{27 - 3.036, 0},
			{27, 5.735}
	};
	public static final double[][] KEY_RED = {
			{27 - 3.036, 0},
			{27 - 9.525, 0},
			{27, 9.525},
			{27, 3.036},
			{27 - 3.036, 0}
	};
	public static final double[][] RETRIEVEAL_ZONE_RED = {
			{0, 5.735},
			{0, 13.79583},
			{7.30325, 0},
			{3.036, 0},
			{0, 5.735}
	};
	public static final double[][] LEFTHOPPERS = {
			{0, 13.7916},
			{0, 15.7916},
			{-1, 15.7916},
			{-1, 13.7916},
			{0, 13.7916},
			{0, 19.7916},
			{-1, 19.7916},
			{-1, 17.7916},
			{0, 17.7916},
	};
	public static final double[][] RIGHTHOPPERS = {
			{27, 6.525},
			{27, 8.525},
			{28, 8.525},
			{28, 6.525},
			{27, 6.525},
			{27, 12.525},
			{28, 12.525},
			{28, 10.525},
			{27, 10.525},
	};
}
