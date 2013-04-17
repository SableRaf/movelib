package movelib.library;


import java.util.HashMap;

import io.thp.psmove.*;

import processing.core.PVector;

/**
 * MoveController abstracts the PSMove class from PSMove API. It focuses on the most common uses: handling the button presses, sphere color, rumble, etc. More advanced functionality like getting the raw sensors values or Bluetooth pairing is left to the parent class.
 */
public class MoveController extends PSMove implements MoveConstants {
	
      MoveManager parent;

	  private boolean debug = false;           // Print debug messages?

	  private boolean has_orientation = false; // Defines if the controller will be configured for sensor fusion

	  private String serial;                   // What is the MAC adress of the controller?  

	  int triggerValue, previousTriggerValue;

	  
	  // Actuators
	  
	  private int rumbleLevel;                // Vibration of the controller (between 0 and 255)
	  private int sphereColor;               // The color values we send to the LEDs

	  
	  // Orientation calculated by sensor fusion (quaternion)
	  
	  float [] quat0 = {0.f}, quat1 = {0.f}, quat2 = {0.f}, quat3 = {0.f};

	  
	  // Sensor values (inertial measurement)
	  
	  float [] ax = {0.f}, ay = {0.f}, az = {0.f};           // Accelerometers
	  float [] gx = {0.f}, gy = {0.f}, gz = {0.f};           // Gyroscopes
	  float [] mx = {0.f}, my = {0.f}, mz = {0.f};           // Magnetometers (compasses)

	  
	  private int battery_level;                             // How much juice left? Is the controller charging?
	  private String battery_name;                           // Same in plain text

	  
	  private int connection_type;                           // USB or Bluetooth?
	  private String connection_name;                        // Same in plain text

	  
	  private MoveButton[] moveButtons = new MoveButton[9];  // The move controller has 9 buttons

	  
	  private long [] pressed = {0};                         // Button press events
	  private long [] released = {0};                        // Button release events

	  
	  /** 
	   * Create a Move Controller object.
	   * 
	   * @param i  Connection index.
	   * @param theParent This is the MoveManager object that created the controller
	   */
	  MoveController(int i, MoveManager theParent) {
	    super(i);
	    parent = theParent;
	    init();
	  }

	  /** 
	   * Build the controller's dependencies and get information about it's status (connection, MAC address). It does so by calling getSerial(), getConnection_type(), createButtons() and updatePoll().
	   */
	  private void init() {    
	    getSerial();
	    getConnectionType();
	    createButtons();
	    updatePoll();
	  }

	  
	  /** 
	  * Get new information from the buttons and update 
	  * the color of the RGB sphere.
	  */
	  public void update() {
	    updatePoll();
	    super.update_leds();
	  }

	  
	  /** 
	  * Put all actuators to rest (vibration & leds) 
	  */
	  public void shutdown() {
	    super.set_rumble(0);
	    super.set_leds(0, 0, 0);
	    super.update_leds();
	  }

	  
	  /** 
	  * Enable orientation tracking.
	  * 
	  * This will enable orientation tracking and update the internal orientation quaternion.
	  * 
	  * In addition to enabling the orientation tracking features, calibration data and an orientation algorithm (usually built-in) has to be used, too. You can use orientation_available() after enabling orientation tracking to check if orientation features can be used.
	  */
	  void enableOrientation() {
		if(orientationAvailable()) {
		    has_orientation = true;
		    super.enable_orientation(1); // We need to tell the PSMove object to activate sensor fusion
		    super.reset_orientation();   // Set the quaternions to their start values [1, 0, 0, 0]
		}
		else if(debug) System.out.println("No orientation available for controller ["+serial+"]");
	  }
	  
	  
	  /** 
	   * Check if orientation features can be used.
	   * 
	   * @return True if orientation is available for the controller, false otherwise
	   */
	  boolean orientationAvailable() {
		  has_orientation = super.has_orientation() == 1 ? 
				            true : 
				            false;
		  return has_orientation;
	  }
	  
	  /** 
	   * Set the orientation quaternions to their start values [1, 0, 0, 0]
	   * 
	   */
	  public void resetOrientation() {
		 super.reset_orientation();
	  }

	  
	  /** 
	   * Print debug messages ? 
	   * 
	   * @param b true for printing debug message, false for silent run
	   */
	  public void debug(boolean b) {
	    debug = b;
	  }

	  
	  /** 
	   * Export all the sensors/buttons readings as a table of String and float
	   * 
	   * @return Sensors and button readings as a HashMap<String,Float> (used to create OSC messages)
	   */
	  public HashMap<String,Float> getData() {
	    HashMap<String, Float> readings = new HashMap<String,Float>();
	    
	    // Populate the list for sending to the OSC module
	    
	    readings.put("sensors/acc/x",get_ax());
	    readings.put("sensors/acc/y",get_ay());
	    readings.put("sensors/acc/z",get_az());
	    
	    readings.put("sensors/gyro/x",get_gx());
	    readings.put("sensors/gyro/y",get_gy());
	    readings.put("sensors/gyro/z",get_gz());
	    
	    readings.put("sensors/mag/x",get_mx());
	    readings.put("sensors/mag/y",get_my());
	    readings.put("sensors/mag/z",get_mz());
	    
	    if(has_orientation) {   
	      readings.put("orientation/quat/0",get_quat0());
	      readings.put("orientation/quat/1",get_quat1());
	      readings.put("orientation/quat/2",get_quat2());
	      readings.put("orientation/quat/3",get_quat3());
	    }
	    
	    readings.put("buttons/triggerValue", (float)get_trigger_value());
	    
	    float pressed = 0.f;
	    
	    if( !isMovePressed() ) pressed = 0.f; // Boolean to floats (0.f = false, 1.f = true)
	    else pressed = 1.f;
	    readings.put("buttons/move", pressed);
	    
	    if( !isSquarePressed() ) pressed = 0.f;
	    else pressed = 1.f;
	    readings.put("buttons/square", pressed);
	    
	    if( !isTrianglePressed() ) pressed = 0.f;
	    else pressed = 1.f;
	    readings.put("buttons/triangle", pressed);
	    
	    if( !isCrossPressed() ) pressed = 0.f;
	    else pressed = 1.f;
	    readings.put("buttons/cross", pressed);
	    
	    if( !isCirclePressed() ) pressed = 0.f;
	    else pressed = 1.f;
	    readings.put("buttons/circle", pressed);
	    
	    if( !isStartPressed() ) pressed = 0.f;
	    else pressed = 1.f;
	    readings.put("buttons/start", pressed);
	    
	    if( !isSelectPressed() ) pressed = 0.f;
	    else pressed = 1.f;
	    readings.put("buttons/select", pressed);
	    
	    if( !isPsPressed() ) pressed = 0.f;
	    else pressed = 1.f;
	    readings.put("buttons/ps", pressed);
	    
	    /*
	    float pressedEvent = 1.f;
	    
	    if( isMovePressedEvent() ) 
	      readings.put("buttons/move/p", pressedEvent);
	    
	    if( isSquarePressedEvent() )
	      readings.put("buttons/square/p", pressedEvent);
	    
	    if( isTrianglePressedEvent() )
	      readings.put("buttons/triangle/p", pressedEvent);
	    
	    if( isCrossPressedEvent() )
	      readings.put("buttons/cross/p", pressedEvent);
	    
	    if( isCirclePressedEvent() )
	      readings.put("buttons/circle/p", pressedEvent);
	    
	    if( isStartPressedEvent() )
	      readings.put("buttons/start/p", pressedEvent);
	    
	    if( isSelectPressedEvent() )
	      readings.put("buttons/select/p", pressedEvent);
	    
	    if( isPsPressedEvent() )
	      readings.put("buttons/ps/p", pressedEvent);
	   */
	    return readings;
	  }

	  // --- Getters & Setters --------------------
	  
	  
	  public PVector getPosition() {
		  PVector pos = new PVector(-1,-1,-1);
		  pos = parent.getPosition(this);
		  return pos;
	  }

	  public int getSphereColor() {
	    return sphereColor;
	  }

	  /**
	   * Get the sphere's red value
	   * @return The red component of the active RGB sphere color
	   */
	  public int getRed() {
	    return sphereColor >> 16 & 0xFF;
	  }

	  /**
	   * Get the sphere's green value
	   * @return The green component of the active RGB sphere color
	   */
	  public int getGreen() {
	    return sphereColor >> 8 & 0xFF;
	  }

	  /**
	   * Get the sphere's blue value
	   * @return The blue component of the active RGB sphere color
	   */
	  public int getBlue() {
	    return sphereColor & 0xFF;
	  }

	  /**
	   * Get the controller's connection type
	   * @return The connection type in plain text
	   */
	  public String get_connection_name() {
	    return connection_name;
	  }

	  /**
	   * Get the controller's battery status
	   * @return The charge level in plain text
	   */
	  public String get_battery_name() {
	    //System.out.println("get_battery_name() "+battery_name);
	    return battery_name;
	  }


	  /**
	   * Get the orientation's first component (quaternion value from sensor fusion)
	   * 
	   * You need to call psmove_poll() first to read new data from the controller.
	   * 
	   * @return a float array containing one value
	   */
	  public float get_quat0() {
	    return quat0[0];
	  }

	  /**
	   * Get the orientation's second component (quaternion value from sensor fusion)
	   * 
	   * You need to call psmove_poll() first to read new data from the controller.
	   * 
	   * @return a float array containing one value
	   */
	  public float get_quat1() {
	    return quat1[0];
	  }

	  /**
	   * Get the orientation's third component (quaternion value from sensor fusion)
	   * 
	   * You need to call psmove_poll() first to read new data from the controller.
	   * 
	   * @return a float array containing one value
	   */
	  public float get_quat2() {
	    return quat2[0];
	  }

	  /**
	   * Get the orientation's fourth component (quaternion value from sensor fusion)
	   * 
	   * You need to call psmove_poll() first to read new data from the controller.
	   * 
	   * @return a float array containing one value
	   */
	  public float get_quat3() {
	    return quat3[0];
	  }

	  /**
	   * Get the calibrated accelerometer values (in g) from the controller. (x axis)
	   * 
	   * You need to call psmove_poll() first to read new data from the controller.
	   * 
	   * @return a float array containing one value
	   */
	  public float get_ax() {
	    return ax[0];
	  }

	  /**
	   * Get the calibrated accelerometer values (in g) from the controller. (y axis)
	   * 
	   * You need to call psmove_poll() first to read new data from the controller.
	   * 
	   * @return a float array containing one value
	   */
	  public float get_ay() {
	    return ay[0];
	  }

	  /**
	   * Get the calibrated accelerometer values (in g) from the controller. (z axis)
	   * 
	   * You need to call psmove_poll() first to read new data from the controller.
	   * 
	   * @return a float array containing one value
	   */
	  public float get_az() {
	    return az[0];
	  }

	  // Gyroscopes
	  /**
	   * Get calibrated gyroscope value (x axis)
	   * 
	   * You need to call psmove_poll() first to read new data from the controller.
	   * 
	   * @return a float array containing one value
	   */
	  public float get_gx() {
	    return gx[0];
	  }

	  public float get_gy() {
	    return gy[0];
	  }

	  public float get_gz() {
	    return gz[0];
	  }

	  // Magnetometers
	  public float get_mx() {
	    return mx[0];
	  }

	  public float get_my() {
	    return my[0];
	  }

	  public float get_mz() {
	    return mz[0];
	  }

	  // Buttons get

	  public int get_trigger_value() {
	    return moveButtons[TRIGGER_BTN].getValue();
	  }

	  public boolean isTriggerPressed() {
	    return moveButtons[TRIGGER_BTN].isPressed();
	  }

	  public boolean isMovePressed() {
	    return moveButtons[MOVE_BTN].isPressed();
	  }

	  public boolean isSquarePressed() {
	    return moveButtons[SQUARE_BTN].isPressed();
	  }

	  public boolean isTrianglePressed() {
	    return moveButtons[TRIANGLE_BTN].isPressed();
	  }

	  public boolean isCrossPressed() {
	    return moveButtons[CROSS_BTN].isPressed();
	  }

	  public boolean isCirclePressed() {
	    return moveButtons[CIRCLE_BTN].isPressed();
	  }

	  public boolean isSelectPressed() {
	    return moveButtons[SELECT_BTN].isPressed();
	  }

	  public boolean isStartPressed() {
	    return moveButtons[START_BTN].isPressed();
	  }

	  public boolean isPsPressed() {
	    return moveButtons[PS_BTN].isPressed();
	  }    

	  // Get button events 
	  // Tells if a given button was pressed/released
	  // since the last call to the event function

	  // Pressed

	  public boolean isTriggerPressedEvent() {
	    boolean event = moveButtons[TRIGGER_BTN].isPressedEvent();
	    return event;
	  }

	  public boolean isMovePressedEvent() {
	    boolean event = moveButtons[MOVE_BTN].isPressedEvent();
	    return event;
	  }

	  public boolean isSquarePressedEvent() {
	    boolean event = moveButtons[SQUARE_BTN].isPressedEvent();
	    return event;
	  }

	  public boolean isTrianglePressedEvent() {
	    boolean event = moveButtons[TRIANGLE_BTN].isPressedEvent();
	    return event;
	  }

	  public boolean isCrossPressedEvent() {
	    boolean event = moveButtons[CROSS_BTN].isPressedEvent();
	    return event;
	  }

	  public boolean isCirclePressedEvent() {
	    boolean event = moveButtons[CIRCLE_BTN].isPressedEvent();
	    return event;
	  }

	  public boolean isSelectPressedEvent() {
	    boolean event = moveButtons[SELECT_BTN].isPressedEvent();
	    return event;
	  }

	  public boolean isStartPressedEvent() {
	    boolean event = moveButtons[START_BTN].isPressedEvent();
	    return event;
	  }

	  public boolean isPsPressedEvent() {
	    boolean event = moveButtons[PS_BTN].isPressedEvent();
	    return event;
	  }   

	  // Released

	  public boolean isTriggerReleasedEvent() {
	    boolean event = moveButtons[TRIGGER_BTN].isReleasedEvent();
	    return event;
	  }

	  public boolean isMoveReleasedEvent() {
	    boolean event = moveButtons[MOVE_BTN].isReleasedEvent();
	    return event;
	  }

	  public boolean isSquareReleasedEvent() {
	    boolean event = moveButtons[SQUARE_BTN].isReleasedEvent();
	    return event;
	  }

	  public boolean isTriangleReleasedEvent() {
	    boolean event = moveButtons[TRIANGLE_BTN].isReleasedEvent();
	    return event;
	  }

	  public boolean isCrossReleasedEvent() {
	    boolean event = moveButtons[CROSS_BTN].isReleasedEvent();
	    return event;
	  }

	  public boolean isCircleReleasedEvent() {
	    boolean event = moveButtons[CIRCLE_BTN].isReleasedEvent();
	    return event;
	  }

	  public boolean isSelectReleasedEvent() {
	    boolean event = moveButtons[SELECT_BTN].isReleasedEvent();
	    return event;
	  }

	  public boolean isStartReleasedEvent() {
	    boolean event = moveButtons[START_BTN].isReleasedEvent();
	    return event;
	  }

	  public boolean isPsReleasedEvent() {
	    boolean event = moveButtons[PS_BTN].isReleasedEvent();
	    return event;
	  }

	  // --- Getters & Setters --------------------
	  
	  public String getSerial() {
		 serial = super.get_serial(); // Save the serial of the controller
		 return serial;
	  }

	public int getConnectionType() {
	    connection_type = super.getConnection_type();
	    connection_name = connection_toString(connection_type);
	    return connection_type;
	  }

	public void setRumble(int level) {
	    rumbleLevel = level;
	    super.set_rumble(level);
	  }

	  public int getRumble() {
	    return rumbleLevel;
	  }

	  public void setLeds(int r, int g, int b) {
	    sphereColor = color(r, g, b);
	    super.set_leds(r, g, b);
	  }

	  // --- Internal methods ---------------------
	  
	  /** Concatenates the r g b values into  an int in the form 0xff000000
	   * 
	   * This is a dumbed down version of the color() function from Processing's PApplet.java class
	   * 
	   * @param r red value
	   * @param g green value
	   * @param b blue value
	   */
	  protected final int color(int r, int g, int b) {
	    if (r > 255) r = 255; else if (r < 0) r = 0;
	    if (g > 255) g = 255; else if (g < 0) g = 0;
	    if (b > 255) b = 255; else if (b < 0) b = 0;

	    return 0xff000000 | (r << 16) | (g << 8) | b;
	  }


	  /** 
	   * Populate the moveButton[] array of the controller with MoveButton objects.
	   */
	  protected void createButtons() {
	    for (int i=0; i<moveButtons.length; i++) {
	      moveButtons[i] = new MoveButton();
	    }
	  }

	  /** 
	   * Read inputs from the Move controller (buttons and sensors)
	   */
	  protected void updatePoll() { 

		  
		// Update all readings in the PSMove object
		  
	    while (super.poll () != 0) {} 

	    
	    // Start by reading all the buttons from the controller
	    
	    int buttons = super.get_buttons();
	    
	    
	    // Then update individual MoveButton objects in the moveButton array
	    
	    if ((buttons & Button.Btn_MOVE.swigValue()) != 0) {
	      moveButtons[MOVE_BTN].press();
	    } 
	    else if (moveButtons[MOVE_BTN].isPressed()) {
	      moveButtons[MOVE_BTN].release();
	    }
	    if ((buttons & Button.Btn_SQUARE.swigValue()) != 0) {
	      moveButtons[SQUARE_BTN].press();
	    } 
	    else if (moveButtons[SQUARE_BTN].isPressed()) {
	      moveButtons[SQUARE_BTN].release();
	    }
	    if ((buttons & Button.Btn_TRIANGLE.swigValue()) != 0) {
	      moveButtons[TRIANGLE_BTN].press();
	    } 
	    else if (moveButtons[TRIANGLE_BTN].isPressed()) {
	      moveButtons[TRIANGLE_BTN].release();
	    }
	    if ((buttons & Button.Btn_CROSS.swigValue()) != 0) {
	      moveButtons[CROSS_BTN].press();
	    } 
	    else if (moveButtons[CROSS_BTN].isPressed()) {
	      moveButtons[CROSS_BTN].release();
	    }
	    if ((buttons & Button.Btn_CIRCLE.swigValue()) != 0) {
	      moveButtons[CIRCLE_BTN].press();
	    } 
	    else if (moveButtons[CIRCLE_BTN].isPressed()) {
	      moveButtons[CIRCLE_BTN].release();
	    }
	    if ((buttons & Button.Btn_START.swigValue()) != 0) {
	      moveButtons[START_BTN].press();
	    } 
	    else if (moveButtons[START_BTN].isPressed()) {
	      moveButtons[START_BTN].release();
	    }
	    if ((buttons & Button.Btn_SELECT.swigValue()) != 0) {
	      moveButtons[SELECT_BTN].press();
	    } 
	    else if (moveButtons[SELECT_BTN].isPressed()) {
	      moveButtons[SELECT_BTN].release();
	    }
	    if ((buttons & Button.Btn_PS.swigValue()) != 0) {
	      moveButtons[PS_BTN].press();
	    } 
	    else if (moveButtons[PS_BTN].isPressed()) {
	      moveButtons[PS_BTN].release();
	    }

	    // Now the same for the events
	    
	    // Start by reading all events from the controller
	    
	    super.get_button_events(pressed, released);
	    // Then register the current individual events to the corresponding MoveButton objects in the moveButtons array
	    if ((pressed[0] & Button.Btn_MOVE.swigValue()) != 0) {
	      if (debug) System.out.println(serial+": The Move button was just pressed.");
	      moveButtons[MOVE_BTN].eventPress();
	    } 
	    else if ((released[0] & Button.Btn_MOVE.swigValue()) != 0) {
	      if (debug) System.out.println(serial+": The Move button was just released.");
	      moveButtons[MOVE_BTN].eventRelease();
	    }
	    if ((pressed[0] & Button.Btn_SQUARE.swigValue()) != 0) {
	      if (debug) System.out.println(serial+": The Square button was just pressed.");
	      moveButtons[SQUARE_BTN].eventPress();
	    } 
	    else if ((released[0] & Button.Btn_SQUARE.swigValue()) != 0) {
	      if (debug) System.out.println(serial+": The Square button was just released.");
	      moveButtons[SQUARE_BTN].eventRelease();
	    }
	    if ((pressed[0] & Button.Btn_TRIANGLE.swigValue()) != 0) {
	      if (debug) System.out.println(serial+": The Triangle button was just pressed.");
	      moveButtons[TRIANGLE_BTN].eventPress();
	    } 
	    else if ((released[0] & Button.Btn_TRIANGLE.swigValue()) != 0) {
	      if (debug) System.out.println(serial+": The Triangle button was just released.");
	      moveButtons[TRIANGLE_BTN].eventRelease();
	    }
	    if ((pressed[0] & Button.Btn_CROSS.swigValue()) != 0) {
	      if (debug) System.out.println(serial+": The Cross button was just pressed.");
	      moveButtons[CROSS_BTN].eventPress();
	    } 
	    else if ((released[0] & Button.Btn_CROSS.swigValue()) != 0) {
	      if (debug) System.out.println(serial+": The Cross button was just released.");
	      moveButtons[CROSS_BTN].eventRelease();
	    }
	    if ((pressed[0] & Button.Btn_CIRCLE.swigValue()) != 0) {
	      if (debug) System.out.println(serial+": The Circle button was just pressed.");
	      moveButtons[CIRCLE_BTN].eventPress();
	    } 
	    else if ((released[0] & Button.Btn_CIRCLE.swigValue()) != 0) {
	      if (debug) System.out.println(serial+": The Circle button was just released.");
	      moveButtons[CIRCLE_BTN].eventRelease();
	    }
	    if ((pressed[0] & Button.Btn_START.swigValue()) != 0) {
	      if (debug) System.out.println(serial+": The Start button was just pressed.");
	      moveButtons[START_BTN].eventPress();
	    } 
	    else if ((released[0] & Button.Btn_START.swigValue()) != 0) {
	      if (debug) System.out.println(serial+": The Start button was just released.");
	      moveButtons[START_BTN].eventRelease();
	    }
	    if ((pressed[0] & Button.Btn_SELECT.swigValue()) != 0) {
	      if (debug) System.out.println(serial+": The Select button was just pressed.");
	      moveButtons[SELECT_BTN].eventPress();
	    } 
	    else if ((released[0] & Button.Btn_SELECT.swigValue()) != 0) {
	      if (debug) System.out.println(serial+": The Select button was just released.");
	      moveButtons[SELECT_BTN].eventRelease();
	    }
	    if ((pressed[0] & Button.Btn_PS.swigValue()) != 0) {
	      if (debug) System.out.println(serial+": The PS button was just pressed.");
	      moveButtons[PS_BTN].eventPress();
	    } 
	    else if ((released[0] & Button.Btn_PS.swigValue()) != 0) {
	      if (debug) System.out.println(serial+": The PS button was just released.");
	      moveButtons[PS_BTN].eventRelease();
	    }

	    
	    // Read the trigger information from the controller
	    
	    previousTriggerValue = triggerValue;             // Store the previous value
	    triggerValue = super.get_trigger();              // Get the new value
	    moveButtons[TRIGGER_BTN].setValue(triggerValue); // Send the value to the button object

	    
	    // press/release behaviour for the trigger
	    
	    if (triggerValue>0) {
	      moveButtons[TRIGGER_BTN].press();
	      if (previousTriggerValue == 0) { // Catch trigger presses
	        if (debug) System.out.println(serial+": The Trigger button was just pressed.");
	        moveButtons[TRIGGER_BTN].eventPress();
	      }
	    }
	    else if (previousTriggerValue>0) { // Catch trigger releases
	      if (debug) System.out.println(serial+": The Trigger button was just released.");
	      moveButtons[TRIGGER_BTN].eventRelease();
	      moveButtons[TRIGGER_BTN].release();
	    }
	    else moveButtons[TRIGGER_BTN].release();

	    
	    // Charge status of the controller
	    
	    battery_level = super.get_battery(); // Read the raw battery level
	    battery_name = battery_toString(battery_level); // Translate the battery level into a readable message

	    
	    // Read the (calibrated) sensor informations from the controller
	    
	    super.get_accelerometer_frame(io.thp.psmove.Frame.Frame_SecondHalf, ax, ay, az);
	    super.get_gyroscope_frame(io.thp.psmove.Frame.Frame_SecondHalf, gx, gy, gz);
	    super.get_magnetometer_vector(mx, my, mz);
	    super.get_orientation(quat0, quat1, quat2, quat3);
	    
	  }

	  /**
	   * Translate the connection type from int (enum) to a readable form
	   *  
	   * @param type The connection type as an int (0 = Bluetooth, 1 = USB, 2 = error).
	   * @return The connection name as a String for printing in messages. 
	   */
	  protected String connection_toString(int type) {
	    switch(type) {
	    case Conn_Bluetooth:  
	      return "Bluetooth";
	    case Conn_USB :       
	      return "USB";
	    case Conn_Unknown :   
	      return "Connection error";
	    default:              
	      return "Error in connection_toString()";
	    }
	  }

	  /**
	  * Translate the battery level from int (enum) to a readable form
	  * 
	  * @param level The charging status as an hex int (from 0x00 = "low" to 0xEF = "charging done")
	  * @return The charging status as a String for printing in messages. 
	  *              
	  */
	  protected String battery_toString(int level) {
	    switch(level) {
	    case Batt_MIN:            
	      return "low";
	    case Batt_20Percent :     
	      return "20%";
	    case Batt_40Percent :     
	      return "40%";
	    case Batt_60Percent :     
	      return "60%";
	    case Batt_80Percent :     
	      return "80%";
	    case Batt_MAX :           
	      return "100%";
	    case Batt_CHARGING :      
	      return "charging...";
	    case Batt_CHARGING_DONE : 
	      return "fully charged";
	    default:                  
	      return "returning [Error in get_battery_level_name()]";
	    }
	  }

	  /**
	   * Print the current battery level, mac adress and connection type of each controller
	   *  
	   * @return True if everything went fine, False in case of error
	   */
	  public boolean printController() {
	    if (null==battery_name || null==serial) {
	      if (debug && null==serial) System.out.println("Error in MoveController.printController(): serial variable not yet instanciated.");
	      if (debug && null==battery_name) System.out.println("Error in MoveController.printController(): battery_name variable not yet instanciated.");
	      return false;
	    }
	    else {
	      System.out.println("PS Move with MAC address: "+serial+ " | Battery "+battery_name+" | Connected via "+connection_name);
	    }
	    return true;
	  }
	}

