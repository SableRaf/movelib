// Import the Move Library
import movelib.library.*;

// The MoveLib object used to initialize the library
MoveLib ml;

// The abstraction layer used to communicate with the controller(s)
MoveManager moveManager;

// Will receive MoveController objects from the moveManager
MoveController move;

// Button enum
final int TRIGGER_BTN  = 0;
final int MOVE_BTN     = 1;
final int SQUARE_BTN   = 2;
final int TRIANGLE_BTN = 3;
final int CROSS_BTN    = 4;
final int CIRCLE_BTN   = 5;
final int START_BTN    = 6;
final int SELECT_BTN   = 7;
final int PS_BTN       = 8;


void setup() {
  
  // Initialise the library object
  ml = new MoveLib(this);  
  
  moveManager = new MoveManager(1);     // Enable move support (pass 1 to activate debug messages)
  moveManager.setLeds(10,255,100);      // Turn the LEDs green on start

  move = moveManager.getController(0);  // Grab first connected controller    

}


void draw() {  
    
  boolean[] eventPressed  = move.getPressedEvents();
  boolean[] eventReleased = move.getReleasedEvents();
  
  // loop through buttons
  for(int btn=0; btn<eventPressed.length; btn++) {  
    
    boolean pressed = eventPressed[btn];
    
    if(pressed) { 
      
      // In case an press was registered for that button, check
      // what button index we're at and do stuff
      switch(btn) {
        
        case TRIGGER_BTN: 
          move.setLeds(255,20,10);         // Turn the LEDs red
          move.setRumble(255);             // Vibration at maximum
          break;
          
        case MOVE_BTN:
          move.setLeds(0,100,255);
          move.setRumble(255);
          println("test");
          break;
          
      }
    } 
  }
      
      
  for(int btn=0; btn<eventReleased.length; btn++) {  
    
    boolean released = eventReleased[btn];
    
    if(released) {
      
      switch(btn) {
        
        case TRIGGER_BTN: 
          move.setLeds(10,255,100);      // Turn the LEDs blue
          move.setRumble(0);             // Vibration off
          break;
          
        case MOVE_BTN:
          move.setLeds(10,255,100);
          move.setRumble(0);             // Vibration off
          break;
          
      }
    }
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
  
