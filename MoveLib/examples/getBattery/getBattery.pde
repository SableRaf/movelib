// Import the Move Library
import movelib.library.*;

// The layer used to communicate with the controller(s)
MoveLib moveManager;

// The actual controller
MoveController move;


void setup() {
  
  moveManager = new MoveLib(this);     // Initialize communication with the controller(s)
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
  
  // Update and poll all active controllers
  moveManager.update();
}


void exit() {

  moveManager.shutdown(); // We clean after ourselves (stop rumble and LEDs off)
  super.exit();           // Whatever Processing usually does at shutdown

} // Note: this function is not called on closing the sketch with the "stop" button

