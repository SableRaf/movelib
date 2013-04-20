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

  int    connection      = move.getConnection();
  String connectionName  = move.getConnectionName();
  
  if(connection<2)
    println("Found a controller connected via "+ connectionName);

}


void draw() { 
  
  moveManager.update();        // Update and poll all active controllers
}

void keyPressed() {      
   if(key=='b'|| key=='B')
      moveManager.printAllControllers(); // print the info about all connected controllers
}

void exit() {

  moveManager.shutdown(); // We clean after ourselves (stop rumble and LEDs off)
  super.exit();           // Whatever Processing usually does at shutdown

}

