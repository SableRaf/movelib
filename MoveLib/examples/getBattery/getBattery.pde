// Import the Move Library
import movelib.library.*;

// The MoveLib object used to initialize the library
MoveLib ml;

// The layer used to communicate with the controller(s)
MoveManager moveManager;

// The actual controller
MoveController move;


void setup() {
  
  ml = new MoveLib(this);              // Initialize the lib
  moveManager = new MoveManager();     // Enable move support. Change to "MoveManager(1)" to activate debug messages 
  move = moveManager.getController(0); // Retreive the first connected controller

}


void draw() { 
  
  /** Get the battery status as an int 
   * 
   *  low       = 0;
   *  20%       = 1;
   *  40%       = 2;
   *  60%       = 3;
   *  80%       = 4;
   *  Maximum   = 5;
   *  Charging  = 6;
   *  Full      = 7;
   */
  int    battery     = move.getBattery();
  
 /** Get the plain text version of the charge status
   * 
   *  "low"
   *  "20%"
   *  "40%"
   *  "60%"
   *  "80"
   *  "charging..."
   *  "fully charged"
   */
  String batteryName = move.getBatteryName();
  
  println("Battery status [" + battery + "] = " + batteryName);
  
  moveManager.update();        // Update and poll all active controllers
}


void exit() {

  moveManager.shutdown(); // We clean after ourselves (stop rumble and LEDs off)
  super.exit();           // Whatever Processing usually does at shutdown

}

