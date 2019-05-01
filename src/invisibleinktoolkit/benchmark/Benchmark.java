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
 * An interface to a benchmarking module.
 *
 * @author Kathryn Hempstalk
 */
public interface Benchmark{
	
	/**
	 * Calculates out the value of the benchmark.
	 *
	 * @param orig The original image.
	 * @param stego The stego image.
	 * @return A benchmark value on how well hidden the information
	 * is in the stego image.
	 */
	public double calculate(BufferedImage orig, BufferedImage stego);
	
	
}
//end of interface.
