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

import java.awt.image.BufferedImage;


/**
 * A filter.
 *
 * Classes that implement this interface provide some sort of filter
 * on an image.  The higher the filter value, the more favourable the
 * given pixel is to change.
 *
 * @author Kathryn Hempstalk.
 */
public interface Filter{
	
	/**
	 * Gets the filter value of a given pixel position.
	 *
	 * @param x The x position of the pixel.
	 * @param y The y position of the pixel.
	 * @return The filter value of the pixel.
	 * @throws Exception When the image isn't known.
	 */
	public int getValue(int x, int y) throws Exception;
	
	/**
	 * Sets the filter's image to a given image.
	 *
	 * @param image The new image for the filter.
	 */
	public void setImage(BufferedImage image);
	
	/**
	 * Sets the starting range of the pixel bits to use to calculate the
	 * filter value.
	 *
	 * @param startrange The starting range of bits to filter.
	 */
	public void setStartRange(int startrange);
	
	/**
	 * Sets the end range of the pixel bits to use to calculate the filter
	 * value.
	 *
	 * @param endrange The end range of bits to filter.
	 */
	public void setEndRange(int endrange);
	
	/**
	 * Gets the starting range of the pixel bits to use to calculate the
	 * filter value.
	 *
	 * @return The starting range of bits to filter.
	 */
	public int getStartRange();
	
	/**
	 * Gets the end range of the pixel bits to use to calculate the
	 * filter value.
	 *
	 * @return The endrange of bits to filter.
	 */
	public int getEndRange();
	
}
