package movelib.library;

import io.thp.psmove.*;

import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PVector;

public class MoveTracker extends PSMoveTracker implements PConstants {
	
	private boolean debug = false;           // Print debug messages?

	byte [] pixels;
	PImage trackerImage;
	int trackerWidth = 0; 
	int trackerHeight = 0;

	private float px[] = {0.f}, py[] = {0.f}, pr[] = {0.f};

	public float x, y, r; // position and radius of the sphere
	
	
	public MoveTracker(MoveController move) {
		super();
		super.set_mirror(1); // Mirror the tracker image horizontally by default
		calibrate(move);
	}
	
	/** 
	 * Print debug messages ? 
	 * 
	 * @param b true for printing debug message, false for silent run
	 */
	public void debug(boolean b) {
	  debug = b;
	}
	
	public PVector getPosition(MoveController move) {
		super.get_position(move, px, py, pr);
		
		// Unpack the float values from their respective arrays
		x = px[0];
		y = py[0];
		r = pr[0];
		
		return new PVector(x,y,r);
	}
	
	private void calibrate(MoveController move) {
		while (super.enable (move) != Status.Tracker_CALIBRATED);
		System.out.println("Calibration successful!");
	}
	
	
	public void updateAll() {
	  super.update_image();
	  super.update();
	  
	  PSMoveTrackerRGBImage image = super.get_image();
	  if (pixels == null) {
	        pixels = new byte[image.getSize()];
	  }
	  image.get_bytes(pixels);
	  if (trackerImage == null) {
		  trackerImage = new PImage(image.getWidth(), image.getHeight(), RGB); // should ideally use createImage() with a ref to the parent PApplet
	  }
	  trackerImage.loadPixels();
	  for (int i=0; i<trackerImage.pixels.length; i++) {
	      // We need to AND the values with 0xFF to convert them to unsigned values
		  trackerImage.pixels[i] = color(pixels[i*3] & 0xFF, pixels[i*3+1] & 0xFF, pixels[i*3+2] & 0xFF);
	  }
	 
	  trackerImage.updatePixels();
	}

	/** Flip the image for the camera horizontally
	 * 
	 * @param b 1 to enable 0 to disable
	 */
	public void setMirror(int b) {
		super.set_mirror(b);
	}
	
	
	/** Returns the current image from the tracker or null if none available
	 * 
	 * @return a PImage or null
	 */
	public Object getImage() {
	  if (trackerImage != null) {
		  return trackerImage;
	  }
	  else {
		 if(debug) System.out.println("MoveTracker.getImage() will return null: couldn't find trackerImage.");
	  }
	  return null;	
	}
	
	
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
}
