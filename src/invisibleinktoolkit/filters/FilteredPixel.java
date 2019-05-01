/*
 *    Digital Invisible Ink Toolkit
 *    Copyright (C) 2005  K. Hempstalk	
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

package invisibleinktoolkit.filters;

/**
 * A filtered pixel.
 * <P>
 * A filtered pixel has an x and y position (as it would
 * have on an image) and a filter value.
 *
 * @author Kathryn Hempstalk.
 */
public class FilteredPixel{
	
	//CONSTRUCTORS
	
	/**
	 * Creates a new filtered pixel.
	 *
	 * @param x The x position of the pixel.
	 * @param y The y position of the pixel.
	 * @param value The filter value of the pixel.
	 */
	public FilteredPixel(int x, int y, int value){
		mXPosition = x;
		mYPosition = y;
		mFilterValue = value;
	}
	
	
	//FUNCTIONS
	
	/**
	 * Gets the x position of the pixel.
	 *
	 * @return The x position of the pixel.
	 */
	public int getX(){
		return mXPosition;
	}
	
	/**
	 * Gets the y position of the pixel.
	 *
	 * @return The y position of the pixel.
	 */
	public int getY(){
		return mYPosition;
	}
	
	/**
	 * Gets the filter value of this pixel.
	 *
	 * @return The filter value of this pixel.
	 */
	public int getFilterValue(){
		return mFilterValue;
	}
	
	/**
	 * Gets a string representation of the filtered pixel.
	 *
	 * @return A string representation of the filtered pixel.
	 */
	public String toString(){
		return "X: " + mXPosition + " Y: " + mYPosition;
	}
	
	
	//VARIABLES
	
	/**
	 * The x position of the pixel.
	 */
	private int mXPosition;
	
	/**
	 * The y position of the pixel.
	 */
	private int mYPosition;
	
	/**
	 * The filter value for this pixel.
	 */
	private int mFilterValue;
	
}
//end of class.
