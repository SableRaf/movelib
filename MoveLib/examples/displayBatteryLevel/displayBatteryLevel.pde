// Import the Move Library
import movelib.library.*;

// The layer used to communicate with the controller(s)
MoveLib moveManager;

// The actual controller
MoveController move;

// How many controllers do we have?
int count;

// Battery enum values
final int ERROR      = -1;
final int MIN        =  0;
final int TWENTY     =  1;
final int FORTY      =  2;
final int SIXTY      =  3;
final int EIGHTY     =  4;
final int FULL       =  5;
final int CHARGING   =  6;
final int DONE       =  7;

void setup() {
  
  moveManager = new MoveLib(this);     // Initialize communication with the controller(s)
  move = moveManager.getController(0); // Retreive the first connected controller

  count = moveManager.getCount();  // Get the number of active controllers
  
}


void draw() { 
  
  // Loop through all connected controllers
  for(int i=0; i< count; i++) {
    
    move = moveManager.getController(i); // Grab each controller
  
    int battery = move.getBattery();
    
    color col = getColor( battery );
    
    int r = (int)red   (col);
    int g = (int)green (col);
    int b = (int)blue  (col);
    
    move.setLeds(r,g,b);
  
  }
  
  // Update and poll all active controllers
  moveManager.update();
}

color getColor( Integer batteryLevel ) {
  
  int glw = (int) map( sin( frameCount*.03 ), -1, 1, 10, 150 ); // Glow
  int rnd = (int) random(0, 50); // Random intensity
  
  color sphereColor = color(0); // By default, the sphere is switched off
  
   switch( batteryLevel ) {
    case ERROR    : sphereColor = color( rnd, rnd, rnd ); break; // Random white
    case MIN      : sphereColor = color( rnd,   0,   0 ); break; // Flickering red
    case TWENTY   : sphereColor = color( 255, 150,   0 ); break; // orange
    case FORTY    : sphereColor = color( 120, 120,   0 ); break; // yellow
    case SIXTY    : sphereColor = color( 120, 240,   3 ); break; // Light green
    case EIGHTY   : sphereColor = color(  50, 240,   0 ); break; // Bright green
    case FULL     : sphereColor = color(   0, 255,   0 ); break; // Brightest Green
    case CHARGING : sphereColor = color(   0, glw, glw ); break; // Glowing blue
    case DONE     : sphereColor = color(   0, 255, 255 ); break; // Blue
    default       : sphereColor = color(0);               break;
  }
  
  return sphereColor;
}

void keyPressed() {      
   if(key=='b'|| key=='B')
      moveManager.printAllControllers(); // print the info about all connected controllers
}

void exit() {

  moveManager.shutdown(); // We clean after ourselves (stop rumble and LEDs off)
  super.exit();           // Whatever Processing usually does at shutdown

} // Note: this function is not called on closing the sketch with the "stop" button

