/*
 * MidSideRightScore.java
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

public class MidSideRightScore implements Runnable{
	
	private Systems systems;
	private DriveTrain driveTrain;
	private Collector collector;
	
	public MidSideRightScore(){
		
		systems = Systems.getInstance();
		driveTrain = Systems.getDriveTrain();
		collector = Systems.getCollector();
	}
	

	@Override
	public void run() {
		
		driveTrain.driveDistance(96, -.7);
		
		collector.outtakeCubeAuto(0.5, false);
		
		driveTrain.driveDistance(15, 0.7);
		
		driveTrain.circleTurn(30, 180, 0.7, true, true);
		
		collector.moveArm(130);
		
		driveTrain.driveIntake(0.6, -0.5, 10);
		
		driveTrain.circleTurn(30, 0, 0.7, false, false);
		
		driveTrain.driveDistance(15, -0.7);
		
		collector.moveArm(10);
		
		collector.outtakeCubeAuto(0.5, false);
		
		driveTrain.driveDistance(10, 0.7);
		
		driveTrain.circleTurn(20, 180, 0.7, true, true);
		
		collector.moveArm(130);
		
		driveTrain.driveIntake(0.6, -0.5, 10);
		
		driveTrain.circleTurn(30, 0, 0.7, false, false);
		
		driveTrain.driveDistance(10, -0.7);
		
		collector.moveArm(10);
		
		collector.outtakeCubeAuto(0.5, false);
		
	}
	
	
}
