// Import the Move Library
import movelib.library.*;

// The MoveLib object used to initialize the library
MoveLib ml;

// The abstraction layer used to communicate with the controller(s)
MoveManager moveManager;

import java.util.Set;

int moveCount; 

void setup() {
  ml = new MoveLib(this);
  moveManager = new MoveManager(1);
  
  moveCount = moveManager.getControllerCount(); // Number of connected controllers
}

void draw() {
  handleMoves();
}


void handleMoves() {
  // Get the data from all controllers in one go (maybe?)
  MoveData data = moveManager.getData();

  for (int i=0; i<moveCount; i++) { // Loop through all connected controllers
    move = moveManager.getController(i); // Grab each controller
    move.setColor(255, 0, 0); // Turn the LED red
    move.setRumble(255); // Vibration to max
    
    PVector pos = move.getPosition();
    PVector orientation = move.getOrientation();

    if (move.isPressedEvent(SELECT_BTN)) {
      move.reset_orientation(); // Set the orientation quaternions to their start values [1, 0, 0, 0]
    }
  }
  // Update the color and rumble of all controller
  // and get the new sensor and button values.
  moveManager.update(); 
}
