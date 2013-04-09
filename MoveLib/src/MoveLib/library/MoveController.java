package MoveLib.library;

import java.util.HashMap;

import io.thp.psmove.*;

import processing.core.*;

public 
class MoveController extends PSMove {
	
	  /** PGraphics renderer used for color() */
	  public PGraphics g;

	  private boolean debug = false; // Print debug messages?

	  private boolean has_orientation = false; // Defines if the controller will be configured for sensor fusion

	  private String serial;       // What is the MAC adress of the controller?  

	  int triggerValue, previousTriggerValue;

	  // Actuators
	  private int rumble_level;  // Vibration of the controller (between 0 and 255)
	  private int sphere_color;  // The color values we send to the leds

	  // Orientation calculated by sensor fusion (quaternion)
	  float [] quat0 = {0.f}, quat1 = {0.f}, quat2 = {0.f}, quat3 = {0.f};

	  // Sensor values (inertial mesurement)
	  float [] ax = {0.f}, ay = {0.f}, az = {0.f}; // Accelerometers
	  float [] gx = {0.f}, gy = {0.f}, gz = {0.f}; // Gyroscopes
	  float [] mx = {0.f}, my = {0.f}, mz = {0.f}; // Magnetometers (compasses)

	  private int battery_level;   // How much juice left? Is the controller charging?
	  private String battery_name;    // Same in plain text

	  // enum values returned by PSMove.get_battery()
	  private final int Batt_MIN           = 0x00;
	  private final int Batt_20Percent     = 0x01;
	  private final int Batt_40Percent     = 0x02;
	  private final int Batt_60Percent     = 0x03;
	  private final int Batt_80Percent     = 0x04;
	  private final int Batt_MAX           = 0x05;
	  private final int Batt_CHARGING      = 0xEE;
	  private final int Batt_CHARGING_DONE = 0xEF;

	  private int connection_type; // USB or Bluetooth?
	  private String connection_name; // Same in plain text

	  // enum values returned by PSMove.connection_type()
	  private final int Conn_Bluetooth = 0; // if the controller is connected via Bluetooth
	  private final int Conn_USB       = 1; // if the controller is connected via USB
	  private final int Conn_Unknown   = 2; // on error

	  private MoveButton[] moveButtons = new MoveButton[9];  // The move controller has 9 buttons

	  // enum values for the moveButtons array
	  private final int TRIGGER_BTN  = 0;
	  private final int MOVE_BTN     = 1;
	  private final int SQUARE_BTN   = 2;
	  private final int TRIANGLE_BTN = 3;
	  private final int CROSS_BTN    = 4;
	  private final int CIRCLE_BTN   = 5;
	  private final int START_BTN    = 6;
	  private final int SELECT_BTN   = 7;
	  private final int PS_BTN       = 8;

	  private long [] pressed = {0};  // Button press events
	  private long [] released = {0}; // Button release events

	  MoveController(int i) {
	    super(i);
	    init();
	  }

	  private void init() {    
	    get_serial();
	    getConnection_type();
	    create_buttons();
	    update_poll();
	  }

	  public void update() {
	    update_poll();
	    super.update_leds();
	  }

	  // Put all actuators to rest (vibration & leds)
	  public void shutdown() {
	    super.set_rumble(0);
	    super.set_leds(0, 0, 0);
	    super.update_leds();
	  }
	  
	  public void enable_orientation() {
	    has_orientation = true;
	    super.enable_orientation(1); // We need to tell the PSMove object to activate sensor fusion
	    super.reset_orientation();   // Set the quaternions to their start values [1, 0, 0, 0]
	  }

	  // Print debug messages?
	  public void debug(boolean b) {
	    debug = b;
	  }

	  // Export all the sensors/buttons readings as a List (optimised for building OSC messages)
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

	  public int get_sphere_color() {
	    return sphere_color;
	  }

	  public int getRed() {
	    return sphere_color >> 16 & 0xFF;
	  }

	  public int getGreen() {
	    return sphere_color >> 8 & 0xFF;
	  }

	  public int getBlue() {
	    return sphere_color & 0xFF;
	  }

	  public String get_connection_name() {
	    return connection_name;
	  }

	  public String get_battery_name() {
	    System.out.println("get_battery_name() "+battery_name);
	    return battery_name;
	  }

	  // Orientation get

	  public float get_quat0() {
	    return quat0[0];
	  }

	  public float get_quat1() {
	    return quat1[0];
	  }

	  public float get_quat2() {
	    return quat2[0];
	  }

	  public float get_quat3() {
	    return quat3[0];
	  }

	  // Sensors get

	  // Accelerometers
	  public float get_ax() {
	    return ax[0];
	  }

	  public float get_ay() {
	    return ay[0];
	  }

	  public float get_az() {
	    return az[0];
	  }

	  // Gyroscopes
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

	  // --- Inherited methods --------------------

	  public String get_serial() {
	    serial = super.get_serial(); // Save the serial of the controller
	    return serial;
	  }

	  public int getConnection_type() {
	    connection_type = super.getConnection_type();
	    connection_name = connection_toString(connection_type);
	    return connection_type;
	  }

	  public void set_rumble(int level) {
	    rumble_level = level;
	    super.set_rumble(level);
	  }

	  public int get_rumble() {
	    return rumble_level;
	  }

	  public void set_leds(int r, int g, int b) {
	    sphere_color = color(r, g, b);
	    super.set_leds(r, g, b);
	  }

	  /*
	  public void set_leds(int col) {
	    sphere_color = col;
	    int r = (int)red(col);
	    int g = (int)green(col);
	    int b = (int)blue(col);
	    super.set_leds(r, g, b);
	  }
	  */

	  // --- Internal methods ---------------------
	  
	  /** Borrowed from processing's PApplet.java
	   * @param v1 red or hue values relative to the current color range
	   * @param v2 green or saturation values relative to the current color range
	   * @param v3 blue or brightness values relative to the current color range
	   */
	  protected final int color(int v1, int v2, int v3) {
	    if (g == null) {
	      if (v1 > 255) v1 = 255; else if (v1 < 0) v1 = 0;
	      if (v2 > 255) v2 = 255; else if (v2 < 0) v2 = 0;
	      if (v3 > 255) v3 = 255; else if (v3 < 0) v3 = 0;

	      return 0xff000000 | (v1 << 16) | (v2 << 8) | v3;
	    }
	    return g.color(v1, v2, v3);
	  }
	  

	  protected void create_buttons() {
	    for (int i=0; i<moveButtons.length; i++) {
	      moveButtons[i] = new MoveButton();
	    }
	  }

	  // Read inputs from the move (buttons and sensors)
	  protected void update_poll() { 
	    //println("update_buttons()");

	    while (super.poll () != 0) {} // Update all readings in the PSMove object

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

	  // Translate the connection type from int (enum) to a readable form
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

	  // Translate the battery level from int (enum) to a readable form
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

	  // Print the current battery level, mac adress and connection type of each controller
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

