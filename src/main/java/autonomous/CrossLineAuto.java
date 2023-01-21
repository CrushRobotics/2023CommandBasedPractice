package autonomous;

import edu.wpi.first.wpilibj.DriverStation;
import systems.Systems;
import systems.subsystems.DriveTrain;

public class CrossLineAuto implements Runnable{
	
	private DriveTrain driveTrain;
	
	public CrossLineAuto() {
		driveTrain = Systems.getInstance().getDriveTrain();
	}
	
	@Override
	public void run() {
		long startTime = System.currentTimeMillis();
		while(System.currentTimeMillis() - startTime < 2000 && DriverStation.isAutonomous()) {
			driveTrain.drive(-0.6, 0);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		driveTrain.drive(0,0);
		
	}

}
