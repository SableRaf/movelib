// Import the Move Library
import movelib.library.*;

// The layer used to communicate with the controller(s)
MoveLib moveManager;


void setup() {
  
  moveManager = new MoveLib(this);  // Initialize communication with the controller(s)
  
}


void draw() {
  
  // Make the rumble value oscillate between 64 and 255 (in my tests, vibration is off at values below 64)
  int rumble = (int)map(sin(radians(frameCount)), -1, 1, 64, 255);
  
  println("Vibration level: "+rumble);
  
  moveManager.setRumble(rumble);  // Set the vibration for all active controllers
  moveManager.update();          // Update the vibration and LEDs for all active controllers 
  
}


void exit() {
  
  moveManager.shutdown(); // We clean after ourselves (stop rumble and LEDs off)
  super.exit();           // Whatever Processing usually does at shutdown
  
} // Note: this function won't be called on closing the sketch with the "stop" button
  
