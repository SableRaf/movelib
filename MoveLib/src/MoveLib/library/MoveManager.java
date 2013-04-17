package movelib.library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import io.thp.psmove.*;

import processing.core.PVector;

//import movelib.MoveConstants;

public class MoveManager implements MoveConstants {
	  
	  int total_connected, unique_connected;
	  
	  MoveTracker moveTracker;
	  
	  private boolean debug = false; // Print debug messages?
	  
	  
	  //private boolean isStreaming = false; // Are we sending the move data to a remote machine? (Default is false)
	  //private boolean isReceiving   = false; // Are we receiving data from a remote source? (Default is false)
	  
	  
	  //OscManager oscManager;  
	  
	  
	  // This is the list where we will store the connected 
	  // controllers and their id (MAC address) as a Key.
	  
	  private HashMap<String, MoveController> controllers;
	  
	  
	  // List of connected controllers by index
	  // This provides an easier access to the controllers
	  // but doesn't allow to identify a controller over
	  // several runs of the program.
	  
	  private ArrayList<MoveController> orderedControllers;
	  
	  
	  // The same controller connected via USB and Bluetooth 
	  // shows twice. If priority_bluetooth is enabled, USB 
	  // controllers will be replaced with their Bluetooth 
	  // counterpart when one is found. 
	  // Otherwise, it is "first in first served".
	  
	  boolean priority_bluetooth = true;
	  
	  /** The MoveManager constructor calls init() and update() to connect to all active controllers
	   * 
	   */
	  public MoveManager() {
	    init();
	    update();
	  }
	  
	  /** The MoveManager constructor calls init() and update() to connect to all active controllers
	   *  This overloaded constructor offers the possiblity to customize the type of messages printed
	   *  by the library. For now, only VERBOSE and SILENT (default) are available.
	   * 
	   * @param mode Pass 1 to display debug messages
	   */
	  public MoveManager(int mode) {
	    if(mode == VERBOSE) debug = true;
	    init();
	    update();
	  }
	  
	  
	  private void init() {    
	    if(debug) System.out.println("Looking for controllers...");
	    if(debug) System.out.println("");
	    
	    total_connected = psmoveapi.count_connected();
	    unique_connected = 0; // Number of actual controllers connected (without duplicates)
	    
	    controllers = new java.util.HashMap<String, MoveController>(); // Create the list of controllers
	    
	    orderedControllers = new ArrayList<MoveController>();

	    
	    // This is only fun if we actually have controllers
	    if (total_connected == 0) {
	      if(debug) System.out.println("WARNING: No controllers connected.");
	    }

	    
	    // Filter via connection type to avoid duplicates
	    for (int i = 0; i<total_connected; i++) {
	  
	      MoveController move = new MoveController(i, this);
	      
	      move.debug(debug); // Do we print debug messages?

	      String serial = move.getSerial();
	      String connection = move.get_connection_name();
	  
	      if (!controllers.containsKey(serial)) { // Check for duplicates
	        try { 
	          controllers.put(serial, move);        // Add the id (MAC address) and controller to the list
	          orderedControllers.add(move);        // Also add the controller to the indexed array
	          if(debug) System.out.println("Found "+serial+" via "+connection);
	        }
	        catch (Exception ex) {
	          if(debug) System.out.println("Error trying to register Controller #"+i+" with address "+serial);
	          ex.printStackTrace();
	        }
	        unique_connected++; // We just added one unique controller
	      }
	      else {
	        if(connection == "Bluetooth" && priority_bluetooth) {
	          MoveController duplicate_move = controllers.get(serial);
	          String duplicate_connection = duplicate_move.get_connection_name(); // 
	          overwrite(serial,move);
	          if(debug) System.out.println("Found "+serial+" via "+connection+" (overwrote "+duplicate_connection+")");
	        }
	        else {
	          if(debug) System.out.println("Found "+serial+" via "+connection+" (duplicate ignored)");
	        }
	      }
	    }
	    //init_serial_array(controllers);
	  }
	  
	  // Replace the duplicate of the specified controller in all lists
	  protected void overwrite(String serial, MoveController move) {
	    controllers.put(serial, move);     // Overwrite the controller at this id
	    // Find the controller with the same serial in ordered_list and overwrite it
	    for(int i=0; i<orderedControllers.size(); i++) { // Loop through the ordered controllers
	      MoveController m = orderedControllers.get(i);
	      String registeredSerial = m.getSerial();
	      if (registeredSerial.equals(serial)) { // Is it the same controller?
	        orderedControllers.set(i,move); // replace the controller at this position by the new one
	      }  
	    }
	  }
	  
	  public void update() {
	    if(!orderedControllers.isEmpty()) { // Do we actually have controllers to update?
	      for (int i=0; i<orderedControllers.size(); i++) {
	        MoveController move = orderedControllers.get(i);     // Give me the controller with that MAC address
	        move.update();	        
	        //if(isStreaming) sendData(); // Send the readings from the controllers via OSC
	      }
	    }
	    
	    if(null != moveTracker) moveTracker.updateAll();
	  }
	  
	  
	  public void shutdown() {
	    if(!controllers.isEmpty()) { // Do we actually have controllers to shut down?
	      for (String id: controllers.keySet()) {
	        MoveController move = controllers.get(id);     // Give me the controller with that MAC address
	        move.shutdown();
	      }
	    }
	  }
	  
	  // Activate sensor fusion for all controllers
	  public void enableOrientation() {
	    if(!controllers.isEmpty()) { // Do we actually have controllers to enable?
	      for (String id: controllers.keySet()) {
	        MoveController move = controllers.get(id);     // Give me the controller with that MAC address
	        move.enableOrientation();
	      }
	    }
	  }
	  
	  /*
	  // Pass move readings to the OSC module for parsing & sending
	  protected void sendData() {
	    // Create all the messages
	    if(null != oscManager) {
	      for(int i=0; i < orderedControllers.size(); i++) { // For each connected move
	        MoveController move = orderedControllers.get(i);
	        String serial = move.getSerial();
	        HashMap<?,?> data  = move.getData(); // Get all the readings from the controller (sensors & button presses)
	        oscManager.createBundle(i,serial,data); // createBundle() will parse the data for the current controller, create the message then add it to the bundle
	      }
	      oscManager.sendBundle(); // request to send the current bundle
	    }
	    else if(debug) 
	      System.out.println("Error in MoveManager.sendData(): oscManager was not instanciated");
	  }
	  */
	  
	   // Print debug messages?
	  protected void debug(boolean b) {
	    debug = b;
	      for (String id: controllers.keySet()) {
	        MoveController move = controllers.get(id);     // Give me the controller with that MAC address
	        move.debug(b);
	      }
	    //oscManager.debug(b);
	  }
	  
	  /*
	  public void stream(Object sketch, int listeningPort, int sendingPort) {
	    isStreaming = true;
	    
	    if(debug) System.out.println("\nSetting up OSC communication...");
	    oscManager = new OscManager(sketch, listeningPort, sendingPort); // Instanciate the OSC object
	    oscManager.debug(debug);       // Do we print debug messages?
	    oscManager.setReceive(isReceiving); // Do we catch messages from a remote source?
	  }
	  */
	  
	  /** Set the same LED color for all the controllers
	   * 
	   * @param r Red value   [0-255]
	   * @param g Green value [0-255]
	   * @param b Blue value  [0-255]
	   */
	  public void setLeds(int r, int g, int b) { 
	    for (String id: controllers.keySet()) {
		    MoveController move = controllers.get(id);     // Give me the controller with that MAC address
		    move.setLeds(r, g, b);
	    }
	  }
	  
	  /** Set the vibration level of all controllers
	   * 
	   * @param rumble is the value of the vibration [0-255]
	   */
	  public void setRumble(int rumble) { 
	    for (String id: controllers.keySet()) {
		    MoveController move = controllers.get(id);     // Give me the controller with that MAC address
		    move.setRumble(rumble);
	    }
	  }
	  
	  /** Print out the status of all controllers in the following form:
	   *  "PS Move with MAC address: FF:FF:FF:FF:FF:FF | Battery 00% | Connected via ..."
	   */
	  public void printAllControllers() { 
	   System.out.println(""); // Line break
	    boolean success = true;
	    for (String id: controllers.keySet()) {
	      success = printController(id);
	      if(!success) { // In case any of the prints fails
	        System.out.println("Error in MoveManager.printController("+id+"):  MoveController.printController() returned false");
	        System.out.println("Tip: Avoid calling printController() methods in setup() as the controllers need time before they start sending data.");
	        break;
	      }
	    }
	  }
	  
	  
	  /** Ask a specific controller to print its information (call by id)
	   * 
	   * @param i the index of the controller
	   * @return success
	   */
	  public boolean printController(int i) {  
	    boolean success = true;
	    String id = orderedControllers.get(i).getSerial();     // Give me the controller with that MAC address
	    success = printController(id);
	      if(!success) { // In case the print fails
		     System.out.println("Error in MoveManager.printController("+id+"):  MoveController.printController() returned false");
		     System.out.println("Tip: Avoid calling printController() in setup() as the controllers need time before they start sending data.");
		  }
	      return success;
	  }
	  
	  
	  /** Ask a specific controller to print its information (call by MAC address)
	   * 
	   * @param id the MAC address of the controller
	   * @return success
	   */
	  public boolean printController(String id) {
		boolean success = true;
	    MoveController move = controllers.get(id);     // Give me the controller with that MAC address
	    success =  move.printController(); // Show the info about each connected controller in the console
	    
	      if(!success) { // In case any of the prints fails
		     System.out.println("Error in MoveManager.printController("+id+"):  MoveController.printController() returned false");
		     System.out.println("Tip: Avoid calling printController() in setup() as the controllers need time before they start sending data.");
		  }

	    return success;
	  }

	  // --- Getters & Setters ----------------------
	  
	  public int getCount() {
	   return unique_connected;
	  }
	  
	  /**  Return the MAC address of a given controller
	   * 
	   * @param i the index of the controller
	   * @return the MAC address as a String
	   */
	  public String getSerial(int i) {
	    int iterator = 0;
	    String serial = "";
	    for (String id: controllers.keySet()) {
	      if(iterator==i)
	        serial = id;
	      else
	        if(debug) serial = "error in getSerial()";
	      iterator++;
	    }
	    return serial;
	  }
	  
	  /** Get a list of all the MAC addresses of the controllers for iteration purposes
	   * 
	   * @return all the serials of the controllers as a Set<String>
	   */
	  public Set<String> getSerials() {
	   Set<String> serials = controllers.keySet();
	   return serials; 
	  }
	  
	  /** Get the controller with a given MAC address or the first connected controller if the parameter is invalid
	   * 
	   * @param id
	   * @return a MoveController object
	   */
	  public MoveController getController(String id) {
	    if(controllers.containsKey(id)) { // Did we register a controller with that serial?
	      MoveController m = controllers.get(id);
	      return m;
	    }
	    if(debug) System.out.println("Error in getController(String id): no MoveController with serial "+id);
	    return orderedControllers.get(0);
	  }
	  
	  
	  /** Get the controller at a given index or the first connected controller if the parameter is invalid
	   * 
	   * @param i the controller's index
	   * @return a MoveController object
	   */
	  public MoveController getController(int i) {
	    if(i>=0 && i < orderedControllers.size()) { // Is the index valid?
	      MoveController m = orderedControllers.get(i);
	      return m;
	    }
	    if(debug) System.out.println("Error in getController(int i): index is out of range (i = "+i+") while orderedControllers.size() = "+orderedControllers.size());
	    if(debug) System.out.println("Returning first connected.");
	    return orderedControllers.get(0);
	  }
	  
	  /** Start the tracking and calibrate the camera using the first connected move
	   * 
	   */
	  public void startTracking() {
		  if(moveTracker==null) {
			  MoveController move = getController(0);
			  move.set_leds(255, 255, 255);
		      move.update_leds();
			  moveTracker = new MoveTracker(move);
		  }
	  }
	  
	  /** Start the tracking and calibrate the camera using the Controller with a specified MAC address
	   * 
	   * @param id the MAC address as a String
	   */
	  public void startTracking(String id) {
		  if(moveTracker==null) {
			  MoveController move = getController(id);
			  moveTracker = new MoveTracker(move);
			  
		  }
	  }
	  
	  PVector getPosition(MoveController move) {
		  PVector pos = new PVector(-1,-1,-1);
		  if(moveTracker!=null) {
			  pos = moveTracker.getPosition(move);
		  }
		  else {
			  System.out.println("You cannot run getPosition() before moveTracker has been initalized");
		  }
	      return pos;
	  }

	}