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
  
  println("Found a controller connected via "+ connectionName);

}


void draw() { 
 noLoop(); 
}

void exit() {

  moveManager.shutdown(); // We clean after ourselves (stop rumble and LEDs off)
  super.exit();           // Whatever Processing usually does at shutdown

} // Note: this function is not called on closing the sketch with the "stop" button

