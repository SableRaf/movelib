package MoveLib.library;

import io.thp.psmove.psmoveapi;

import MoveLib.library.MoveController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class MoveManager implements MoveConstants {
	  
	  int total_connected, unique_connected;
	  
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
	  private ArrayList<MoveController> ordered_controllers;
	  
	  // The same controller connected via USB and Bluetooth 
	  // shows twice. If priority_bluetooth is enabled, USB 
	  // controllers will be replaced with their Bluetooth 
	  // counterpart when one is found. 
	  // Otherwise, it is "first in first served".
	  boolean priority_bluetooth = true;
	  
	  protected MoveManager() {
	    init();
	    update();
	  }
	  
	  protected MoveManager(int mode) {
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
	    
	    ordered_controllers = new ArrayList<MoveController>();

	    // This is only fun if we actually have controllers
	    if (total_connected == 0) {
	      if(debug) System.out.println("WARNING: No controllers connected.");
	    }

	    // Filter via connection type to avoid duplicates
	    for (int i = 0; i<total_connected; i++) {
	  
	      MoveController move = new MoveController(i);
	      
	      move.debug(debug); // Do we print debug messages?

	      String serial = move.get_serial();
	      String connection = move.get_connection_name();
	  
	      if (!controllers.containsKey(serial)) { // Check for duplicates
	        try { 
	          controllers.put(serial, move);        // Add the id (MAC address) and controller to the list
	          ordered_controllers.add(move);        // Also add the controller to the indexed array
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
	    for(int i=0; i<ordered_controllers.size(); i++) { // Loop through the ordered controllers
	      MoveController m = ordered_controllers.get(i);
	      String registeredSerial = m.get_serial();
	      if (registeredSerial.equals(serial)) { // Is it the same controller?
	        ordered_controllers.set(i,move); // replace the controller at this position by the new one
	      }  
	    }
	  }
	  
	  public void update() {
	    if(!ordered_controllers.isEmpty()) { // Do we actually have controllers to update?
	      for (int i=0; i<ordered_controllers.size(); i++) {
	        MoveController move = ordered_controllers.get(i);     // Give me the controller with that MAC address
	        move.update();
	        //if(isStreaming) sendData(); // Send the readings from the controllers via OSC
	      }
	    }
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
	  public void enable_orientation() {
	    if(!controllers.isEmpty()) { // Do we actually have controllers to enable?
	      for (String id: controllers.keySet()) {
	        MoveController move = controllers.get(id);     // Give me the controller with that MAC address
	        move.enable_orientation();
	      }
	    }
	  }
	  
	  /*
	  // Pass move readings to the OSC module for parsing & sending
	  protected void sendData() {
	    // Create all the messages
	    if(null != oscManager) {
	      for(int i=0; i < ordered_controllers.size(); i++) { // For each connected move
	        MoveController move = ordered_controllers.get(i);
	        String serial = move.get_serial();
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
	  public void debug(boolean b) {
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
	  
	  public void printControllerAt(int i) {  
	    boolean success = true;
	    String id = ordered_controllers.get(i).get_serial();     // Give me the controller with that MAC address
	    success = printController(id);
	      if(!success) { // In case any of the prints fails
		     System.out.println("Error in MoveManager.printControllerAt("+id+"):  MoveController.printController() returned false");
		     System.out.println("Tip: Avoid calling printControllerAt() in setup() as the controllers need time before they start sending data.");
		  }
	  }
	  
	  public boolean printController(String id) {  
	    MoveController move = controllers.get(id);     // Give me the controller with that MAC address
	    return move.printController(); // Show the info about each connected controller in the console
	    // Return true if the printing was successful
	  }

	  // --- Getters & Setters ----------------------
	  
	  public int get_controller_count() {
	   return unique_connected;
	  }
	  
	  // Return the Mac adress of a given controller
	  public String get_serial(int i) {
	    int iterator = 0;
	    String serial = "";
	    for (String id: controllers.keySet()) {
	      if(iterator==i)
	        serial = id;
	      else
	        if(debug) serial = "error in get_serial()";
	      iterator++;
	    }
	    return serial;
	  }
	  
	  // In case you need to iterate through MAC addresses of the controllers
	  public Set<String> get_serials() {
	   Set<String> serials = controllers.keySet();
	   return serials; 
	  }
	  
	  // Get the controller with a given MAC adress
	  public MoveController getController(String id) {
	    if(controllers.containsKey(id)) { // Did we register a controller with that serial?
	      MoveController m = controllers.get(id);
	      return m;
	    }
	    if(debug) System.out.println("Error in getController(String id): no MoveController with serial "+id);
	    return ordered_controllers.get(0);
	  }
	  
	  // Get the controller at a given index
	  public MoveController getController(int i) {
	    if(i>=0 && i < ordered_controllers.size()) { // Is the index valid?
	      MoveController m = ordered_controllers.get(i);
	      return m;
	    }
	    if(debug) System.out.println("Error in getController(int i): index is out of range (i = "+i+") while ordered_controllers.size() = "+ordered_controllers.size());
	    if(debug) System.out.println("Returning first connected.");
	    return ordered_controllers.get(0);
	  }

	}