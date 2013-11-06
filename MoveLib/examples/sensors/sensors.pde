// Import the Move Library
import movelib.library.*;

// The layer used to communicate with the controller(s)
MoveLib moveManager;

// The actual controller
MoveController move;


void setup() {
  
  moveManager = new MoveLib(this);     // Change to "MoveLib(this,1)" to activate debug messages 
  move = moveManager.getController(0); // Retreive the first connected controller

}


void draw() { 
  
  PVector acc = move.getAccelerometers();
  PVector mag = move.getMagnetometers();
  
  // When the controller is pointing down
  if (acc.y < 0  &&  mag.y > 0) {
    // Make the controller vibrate and flash red
    move.setLeds((int)random(255), 0, 0);
    move.setRumble(255);
  }
  else {
    // Otherwise, make the sphere blue and stop the vibration
    move.setLeds(0, 100, 200); 
    move.setRumble(0); 
  }
  
  moveManager.update();        // Update and poll all active controllers
}


void exit() {

  moveManager.shutdown(); // We clean after ourselves (stop rumble and LEDs off)
  super.exit();           // Whatever Processing usually does at shutdown

} // Note: this function is not called on closing the sketch with the "stop" button
  

