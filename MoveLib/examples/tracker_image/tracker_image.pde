
/* IMPORTANT NOTE
 *
 * The PSeye camera does not have a MacOS driver. 
 * Here is the calibration process for the internal 
 * camera on Mac computers. Launch the sketch and put 
 * the sphere in front of the camera so that it touches 
 * the lense. The LEDs will light in white for a few 
 * seconds. Then move the controller further from the 
 * lense as it starts to blink in a solid color. It 
 * tracking should start after a few seconds.
 *
 */

// Import the Move Library
import movelib.library.*;

// The MoveLib object used to initialize the library
MoveLib ml;

// The layer used to communicate with the controller(s)
MoveManager moveManager;

// The actual controller
MoveController move;

// The image from the camera
PImage trackerImage;

boolean isTracking = false;

void setup() {
  
  size(640,480,P2D);
  
  // Instanciate the library class
  ml = new MoveLib(this);
  
  // Enable move support. Change to "MoveManager(1)" to activate debug messages 
  moveManager = new MoveManager(1);
  
  // Retrieve the first connected controller
  move = moveManager.getController(0); 
  
  // Sensor fusion is enabled by default. Call disableOrientation() 
  // in case you don't need orientation and performance is an issue.
  move.disableOrientation();     
 
}


void draw() { 
  
  // Initialize the camera if not already done.
  // Note: moveManager.startTracking() could be simply called 
  // in setup and the test wouldn't be necessary but this is
  // to bypass a Processing 2.0b8 bug.
  // See this issue: https://github.com/processing/processing/issues/1735
  if(!isTracking) {
    moveManager.startTracking();
    isTracking = true;
  }
  
  // Get the image from the camera (through the API)
  trackerImage = moveManager.getImage();
  
  // Do we have a tracker image?
  if( null != trackerImage ) {
    
    // Get the x,y position. The third value of the vector 
    // is actually the radius of the sphere in pixels
    PVector pos = move.getPosition();
    float x = pos.x;
    float y = pos.y;
    float r = pos.z;
    
    // Display the camera image
    image(trackerImage, 0, 0);
    
    // Draw a green circle at the position
    noFill();
    stroke(0,255,0);
    ellipse(x, y, r*2, r*2);
  }
  
  // Update and poll all active controllers
  moveManager.update();
  
}

void keyPressed() {      

  if(key=='b'|| key=='B')
      moveManager.printAllControllers(); // print the info about all connected controllers

}


void exit() {

  moveManager.shutdown(); // We clean after ourselves (stop rumble and LEDs off)
  super.exit();           // Whatever Processing usually does at shutdown

} // Note: this function is not called on closing the sketch with the "stop" button
  

