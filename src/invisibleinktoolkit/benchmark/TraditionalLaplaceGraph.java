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
import invisibleinktoolkit.filters.TraditionalLaplace;
import invisibleinktoolkit.filters.FilteredPixel;
import invisibleinktoolkit.filters.FPComparator;
import java.util.Arrays;


/**
 * A Traditional Laplacian Graph.
 * <P>
 * This class provides functionality to output the laplacian graph
 * of a given image.  All the classes run a laplacian filter of the given
 * image, outputting it in the various formats as specified.
 * <P>
 * This class is a traditional laplace graph - that is, it contains negative
 * values and works on black and white.
 * <P>
 * 
 * <B>This class will have no effect if instantiated.<\B>
 *
 * @author Kathryn Hempstalk
 */
public class TraditionalLaplaceGraph{
			
	/**
	 * Outputs a laplace graph in csv format.
	 *
	 * The CSV file will be comma separated, and is 
	 * written to the specified place on disk.
	 *
	 * @param image The image to create the laplace graph of.
	 * @return A string representation of the laplace graph in
	 * the format of CSV.
	 */
	public static String getCSVGraph(BufferedImage image) throws Exception{
		StringBuffer sb = new StringBuffer();
		sb.append("\"Frequency\",\"Laplace Value\"\n");
		double [][] graph = getGraph(image);
		for(int i = 0; i < graph.length; i++){
			sb.append(graph[i][1] + "," + graph[i][0] + "\n");
		}
		
		sb.append("\n\n");
		return sb.toString();
	}
	
	
	/**
	 * Gets the traditional laplace graph of an image.  The image
	 * is assumed to be able to be translated into black and white.
	 * The values in the graph can be negative.
	 * 
	 * @param image The image to get the graph of.
	 * @return The graph of the image.
	 */
	public static double[][] getGraph(BufferedImage image) throws Exception{
		
		TraditionalLaplace filter = new TraditionalLaplace(0, 8);
		
		//set up the image for the filter
		filter.setImage(image);
		
		//filter the image
		FilteredPixel[] fparray = 
			new FilteredPixel[image.getWidth() * image.getHeight()];
		for(int i = 0; i < image.getWidth(); i++){
			for(int j = 0; j < image.getHeight(); j++){
				fparray[(i * image.getHeight()) + j] = 
					new FilteredPixel(i, j, 
							filter.getValue(i, j));
				}
		} 
		
		//sort the filter results
		//is in ascending order - low at start, high at end
		Arrays.sort(fparray, new FPComparator());
		
		//now for each individual filter result, we count how many we have
		
		//first find out how many different values we have
		int numdistinct = 1;
		for(int i = 1; i < fparray.length; i++){
			if(fparray[i].getFilterValue() != fparray[i - 1].getFilterValue())
				numdistinct++;
		}
		
		//now we create an array to hold the filter values and their counts
		double [][] results = new double[numdistinct][2];
		results[0][0] = fparray[0].getFilterValue();
		results[0][1] = 1;
		int j = 0;
		
		//now we fill up the array
		for(int i = 0; i < fparray.length; i++){
			if(results[j][0] != fparray[i].getFilterValue()){
				j++;
				results[j][0] = fparray[i].getFilterValue();
				results[j][1] = 1;
			}else{
				results[j][1]++;
			}
		}
		
		//now normalise the graph
		for(int i = 0; i < results.length; i++){
			results[i][1] = results[i][1] / fparray.length;
		}
		
		//graph produced, return results
		return results;
	}
	
	
	
	
}//end of class
