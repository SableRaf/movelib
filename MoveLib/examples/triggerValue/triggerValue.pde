// Import the Move Library
import movelib.library.*;

// The layer used to communicate with the controller(s)
MoveLib moveManager;

// The actual controller
MoveController move;


void setup() {
  
  moveManager = new MoveLib(this);     // Initialize communication with the controller(s)
  move = moveManager.getController(0); // Retrieve the first connected controller
  
}


void draw() {
  
  int trigger = move.getTriggerValue(); // How much pressure on the trigger? [0-255]
  
  move.setLeds(200,100,trigger);         // Set the sphere color
  
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
  
