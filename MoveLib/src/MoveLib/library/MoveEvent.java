package movelib.library;

// Probably not the way you're supposed to do this... let's try again.

/*
package movelib;

import processing.event.*;

public class MoveEvent extends Event {
	static public final int PRESS = 1;
	static public final int RELEASE = 2;
	
	protected int id;          // Connection number
	protected String serial;   // MAC address
	protected int x, y, z;     // 3D Position
	
	protected int button;      // 0 to 9 for the Move Controller
	
	protected int pressure;    // 0 to 254 for the Move's analog trigger
	
	// Additional flavor to the 3 Processing natives: KEY, MOUSE and TOUCH
	static public final int MOVE = 4;
	
	public MoveEvent(int x, int y, int z, int button, String buttonName, int pressure) {
	//super(nativeObject, millis, action, modifiers);
	this.flavor = MOVE;
	this.x = x;
	this.y = y;
	this.z = z;
	this.button = button;
	this.pressure = pressure;
	}

	public int getX() {
	  return x;
	}
	
	
	public int getY() {
	  return y;
	}
	
	
	// Which button was pressed (0 to 9 for the Move Controller).
	public int getButton() {
	  return button;
	}
	

	public float getPressure() {
	   return pressure;
    }

}

*/