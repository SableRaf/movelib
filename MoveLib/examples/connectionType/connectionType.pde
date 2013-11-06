// Import the Move Library
import movelib.library.*;

// The layer used to communicate with the controller(s)
MoveLib moveManager;

// The actual controller
MoveController move;

void setup() {
  
  moveManager = new MoveLib(this, 1);  // Initialize communication with the controller(s) (pass 1 to activate debug message)
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

