package autonomous;


import edu.wpi.first.wpilibj.DriverStation;
import systems.Resources;
import systems.Systems;
import systems.subsystems.DriveTrain;
import systems.subsystems.NavX;

public class AutonLine implements Runnable {

	double angle, topSpeed, distance;
	double rotateOutput;
	double loopCount, pastCount;

	final double acceleration = 300;
	final double kA = (1 / acceleration) * 0;

	DriveTrain drive;

	NavX navx;

	private static final double P = 0.02;

	public AutonLine(DriveTrain driveTrain, NavX navx, double distance, double topSpeed, double angle) {

		this.angle = angle;
		this.topSpeed = topSpeed;
		this.distance = distance;

		this.drive = driveTrain;
		this.navx = navx;
	}

	double delT = 50;

	public void run() {
		boolean stop = false;
		// 300 is accel

		int counter =0;
		boolean deAccelerate = false;
		boolean deAccelerateStarted = false;
		double initialPos = Systems.getInstance().getAverageDriveEncoderDistance();

		double distanceTravelled = 0;
		double pastDistanceTravelled = 0;

		boolean backwards = distance < 0;

		double currentVelocity = 0;
		double lastVelocity = 0;

		while (!stop && DriverStation.isAutonomous() && DriverStation.isEnabled()) {
			long startTime = System.currentTimeMillis();

			double timeToStop = Math.abs(currentVelocity / acceleration);

			double distanceNeededToStop = (Math.abs(currentVelocity)) * timeToStop;

			distanceTravelled = Math.abs(Systems.getInstance().getAverageDriveEncoderDistance() - initialPos);

			// remainingdistance
			if (Math.abs(distance) - distanceTravelled <= distanceNeededToStop) {
				deAccelerate = true;
				deAccelerateStarted=true;
			} else if ((Math.abs(distance) > distanceTravelled)) {
				//deAccelerate = false;
			}

			double currentAcceleration;
			if (deAccelerate ? !backwards : backwards) {
				currentVelocity -= (delT / 1000) * acceleration;
				currentAcceleration = -acceleration;
			} else {
				currentVelocity += (delT / 1000) * acceleration;
				currentAcceleration = acceleration;
			}
			if (Math.abs(currentVelocity) - topSpeed >= 0) {
				currentAcceleration = 0;
			}
			currentVelocity = Math.max(Math.min(topSpeed, currentVelocity), -topSpeed);
			/*System.out.println(
					"CurrentTime:" + (System.currentTimeMillis() - startTimeLine) + "    currentV:" + currentVelocity
							+ "    CURRENTP" + distanceTravelled + "    DistanceToSTOP" + distanceNeededToStop);
			System.out.println("DEACC" + deAccelerate);*/

			double currentError = Resources.getAngleError(angle, navx.getCurrentAngle());
			

			double rotateOutput = currentError * P;
			
			//System.out.println("CurrentError: " + currentError  + "   rotateOutput: " + rotateOutput + "   Angle: " + navx.getCurrentAngle());
			
			pastCount = loopCount;
			double actualVelocity = currentVelocity / 150;
			drive.drive(actualVelocity + (kA * currentAcceleration), rotateOutput, false);
			if (backwards) {
				if (currentVelocity >= 0 && lastVelocity < 0) {
					stop = true;
				}
			} else {
				if (currentVelocity <= 0 && lastVelocity > 0) {
					stop = true;
				}
			}
			if((Math.abs(distanceTravelled - pastDistanceTravelled) < 0.1 )){
				counter++;
			}
			if(counter>20){
				stop = true;
			}
			lastVelocity = currentVelocity;
			try {
				long sleepTime = ((long) delT - (System.currentTimeMillis() - startTime));
				//System.out.println("SLEEP" + sleepTime);
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		long startTime = System.currentTimeMillis();
		while (System.currentTimeMillis() - startTime < 300) {
			drive.drive(backwards ? 0.5 : -0.5, 0);
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		drive.drive(0, 0);

	}

}
