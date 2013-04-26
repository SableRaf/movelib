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
  
  PVector acc = move.getAccelerometers();
  PVector mag = move.getMagnetometers();
  
  if (acc.y < 0  &&  mag.y > 0) {
    move.setLeds((int)random(255), 0, 0);
    move.setRumble(255);
  }
  else {
    move.setLeds(0, 100, 200); 
    move.setRumble(0); 
  }
  
  moveManager.update();        // Update and poll all active controllers
}


void exit() {

  moveManager.shutdown(); // We clean after ourselves (stop rumble and LEDs off)
  super.exit();           // Whatever Processing usually does at shutdown

} // Note: this function is not called on closing the sketch with the "stop" button
  

