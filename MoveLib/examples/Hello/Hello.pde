import MoveLib.library.*;

MoveManager moveManager;
MoveController move;

int moveCount; 

// Create the move library object
MoveLib movelib;

void setup() {
  //frameRate(25);
  movelib = new MoveLib(this);
  moveManager = movelib.createManager(MoveLib.VERBOSE); // Enable move support (VERBOSE to activate debug messages)
  moveCount = moveManager.get_controller_count();       // Number of connected controllers  
}

void draw() {  
  for(int i=0; i<moveCount; i++) { // Loop through all connected controllers
    move = moveManager.getController(i); // Grab each controller
   
    if (move.isMovePressed()) { // What happens when the MOVE button is pressed?
      move.set_leds(255,0,0); // Red sphere
      move.set_rumble(255);   // Vibration at maximum
    }
      
    else { // What happens when the MOVE button is not pressed?
      move.set_leds(0, 255, 0); // Green sphere
      move.set_rumble(0);       // Vibration off
    }

    
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
