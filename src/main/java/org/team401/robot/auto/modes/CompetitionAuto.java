package org.team401.robot.auto.modes;

import org.team401.robot.auto.AutoMode;
import org.team401.robot.auto.actions.CalibrateTurretAction;
import org.team401.robot.auto.actions.DriveStraightAction;
import org.team401.robot.auto.actions.RotateAction;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

/**
 * This is early code. Replace all numbers in the class, as they are zeroed right now!
 */
public class CompetitionAuto extends AutoMode {
	public enum Destination{
		MIDGEAR, LEFTGEAR, RIGHTGEAR, LEFTHOPPER, RIGHTHOPPER
	}
	private Destination destination;
	private Alliance team;
	public CompetitionAuto(Destination destination){
		this.destination = destination;
		team = DriverStation.getInstance().getAlliance();
	}
	public void routine() {
		runAction(new CalibrateTurretAction());
		switch(destination) {
			case MIDGEAR:
				runAction(new DriveStraightAction(0, 0, 0));//Drive onto the peg
				findPath(AutoPaths.START_MID_TO_LIFT.getArr(), AutoPaths.START_MID_TO_LIFT.getTime());
				break;
			case LEFTGEAR:
				runAction(new DriveStraightAction(0, 0, 0));//Drive in front of the peg
				runAction(new RotateAction(0));//Face the peg
				runAction(new DriveStraightAction(0, 0, 0));//Drive onto the peg
				//add code to go to the hopper after gear
				//add code to shoot after hopper
				break;
			case RIGHTGEAR:
				runAction(new DriveStraightAction(0, 0, 0));//Drive in front of the peg
				runAction(new RotateAction(0));//Face the peg
				runAction(new DriveStraightAction(0, 0, 0));//Drive onto the peg
				//add code to go to the hopper after gear
				//add code to shoot after hopper
				break;
			case LEFTHOPPER:
				runAction(new DriveStraightAction(0, 0, 0));//Drive to the hopper area
				runAction(new DriveStraightAction(0, team.equals(Alliance.Blue)?0:0, team.equals(Alliance.Blue)?0:0));//Drive into the hopper
				//add code to shoot after hopper
				break;
			case RIGHTHOPPER:
				runAction(new DriveStraightAction(0, 0, 0));//Drive to the hopper area
				runAction(new DriveStraightAction(0, team.equals(Alliance.Blue)?0:0, team.equals(Alliance.Blue)?0:0));//Drive into the hopper
				//add code to shoot after hopper
				break;
		}
	}
	public void done(){

	}

	/**
	 * Method for translating the autopaths waypoints into Zachs methods.
	 * @param path the autopath being used
	 * @param time the time it takes to run it (should be part of the autopath)
	 */

	private void findPath(double[][] path, double time){
		double speed = time/path.length;
		for(int i = 0;i<path.length;i++){
			if(Math.atan2(path[i][1], path[i][0]) != Math.atan2(path[i+1][1], path[i+1][0])){//a turn is required
				runAction(new RotateAction(Math.toDegrees(Math.atan2(path[i+1][1], path[i+1][0]))));
			}
			//no turn needed
			runAction(new DriveStraightAction(speed, findDistance(path[i], path[i+1]),Math.atan2(path[i][1], path[i][0])));
		}

	}

	private double findDistance(double[] point1, double[] point2){
		return Math.sqrt(Math.pow(point1[0] + point2[0],2) + Math.pow(point1[1] + point2[1],2));

	}
}
