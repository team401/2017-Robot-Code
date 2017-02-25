package org.team401.robot.auto.modes;


import org.team401.robot.auto.AutoMode;
import org.team401.robot.auto.actions.CalibrateTurretAction;
import org.team401.robot.auto.actions.DriveStraightAction;
import org.team401.robot.auto.actions.FollowPathAction;

public class CompetitionAuto extends AutoMode {
	public enum Mode{
		MIDGEAR, LEFTGEAR, RIGHTGEAR, REDHOPPER, BLUHOPPER
	}
	private Mode currentMode;
	public void routine() {
		runAction(new CalibrateTurretAction());
		switch(currentMode) {
			case MIDGEAR:
				runAction(new DriveStraightAction(0, 0, 0));//Replace all measurements!
				break;
			case LEFTGEAR:
				break;
			case RIGHTGEAR:
				break;
			case REDHOPPER:
				runAction(new FollowPathAction());
				break;
			case BLUHOPPER:
				runAction(new FollowPathAction());
				break;
		}
	}
	public void done(){

	}
}
