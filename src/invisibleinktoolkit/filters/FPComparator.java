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

import java.util.Comparator;

/**
 * A comparator for filtered pixels.
 * <P>
 * This comparator allows filtered pixels to be sorted
 * by using Arrays.sort().  This makes sorting pixels
 * a trivial problem.
 * 
 * @author Kathryn Hempstalk.
 */
public class FPComparator implements Comparator{
	
	/**
	 * Compares two objects to see if they are similar.
	 *
	 * A pixel is first compared by it's filter value.  To prevent deadlock
	 * pixels with the same value, the x and y positions are used.  Therefore
	 * to get a value of zero, the pixels must be in the same positions, as
	 * well as having the same filter value.
	 * 
	 * @see java.util.Comparator
	 * @param o1 The first object to be compared.
	 * @param o2 The second object to be compared.
	 * @return 0 if they are the same, 
	 * negative if o2 is bigger, positive if o1 is bigger.
	 */
	public int compare(Object o1, Object o2){
		FilteredPixel a = (FilteredPixel) o1;
		FilteredPixel b = (FilteredPixel) o2;
		
		//break a tie
		if(a.getFilterValue() == b.getFilterValue()){
			if(a.getX() == b.getX()){
				if(a.getY() == b.getY())
					return 0;
				else if (a.getY() > b.getY())
					//bigger y value, make it "bigger"
					return 20;
				else
					return -20;
			}else if (a.getX() > b.getX())
				//bigger x value, make it "bigger"
				return 50;
			else
				return -50;
			
		}
		else if (a.getFilterValue() < b.getFilterValue())
			return -100;
		else
			return 100;
		
	}
	
	/**
	 * Compares two objects to see if they are equal.
	 *
	 * 
	 * @see java.util.Comparator
	 * @param o1 The first object to be compared.
	 * @param o2 The second object to be compared.
	 * @return True if they are equal, false otherwise.
	 */
	public boolean equals(Object o1, Object o2){
		FilteredPixel a = (FilteredPixel) o1;
		FilteredPixel b = (FilteredPixel) o2;
		if (a.getFilterValue() == b.getFilterValue() &&
				a.getX() == b.getX() && a.getY() == b.getY())
			return true;
		else
			return false;
	}
	
}
//end of class.
