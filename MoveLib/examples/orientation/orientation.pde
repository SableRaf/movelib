
// Import the Quaternion class from Toxiclib
// Get the lib at: http://toxiclibs.org/downloads/
import toxi.geom.Quaternion;

// Import the Move Library
import movelib.library.*;

// The MoveLib object used to initialize the library
MoveLib ml;

// The layer used to communicate with the controller(s)
MoveManager moveManager;

// The actual controller
MoveController move;

// Quaternion object. With a little help from Toxiclib
Quaternion orientation;


void setup() {
  
  size(500,500,P3D);
  
  ml = new MoveLib(this);              // Instanciate the library class
  
  moveManager = new MoveManager(1);    // Enable move support. Change to "MoveManager(1)" to activate debug messages 
  
  move = moveManager.getController(0); // Retreive the first connected controller
  
  //move.disableOrientation();         // Sensor fusion is enabled by default. Call disableOrientation() if you don't need orientation tracking and performance is an issue.
    
  orientation = new Quaternion();      // Instanciate the Quaternion object
  
}


void draw() { 
  
  background(150);
    
  float[] quat = move.getQuaternions();                  // Retrieve the quaternions values

  orientation.set( quat[0], quat[1], quat[2], quat[3] ); // Pass them to Toxiclib's Quaternion object

  float[] euler = orientation.toAxisAngle();             // Convert the values from Quaternion to Euler rotation
  
  float angle = euler[0], axisX = -euler[1], axisY = euler[2], axisZ = -euler[3];
  
  println("angle:"+angle+" axisX:"+axisX+" axisY:"+axisY+" axisZ:"+axisZ);
  
  // Draw a box and rotate the coordinate system according to the orientation of the controller
  lights();
  pushMatrix();
  translate(width*.5f, height*.5f);
  rotate(angle, axisX, axisY, axisZ); 
  fill(255);
  scale(height*.05);
  box(4, 2, 10);
  popMatrix();
  

  if(move.isMovePressedEvent()) move.resetOrientation(); // Press the "Move" button to reset the rotation matrix
  
  move.setLeds(100,255,250); // Lights on
  
  moveManager.update();      // Update and poll all active controllers
  
}


void keyPressed() {      

  if(key=='b'|| key=='B')
      moveManager.printAllControllers(); // print the info about all connected controllers

}


void exit() {

  moveManager.shutdown(); // We clean after ourselves (stop rumble and LEDs off)
  super.exit();           // Whatever Processing usually does at shutdown

} // Note: this function is not called on closing the sketch with the "stop" button
  

