package autonomous;

import systems.Systems;
import systems.subsystems.Collector;

public class MoveArmTest implements Runnable{
	private Systems systems;
	private Collector collector;
	
	public MoveArmTest() {
		systems = Systems.getInstance();
		collector = systems.getCollector();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		collector.moveArm(75);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		collector.moveArm(104);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		collector.moveArm(10);
	}

}
