package autonomous;

import systems.Systems;
import systems.subsystems.Collector;
import systems.subsystems.DriveTrain;

public class DriveShootLeft implements Runnable{
	private DriveTrain driveTrain;
	private Collector collector;
	
	public DriveShootLeft(){
		
		driveTrain = Systems.getDriveTrain();
		collector = Systems.getCollector();
}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		

		driveTrain.driveLineDontStop(-100, -28, 140, false); //drive for 110 inches to switch
		
		collector.outtakeCubeAuto(0.5, false); //outtake	
		
		
	
	}
}
