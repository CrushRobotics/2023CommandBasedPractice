/*
 * Controls.java
 * Author: Ruben Castro
 * Contributor: Jeremiah Hanson
 * ------------------------------------------------------------------
 * This class is designed to more easily handle input from the controllers
 */

package systems.subsystems;

import java.util.HashMap;

import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.Joystick;
import systems.Subsystem;
import systems.SysObj;

public class Controls implements Subsystem{
	
	private HashMap<Button, Boolean> driverButtons;
	private HashMap<Button, Boolean> operatorButtons;
	private HashMap<POV, Boolean> driverDPadButtons;
	private HashMap<POV, Boolean> operatorDPadButtons;
	private HashMap<Axis, Double> driverAxis;
	private HashMap<Axis, Double> operatorAxis;
	
	/*
	 * Button
	 * Author: Jeremiah Hanson
	 * -----------------------------------------------
	 * This enum is designed to make handling the button
	 * presses easier.
	 */
	public enum Button {
		A(1),
		B(2),
		X(3),
		Y(4),
		RIGHT_BUMPER(6),
		LEFT_BUMPER(5),
		BACK(7),
		START(8),
		LEFTJOY(9),
		RIGHTJOY(10);
		
		int index;
		
		// Constructor
		private Button(int index){
			this.index=index;
		}
		
		/*
		 * getIndex
		 * Author: Jeremiah Hanson
		 * --------------------------------------------
		 * Purpose: This is a getter for the associated 
		 * 	button number
		 * Returns: an int 
		 */
		public int getIndex(){
			return index;
		}
		
	}
	
	/*
	 * POV
	 * Author: Finlay P
	 * -------------------------
	 * Purpose: Enum for the POV or DPad "buttons."
	 */
	public enum POV{
		UP(1),
		RIGHT(2),
		DOWN(3),
		LEFT(4),
		RIGHTUP(5),
		RIGHTDOWN(6),
		LEFTDOWN(7),
		LEFTUP(8);
		
		int index;
		
		//Constructor
		private POV(int index){
			this.index = index;
		}
		
		/*
		 * getDPadIndex
		 * Author: Ethan Yes
		 * Collaborator: Nitesh Puri
		 * --------------------------------------------
		 * Purpose: This is a getter for the associated 
		 * 	pov number
		 * Returns: an int 
		 */
		public int getDPadIndex(){
			return index;
		}
	}
	
	/*
	 * Axis
	 * Author: Ruben Castro
	 * -----------------------------------------------
	 * This enum is designed to make handling the Axis
	 * controls easier.
	 */
	public enum Axis{
		LEFT_X(0),
		LEFT_Y(1),
		RIGHT_X(4),
		RIGHT_Y(5),
		LEFT_TRIGGER(2),
		RIGHT_TRIGGER(3);
		
		int index;
		private Axis(int index){
			this.index=index;
		}
		
		public int getIndex(){
			return index;
		}
	}
	
	Joystick driverJoystick, operatorJoystick;
	
	/*
	 * Constructor
	 * Author: Ruben Castro
	 * Contributor(s): Jeremiah Hanson
	 * --------------------------------------------------
	 * Purpose: this created the controls class there should
	 * 	only be one.
	 */
	public Controls(Joystick driverJoystick, Joystick operatorJoystick){
		this.driverJoystick=driverJoystick;
		this.operatorJoystick=operatorJoystick;
		
		driverButtons = new HashMap<>();
		driverAxis = new HashMap<>();
		driverDPadButtons = new HashMap<>();
		operatorButtons = new HashMap<>();
		operatorAxis = new HashMap<>();
		operatorDPadButtons = new HashMap<>();
		
	}
	
	/*
	 * update
	 * Author: Ruben Castro
	 * ------------------------------------------------
	 * Purpose: updates the hashmaps for the controls
	 */
	@Override
	public void update(){
		updateButtonHash(driverButtons, driverJoystick);
		updateButtonHash(operatorButtons, operatorJoystick);
		updatePOVHash(driverDPadButtons, driverJoystick);
		updatePOVHash(operatorDPadButtons, operatorJoystick);
		updateAxisHash(driverAxis, driverJoystick);
		updateAxisHash(operatorAxis, operatorJoystick);
	}
	
	/*
	 * updateButtonHash
	 * Author: Ruben Castro
	 * --------------------------------------
	 * Purpose: updates just the button hashmaps
	 */
	private static void updateButtonHash(HashMap<Button, Boolean> hash, Joystick stick){
		hash.put(Button.A, stick.getRawButton(Button.A.getIndex()));
		hash.put(Button.B, stick.getRawButton(Button.B.getIndex()));
		hash.put(Button.X, stick.getRawButton(Button.X.getIndex()));
		hash.put(Button.Y, stick.getRawButton(Button.Y.getIndex()));
		hash.put(Button.BACK, stick.getRawButton(Button.BACK.getIndex()));
		hash.put(Button.START, stick.getRawButton(Button.START.getIndex()));
		hash.put(Button.LEFT_BUMPER, stick.getRawButton(Button.LEFT_BUMPER.getIndex()));
		hash.put(Button.RIGHT_BUMPER, stick.getRawButton(Button.RIGHT_BUMPER.getIndex()));
		hash.put(Button.LEFTJOY, stick.getRawButton(Button.LEFTJOY.getIndex()));
		hash.put(Button.RIGHTJOY, stick.getRawButton(Button.RIGHTJOY.getIndex()));
	}
	
	/*
	 * updatePOVHash
	 * Author: Ethan Yes
	 * Collaborator: Nitesh Puri
	 * --------------------------------------
	 * Purpose: updates just the pov hashmaps
	 */
	private static void updatePOVHash(HashMap<POV, Boolean> hash, Joystick stick){
		hash.put(POV.RIGHT, stick.getRawButton(POV.RIGHT.getDPadIndex()));
		hash.put(POV.RIGHTUP, stick.getRawButton(POV.RIGHTUP.getDPadIndex()));
		hash.put(POV.RIGHTDOWN, stick.getRawButton(POV.RIGHTDOWN.getDPadIndex()));
		hash.put(POV.LEFT, stick.getRawButton(POV.LEFT.getDPadIndex()));
		hash.put(POV.LEFTUP, stick.getRawButton(POV.LEFTUP.getDPadIndex()));
		hash.put(POV.LEFTDOWN, stick.getRawButton(POV.LEFTDOWN.getDPadIndex()));
		hash.put(POV.UP, stick.getRawButton(POV.UP.getDPadIndex()));
		hash.put(POV.DOWN, stick.getRawButton(POV.DOWN.getDPadIndex()));
	}
	
	/*
	 * updateAxisHash
	 * Author: Ruben Castro
	 * --------------------------------------
	 * Purpose: updates just the axis hashmaps
	 */
	private static void updateAxisHash(HashMap<Axis,Double> hash, Joystick stick){
		hash.put(Axis.LEFT_TRIGGER, stick.getRawAxis(Axis.LEFT_TRIGGER.getIndex()));
		hash.put(Axis.RIGHT_TRIGGER, stick.getRawAxis(Axis.RIGHT_TRIGGER.getIndex()));
		hash.put(Axis.LEFT_X, stick.getRawAxis(Axis.LEFT_X.getIndex()));
		hash.put(Axis.LEFT_Y, -stick.getRawAxis(Axis.LEFT_Y.getIndex()));
		hash.put(Axis.RIGHT_X, stick.getRawAxis(Axis.RIGHT_X.getIndex()));
		hash.put(Axis.RIGHT_Y, stick.getRawAxis(Axis.RIGHT_Y.getIndex()));
	}
	
	/*
	 * getDriverAxis
	 * Author: Jeremiah Hanson
	 * --------------------------------------------
	 * Purpose: gets the drivers current axis hashmap
	 * Returns: HashMap
	 */
	public HashMap<Axis, Double> getDriverAxis() {
		return driverAxis;
	}
	
	/*
	 * getOperatorAxes
	 * Author: Nitesh Puri
	 * -------------------------------------------
	 * Purpose: gets the operator's current axis value
	 * Returns: Operator's Axis Value As Double
	 */
	public HashMap<Axis, Double> getOperatorAxes(){
		return operatorAxis;
	}
	
	/*
	 * getButton
	 * Author: Finlay Parsons
	 * Collaborators: Jeremiah Hanson, Nitesh Puri, Ethan Ngo
	 * --------------------------------------------------------
	 * Purpose: Gets current button being pressed
	 * Parameters: 
	 * 	button: enum representing the button to get
	 * Returns: boolean
	 */
	public boolean getButton(Button button, SysObj.Sensors controller){
		if(controller == SysObj.Sensors.DRIVER_STICK) {
			return driverButtons.get(button);
		}
		else if(controller == SysObj.Sensors.OPERATOR_STICK){
			return operatorButtons.get(button);
		}
		else {
			return false;
		}
	}
	
	/*
	 * getDPadButton
	 * Author: Ethan Yes
	 * Collaborators: Nitesh Puri
	 * --------------------------------------------------------
	 * Purpose: Gets current DPad button being pressed
	 * Parameters: 
	 * 	button: enum representing the button to get
	 * Returns: boolean
	 */
	public boolean getDPadButton(POV pov, SysObj.Sensors controller){
		if(controller == SysObj.Sensors.DRIVER_STICK) {
			return driverDPadButtons.get(pov);
		}
		else if(controller == SysObj.Sensors.OPERATOR_STICK){
			return operatorDPadButtons.get(pov);
		}
		else {
			return false;
		}
	}
	/*
	 * (non-Javadoc)
	 * @see systems.Subsystem#toSmartDashboard()
	 */
	@Override
	public void toSmartDashboard() {
		// TODO Auto-generated method stub
		
	}
	
	public void setRumbleOperator(double val, boolean both) {
		if (both) {
			operatorJoystick.setRumble(RumbleType.kLeftRumble, val);
			operatorJoystick.setRumble(RumbleType.kRightRumble, val);
		}else {
			operatorJoystick.setRumble(RumbleType.kLeftRumble, val);
		}
	}

	
	public void setRumbleDriver(double val){
		driverJoystick.setRumble(RumbleType.kLeftRumble, val);
		driverJoystick.setRumble(RumbleType.kRightRumble, val);		
	}
}
