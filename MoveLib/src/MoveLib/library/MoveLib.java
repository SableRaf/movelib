/**
 * ##library.name##
 * ##library.sentence##
 * ##library.url##
 *
 * Copyright ##copyright## ##author##
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 * 
 * @author      ##author##
 * @modified    ##date##
 * @version     ##library.prettyVersion## (##library.version##)
 */

package movelib.library;

import java.lang.reflect.Method;

//import movelib.MoveManager;
//import movelib.MoveConstants;

import processing.core.*;

// import io.thp.psmove.*;


/**
 * Call MoveLib to initialize PS Move Controller support. 
 * 
 * @example Hello
 * 
 */

public class MoveLib  implements MoveConstants, PConstants {
	
	// parent is a reference to the parent sketch
	PApplet parent;
	
	Method moveButtonEvent;

	int myVariable = 0;
	
	public final static String VERSION = "##library.prettyVersion##";
	

	/**
	 * Constructor for the MoveLib object
	 * 
	 * @param theParent Current PApplet. In most cases, you will write movelib ml = new movelib(this);
	 */
	public MoveLib(PApplet theParent) {
		parent = theParent;
	}	
	
	/**
	 * 
	 * @return MoveManager
	 */
	public MoveManager createManager() {
		return new MoveManager();
	}
	
	
	/**
	 * @param debug
	 * 			pass 1 for debug messages, 0 for silent run
	 * @return MoveManager
	 */
	public MoveManager createManager(int mode) {
		return new MoveManager(mode);
	}
	
	
	/**
	 * Print out the presentation of the library.
	 */
	private void welcome() {
		System.out.println("##library.name## ##library.prettyVersion## by ##author##");
	}
	
	
	/**
	 * return the version of the library.
	 * 
	 * @return String
	 */
	public static String version() {
		return VERSION;
	}
}

