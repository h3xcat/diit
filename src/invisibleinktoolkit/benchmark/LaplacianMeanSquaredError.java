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
import invisibleinktoolkit.filters.Laplace;

/**
 * A measure of laplacian mean squared error.
 *
 *
 * @author Kathryn Hempstalk.
 */
public class LaplacianMeanSquaredError 
extends PixelBenchmark 
implements Benchmark{
	
	
	/**
	 * Calculates the laplacian mean squared error of two images.
	 * The formula used is:
	 * <BR>
	 * LMSE = sum( (lp(p(x,y)) - lp(p~(x,y))) ^ 2) / sum(lp(p(x,y)) ^ 2)
	 * <P>
	 * Where p(x,y) is a pixel in the original image, and p~(x,y) is a
	 * pixel in the stego image.  Colours are worked out together in
	 * the Laplace filter (lp()).
	 *
	 * @param original The original image.
	 * @param stego The stego image.
	 * @return A benchmark value on how well hidden the information
	 * is in the stego image.
	 */
	public double calculate(BufferedImage original,
			BufferedImage stego){
		
		double totaldifference = 0, originaldiff = 0;
		Laplace origfilter = new Laplace(0,8);
		Laplace stegfilter = new Laplace(0,8);
		origfilter.setImage(original);
		stegfilter.setImage(stego);
		try{
			for(int i = 0; i < original.getHeight(); i++){
				for(int j = 0; j < original.getWidth(); j++){
					originaldiff += Math.pow(origfilter.getValue(j,i)
							- stegfilter.getValue(j,i), 2);
					totaldifference += (Math.pow(origfilter.getValue(j,i), 2));
				}
			}
		}catch(Exception e){
			System.out.println("Error filtering image.");
			e.printStackTrace();
		}
		
		if(originaldiff == 0 || totaldifference == 0)
			return 0;
		else
			return (originaldiff / totaldifference);
	}
	
	
	/**
	 * Returns the string representation of this measure - this
	 * is just the name of the measure.
	 *
	 * @return The name of the measure.
	 */
	public String toString(){
		return "Laplacian Mean Squared Error";
	}
}
