// Import the Move Library
import movelib.library.*;

// The abstraction layer used to communicate with the controller(s)
MoveLib moveManager;

// Will receive MoveController objects from the moveManager
MoveController move;

int connectedCount;

void setup() {
  
  moveManager = new MoveLib(this, 1);  // Initialize communication with the controller(s)
  
}


void draw() {  
  
  // How many active controllers do we have?
  connectedCount = moveManager.getCount();
  
  // Loop through all connected controllers
  for(int i=0; i< connectedCount; i++) {
    
    move = moveManager.getController(i); // Grab each controller
      
    // Compile a color based on the controller's index
    colorMode(HSB);
    int hue = (int)map(i, 0, connectedCount, 0, 255);
    color sphereColor = color(hue,255,255);
    
    // Convert to RBG
    int r = (int)red(sphereColor);
    int g = (int)green(sphereColor);
    int b = (int)blue(sphereColor);
      
    move.setLeds(r,g,b);
  }
  
  moveManager.update(); // Update and poll all active controllers 
}


void keyPressed() {      
   if(key=='b'|| key=='B')
      moveManager.printAllControllers(); // print the info about all connected controllers
}


void exit() {

  moveManager.shutdown(); // We clean after ourselves (stop rumble and lights off)
  super.exit();           // Whatever Processing usually does at shutdown

} // Note: this function is not called on closing the sketch with the "stop" button
  
