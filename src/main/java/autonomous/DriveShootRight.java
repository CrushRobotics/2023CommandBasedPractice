package autonomous;

import systems.Systems;
import systems.subsystems.Collector;
import systems.subsystems.DriveTrain;

public class DriveShootRight implements Runnable{
	private DriveTrain driveTrain;
	private Collector collector;
	
	public DriveShootRight(){
		
		driveTrain = Systems.getDriveTrain();
		collector = Systems.getCollector();
}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		

		driveTrain.driveLineDontStop(-100, 30, 140, false); //drive for 110 inches to switch
		
		collector.outtakeCubeAuto(0.5, false); //outtake	
		
	
	}
}
