// Import the Move Library
import movelib.library.*;

// The layer used to communicate with the controller(s)
MoveLib moveManager;

void setup() {
  
  moveManager = new MoveLib(this);            // Change to "MoveLib(this,1)" to activate debug messages 
  
}


void draw() {
  
  colorMode(HSB);
  
  int hue = frameCount % 255; // Loop through colors
  int sat = 255;              // Maximum saturation
  int brg = 255;              // Maximum brightness
  
  color sphereColor = color(hue,sat,brg);
  
  // Convert to RBG
  int r = (int)red(sphereColor);
  int g = (int)green(sphereColor);
  int b = (int)blue(sphereColor);
  
  moveManager.setLeds(r,g,b);  // Set the LED color for all active controllers
  moveManager.update();        // Update the LEDs for all active controllers 

}

void exit() {
  moveManager.shutdown(); // We clean after ourselves (stop rumble and LEDs off)
  super.exit();           // Whatever Processing usually does at shutdown
} // Note: this function is not called on closing the sketch with the "stop" button
  
  
