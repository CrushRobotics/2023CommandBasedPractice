// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.sql.Time;
import java.time.LocalDateTime;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import autonomous.CrossLineAuto;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.cscore.VideoMode.PixelFormat;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import systems.Systems;
import systems.subsystems.Collector;
import systems.subsystems.DriveTrain;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();
	private static UsbCamera visionCam;
  
	private static Systems systems;
	private static Collector collector;
	private static DriveTrain driveTrain;
	private static Thread auton;
	private static CameraServer camServer;
	private static final int MJPG_STREAM_PORT = 115200;
	private long startTime;

  private TalonSRX leftController1;
  private TalonSRX leftController2;
  private TalonSRX leftController3;

  private TalonSRX rightController1;
  private TalonSRX rightController2;
  private TalonSRX rightController3;

  private Joystick stick;

  XboxController exampleXbox;

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

  
    /* 
		systems = Systems.getInstance();
		collector = Systems.getCollector();
		driveTrain = Systems.getDriveTrain();
		collector.enable();
		System.out.println("Okay got here...");
		systems.resetEncoders();
		systems.update();
    */
		
		SmartDashboard.putString("DB/String 0", "");
		SmartDashboard.putString("DB/String 1", "");
		SmartDashboard.putString("DB/String 2", "");
		SmartDashboard.putString("DB/String 3", "");
		SmartDashboard.putString("DB/String 4", "");
		SmartDashboard.putString("DB/String 5", "");

		SmartDashboard.putString("DB/String 6", "");
		SmartDashboard.putString("DB/String 7", "");
		SmartDashboard.putString("DB/String 8", "");
		SmartDashboard.putString("DB/String 9", "");
		SmartDashboard.putData("Auto choices", m_chooser);
		System.out.println("Initializing robot!");

    exampleXbox = new XboxController(0); // 0 is the USB Port to be used as indicated on the Driver Station




		/* 
		visionCam = new UsbCamera("cam0", 1);
		visionCam.setVideoMode(PixelFormat.kYUYV, 320, 240, 15);  // start ObjectDetect	
		visionCam.setResolution(320, 240);
		visionCam.setFPS(15);
		visionCam.setBrightness(900);
		visionCam.setExposureAuto();
    CameraServer.addCamera(visionCam);
    CameraServer.startAutomaticCapture();
*/


    //
    // The TalonSRX motor controller documentation can be found here:
    // https://store.ctr-electronics.com/content/api/java/html/classcom_1_1ctre_1_1phoenix_1_1motorcontrol_1_1can_1_1_talon_s_r_x.html
    //
    
    // Set up motor controllers. The motor controllers are the 'brains' behind 
    // the motors themselves. They listen to our commands and tell the motors
    // to operate at higher or lower speeds and in different directions.

    // There are 3 motors on the left side of the robot's drive train and 
    // 3 motors on its right. Each motor has a motor controller responsible
    // for its function. That gives us 6 total motor controllers. 

    // When we construct the motor controllers we have to pass the CAN bus ID
    // that we've programmed the motor to attach to. This is a unique identifier
    // used only by that motor controller.
    leftController1 = new TalonSRX(2);
    leftController2 = new TalonSRX(4);
    leftController3 = new TalonSRX(6);

    rightController1 = new TalonSRX(1);
    rightController2 = new TalonSRX(3);
    rightController3 = new TalonSRX(5);

    // The 3 motors on the left side must work in concert with one another. That
    // is, they must operate at the same speeds because their gears are interlocked.
    // The same holds true for the 3 motors on the right side of the robot. 
    // We can get the motors on the left and right sides to operate at the same speeds
    // by instructing two of the motor controllers to follow the other one.

    // This tells 2 of the left motor controllers to follow the first left motor controller. 
    leftController2.follow(leftController1);
    leftController3.follow(leftController1);

    // This tells 2 of the right motor controllers to follow the first right motor controller.
    rightController2.follow(rightController1);
    rightController3.follow(rightController1);

    // Set netural modes to brake so that robot doesn't move when joysticks 
    // are at 0 position.
    leftController1.setNeutralMode(NeutralMode.Brake);
    leftController2.setNeutralMode(NeutralMode.Brake);
    leftController3.setNeutralMode(NeutralMode.Brake);
    rightController1.setNeutralMode(NeutralMode.Brake);
    rightController2.setNeutralMode(NeutralMode.Brake);
    rightController3.setNeutralMode(NeutralMode.Brake);
  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
	CommandScheduler.getInstance().run();
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    /*
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
    */

    
		systems.inAuto = true;
		systems.resetAutoSystems();
		
		m_autoSelected = m_chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		System.out.println("Auto selected: " + m_autoSelected);
		
		systems.instantiate();
		systems.update();

		startTime = System.currentTimeMillis();
		
		
		//systems.getDriveTrain().turnTo(90, 0.95, 5500);
		//new AutonLine(systems.getDriveTrain(), systems.getNavX(), 60,140, 0).run();
		//new FourCubeRightAuto().run();
		//driveTrain.turnToOneSide(195, 0.8, 800, true);
		//driveTrain.driveLine(30, 0, 140);
		//driveTrain.driveAuton(-0.5, 300);
		//new AutonCircle(systems.getDriveTrain(), systems.getNavX(), 200, 3, true, false).run();
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    long currentTime = System.currentTimeMillis();

    if (currentTime - startTime < 1000)
    {
      driveTrain.arcadeDrive(0.5, 0, true);

    } 
    else 
    {
      driveTrain.arcadeDrive(0, 0);
    }
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {
	System.out.println("Starting teleoperation...");
	System.out.println("This is just a test!");
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {

    /*
		systems.inAuto = false;
		systems.update();
    */
    
		//systems.printEncoderInfo(true, false, false, SysObj.Sensors.ARM_ENCODER_1);
		//systems.printEncoderInfo(true, false, false, SysObj.Sensors.ARM_ENCODER_2);
		//systems.printEncoderInfo(true, false, false, SysObj.Sensors.LEFT_ENCODER);
		//systems.printEncoderInfo(true, false, false, SysObj.Sensors.RIGHT_ENCODER);
		/*System.out.println("Left Motor: " + systems.getMotorCurrent(10));
		System.out.println("Right Motor: " + systems.getMotorCurrent(11));*/
		//System.out.println("Robot.teleopPeriodic(): Encoder1: " + (systems.getEncoderDistance(SysObj.Sensors.ARM_ENCODER_1)));
		//System.out.println("Robot.teleopPeriodic(): Encoder2: " + (systems.getEncoderDistance(SysObj.Sensors.ARM_ENCODER_2)));
		//systems.printEncoderInfo(true, false, false, SysObj.Sensors.ARM_ENCODER_1);
		//systems.printEncoderInfo(true, false, false, SysObj.Sensors.ARM_ENCODER_2);
    

    // Get joystick values
    //var leftJoystickXValue = stick.getRawAxis(0);
    //var leftJoystickYValue = stick.getRawAxis(1);

    // Set motor voltages. The ControlMode.PercentOutput tells the controller to 
    // expect a value in range of -1 to 1. 0 is stopped.  
    
    /*leftController1.set(ControlMode.PercentOutput, leftJoystickYValue);
    rightController1.set(ControlMode.PercentOutput, leftJoystickYValue);

    /* 
    if(exampleXbox.getAButton() == true)
    {
      leftController1.set(ControlMode.PercentOutput, 1);
      rightController1.set(ControlMode.PercentOutput, 1);
    }
    else if(exampleXbox.getBButton() == true)
    {
      leftController1.set(ControlMode.PercentOutput, -1);
      rightController1.set(ControlMode.PercentOutput, -1);

    }
    else
    {
      leftController1.set(ControlMode.PercentOutput, 0);
    }*/

    //sets the left wheels to the left stick Y axis and the right wheels to the right stick Y axis
    leftController1.set(controlMode.PercentOutput, getLeftY);
    rightController1.set(controlMode.PercentOutput, -getRightY);

  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {	
  }

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {}

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {
	
	systems.inAuto = false;
	systems.update();
  }
}
