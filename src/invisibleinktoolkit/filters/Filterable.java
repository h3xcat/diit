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
 * An interface for filterable steganographic algorithms.
 * <P>
 * Algorithms that implement this interface use a filter
 * to provide adaptive steganography, which can be changed
 * to a different filter by using the methods in this interface.
 *
 * @author Kathryn Hempstalk.
 */
public interface Filterable{
	
	/**
	 * Sets the filter of this algorithm.
	 *
	 * @param filter The new filter.
	 */
	public void setFilter(Filter filter);
	
	/**
	 * Gets the current filter of this algorithm.
	 *
	 * @return The current filter being used.
	 */
	public Filter getFilter();
	
}
//end of class.
