/*
 * AutonCircle.java
 * Author: Argeo Leyva
 * Collaborators: Nitesh Puri, Ethan Yes, Jeremiah Hanson
 * Purpose: Class allowing circle turns in Auto
 */

package autonomous;

import edu.wpi.first.wpilibj.DriverStation;
import systems.Resources;
import systems.subsystems.DriveTrain;
import systems.subsystems.NavX;
import systems.subsystems.RPID;

public class AutonCircle implements Runnable {

	double angle, speed, radius;
	double rotateOutput;
	double loopCount, pastCount;
	boolean forward, right;
	int counter, counter1;

	final double acceleration = 300;
	final double kA = (1 / acceleration) * 0;

	DriveTrain drive;
	RPID pid;
	NavX navx;
	

	public AutonCircle(DriveTrain driveTrain, NavX navx, double angle, double radius,
			boolean forward, boolean right, int counter1) {
		this.angle = angle;

		this.drive = driveTrain;
		this.navx = navx;
		this.forward = forward;
		this.right = right;
		this.counter1 = counter1;
		
		pid = new RPID(0.033, 0, 0.003, 0.05);
		pid.setContinuous(true);
		pid.setInputRange(0, 360);
		pid.setOutputRange(-0.8, 0.8);
		
		this.radius = radius;
		this.speed = ((1.7174) + (-0.2326 * Math.log(Math.abs(radius)))); //random numbers, fine tuning
		speed *= 0.73;
	}

	double delT = 50;

	public void run() {
		pid.setSetPoint(Resources.mod(angle, 360));

		double forwardC = forward? 1:-1;
		
		while (counter < counter1 && DriverStation.isAutonomous()) {
			double pidOutput = pid.crunch(navx.getCurrentAngle());
			pidOutput += pidOutput > 0 ? 0.0875 : -0.0875;
			double absPidOutput = Math.abs(pidOutput);
			//System.out.println("AutonCircle.run() Current Angle: " + navx.getCurrentAngle());
			if(Resources.getAngleError(Resources.mod(angle,  360), navx.getCurrentAngle()) >50) {
				if((right && forward) || ( !right && !forward)) {
					drive.drive(0.8 * absPidOutput * forwardC, Math.abs(0.8 * speed * pidOutput ),false);
				}else {
					drive.drive(0.8 * absPidOutput * forwardC, -Math.abs(0.8 * speed * pidOutput ),false);	
				}				
			}else {
				if(right) {
					drive.drive(0.8 * pidOutput , 0.8 * speed * pidOutput ,false);
				}else {
					drive.drive(0.8 * pidOutput * -1.0, 0.8 * speed * pidOutput ,false);	
				}				
			}

			//System.out.println("Output: " + 0.8 * pidOutput + "   Current Angle: " + navx.getCurrentAngle() + "   Setpoint: " + pid.getSetPoint() + "   Counter: " + counter);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(Math.abs(Resources.getAngleError(Resources.mod(angle,  360), navx.getCurrentAngle())) < 3){
				counter++;
			}
		}

		drive.drive(0, 0);

	}
}
