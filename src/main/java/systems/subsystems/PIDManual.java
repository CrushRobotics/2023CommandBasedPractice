/*
 * PIDManual
 * Author: Finlay Parsons
 * ------------------------
 * This class is an attempt to manually control the PID outputs and inputs.
 * Delete it if it sucks.
 */
package systems.subsystems;

import systems.Resources;
import systems.Subsystem;
import systems.Systems;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class PIDManual implements Subsystem{
	
	private Systems systems;
	
	private double integral, derivative, prevError, error, dValue, cValue;
	private double output;
	private double refreshTime;
	private double p;
	private double i;
	private double d;
	
	
	private int counter;
	private boolean angle;
	
	
	public PIDManual(double p, double i, double d, double dt){
		this.dValue = 0;
		this.p = p;
		this.i = i;
		this.d = d;
		refreshTime = dt;
		
		error = 0;
		counter = 0;
		prevError = 0;
		angle = false;
		
	}
	
	/*
	 * setPID
	 * Author: Finlay Parsons
	 * -------------------------
	 * Purpose: Sets the values of P, I, and D for whatever you're doing.
	 * Parameters:
	 * 	dP: Desired P
	 * 	dI: Desired I
	 * 	dD: Desired D
	 * Returns void
	 */
	public void setPID(double dP, double dI, double dD){
		p = dP;
		i = dI;
		d = dD;
	}
	
	/*
	 * setCValue
	 * Author: Finlay Parsons
	 * --------------------------
	 * Purpose: Sets the current value
	 */
	public void setCValue(double value){
		this.cValue = value;
	}
	
	/*
	 * setdValue
	 * Author: Finlay Parsons
	 * --------------------------
	 * Purpose: Sets the value to approach
	 */
	public void setDValue(double value){
		this.dValue = value;
	}
	
	/*
	 * update
	 * Author: Finlay Parsons
	 * ------------------------
	 * Purpose: Updates all of the information needed to make the output calculations
	 * Note- Needs to be constantly updated while driving
	 */
	public void update(){
		if (systems == null){
			systems = Systems.getInstance();
		}
			
		if(angle) {
		error = Resources.getAngleError(dValue, cValue);
		}
		else {
			error = cValue - dValue;
		}
		integral += error * refreshTime;
		derivative = (error - prevError) / refreshTime;
		output = p * error + i * integral + d * derivative;
		
		prevError = error;
	
		if (counter % 2000 == 0) {
			this.toSmartDashboard();
		}
		counter++;
	}
	
	/*
	 * getOutput
	 * Author: Finlay Parsons
	 * ------------------------
	 * Purpose: Outputs the value that PID is telling you to give
	 * Returns: A double
	 */
	public double getOutput(){		
		update();
		return output;
	}
	
	/*
	 * getDValue
	 * Author: Finny
	 * ------------ 
	 */
	public double getDValue(){
		return dValue;
	}
	
	/*
	 * setAngle
	 * Author: Finlay Parsons
	 * Collaborators: Ethan Yes
	 * ----------------------------
	 * Purpose: Changes it to be working in degrees
	 */
	public void setAngle(boolean angle) {
		this.angle = angle;
	}

	@Override
	public void toSmartDashboard() {
		// TODO Auto-generated method stub
		//SmartDashboard.putString("DB/String 1", "Error: " + resources.roundDouble(error, -1));
		//SmartDashboard.putString("DB/String 9", "PIDOutput:" + resources.roundDouble(output, -1));
		//SmartDashboard.putString("DB/String 2", "Derivative:" + derivative);
		//SmartDashboard.putString("DB/String 3", "Integral:" + integral);
		//SmartDashboard.putString("DB/String 4", "Angle:" + resources.roundDouble(systems.getNavXAngle(), -1));
		//SmartDashboard.putString("DB/String 5", "Counter:" + counter);
		SmartDashboard.putString("DB/String 6", "P:" + p);
		SmartDashboard.putString("DB/String 7", "I:" + i);
		SmartDashboard.putString("DB/String 8", "D:" + d);

		
	}
	
	public double getSetpoint(){
		return dValue;
	}
	
}
