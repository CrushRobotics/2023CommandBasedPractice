/*
 * Collector.java
 * Author: Nitesh Puri
 * Collaborator: Jeremiah Hanson
 * -----------------------------------------------------
 * This class controls the motors on the collector
 */

package systems.subsystems;

import java.text.DecimalFormat;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import autonomous.Loop;
import systems.Resources;
import systems.Subsystem;
import systems.SysObj;
import systems.Systems;

public class Collector implements Subsystem {

	private TalonSRX collectorArm1, collectorArm2;
	private WPI_VictorSPX intakeLeft, intakeRight;
	private static Systems systems;
	private RobotEncoder armEncoder1;
	private RobotEncoder armEncoder2;
	private RPID goodArmPID; // Just in case
	private Solenoid punch;

	private double averageArmEncoderPos, desiredPos, startPos;
	private double encoderRange;
	private double angleConstant, armConstant;
	private double idleTurnConstant, slowConst;
	
	private static final double DISTANCE_CONSTANT = 0.023; //135/5800;
	private Controls.Button prevButt;
	
	private boolean broke;

	private boolean idleTurn, manualMode, collecting, fast;

	private int position, counter, curCount;

	DecimalFormat df;

	private static Thread cubeThrowThread;

	private Runnable updateRunnable;
	private Loop updateLoop;

	/*
	 * Constructor Author: Nitesh Puri ----------------------------------------
	 * constructor
	 */

	public Collector(TalonSRX talonSRX, TalonSRX talonSRX2, WPI_VictorSPX intakeLeft, WPI_VictorSPX intakeRight,
			RobotEncoder armEncoder1, RobotEncoder armEncoder2, Solenoid punch) {
		this.collectorArm1 = talonSRX;
		this.collectorArm2 = talonSRX2;
		this.intakeLeft = intakeLeft;
		this.intakeRight = intakeRight;
		this.armEncoder1 = armEncoder1;
		this.armEncoder2 = armEncoder2;
		this.punch = punch;
		this.goodArmPID = new RPID(0.015, 0.0, 0.001, 0.02); // 0.015, 0, 0
		//0.03, 0.0, 0.00555
		// 0.035, 0,
		// 0.005, 0.02
		broke = false;
		
		curCount = 0;

		df = new DecimalFormat("#.##");

		intakeRight.setInverted(true);

		//collectorArm1.setNeutralMode(NeutralMode.Coast);
		collectorArm2.setNeutralMode(NeutralMode.Coast);
		
		
		//talonSRX.configOpenloopRamp(0.3, 0);
		talonSRX2.configOpenloopRamp(0.3, 0);
		
		
		//talonSRX.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 20);
		talonSRX2.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, 0, 20);
		
		
		//talonSRX.setSensorPhase(true);
		talonSRX2.setSensorPhase(true);
		
		//talonSRX.configNominalOutputForward(0, 20);
		talonSRX2.configNominalOutputForward(0, 20);
		//talonSRX.configNominalOutputReverse(0, 20);
		talonSRX2.configNominalOutputReverse(0, 20);
		//talonSRX.configPeakOutputForward(1, 20);
		talonSRX2.configPeakOutputForward(1, 20);
		//talonSRX.configPeakOutputReverse(-1, 20);
		talonSRX2.configPeakOutputReverse(-1, 20);
		
		//talonSRX.config_kF(0, 0.1, 20);
		//talonSRX.config_kP(0, 0.1, 20);
		//talonSRX.config_kI(0, 0.0, 20);
		//talonSRX.config_kD(0, 0.0, 20);
		talonSRX2.config_kF(0, 0.0, 20);
		talonSRX2.config_kP(0, 0.1, 20);
		talonSRX2.config_kI(0, 0.0, 20);
		talonSRX2.config_kD(0, 0.0, 20);
		
		//int absolutePosition1 = talonSRX.getSensorCollection().getPulseWidthPosition();
		int absolutePosition2 = talonSRX2.getSensorCollection().getPulseWidthPosition();
		//absolutePosition1 *= -1;
		
		talonSRX.setSelectedSensorPosition(absolutePosition2, 0, 20);
		
		//absolutePosition1 &= 0xFFF;
		absolutePosition2 &= 0xFFF;
		
		
		averageArmEncoderPos = 0;
		startPos = collectorArm2.getSelectedSensorPosition(0);

		talonSRX2.setInverted(true);

		position = 0;
		encoderRange = 0;
		goodArmPID.setSetPoint(0);
		goodArmPID.setOutputRange(-1, 1);
		armConstant = 1;
		idleTurnConstant = 0;
		
		prevButt = Controls.Button.RIGHTJOY;

		idleTurn = false;
		manualMode = false;
		collecting = false;
		fast = false;

		updateRunnable = () -> {
			if (DriverStation.isEnabled())
				update();
		};

		updateLoop = new Loop(updateRunnable, 50);

		cubeThrowThread = new Thread(new CubeThrow());
	}

	/*
	 * CubeThrow Author: Nitesh Puri Collaborators: Jeremiah Hanson
	 * -------------------------------------- Runnable Class Purpose: Throw the
	 * cube.
	 */

	private class CubeThrow implements Runnable {

		@Override
		public void run() {

			goodArmPID.setSetPoint(35);

			while (true) {
				boolean stop = false;
				averageArmEncoderPos = 0.5 * (systems.getEncoderDistance(SysObj.Sensors.ARM_ENCODER_1)
						+ systems.getEncoderDistance(SysObj.Sensors.ARM_ENCODER_2));

				// goodArmPID.setCValue(averageArmEncoderPos);
				collectorArm1.set(ControlMode.PercentOutput, goodArmPID.crunch(averageArmEncoderPos));
				collectorArm2.set(ControlMode.PercentOutput, goodArmPID.crunch(averageArmEncoderPos));

				if (averageArmEncoderPos <= 50) {
					outtakeCubeAuto(.6, false);
					stop = true;
				}
				if (stop)
					break;
			}

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see systems.Subsystem#update()
	 */
	@Override
	public void update() {

		if (cubeThrowThread.isAlive()) {
			return;
		}

		/*
		 * averageArmEncoderPos = 0.5 *
		 * (systems.getEncoderDistance(SysObj.Sensors.ARM_ENCODER_1) +
		 * systems.getEncoderDistance(SysObj.Sensors.ARM_ENCODER_2));
		 */
		averageArmEncoderPos = -systems.getEncoderDistance(SysObj.Sensors.ARM_ENCODER_1);
		double newEncoderPos = 118 - Math.abs(collectorArm2.getSelectedSensorPosition(0) - startPos) * DISTANCE_CONSTANT;
		//System.out.println("1: " + averageArmEncoderPos);
		//double averageArmEncoderPos0 = systems.getEncoderDistance(SysObj.Sensors.ARM_ENCODER_2);
		//System.out.println("2: " + averageArmEncoderPos0);
		if (true) {
			// Controls for intake
			//TODO MIGHT BE DIFFERENT FOR REAL ROBOT (negative/positive intake values) 
			if (systems.getMotorCurrent(10) < 75 && systems.getMotorCurrent(11) < 75) {
				if(systems.getButton(Controls.Button.LEFT_BUMPER, false) || systems.getDriverRtTrigger() > 0.1) {
					intakeLeft.set(-0.4);
					intakeRight.set(-0.4);
				} else if(systems.getOperatorRtTrigger() > .1) {
					intakeLeft.set(0.80);
					intakeRight.set(0.80);
					if (position == 3 && !collecting) {
						//collectorArm1.set(ControlMode.Position, -5200);
						if (!broke) goodArmPID.setSetPoint(104);
						else goodArmPID.setSetPoint(120);
						collecting = true;
					}
				} else if (systems.getOperatorLtTrigger() > .1 || systems.getDriverLtTrigger() > 0.1) {
					intakeLeft.set(-1.0);
					intakeRight.set(-1.0);
				} else {
					if (!systems.inAuto) {
						intakeLeft.set(idleTurnConstant);
						intakeRight.set(idleTurnConstant);
					}
					if (collecting) {
						if (!broke) goodArmPID.setSetPoint(95);
						else goodArmPID.setSetPoint(110);
						//collectorArm1.set(ControlMode.Position, -4900);
						SmartDashboard.putString("DB/String 2", "Hellu");
						collecting = false;
					}
				}
			} else {

				intakeLeft.set(0);
				intakeRight.set(0);
			}

			// goodArmPID.setCValue(averageArmEncoderPos);
			
			if (averageArmEncoderPos > 125 && position == 1) {
				slowConst = 0.2;
			} else {
				slowConst = 1;
			}

			// Controls for arm
			if (systems.getButton(Controls.Button.LEFT_BUMPER, false)) {
				intakeLeft.set(-0.5);
				intakeRight.set(-0.5);
				prevButt = Controls.Button.LEFT_BUMPER;
			}
			if (systems.getButton(Controls.Button.RIGHT_BUMPER, false)) {
				idleTurnConstant = 0.5; // might be different for real robot
				prevButt = Controls.Button.RIGHT_BUMPER;
			} else {
				idleTurnConstant = 0;
			}
			if (systems.getButton(Controls.Button.B, false)) {
				/*position = 3;
				//collectorArm1.set(ControlMode.Position, 115); //4900
				//collectorArm2.set(ControlMode.Position, 115);
				goodArmPID.setSetPoint(115);
				prevButt = Controls.Button.B;*/
				position = 3;
				//goodArmPID.setSetPoint(115);
				if (!broke) goodArmPID.setSetPoint(95);
				else goodArmPID.setSetPoint(110);
				//outtakeCube(1, 400, 1000);
				//toggle(true, 450);
			}
			if (systems.getButton(Controls.Button.A, false) || systems.getButton(Controls.Button.A, true)) {
				position = 4;
				//collectorArm1.set(ControlMode.Position, 15); //250
				//collectorArm2.set(ControlMode.Position, 15);
				goodArmPID.setSetPoint(15);
				prevButt = Controls.Button.A;
			}
			//TODO MIGHT BE DIFFERENT FOR REAL ROBOT (negative/positive intake values)
			if (systems.getButton(Controls.Button.Y, false) || systems.getButton(Controls.Button.Y, true)) {
				position = 1;
				//collectorArm1.set(ControlMode.Position, 120); //5200
				//collectorArm2.set(ControlMode.Position, 120);
				if (!broke) goodArmPID.setSetPoint(104);
				else goodArmPID.setSetPoint(120);
				prevButt = Controls.Button.Y;
			}
			if(systems.getButton(Controls.Button.X, false) || systems.getButton(Controls.Button.X, true)) {
				position = 2;
				//collectorArm1.set(ControlMode.Position, 70); //2875
				//collectorArm2.set(ControlMode.Position, 70);
				goodArmPID.setSetPoint(70);
				prevButt = Controls.Button.X;
			}
			/*if(systems.getButton(Controls.Button.LEFTJOY, false)) {
				position = 5;
				goodArmPID.setSetPoint(55);
				toggle(true);
				prevButt = Controls.Button.LEFTJOY;
			}*/

			if (systems.getButton(Controls.Button.BACK, false) && prevButt != Controls.Button.BACK) {
				manualMode = !manualMode;
				prevButt = Controls.Button.BACK;
			}
			
			
			if (systems.getButton(Controls.Button.START, false) && prevButt != Controls.Button.START) {
				if (!broke) this.goodArmPID = new RPID(0.015, 0.0, 0.001, 0.02);
				else this.goodArmPID = new RPID(0.018, 0.0, 0.001, 0.02);
				broke = !broke;
				prevButt = Controls.Button.START;
			}
			
			/*if (curCount == 50) {
				System.out.println("Collector.update(): arm2 minus start: " + newEncoderPos);
				System.out.println("                    arm2 pos: " + collectorArm2.getSelectedSensorPosition(0));
				System.out.println("                    start: " + startPos);
				curCount = 0;
			}
			curCount++;*/
			
				

			/*
			 * if(systems.getDPadButton(Controls.POV.UP, false)){ double i =
			 * averageArmEncoderPos; goodArmPID.setSetPoint(i - 5); }
			 * 
			 * if(systems.getDPadButton(Controls.POV.DOWN, false)){ double i =
			 * averageArmEncoderPos; goodArmPID.setSetPoint(i + 5); }
			 */

			// Automatic operator controls
			// manualMode = true;
			if (!manualMode) {
				systems.setRumbleOperator(0, true);
				if (fast) {
					collectorArm1.set(ControlMode.PercentOutput, 0.75);
					collectorArm2.set(ControlMode.PercentOutput, 0.75);
				} else {
					double angleFromTop, motorValue, feedForward;
					if (!broke) {
						angleFromTop = newEncoderPos - 45.0;
						feedForward = Math.sin(Math.toRadians(angleFromTop)) * 0.1;
						motorValue = (goodArmPID.crunch(newEncoderPos) + feedForward) * slowConst;
					}
					else {
						angleFromTop = averageArmEncoderPos - 45.0;
						feedForward = Math.sin(Math.toRadians(angleFromTop)) * 0.1;
						motorValue = (goodArmPID.crunch(averageArmEncoderPos) + feedForward) * slowConst;
					}
					
					//System.out.println("motorVAlue :" + motorValue + "    SETPOINT:" + goodArmPID.getSetPoint()
					//		+ "  CurrentValue:" + averageArmEncoderPos);
					if(!cubeThrowThread.isAlive()) {
						collectorArm1.set(ControlMode.PercentOutput, motorValue);
						collectorArm2.set(ControlMode.PercentOutput, motorValue);
					}
				}
			}

			// Manual operator controls
			if (manualMode) {
				systems.setRumbleOperator(0.1, true);
				collectorArm1.set(ControlMode.PercentOutput, armConstant * -systems.getOperatorLJoystick());
				collectorArm2.set(ControlMode.PercentOutput, armConstant * -systems.getOperatorLJoystick());
			}
			if(broke) {
				systems.setRumbleOperator(0.2, false);
			}
		}

		if (counter % 2000 == 0) {
			// this.toSmartDashboard();
		}
		counter++;
	}
	
	/**
	 * resetNewEncoder<p>
	 * Author: Jeremiah Hanson<p>
	 * Resets the start position for the new encoders
	 */
	public void resetNewEncoder() {
		startPos = Math.abs(collectorArm2.getSelectedSensorPosition(0));
	}

	/*
	 * moveArm 
	 * Author: Finlay Parsons 
	 * ------------------------- 
	 * Purpose: Moves
	 * the arm to specified angle - all the way back is 0, all the way down is
	 * 135 Parameters: angle: Desired angle of arm
	 */
	public void moveArm(double angle) {

		goodArmPID.setSetPoint(angle);

	}
	
	/*
	 *toggle
	 *Author: Ethan Yes
	 *Collaborators: Nitesh Puri
	 *-------------------------------------------------------------
	 *Purpose: Piston toggling for scaling
	 *Parameters: state && back 
	 */
	public void toggle(boolean state, int time) {
		Runnable toggle = () ->{
			try {
				Thread.sleep(time);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			punch.set(state);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			punch.set(!state);
		};
		new Thread(toggle).start();
	}
	

	/*
	 * intakeCube 
	 * Author: Finlay Parsons 
	 * ------------------------ 
	 * Purpose: Spins
	 * the intake motors until the the cube is gained
	 */
	public void intakeCube(double speed) {
		intakeLeft.set(speed);
		intakeRight.set(speed);
		try { 
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		intakeLeft.set(0);
		intakeRight.set(0);
	}
	//TODO INTAKE AND OUTTAKE VALUES ARE SWITCHED
	public void intakeCubeAuto(double speed, double delay, double time) {
		Runnable intake = () -> {
			try {
				Thread.sleep((long) delay);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			long startTime = System.currentTimeMillis();
			while(System.currentTimeMillis() - startTime < time) {
				intakeLeft.set(speed);
				intakeRight.set(speed);
				try {
					Thread.sleep((long) 50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
			
			intakeLeft.set(0);
			intakeRight.set(0);
		};
		new Thread(intake).start();
	}
	
	/*
	 * outtakeCube
	 * Author: Ethan Yes
	 * Collaborators: Jeremiah Hanson
	 * --------------------------------------------
	 * Parameters: speed, delay, time
	 * 
	 * Purpose: Thread to outtake cube during teleop for scaling
	 */
	public void outtakeCube(double speed, double delay, double time) {
		Runnable outtake = () -> {
			try {
				Thread.sleep((long) delay);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			long startTime = System.currentTimeMillis();
			while(System.currentTimeMillis() - startTime < time) {
				intakeLeft.set(-speed);
				intakeRight.set(-speed);
				try {
					Thread.sleep((long) 50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
			
			intakeLeft.set(0);
			intakeRight.set(0);
		};
		new Thread(outtake).start();
	}

	/*
	 * outtakeCubeAuto 
	 * Author: Nitesh Puri 
	 * Collaborators: Ethan Ngo and Finlay Parsons 
	 * -------------------------------------------------- 
	 * Parameters:
	 * 	None 
	 * Purpose: Outtakes the cube in auto
	 */
	public void outtakeCubeAuto(double speed, boolean forward) {
		long startTime = System.currentTimeMillis();
		while (System.currentTimeMillis() - startTime < 500) {
			intakeLeft.set(-speed);
			intakeRight.set(-speed);
			Systems.getDriveTrain().drive(forward ? 0.5 : -0.5, 0);
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		Systems.getDriveTrain().drive(0, 0);
		intakeLeft.set(0);
		intakeRight.set(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see systems.Subsystem#toSmartDashboard()
	 */
	@Override
	public void toSmartDashboard() {
		// TODO Auto-generated method stub
		/*
		 * SmartDashboard.putString("DB/String 4", "Encoder1: " +
		 * df.format(systems.getEncoderDistance(SysObj.Sensors.ARM_ENCODER_1)));
		 * SmartDashboard.putString("DB/String 3", "Encoder2: " +
		 * df.format(systems.getEncoderDistance(SysObj.Sensors.ARM_ENCODER_2)));
		 * SmartDashboard.putString("DB/String 5", "Distance: " +
		 * df.format(averageArmEncoderPos));
		 */
		// SmartDashboard.putString("DB/Slider 4", "I hate this.");

	}

	public void enable() {

		if (systems == null) {
			systems = Systems.getInstance();
			armEncoder1 = Systems.getRobotEncoder(SysObj.Sensors.ARM_ENCODER_1);
			armEncoder2 = Systems.getRobotEncoder(SysObj.Sensors.ARM_ENCODER_2);
			armEncoder1.setDistancePerPulse(0.42);
			armEncoder2.setDistancePerPulse(0.42);
			//startPos = Math.abs(collectorArm2.getSelectedSensorPosition(0));

		//	 System.out.println("Collector.update(): " +
			// systems.getEncoderDistance(SysObj.Sensors.ARM_ENCODER_2));
		}

		updateLoop.start();
	}

}
