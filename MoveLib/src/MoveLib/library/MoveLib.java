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

package MoveLib.library;

import processing.core.*;

// import io.thp.psmove.*;

// import MoveLib.library.MoveController;
import MoveLib.library.MoveManager;

/**
 * Call MoveLib to . 
 * 
 * @example Hello
 * 
 * (the tag @example followed by the name of an example included in folder 'examples' will
 * automatically include the example in the javadoc.)
 *
 */

public class MoveLib  implements MoveConstants {
	
	// myParent is a reference to the parent sketch
	PApplet myParent;

	int myVariable = 0;
	
	public final static String VERSION = "##library.prettyVersion##";
	

	/**
	 * Constructor for the MoveLib object
	 * 
	 * @example Hello
	 * @param theParent
	 */
	public MoveLib(PApplet theParent) {
		myParent = theParent;
		welcome();
	}
	
	/**
	 * 
	 * @return MoveManager
	 */
	public MoveManager createManager() {
		return new MoveManager();
	}
	
	/**
	 * 
	 * @param debug
	 * 			pass 1 for debug messages, 0 for silent run
	 * @return MoveManager
	 */
	public MoveManager createManager(int mode) {
		return new MoveManager(mode);
	}
	
	
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

