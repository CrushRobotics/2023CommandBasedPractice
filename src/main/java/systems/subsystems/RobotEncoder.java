package systems.subsystems;
/*
 * Class Name: Encoder.java
 * Author: Ethan Ngp
 * -----------------------------------------------------------
 * Purpose: Class for getting and setting values for encoder
 */


import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import systems.Subsystem;

public class RobotEncoder implements Subsystem{
	private Encoder e;
	private double startPos;
	private boolean negative;
	private double currentPos;
	
	/*
	 * Constructor
	 * Author: Ethan Yes
	 * ------------------------------------------------
	 * Constructor
	 * Parameters: Encoder
	 */
	public RobotEncoder(Encoder e){
		this.e=e;
		startPos=e.getDistance();
		e.setDistancePerPulse(0.05875);
		negative=false;
	}
	
	/*
	 * getValue()
	 * Author: Ethan Yes
	 * -------------------------------------
	 * Purpose: Return currentPos value
	 * returns type double
	 */
	public double getValue(){
		update();
		return currentPos;
	}
	
	public double getPulse(){
		return e.get();
	}
	
	public double getRate(){
		return e.getRate();
	}
	
	
	public void reset(){
		e.reset();
		startPos = e.getDistance();
	}
	
	public void setNegative(boolean val) {
		this.negative=val;
	}
	
	

	public void setDistancePerPulse(double x){
		e.setDistancePerPulse(x);
	}
	
	@Override
	public void update() {
		// TODO Auto-generated method stub
		if(e!=null){
			currentPos = e.getDistance()-startPos;
			currentPos *= negative? -1:1;
		}
	}

	@Override
	public void toSmartDashboard() {
		SmartDashboard.putString("DB/String 0", "Distance: " + this.getValue() );
		SmartDashboard.putString("DB/String 1", "Pulse1: " + this.getPulse());
	}

}
