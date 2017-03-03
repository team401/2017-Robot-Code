package org.team401.robot.auto.actions;

public class FollowPathAction implements Action {
	private double[][] path;
	private double time;
	private int i = 0;
	public FollowPathAction(double[][] path, double time){
		this.path = path;
		this.time = time;
	}
	public void start(){

	}
	public void update(){
		double t1 = Math.toDegrees(Math.atan2(path[i][1], path[i][0])),
				t2 = Math.toDegrees(Math.atan2(path[i+1][1], path[i+1][0]));//t stands for theta

		if(Math.abs(t1 - t2) > 3)//a turn is required
			runAction(new RotateAction(t2));

		//Drive to next point
		runAction(new DriveStraightAction(time/path.length, Math.sqrt(
				Math.pow(path[i][0] + path[i+1][0], 2) +
						Math.pow(path[i][1] + path[i+1][1],2)), t1));
		i++;
	}
	public boolean isFinished(){
		return i >= path.length;
	}
	public void end(){

	}
	//Copied from AutoMode.kt because Actions normally couldn't call other actions
	private void runAction(Action action){
		action.start();
		while(!action.isFinished()) {
			action.update();
			try{
				Thread.sleep((long)(1000.0 / 50.0));
			}catch(InterruptedException e){
				e.printStackTrace();
			}
		}
		action.end();
	}
}