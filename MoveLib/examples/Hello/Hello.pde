// Import the Move Library
import movelib.library.*;

// The MoveLib object used to initialize the library
MoveLib ml;

// The abstraction layer used to communicate with the controller(s)
MoveManager moveManager;

import java.util.Set;

// Will not be initialized but will receive 
// MoveController objects from the moveManager
MoveController move;

void setup() {

  ml = new MoveLib(this);  
  
  
  moveManager = new MoveManager(1);              // Enable move support (pass 1 to activate debug messages)
  // moveManager.stream(this, 12000, 12000);     // Send the data via OSC (sketch instance, listening port, sending port)
  moveManager.enableOrientation();               // Activate sensor fusion for all controllers
  moveManager.setLeds(10,255,100);               // Turn the LEDs green on start
  moveManager.startTracking();
}

void draw() {  
  moveHandle();
}

void moveHandle() {
   for(int i=0; i<moveManager.getCount(); i++) { // Loop through all connected controllers
    move = moveManager.getController(i); // Grab each controller
      
    if (move.isMovePressedEvent()) { // What happens the moment I press the MOVE button?
      move.setLeds(255,20,10);       // Turn the LEDs red
      move.setRumble(255);           // Vibration at maximum
    }
      
    if (move.isMoveReleasedEvent()) { // What happens the moment I release the MOVE button?
      move.setLeds(0, 100, 255);      // Turn the LEDs blue
      move.setRumble(0);              // Vibration off
    }
    
    if (move.isSelectPressedEvent()) {
      move.resetOrientation(); // Set the orientation quaternions to their start values [1, 0, 0, 0]
    }
    
    PVector pos = move.getPosition();
    println("x:"+pos.x+" y:"+ pos.y +" z:"+ pos.z);
    //PVector orientation = move.getOrientation();
    
  }
  moveManager.update(); // Read new input and update actuators (leds & rumble) for all controllers 
}

void keyPressed() {      
   if(key=='b'|| key=='B')
      moveManager.printAllControllers(); // print the info about all connected controllers
}

void exit() {
  moveManager.shutdown(); // We clean after ourselves (stop rumble and lights off)
  super.exit();           // Whatever Processing usually does at shutdown
}
  
