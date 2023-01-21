/*
 * RightSideRightScore.java
 * Author: Finlay Parsons
 * Collaborators: Jeremiah Hanson
 * --------------------------------
 * The autonomous program being run when in
 * starting position 1 and trying to score on
 * the left side of the switch.
 */
package autonomous;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import systems.SysObj;
import systems.Systems;
import systems.subsystems.Collector;
import systems.subsystems.DriveTrain;
import systems.subsystems.NavX;
import systems.subsystems.RobotEncoder;

public class RightSideRightScore implements Runnable{
	
	private Systems systems;
	private DriveTrain driveTrain;
	private Collector collector;
	
	public RightSideRightScore(){
		
		systems = Systems.getInstance();
		driveTrain = Systems.getDriveTrain();
		collector = Systems.getCollector();
	}

	@Override
	public void run() {
		
		driveTrain.driveDistance(110, -0.7); //Drives backwards to the switch fence
		
		driveTrain.circleTurn(45, 90, 0.7, true, false); //Turns into the switch fence
		
	//	collector.outtakeCube(0.7); //outtake
		
		driveTrain.circleTurn(40, 270, 0.6, false, true); //Turns out of the switch fence finishing forwards
		
		driveTrain.circleTurn(40, 0, 0.6, false, true); //Turn to fence cubes
		
		collector.moveArm(135); //move collector down
		
		driveTrain.driveDistance(40, 0.6); //Drives towards the fence cubes
		
		driveTrain.driveDistance(10, 0.4); //Drive slowly towards cube
		
	//	collector.intakeCube(0.7); //intake
		
		driveTrain.driveDistance(10, -0.6); //back up a little
		
		collector.moveArm(75); //move collector to switch angle
		
		collector.outtakeCubeAuto(0.7, true); //outtake
	}
	
	
}
