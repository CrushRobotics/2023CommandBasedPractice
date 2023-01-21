package autonomous;

import systems.Systems;
import systems.subsystems.Collector;
import systems.subsystems.DriveTrain;

public class FourCubeLeftCustom implements Runnable {
	private DriveTrain driveTrain;
	private Collector collector;

	public FourCubeLeftCustom() {

		driveTrain = Systems.getDriveTrain();
		collector = Systems.getCollector();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		collector.moveArm(75); // move arm to switch angle

		driveTrain.driveLineDontStop(105, -26, 140, true); // drive for 130 inches to switch

		collector.outtakeCubeAuto(0.5, true); // outtake

		driveTrain.driveCircle(60, 65, false, false, 1); //back up while turning towards cubes

		collector.moveArm(104); // move arm down to collecting position 131 if broke

		collector.intakeCubeAuto(1.0, 200, 2000); // intake thread starts

		driveTrain.driveLine(75, 65, 140); // drive towards the second cube while intaking

		collector.moveArm(10); //move arm up

		driveTrain.driveCircle(7, 180, false, false, 3); //back up while turning into fence
		
		driveTrain.driveAuton(-0.5, 300);

		collector.outtakeCubeAuto(0.6, false); //outtake

		collector.moveArm(104); //move arm down 131 if broke

		collector.intakeCubeAuto(1.0, 200, 2500); //intake thread starts

		driveTrain.driveCircle(3, 40, true, false, 6); //drive while turning into third cube

		collector.moveArm(10); //move arm up

		driveTrain.driveCircle(5, 180, false, false, 3); //back up while turning into fence
		
		driveTrain.driveAuton(-0.5, 300);

		collector.outtakeCubeAuto(0.5, false); //outtake

		collector.moveArm(104); //move arm down 131 if broke

		collector.intakeCubeAuto(1.0, 200, 3000); //intake thread starts

		driveTrain.driveCircle(1, 30, true, false, 4); //drive forward while turning toward fourth cube
		
		driveTrain.driveAuton(0.4, 600); //drive into fourth cube
		
		collector.moveArm(10); //move arm up

		driveTrain.driveCircle(5, 180, false, false, 3); //back up while turning 

		collector.outtakeCubeAuto(0.6, false); // intake thread starts

	}
}
