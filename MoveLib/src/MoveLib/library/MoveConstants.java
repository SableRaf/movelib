package movelib.library;


/**
 * Numbers shared throughout movelib
 */
public interface MoveConstants {
	
	
	// Connection type
	
	static public final int Conn_Bluetooth = 0;
	static public final int Conn_USB       = 1;
	static public final int Conn_Unknown   = 2; // connection error
	
	
	// Buttons
	
	static public final int TRIGGER_BTN  = 0;
	static public final int MOVE_BTN     = 1;
	static public final int SQUARE_BTN   = 2;
	static public final int TRIANGLE_BTN = 3;
	static public final int CROSS_BTN    = 4;
	static public final int CIRCLE_BTN   = 5;
	static public final int START_BTN    = 6;
	static public final int SELECT_BTN   = 7;
	static public final int PS_BTN       = 8;
	
	
	// Battery levels
	
	static public final int Batt_MIN           = 0;
	static public final int Batt_20Percent     = 1;
	static public final int Batt_40Percent     = 2;
	static public final int Batt_60Percent     = 3;
	static public final int Batt_80Percent     = 4;
	static public final int Batt_MAX           = 5;
	static public final int Batt_CHARGING      = 6;
	static public final int Batt_CHARGING_DONE = 7;
	
	
	// Execution modes
	
	static public final int QUIET   = 0;
	static public final int VERBOSE = 1;	
}
