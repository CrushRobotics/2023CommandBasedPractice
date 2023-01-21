/*
 * Class: SysObj
 * Author: Jeremiah Hanson
 * --------------------------------------------------------
 * Purpose: This is an enum class that is made to act as an
 * easy way to handle keys for the hashmap that is used to 
 * store all of the systems and subsystem in the System 
 * class.
 */

package systems;

// This is an interface to store all the enums for all of 
// the major systems
public interface SysObj {
	
	// This is the enum for the MotorControllers
	public enum MotorController implements SysObj{
		
		LEFT_1("left1"), LEFT_2("left2"), LEFT_3("left3"),
		RIGHT_1("right1"), RIGHT_2("right2"), RIGHT_3("right3"),
		COLLECTOR_ARM_1("collectorMotor1"), COLLECTOR_ARM_2("collectorMotor2"),
		INTAKE_LEFT("intakeLeft"), INTAKE_RIGHT("intakeRight"),
		CLIMBER("climber");
		
		public String motor;
		
		// Constructor for the MotorController Enum
		private MotorController(String motor){
			this.motor = motor;
		}
	}
	
	// This is the enum for the sensors
	public enum Sensors implements SysObj{
		
		DRIVER_STICK("DriverJoystick"), OPERATOR_STICK("OpertatorJoystick"),
		NAVX("NavX"), PDP("PowerDistributionPanel"),
		ARM_ENCODER_1("ArmEncoder1"), ARM_ENCODER_2("ArmEncoder2"), CLIMB_ENCODER("ClimbEncoder"), 
		LEFT_ENCODER("LeftEncoder"), RIGHT_ENCODER("RightEncoder"),
		VISION("Vision");
		
		public String sensor;
		
		// Constructor the the Sensor Enum
		private Sensors(String sensor){
			this.sensor = sensor;
		}
	}
	
	//This is the enum for the solenoid
	public enum Solenoid implements SysObj{
		SOLENOID("solenoid");
		
		public String solenoid;
		
		// Constructor for the Solenoid Enum
		private Solenoid(String solenoid) {
			this.solenoid = solenoid;
		}
	}
}