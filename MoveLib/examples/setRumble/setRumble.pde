// Import the Move Library
import movelib.library.*;

// The MoveLib object used to initialize the library
MoveLib ml;

// The layer used to communicate with the controller(s)
MoveManager moveManager;


void setup() {
  
  ml = new MoveLib(this);            // Initialize the lib
  moveManager = new MoveManager();   // Enable move support
  
}


void draw() {
  
  int level = (int)map(sin(radians(frameCount)), -1, 1, 70, 255); // All vibration levels between 1 and 70 are virtually the same
  println("Vibration level: "+level);
  
  moveManager.setRumble(level);  // Set the vibration for all active controllers
  moveManager.update();          // Update the vibration and LEDs for all active controllers 

  println("Vibration level: "+level);
  
}


void exit() {
  
  moveManager.shutdown(); // We clean after ourselves (stop rumble and LEDs off)
  super.exit();           // Whatever Processing usually does at shutdown
  
} // Note: this function won't be called on closing the sketch with the "stop" button
  
