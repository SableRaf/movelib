// Import the Move Library
import movelib.*;

// The MoveLib object used to initialize the library
MoveLib ml;

// The abstraction layer used to communicate with the controller(s)
MoveManager moveManager;

import java.util.Set;

int moveCount; 

void setup() {
  
  ml = new MoveLib(this);
  moveManager = new MoveManager(1);
  
}

void draw() {
  
}
