/*
 * Class: Systems
 * Author: Jeremiah Hanson
 * ----------------------------------------------------------------------------
 * Purpose: The purpose of this class is to create a hashmap that stores all of
 * objects that represent the various systems and sysObjects on the robot, such
 * as solenoids and speed-controllers 
 * 
 * Responsibilities: Responsible for maintaining the systems so that no class 
 * should have to create their own system objects but instead ask this class 
 * for those objects.
 */

package systems;

import java.util.HashMap;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Solenoid;
import systems.subsystems.Collector;
import systems.subsystems.Controls;
import systems.subsystems.Controls.Axis;
import systems.subsystems.Controls.Button;
import systems.subsystems.Controls.POV;
import systems.subsystems.DriveTrain;
import systems.subsystems.NavX;
import systems.subsystems.RobotEncoder;

public class Systems{
	
	private static Systems systems;
	private HashMap<SysObj, Object> sysObjects;
	
	private static Controls controls;
	private static DriveTrain driveTrain;
	private static Collector collector;
	private static NavX navX;
	private static RobotEncoder lEncoder, rEncoder, armEncoder1, armEncoder2;
	private static PowerDistribution pdp;
	
	public boolean inAuto;
	
	/*
	 * Constructor
	 * Author: Jeremiah Hanson
	 * -------------------------------------------------
	 * Singlton constructor for the systems
	 */
	private Systems(){

		sysObjects = new HashMap<SysObj, Object>();
		
		System.out.println("Start building bot");
		
//=======================================================================================
// Motor Controllers
//=======================================================================================

		System.out.println("Start with motors");
		
		// Left Motor Controllers
		sysObjects.put(SysObj.MotorController.LEFT_1, new TalonSRX(2));
		sysObjects.put(SysObj.MotorController.LEFT_2, new TalonSRX(4));
		sysObjects.put(SysObj.MotorController.LEFT_3, new TalonSRX(6));

		
		// Right Motor Controllers
		sysObjects.put(SysObj.MotorController.RIGHT_1, new TalonSRX(1));
		sysObjects.put(SysObj.MotorController.RIGHT_2, new TalonSRX(3));
		sysObjects.put(SysObj.MotorController.RIGHT_3, new TalonSRX(5));
		
		// Create the driveTrain
		driveTrain = new DriveTrain((TalonSRX) sysObjects.get(SysObj.MotorController.LEFT_1), 
									(TalonSRX) sysObjects.get(SysObj.MotorController.LEFT_2), 
									(TalonSRX) sysObjects.get(SysObj.MotorController.LEFT_3), 
									(TalonSRX) sysObjects.get(SysObj.MotorController.RIGHT_1), 
									(TalonSRX) sysObjects.get(SysObj.MotorController.RIGHT_2), 
									(TalonSRX) sysObjects.get(SysObj.MotorController.RIGHT_3));
		
		// Arm Motor Controllers
		sysObjects.put(SysObj.MotorController.COLLECTOR_ARM_1, new TalonSRX(7)); // right
		sysObjects.put(SysObj.MotorController.COLLECTOR_ARM_2, new TalonSRX(8)); // left
		sysObjects.put(SysObj.MotorController.INTAKE_LEFT, new WPI_VictorSPX(10));
		sysObjects.put(SysObj.MotorController.INTAKE_RIGHT, new WPI_VictorSPX(9));
		
		
		// Climber Motor Controller(s)
		sysObjects.put(SysObj.MotorController.CLIMBER, new edu.wpi.first.wpilibj.motorcontrol.Spark(11));
		//Solenoid Controller
		sysObjects.put(SysObj.Solenoid.SOLENOID, new Solenoid(PneumaticsModuleType.CTREPCM, 1));
 
//=======================================================================================
// Sensors
//=======================================================================================

		System.out.println("Next, encoders");
		
		// Encoders
		sysObjects.put(SysObj.Sensors.CLIMB_ENCODER, new Encoder(8,9));
		sysObjects.put(SysObj.Sensors.ARM_ENCODER_1, new Encoder(0,1));
		sysObjects.put(SysObj.Sensors.ARM_ENCODER_2, new Encoder(2,3));
		sysObjects.put(SysObj.Sensors.LEFT_ENCODER, new Encoder(4,5, true, Encoder.EncodingType.k4X));
		sysObjects.put(SysObj.Sensors.RIGHT_ENCODER, new Encoder(6,7, true, Encoder.EncodingType.k4X));
		//((Encoder) sysObjects.get(SysObj.Sensors.LEFT_ENCODER)).setDistancePerPulse(Math.PI);
		lEncoder = new RobotEncoder((Encoder) sysObjects.get(SysObj.Sensors.LEFT_ENCODER));
		lEncoder.setNegative(true);
		rEncoder = new RobotEncoder((Encoder) sysObjects.get(SysObj.Sensors.RIGHT_ENCODER));
		armEncoder1 = new RobotEncoder((Encoder) sysObjects.get(SysObj.Sensors.ARM_ENCODER_1));
		armEncoder2 = new RobotEncoder((Encoder) sysObjects.get(SysObj.Sensors.ARM_ENCODER_2));          
		
		System.out.println("Don't forget controls");
		
		// Joysticks
		Joystick drive = new Joystick(0), operator = new Joystick(1);
		sysObjects.put(SysObj.Sensors.DRIVER_STICK, drive);
		sysObjects.put(SysObj.Sensors.OPERATOR_STICK, operator);
		((Joystick)sysObjects.get(SysObj.Sensors.OPERATOR_STICK)).setRumble(RumbleType.kLeftRumble, 0);
		((Joystick)sysObjects.get(SysObj.Sensors.OPERATOR_STICK)).setRumble(RumbleType.kRightRumble, 0);
		controls = new Controls(drive, operator);
		
		System.out.println("Gotta have power");
		// Other Sensors
		sysObjects.put(SysObj.Sensors.PDP, new PowerDistribution());
		pdp = new PowerDistribution();
		sysObjects.put(SysObj.Sensors.NAVX, new AHRS(SPI.Port.kMXP));
		navX = new NavX((AHRS) sysObjects.get(SysObj.Sensors.NAVX));
	
		// Create the Collector
			collector = new Collector((TalonSRX) sysObjects.get(SysObj.MotorController.COLLECTOR_ARM_1),
										  (TalonSRX) sysObjects.get(SysObj.MotorController.COLLECTOR_ARM_2),
										  (WPI_VictorSPX) sysObjects.get(SysObj.MotorController.INTAKE_LEFT), 
										  (WPI_VictorSPX) sysObjects.get(SysObj.MotorController.INTAKE_RIGHT),
										  armEncoder1,
										  armEncoder2,
										  (Solenoid) sysObjects.get(SysObj.Solenoid.SOLENOID));
										  
				
	}
	
	/*
	 * getInstance
	 * Author: Jeremiah Hanson
	 * --------------------------------------------------
	 * Purpose: gets the single instance of Systems. Creates
	 * 	Systems if one does not exist
	 * Returns: a Systems object
	 */
	public static Systems getInstance() {
		if (systems == null) {
			systems = new Systems();
		}
		return systems;
	}
	
	/*
	 * update
	 * Author: Jeremiah Hanson
	 * ------------------------------------------------
	 * Purpose: updates all the systems
	 */
	public void update() {
		controls.update();
		driveTrain.update();
		//collector.update(); on its own thread now
		navX.update();
		lEncoder.update();
		rEncoder.update();
		armEncoder1.update();
		armEncoder2.update();
		if(controls.getButton(Controls.Button.BACK, SysObj.Sensors.DRIVER_STICK)){
			navX.zeroAngler();
		}
		if(controls.getButton(Controls.Button.BACK, SysObj.Sensors.OPERATOR_STICK)){
			//System.out.println("Hi Ethan");
		}
		//lEncoder.toSmartDashboard();
		//collector.toSmartDashboard();
		
	}
	
	/*
	 * getDriverAxisLeftX
	 * Author: Jeremiah Hanson
	 * --------------------------------------------
	 * Purpose: gets the drivers current axis hashmap
	 * Returns: HashMap
	 */
	public double getDriverAxisLeftY() {
		return controls.getDriverAxis().get(Axis.LEFT_Y);
	}
	
	/*
	 * getDriverAxisRightY
	 * Author: Jeremiah Hanson
	 * --------------------------------------------
	 * Purpose: gets the drivers current axis hashmap
	 * Returns: HashMap
	 */
	public double getDriverAxisRightX() {
		return controls.getDriverAxis().get(Axis.RIGHT_X);
	}
	
	
	public void setRumbleOperator(double val, boolean both){
		controls.setRumbleOperator(val, both);
	}
	
	public void setRumbleDriver(double val){
		controls.setRumbleDriver(val);
	}
	
	/*
	 * getDriverLtTrigger
	 * Author: Nitesh Puri
	 * ---------------------------------------------
	 * Purpose: gets the driver's current trigger value
	 * Returns: Driver's Trigger Value As Double
	 */
	public double getDriverLtTrigger() {
		return controls.getDriverAxis().get(Axis.LEFT_TRIGGER);
	}
	
	/*
	 * getDriverRtTrigger
	 * Author: Nitesh Puri
	 * ---------------------------------------------
	 * Purpose: gets the driver's current trigger value
	 * Returns: Driver's Trigger Value As Double
	 */
	public double getDriverRtTrigger() {
		return controls.getDriverAxis().get(Axis.RIGHT_TRIGGER);
	}
	
	/*
	 * getOperatorRtTrigger
	 * Author: Nitesh Puri
	 * ---------------------------------------------
	 * Purpose: gets the operator's current trigger value
	 * Returns: Operator's Trigger Value As Double
	 */
	public double getOperatorRtTrigger() {
		return controls.getOperatorAxes().get(Axis.RIGHT_TRIGGER);
	}
	
	/*
	 * getOperatorLtTrigger
	 * Author: Nitesh Puri
	 * ---------------------------------------------
	 * Purpose: gets the operator's current trigger value
	 * Returns: Operator's Trigger Value As Double
	 */
	public double getOperatorLtTrigger() {
		return controls.getOperatorAxes().get(Axis.LEFT_TRIGGER);
	}
	
	/*
	 * getOperatorLJostick
	 * Author: Nitesh Puri
	 * ---------------------------------------------
	 * Purpose: gets the operator's current left joystick value
	 * Returns: Operator's Left Joystick Value As Double
	 */
	public double getOperatorLJoystick(){
		return controls.getOperatorAxes().get(Axis.LEFT_Y);
	}
	
	/*
	 * getButton
	 * Author: Finlay Parsons
	 * ------------------------
	 * Purpose: Looks if the button is being pressed
	 */
	public boolean getButton(Button button, boolean driver) {
		if(driver) {
			return controls.getButton(button, SysObj.Sensors.DRIVER_STICK);
		}
		else {
			return controls.getButton(button, SysObj.Sensors.OPERATOR_STICK);
		}
	}
	
	/*
	 * getDPadButton
	 * Author: Ethan Yes
	 * Collaborator: Nitesh Puri
	 * ------------------------
	 * Purpose: Looks if the POV is being pressed
	 */
	public boolean getDPadButton(POV pov, boolean driver) {
		if(driver) {
			return controls.getDPadButton(pov, SysObj.Sensors.DRIVER_STICK);
		}
		else {
			return controls.getDPadButton(pov, SysObj.Sensors.OPERATOR_STICK);
		}
	}
	
	/*
	 * getDriveTrain
	 * Author: Finlay Parsons
	 * Collaborators: Jeremiah Hanson
	 * ---------------------------------
	 * Purpose: Returns Systems instance of DriveTrain
	 * Returns: DriveTrain
	 */
	public static DriveTrain getDriveTrain(){
		return driveTrain;
	}
	
	/*
	 * getCollector
	 * Author Finlay Parsons
	 * Collaborators: Jeremiah Hanson
	 * ----------------------------------
	 * Purpose: Returns Systems instance of Collector
	 * Returns: Collector
	 */
	public static Collector getCollector(){
		return collector;
	}
	
	/*
	 * getNavX
	 * Author Finlay Parsons
	 * Collaborators: Jeremiah Hanson
	 * ----------------------------------
	 * Purpose: Returns Systems instance of NavX
	 * Returns: NavX
	 */
	public static NavX getNavX(){
		return navX;
	}
	
	/*
	 * getRobotEncoder
	 * Author Finlay Parsons
	 * Collaborators: Jeremiah Hanson
	 * ----------------------------------
	 * Purpose: Returns Systems instance of RobotEncoder
	 * Returns: RobotEncoder
	 */
	public static RobotEncoder getRobotEncoder(SysObj.Sensors sensor){
		switch(sensor){
		case LEFT_ENCODER:
			return lEncoder;
		case RIGHT_ENCODER:
			return rEncoder;
		case ARM_ENCODER_1:
			return armEncoder1;
		case ARM_ENCODER_2:
			return armEncoder2;
		default:
			return null;
		
		}
	}
	
	/*
	 * resetEncoders
	 * Author: Finlay Parsons
	 * Collaborators: Jeremiah Hanson, Ethan Ngo, Nitesh Puri
	 * --------------------------------------------------------
	 * Purpose: Resets the value of the encoders before to make the starting
	 * position equal to 0
	 */
	public void resetEncoders(){
		lEncoder.reset();
		rEncoder.reset();
		armEncoder1.reset();
		armEncoder2.reset();
	}
	
	/*
	 * getPulse
	 * Author: Nitesh Puri
	 * Contributors: Jeremiah Hanson, Finlay Parsons
	 * -----------------------------------------------
	 * Purpose: Gets the pulse
	 * Return: Returns a double
	 */
	//public double getPulse(){
		//return lEncoder.getPulse();
	//}
	
	/*
	 * printEncoderInfo
	 * Author: Finlay Parsons
	 * Contrubitors: Jeremiah Hanson
	 * -------------------------------------
	 * Purpose: prints encoder info to system.out
	 * Parameters:db
	 * 	a: print distance
	 * 	b: print rate
	 *  c: print pulse
	 * returns nothing
	 */
	public void printEncoderInfo(boolean distance, boolean rate, boolean pulse, SysObj.Sensors sensor){
		
		switch(sensor){
			case LEFT_ENCODER: 
				if(distance) System.out.print("Left Distance: "+ lEncoder.getValue());
				if(rate) System.out.print("    Left Rate: " + lEncoder.getRate());
				if(pulse) System.out.print("    Left Pulse: " + lEncoder.getPulse());
				break;
			case RIGHT_ENCODER:
				if(distance) System.out.print("Right Distance: "+ rEncoder.getValue());
				if(rate) System.out.print("    Right Rate: " + rEncoder.getRate());
				if(pulse) System.out.print("    Right Pulse: " + rEncoder.getPulse());
				break;
			case ARM_ENCODER_1:
				if(distance) System.out.print("Arm 1 Distance: "+ armEncoder1.getValue());
				if(rate) System.out.print("    Arm 1 Rate: " + armEncoder1.getRate());
				if(pulse) System.out.print("    Arm 1 Pulse: " + armEncoder1.getPulse());
				break;
			case ARM_ENCODER_2:
				if(distance) System.out.print("Arm 2 Distance: "+ armEncoder2.getValue());
				if(rate) System.out.print("    Arm 2 Rate: " + armEncoder2.getRate());
				if(pulse) System.out.print("    Arm 2 Pulse: " + armEncoder2.getPulse());
				break;
			default:
				System.out.println("ERROR: no encoder selected!");
				break;
		}
		
		
		System.out.println();
	
	}
	
	/*
	 * getNavXAngle
	 * Author: Finlay Parsons
	 * Collaborators: Nitesh Puri, Jeremiah Hanson
	 * ----------------------------------------------
	 * Purpose: Gets the current angle of the robot
	 * Returns: Double
	 */
	public double getNavXAngle(){
		return navX.getCurrentAngle();
	}
	
	/*
	 * resetNavXAngle
	 * Author: Finlay Parsons
	 * Collaborators: Nitesh Puri, Jeremiah Hanson, Ethan Yes
	 * -------------------------------------------------------
	 * Purpose: Sets the current navX angle to 0
	 * Returns: nothing
	 */
	public void resetNavXAngle(){
		navX.zeroAngler();
	}
	
	/*
	 * getNavXDriveAngle
	 * Author: Finlay Parsons
	 * Collaborators: Nitesh Puri, Jeremiah Hanson, Ethan Yes
	 * -------------------------------------------------------
	 * Purpose: Gets the current angle that the robot is pointing
	 * Returns: A double in between -1 and 1. 0 is forward, positive is right.
	 */
	public double getNavXDriveAngle(){
		return navX.getDriveAngle();
	}
	
	/*
	 * getEncoderDistance
	 * Author: Finlay Parsons
	 * Collaborators: Nitesh Puri, Jeremiah Hanson, Ethan Yes
	 * -------------------------------------------------------
	 * Purpose: Gets the current distance travelled by the encoders.
	 * Parameters:
	 * 	Encoders
	 * Returns a double
	 */
	public double getEncoderDistance(SysObj.Sensors encoder){
		switch(encoder){
		case LEFT_ENCODER:
			return lEncoder.getValue();
		case RIGHT_ENCODER:
			return rEncoder.getValue();
		case ARM_ENCODER_1:
			return armEncoder1.getValue();
		case ARM_ENCODER_2:
			return armEncoder2.getValue();
		default:
			return 0;
		}
	}
	
	/*
	 * setDistancePerPulse
	 * Author: Finlay Parsons
	 * ----------------------
	 * Sets distance per pulse of a specified encoder
	 */
	public void setDistancePerPulse(RobotEncoder encoder, double x){
		encoder.setDistancePerPulse(x);
	} 
	
	/*
	 * instantiate
	 * Author: Finlay Parsons
	 * Collaborators: Nitesh Puri, Ethan Yes, Jeremiah Hanson
	 * -------------------------------------------------------
	 * Purpose: Gives an instance of systems to every class in here
	 * Returns: nothing
	 */
	public void instantiate(){
		driveTrain.getSystems();
	}
	
	
	/*
	 * resetAutoSystems
	 * Author: Finlay Parsons
	 * Collaborators: Nitesh Puri, Argeo Leyva 
	 * ------------------------------------------------------
	 * Purpose: Reset all of the subsystems auto uses
	 * Returns nothing
	 */
	public void resetAutoSystems(){
		resetNavXAngle();
		resetEncoders();
	}
	
	/*
	 * getMotorCurrent
	 * Author: Nitesh Puri
	 * ------------------------------------------------------
	 * Purpose: Gets the motor current in amps
	 * Returns: Double
	 */
	public double getMotorCurrent(int channel){
			return pdp.getCurrent(channel);
	}
	
	/*
	 * getAverageDriveEncoderDistance
	 * Author: Finlay Parsons
	 * Collaborators: Ethan Yes, Nitesh Puri
	 * -------------------------------------------
	 * Purpose: getAverageDriveEncoderDistance with failsafe if one goes wrong
	 */
	public double getAverageDriveEncoderDistance(){
		/*if(Math.abs(lEncoder.getValue() - rEncoder.getValue()) <
		0.1 * resources.returnGreater(lEncoder.getValue(), rEncoder.getValue())){
			return 0.5 * (lEncoder.getValue() + rEncoder.getValue());
		}
		else{
			return resources.returnGreater(lEncoder.getValue(), rEncoder.getValue());
		}
		*/
	//	return (rEncoder.getValue() + lEncoder.getValue())/2.0;	
		return rEncoder.getValue();
	}
	
	/*
	 * intake
	 * Author: Finlay Parsons
	 * Collaborators: Nitesh Puri, Ethan Yes
	 * ---------------------------------------
	 * Purpose: Set intake motors
	 */
	public void intake(double speed){
		collector.intakeCube(speed);
	}
	
	/*
	 * eject
	 * Author: Finlay Parsons
	 * ------------------------
	 * Purpose: Shoots out the cube
	 */
	public void eject(double speed, boolean forward){
		
			collector.outtakeCubeAuto(speed, forward);
		
	}
}

