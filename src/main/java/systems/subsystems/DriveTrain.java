/*
 * DriveTrain.java
 * Author: Jeremiah Hanson
 * ------------------------------------------------------------
 * This class controls all the speedcontrollers for the drive train
 */

package systems.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import autonomous.AutonCircle;
import autonomous.AutonLine;
import autonomous.AutonLineDontStop;
import systems.Resources;
import systems.Subsystem;
import systems.SysObj;
import systems.Systems;

public class DriveTrain implements Subsystem {

	private TalonSRX ltMain, ltSlave1, ltSlave2;
	private TalonSRX rtMain, rtSlave1, rtSlave2;
	private static Systems systems;
	private PIDManual driveStraightPID;
	private RPID turnPID, turnSidePID;
	
	public Resources resources;
	private double driveConstant, distancePerPulse, turnConstant;
	private final double ROBOT_WIDTH; // Distance between wheels


	/*
	 * Constructor Author: Jeremiah Hanson
	 * --------------------------------------- constructor
	 */
	public DriveTrain(TalonSRX talonSRX, TalonSRX talonSRX2, TalonSRX talonSRX3, TalonSRX talonSRX4, TalonSRX talonSRX5,
			TalonSRX talonSRX6) {

		// assign motors
		ltMain = talonSRX;
		ltSlave1 = talonSRX2;
		ltSlave2 = talonSRX3;
		ltSlave1.follow(talonSRX);
		ltSlave2.follow(talonSRX);
		ltMain.setNeutralMode(NeutralMode.Brake);
		ltSlave1.setNeutralMode(NeutralMode.Brake);
		ltSlave2.setNeutralMode(NeutralMode.Brake);

		rtMain = talonSRX4;
		rtSlave1 = talonSRX5;
		rtSlave2 = talonSRX6;
		rtSlave1.follow(talonSRX4);
		rtSlave2.follow(talonSRX4);
		rtMain.setNeutralMode(NeutralMode.Brake);
		rtSlave1.setNeutralMode(NeutralMode.Brake);
		rtSlave2.setNeutralMode(NeutralMode.Brake);

		ltMain.configOpenloopRamp(0.15, 0);
		rtMain.configOpenloopRamp(0.15, 0);

		driveStraightPID = new PIDManual(0.11, 0, 0, 0.02);// 0.11,0,0
		turnPID = new RPID(0.023, 0.043, 0.003, 0.02);
		turnSidePID = new RPID(0.023, 0, 0.003, 0.02);

		driveStraightPID.setAngle(true);
		turnPID.setContinuous(true);
		turnPID.setInputRange(0, 360);
		turnPID.setOutputRange(-1,1);
		turnSidePID.setContinuous(true);
		turnSidePID.setInputRange(0, 360);
		turnSidePID.setOutputRange(-1,1);

		resources = new Resources();

		driveConstant = 1.0;
		turnConstant = 0.9;

		ROBOT_WIDTH = 21;

		distancePerPulse = 0.064;

		//leftAlign = new Thread(new LeftAlign());
		//rightAlign = new Thread(new RightAlign());

	}

	/*
	 * LeftAlign Author: Finny Collaborators: Nitesh Puri, Ethan Yes, Jeremiah
	 * Hanson --------------------------------------------------------- Purpose:
	 * Aligns the robot to the multiple of 90 degrees to the left of the robot's
	 * current direction
	 */
	/*private class LeftAlign implements Runnable {

		@Override
		public void run() {
			double dAngle = 0;
			double cAngle = systems.getNavXAngle();
			if (cAngle < 90 && cAngle > 0)
				dAngle = 0;
			else if (cAngle < 180 && cAngle > 90)
				dAngle = 90;
			else if (cAngle < 270 && cAngle > 180)
				dAngle = 180;
			else
				dAngle = 270;

			turnTo(dAngle, 1, 1500);
		}

	}*/

	/*
	 * RightAlign Author: Finny Collaborators: Nitesh Puri, Ethan Yes, Jeremiah
	 * Hanson --------------------------------------------------------- Purpose:
	 * Aligns the robot to the multiple of 90 degrees to the right of the
	 * robot's current direction
	 */
	/*private class RightAlign implements Runnable {

		@Override
		public void run() {
			double dAngle = 0;
			double cAngle = systems.getNavXAngle();
			if (cAngle < 90 && cAngle > 0)
				dAngle = 90;
			else if (cAngle < 180 && cAngle > 90)
				dAngle = 180;
			else if (cAngle < 270 && cAngle > 180)
				dAngle = 270;
			else
				dAngle = 0;

			turnTo(dAngle, 1, 1500);

		}

	}*/

	@Override
	public void update() {
		if (systems == null) {
			systems = Systems.getInstance();
			systems.setDistancePerPulse(Systems.getRobotEncoder(SysObj.Sensors.LEFT_ENCODER), distancePerPulse);
			systems.setDistancePerPulse(Systems.getRobotEncoder(SysObj.Sensors.RIGHT_ENCODER), distancePerPulse);
		}

		/*if (leftAlign.isAlive() || rightAlign.isAlive()) {
			return;
		}*/

		if (systems.inAuto) {
			return;
		}

		if (systems.getButton(Controls.Button.LEFT_BUMPER, true)) {
			driveConstant = 0.6;
			turnConstant = 0.6;
		} else if (systems.getButton(Controls.Button.RIGHT_BUMPER, true)) {
			driveConstant = 0.8;
			turnConstant = 0.8;
		} else {
			driveConstant = 0.9;
			turnConstant = 0.9;
		}

		/*if (systems.getDriverLtTrigger() > 0.5) {
			leftAlign.start();
		} else if (systems.getDriverRtTrigger() > 0.5) {
			rightAlign.start();
		}*/

		arcadeDrive(driveConstant * systems.getDriverAxisLeftY(), turnConstant * systems.getDriverAxisRightX(),
				true);
		// System.out.println("" + systems.getPulse());
	}

	@Override
	public void toSmartDashboard() {
		SmartDashboard.putString("DB/String 0", "Distance: " + systems.getEncoderDistance(SysObj.Sensors.LEFT_ENCODER));
	}

	/*
	 * getSystems Author: Finlay Parsons Collaborators: Nitesh Puri, Ethan Yes,
	 * Jeremiah Hanson -------------------------------------------------------
	 * Purpose: Gets an instance of Systems Returns: nothing
	 */
	public void getSystems() {
		systems = Systems.getInstance();
	}



	/*
	 * tankDrive Author: Finlay Parsons ------------------------- Sets each
	 * wheel speed individually. Used for circleTurn-ing in auto Parameters:
	 * double leftSpeed- left wheels speed double rightSpeed- right wheels speed
	 */
	public void tankDrive(double leftSpeed, double rightSpeed) {
		ltMain.set(ControlMode.PercentOutput, leftSpeed);
		rtMain.set(ControlMode.PercentOutput,-rightSpeed);
		
	}
	
	public void tankDrive(double leftSpeed, double rightSpeed, boolean squared) {
		ltMain.set(ControlMode.PercentOutput, leftSpeed);
		rtMain.set(ControlMode.PercentOutput, -rightSpeed);
//NO SQARONG YET
	}
	
	

	/*
	 * align Author: Finlay Parsons --------------------------- Purpose: Aligns
	 * robot to a multiple of 90 degrees
	 */
	public void align(int direction) {
		driveStraightPID.setDValue(direction);

	}

	/*
	 * driveDistance Author: Finlay Parsons Collaborators: Nitesh Puri, Ethan
	 * Yes, Jeremiah Hanson
	 * -------------------------------------------------------- Purpose: Drive a
	 * distance at a speed Parameters: distance: how far to drive (in inches)
	 * speed: how fast to drive Returns: nothing
	 */
	public void driveDistance(double distance, double speed) {
		SmartDashboard.putString("DB/String 6", "lol");
		//System.out.println(systems.getEncoderDistance(SysObj.Sensors.LEFT_ENCODER));
		//System.out.println(systems.getEncoderDistance(SysObj.Sensors.RIGHT_ENCODER));
		double desiredDriveAngle = systems.getNavXAngle();

		Systems.getRobotEncoder(SysObj.Sensors.LEFT_ENCODER).setDistancePerPulse(distancePerPulse);
		Systems.getRobotEncoder(SysObj.Sensors.RIGHT_ENCODER).setDistancePerPulse(distancePerPulse);

		driveStraightPID.setDValue(desiredDriveAngle);
		/*
		 * while(DriverStation.isAutonomous()){
		 * SmartDashboard.putString("DB/String 6", "lol");
		 * drive.arcadeDrive(0.5, 0); }
		 */

		while (DriverStation.isAutonomous()
				&& (systems.getEncoderDistance(SysObj.Sensors.RIGHT_ENCODER) < distance)) {
			Systems.getNavX().update();
			Systems.getRobotEncoder(SysObj.Sensors.RIGHT_ENCODER).update();
			Systems.getRobotEncoder(SysObj.Sensors.LEFT_ENCODER).update();
			driveStraightPID.setCValue(Resources.getAngleError(desiredDriveAngle, systems.getNavXAngle()));
			this.toSmartDashboard();
			driveStraightPID.toSmartDashboard();
			// drive.arcadeDrive(0.5, 0);
			arcadeDrive(speed, -(speed / (Math.abs(speed)) * driveStraightPID.getOutput()));
		}
		//System.out.print(systems.getEncoderDistance(SysObj.Sensors.LEFT_ENCODER));
		//System.out.println(systems.getEncoderDistance(SysObj.Sensors.RIGHT_ENCODER));

		arcadeDrive(0, 0);
		systems.resetEncoders();

	}

	/*
	 * driveIntake Author: Finlay Parsons Collaborators: Nitesh Puri, Ethan Yes
	 * ----------------------------------------- Purpose: Drive while intaking
	 */
	public void driveIntake(double speed, double intakeSpeed, double maxDistance) {
		while (systems.getMotorCurrent(10) < 50 && (systems.getAverageDriveEncoderDistance()) < maxDistance) {
			arcadeDrive(speed, -(speed / (Math.abs(speed)) * driveStraightPID.getOutput()));
			systems.intake(intakeSpeed);
		}
		arcadeDrive(0, 0);
		systems.intake(0);
	}

	/*
	 * turnTo Author: Finlay Parsons Collaborators: Nitesh Puri
	 * -------------------------------- Purpose: Turns the robot to a certain
	 * angle.
	 */

	public void turnTo(double angle, double speed, double time) {
		turnPID.setSetPoint(angle);
		long startTime = System.currentTimeMillis();

		while ((System.currentTimeMillis() - startTime) < time) {
			Systems.getNavX().update();

			double rotateOutput = Resources.limit(turnPID.crunch(systems.getNavXAngle()), -speed, speed);
			rotateOutput += rotateOutput > 0 ? 0.07 : -0.07;
			//System.out.println("CURRENTANGLE:" + systems.getNavXAngle() +"  SETPOINT:" + turnPID.getSetPoint() + "      output" + rotateOutput);
			arcadeDrive(0, Resources.limit(rotateOutput,-1,1), false);
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		arcadeDrive(0, 0);
	}
	/*
	 * turnToOneSide
	 * Author: Ethan Yes
	 * Collaborator: Ruben Castro
	 * --------------------------------------------
	 * Purpose: turn powering only one side
	 * Parameters: angles, speed, time, rightside
	 */
	public void turnToOneSide(double angle, double speed, double time, boolean right) {
		turnSidePID.setSetPoint(angle);
		long startTime = System.currentTimeMillis();

		while ((System.currentTimeMillis() - startTime) < time) {
			Systems.getNavX().update();

			double rotateOutput = Resources.limit(turnSidePID.crunch(systems.getNavXAngle()), -speed, speed);
			rotateOutput += rotateOutput > 0 ? 0.04 : -0.04;
			//System.out.println("CURRENTANGLE:" + systems.getNavXAngle() +"  SETPOINT:" + turnPID.getSetPoint() + "      output" + rotateOutput);
			tankDrive(Resources.limit(right? 0:rotateOutput,-1,1), Resources.limit(right? -rotateOutput:0,-1,1), false);
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		arcadeDrive(0, 0);
	}

	/*
	 * circleTurn Author: Finlay Parsons ------------------------ Robot moves
	 * along a circle with a specified radius to a specified angle FIN DO THE
	 * PARAMETERS
	 */
	public void circleTurn(double radius, double angle, double speed, boolean right, boolean forwards) {
		double v1 = 1, v2;
		int counter = 0;
		double prevEncoder = 0;

		if (!forwards)
			speed *= -1;
		v2 = v1 / ((ROBOT_WIDTH + radius) / radius);

		while (DriverStation.isAutonomous()
				&& (systems.getNavXAngle() >= angle + 2 || systems.getNavXAngle() <= angle - 2)) {
			Systems.getNavX().update();
			systems.update();

			if ((right && angle < systems.getNavXAngle()) || (!right && angle > systems.getNavXAngle())) {
				if (360 - Math.abs(angle - systems.getNavXAngle()) < 30) {
					// speed =
					// speed*(Math.abs(angle-systems.getNavXAngle())/30);
					speed = 0.6;
				}
			} else {
				if (Math.abs(angle - systems.getNavXAngle()) < 30) {
					// speed =
					// speed*(Math.abs(angle-systems.getNavXAngle())/30);
					speed = 0.6;
				}
			}
			if (right) {
				tankDrive(speed * v1, speed * v2);
			} else {
				tankDrive(speed * v2, speed * v1);
			}

			if (systems.getEncoderDistance(SysObj.Sensors.RIGHT_ENCODER) == prevEncoder) {
				counter++;
			} else {
				counter = 0;
			}
			prevEncoder = systems.getEncoderDistance(SysObj.Sensors.RIGHT_ENCODER);

			if (counter > 600)
				break;

		}
		// System.out.print("v1: " + v1 + " ");
		// System.out.print("v2: " + v2);
		// System.out.println(" Angle:" + systems.getNavXAngle());
		arcadeDrive(0, 0);
	}
	
		/*
		 * driveLine
		 * Author: Ethan Yes
		 * Collaborator: Ruben Castro
		 * -----------------------------------------------
		 * Purpose: Drive a distance using AutonLine
		 * Parameter: distance, angle
		 */
	public void driveLine(double distance, double angle, double speed){
		new AutonLine(Systems.getDriveTrain(), Systems.getNavX(), distance, speed, angle).run();
	}
	
	public void driveAuton(double speed,double time){
		long startTime = System.currentTimeMillis();
		while(System.currentTimeMillis() - startTime < time && DriverStation.isAutonomous()) {
			drive(speed, 0, false);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		drive(0,0);
	}
	
	public void driveLineDontStop(double distance, double angle, double speed, boolean straighten){
		new AutonLineDontStop(Systems.getDriveTrain(), Systems.getNavX(), distance, speed, angle, straighten).run();
	}
	
	public void driveCircle(double radius, double angle, boolean forward, boolean right, int counter) {
		new AutonCircle(Systems.getDriveTrain(), Systems.getNavX(), angle, radius, forward, right, counter).run();
	}
	
	/*
	 * drive Author: Finlay Parsons Collaborators: Jeremiah Hanson
	 * ------------------------------- Purpose: Set velocity and rotation of
	 * robot. Parameters: x: Speed of the motors (-1 to 1) z: Rotation of the
	 * robot (-1 to 1 - Clockwise is positive) Return: None
	 */

	
	public void drive(double move, double rotate){
		arcadeDrive(move,rotate, true);
	}
	
	public void drive(double move, double rotate, boolean squared){
		arcadeDrive(move,rotate, squared);
	}
	public void arcadeDrive(double move, double rotate){
		arcadeDrive(move,rotate, true);
	}
	
	 public void arcadeDrive(double move, double rotate, boolean squared) {
			double leftMotorSpeed;
			double rightMotorSpeed;
			double moveValue, rotateValue;
			moveValue = Resources.limit(move, -1, 1);
			rotateValue = -Resources.limit(rotate, -1, 1); // negative cus yeah

			//squared inputs
			if (squared) {
				// square the inputs (while preserving the sign) to increase fine
				// control
				// while permitting full power
				if (moveValue >= 0.0) {
					moveValue = (moveValue * moveValue);
				} else {
					moveValue = -(moveValue * moveValue);
				}
				if (rotateValue >= 0.0) {
					rotateValue = (rotateValue * rotateValue);
				} else {
					rotateValue = -(rotateValue * rotateValue);
				}
			}

			if (moveValue > 0.0) {
				if (rotateValue > 0.0) {
					leftMotorSpeed = moveValue - rotateValue;
					rightMotorSpeed = Math.max(moveValue, rotateValue);
				} else {
					leftMotorSpeed = Math.max(moveValue, -rotateValue);
					rightMotorSpeed = moveValue + rotateValue;
				}
			} else {
				if (rotateValue > 0.0) {
					leftMotorSpeed = -Math.max(-moveValue, rotateValue);
					rightMotorSpeed = moveValue + rotateValue;
				} else {
					leftMotorSpeed = moveValue - rotateValue;
					rightMotorSpeed = -Math.max(-moveValue, -rotateValue);
				}
			}
			setVoltage(leftMotorSpeed,rightMotorSpeed);
		}
	 
	 public void setVoltage(double left, double right){
		 //System.out.println("LEFT: " + left  +"   RIGHT" + right);
		 ltMain.set(ControlMode.PercentOutput, left);
		 rtMain.set(ControlMode.PercentOutput,- right);
		
	 }
}
