/*
 * Resources
 * Author: Finlay Parsons
 * -------------
 * Class for commonly used functions 
 */
package systems;

public class Resources {
	
	/*
	 * getAngleError
	 * Author: Finlay Parsons
	 * -------------------------
	 * Purpose: Returns the difference between two angles
	 * Parameters:
	 * 	dAngle - The desired angle
	 * 	cAngle - The current angle
	 * Returns: A doubleS
	 */
	
	public static double getAngleError(double dAngle, double cAngle){

		dAngle = ((dAngle % 360) + 360) % 360;
		cAngle = ((cAngle % 360) + 360) % 360;
		double currentError = dAngle- cAngle;
        if(Math.abs(currentError) > (360 - 0)/2){
            currentError  = currentError>0? currentError-360+0 : currentError+360-0;
        }
        return currentError;
	}
	
	/*
	 * returnGreater
	 * Author: Finlay Parsons
	 * ------------------------
	 * Takes in two doubles and returns the greater
	 */
	public static double returnGreater(double a, double b){
		if(a>b) return a;
		else return b;
	}
	
	/*
	 * returnLesser
	 * Author: Finlay Parsons
	 * -----------------------
	 * Takes in two doubles and returns the lesser
	 */
	public static double returnLesser(double a, double b){
		if(a<b) return a;
		else return b;
	}
	
	/*
	 * roundDouble
	 * Author: Finlay Parsons
	 * ------------------------
	 * Rounds a given double to given power of 10
	 * Parameters:
	 * 	n: Double to round
	 * 	p: Rounds to the nearest 10^p
	 */
	public static double roundDouble(double n, double p){
		if(n!=0) {
		return n - (Math.abs((n)/n) * (n % (Math.pow(10, p))));
		}
		else {
			return 0;
		}
	}
	
	/*
	 * limit
	 * Author: Finlay Parsons
	 * Collaborators: Ruben Castro, 520-461-0733
	 * --------------------------------------
	 * Limits a value between values
	 */
	public static double limit(double numberIn, double lower, double upper){
		return Math.min(Math.max(lower, numberIn), upper);
	}
	
	/*
	 * mod
	 * Author: Ethan Yes
	 * Collaborator: Ruben Castro
	 * -----------------------------------
	 * Mod for angles and circles
	 */
	public static double mod(double input, double modVal) {
		return ((input % modVal) + modVal) % modVal;
	}
}
