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
  moveManager = new MoveManager();     // Enable move support
  move = moveManager.getController(0); // Retrieve the first connected controller
  
}


void draw() {
  
  int trigger = move.getTriggerValue(); // How much pressure on the trigger? [0-255]
  
  move.setLeds(50,100,trigger);         // Set the sphere color
  
  move.setRumble(trigger);              // Set the vibration
  
  moveManager.update();                 // Update the vibration and LEDs and get new values for all active controllers 
  
}


void keyPressed() {      

  if(key=='b'|| key=='B')
      moveManager.printAllControllers(); // print the info about all connected controllers

}

void exit() {
  
  moveManager.shutdown(); // We clean after ourselves (stop rumble and LEDs off)
  super.exit();           // Whatever Processing usually does at shutdown
  
} // Note: this function is not called on closing the sketch with the "stop" button
  
