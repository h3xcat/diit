/*
 *    Digital Invisible Ink Toolkit
 *    Copyright (C) 2006  K. Hempstalk	
 *
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 */

package invisibleinktoolkit.stego;

/**
 * An exception that occurs when no message is found on an image.
 * 
 * 
 * @author K. Hempstalk
 */
public class NoMessageException extends Exception{

	//VARIABLES
	
	/**
	 * Generated Serialization ID.
	 */
	private static final long serialVersionUID = -3478476548544952717L;

	//FUNCTIONS
	
	/**
	 * The message associated with this exception.
	 * 
	 * @return The message for this exception.
	 */
	public String toString(){
		return "No message found with current settings";
	}
}
//end of class.
