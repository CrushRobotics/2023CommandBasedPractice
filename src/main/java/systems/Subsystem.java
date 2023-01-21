/*
 * Subsystem.java
 * Author: Jeremiah Hanson
 * --------------------------------------------------------------
 * This interface is used for any subsystem of the robot. It contains
 * methods that must be implemented separately for each subsystem.
 */
package systems;

public interface Subsystem {

	/*
	 * update
	 * --------------------------------------
	 * Purpose: used to update the subsystem
	 * 	every cycle
	 */
	public void update();
	
	/*
	 * toSmartDashboard
	 * -------------------------------------
	 * Purpose: Used to send vital information 
	 * 	to the SmartDashboard
	 */
	public void toSmartDashboard();
}
