package org.team401.robot.auto.modes;

import org.team401.robot.auto.AutoMode;
import org.team401.robot.auto.actions.CalibrateTurretAction;
import org.team401.robot.auto.actions.DriveStraightAction;
import org.team401.robot.auto.actions.FollowPathAction;
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
	private boolean team;
	public CompetitionAuto(Destination destination){
		this.destination = destination;
		team = DriverStation.getInstance().getAlliance().equals(Alliance.Blue);
	}
	public void routine() {
		runAction(new CalibrateTurretAction());
		switch(destination) {
			case MIDGEAR:
				runAction(new FollowPathAction(AutoPaths.START_MID_TO_LIFT.getArr(), AutoPaths.START_MID_TO_LIFT.getTime()));
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
				runAction(new DriveStraightAction(0, team?0:0, team?0:0));//Drive into the hopper
				//add code to shoot after hopper
				break;
			case RIGHTHOPPER:
				runAction(new DriveStraightAction(0, 0, 0));//Drive to the hopper area
				runAction(new DriveStraightAction(0, team?0:0, team?0:0));//Drive into the hopper
				//add code to shoot after hopper
				break;
		}
	}
	public void done(){

	}
}
