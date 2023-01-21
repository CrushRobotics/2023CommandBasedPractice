package autonomous;

import systems.Systems;
import systems.subsystems.Collector;
import systems.subsystems.DriveTrain;

public class FourCubeRightCustom implements Runnable {
	private DriveTrain driveTrain;
	private Collector collector;

	public FourCubeRightCustom() {

		driveTrain = Systems.getDriveTrain();
		collector = Systems.getCollector();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		collector.moveArm(75); // move arm to switch angle

		driveTrain.driveLineDontStop(98, 20, 140, true); // drive for 115 inches to switch

		collector.outtakeCubeAuto(0.5, true); // outtake

		//TODO FIX BECAUSE NOT GOING FAR ENOUGH AND OUT ENOUGH TO GET OUTER MOST CUBE
		
		driveTrain.driveCircle(65, 300, false, true, 3); //back up while turning toward cubes

		collector.moveArm(104); // move arm down to collecting position 131 if broke

		collector.intakeCubeAuto(1.0, 200, 2000); // intake thread starts

		driveTrain.driveLine(80, -60, 140); // drive towards the second cube while intaking

		collector.moveArm(10); //move arm up

		driveTrain.driveCircle(9, 180, false, true, 6); //back up while turning into fence

		collector.outtakeCubeAuto(0.8, false); //outtake

		collector.moveArm(104); //move arm down 131 if broke

		collector.intakeCubeAuto(1.0, 200, 2500); //intake thread starts

		driveTrain.driveCircle(4, 320, true, true, 6); //drive while turning into third cube

		collector.moveArm(10); // move arm up

		driveTrain.driveCircle(3, 180, false, true, 6); //back up while turning into fence
		
		driveTrain.driveAuton(-0.5, 300);

		collector.outtakeCubeAuto(0.5, false); //outtake

		collector.moveArm(104); //move arm down 131 if broke

		collector.intakeCubeAuto(1.0, 200, 3000); //intake thread starts

		driveTrain.driveCircle(1, 335, true, true, 6); //drive while turning into fourth cube
		
		driveTrain.driveAuton(0.4, 400); //drive into fourth cube
		
		collector.moveArm(10); // move arm up

		driveTrain.driveCircle(5, 180, false, true, 6); //back up while turning into fence

		collector.outtakeCubeAuto(0.6, false); // outtake

	}
}
