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

package invisibleinktoolkit.benchmark;

import java.awt.image.BufferedImage;

/**
 * A measure of average absolute difference.
 *
 *
 * @author Kathryn Hempstalk.
 */
public class AverageAbsoluteDifference 
extends PixelBenchmark 
implements Benchmark{
	
	
	/**
	 * Calculates out the value of the average absolute difference
	 * between the two images.  The average absolute difference is: <BR>
	 * AD = (1 / XY) * sum(|p(x,y) - p~(x,y)|)
	 * <P>
	 * Where X and Y are the image width and height, and p(x,y) is the value
	 * of a given pixel in the original, and p~(x,y) is the value of a pixel
	 * in the stego image.  These values are broken down into colours for this 
	 * formula, so it is applicable to 24 bit colour images.
	 *
	 * @param original The original image.
	 * @param stego The stego image.
	 * @return A benchmark value on how well hidden the information
	 * is in the stego image.
	 */
	public double calculate(BufferedImage original,
			BufferedImage stego){
		
		double ratio = (original.getHeight() * original.getWidth());
		double totaldifference = 0;
		int origpix, stegpix;
		for(int i = 0; i < original.getHeight(); i++){
			for(int j = 0; j < original.getWidth(); j++){
				origpix = original.getRGB(j,i);
				stegpix = stego.getRGB(j,i);
				totaldifference += (Math.abs(getRed(origpix) 
						- getRed(stegpix))				
						+ Math.abs(getGreen(origpix)
								- getGreen(stegpix))
								+ Math.abs(getBlue(origpix)
										- getBlue(stegpix)));
			}
		}
		
		return totaldifference / ratio;
	}
	
	/**
	 * Returns the string representation of this measure - this
	 * is just the name of the measure.
	 *
	 * @return The name of the measure.
	 */
	public String toString(){
		return "Average Absolute Difference";
	}
	
}
