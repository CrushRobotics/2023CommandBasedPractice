// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import autonomous.CrossLineAuto;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.cscore.VideoMode.PixelFormat;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
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

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

  
    
		systems = Systems.getInstance();
		collector = Systems.getCollector();
		driveTrain = Systems.getDriveTrain();
		collector.enable();
		System.out.println("Okay got here...");
		systems.resetEncoders();
		systems.update();
		
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
		String gameData = null;
		gameData = DriverStation.getGameSpecificMessage();
		
		int i = 3;
		while(gameData == null && i-->0) 
		{
			System.out.print("Game data was null\r\n");
			gameData = DriverStation.getGameSpecificMessage();
		}
		System.out.println("robot.autonomousInit()  Game data is " + gameData);
		
		systems.resetAutoSystems();
		
		m_autoSelected = m_chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		System.out.println("Auto selected: " + m_autoSelected);
		
		systems.instantiate();
		systems.update();
		
		/*
		while (gameData == null) {
			gameData = DriverStation.getGameSpecificMessage();
		}*/
		
		switch (m_autoSelected) {
		case (kCustomAuto):
			auton = new Thread(new CrossLineAuto());
		

		//default:
			//if (gameData.charAt(0) == 'L') 
				//auton = new Thread(new ThreeCubeLeftAuto());
				//auton = new Thread(new DriveShootLeft());
			//else 
				//auton = new Thread(new ThreeCubeRightAuto());
				//auton = new Thread(new DriveShootRight());

			//System.out.println("No autonomous selected.");
			break;
			
		}
		
		//auton = new Thread(new CrossLineAuto());
		
		//auton = new Thread(new MoveArmTest());
		
		//driveTrain.driveLine(60, 0, 140);
		
		auton.start();
		
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
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here

        break;
    }
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {
	System.out.println("Starting teleoperation...");
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
		systems.inAuto = false;
		systems.update();
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
