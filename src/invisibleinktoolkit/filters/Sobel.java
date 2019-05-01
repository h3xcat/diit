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
 * A sobel edge detector.
 *
 * A sobel edge detector is very similar to a laplace filter.  It 
 * detects the edges in a picture by using a pair of convolution
 * masks and calculating out the magnitude of the gradients of
 * these masks.  
 *
 * @author Kathryn Hempstalk.
 */
public class Sobel implements Filter{
	
	//CONSTRUCTORS
	/**
	 * Creates a new sobel edge filter, with default
	 * options of no image, and pixels to filter being 4 -> 8.
	 *
	 */
	public Sobel(){
		this(null, 1, 8);
	}
	
	/**
	 * Creates a new sobel edge filter, with no image, but
	 * given starting and end filter positions.
	 *
	 * @param startbits The start range of bits to use to calculate out 
	 * filter values.
	 * @param endbits The end range of bits to use to calculate out
	 * filter values.
	 *
	 */
	public Sobel(int startbits, int endbits){
		this(null, startbits, endbits);
	}
	
	/**
	 * Creates a new sobel edge filter with given image and
	 * start and end filter positions.
	 *
	 * @param image The image to filter.
	 * @param startbits The start range of bits to use to calculate out 
	 * filter values.
	 * @param endbits The end range of bits to use to calculate out
	 * filter values.
	 *
	 */
	public Sobel(BufferedImage image, int startbits, int endbits){
		mStartRange = startbits;
		mEndRange = endbits;
		mImage = image;
	}
	
	
	//FUNCTIONS
	
	/**
	 * Produces a Sobel filter value on a given pixel.
	 * <P>
	 * A sobel filter uses a pair of convolution masks on the image
	 * to decide if the pixel is part of an edge of the image.  It is 
	 * very similar to a Laplace filter, but produces different results.
	 * <P>
	 * In this filter, the gradients of each colour are worked out,
	 * and added together to produce a positive filter value.  
	 *
	 * @param xpos The x position of the pixel to filter.
	 * @param ypos The y position of the pixel to filter.
	 * @return The filter value for the given pixel.
	 */
	public int getValue(int xpos, int ypos) throws Exception{
		
		if(mImage == null)
			throw new Exception("No image has been set!");
		
		//initialise all the local variables
		int imgheight = mImage.getHeight();
		int imgwidth = mImage.getWidth();
		int matrix1left = 0, matrix1leftup = 0, matrix1leftdown = 0,
		matrix1right = 0, matrix1rightup = 0, matrix1rightdown = 0;
		int matrix2up = 0, matrix2upleft = 0, matrix2upright = 0,
		matrix2down = 0, matrix2downleft = 0, matrix2downright = 0;
		
		if(xpos == 0 || ypos == 0 
				|| ypos == imgheight - 1 || xpos == imgwidth - 1)
			return 0;
		
		//work out what the pixel values are
		if (xpos > 0){
			matrix1left = mImage.getRGB(xpos - 1, ypos);
			if(ypos > 0){
				matrix1leftup = mImage.getRGB(xpos - 1, ypos - 1);
				matrix2upleft = mImage.getRGB(xpos - 1, ypos - 1);
			}
			if(ypos < imgheight - 1){
				matrix1leftdown = mImage.getRGB(xpos - 1, ypos + 1);
				matrix2downleft = mImage.getRGB(xpos - 1, ypos + 1);
			}
		}
		if(xpos < imgwidth - 1){
			matrix1right = mImage.getRGB(xpos + 1, ypos);
			if(ypos > 0){
				matrix1rightup = mImage.getRGB(xpos + 1, ypos - 1);
				matrix2upright = mImage.getRGB(xpos + 1, ypos - 1);
			}
			if(ypos < imgheight - 1){
				matrix1rightdown = mImage.getRGB(xpos + 1, ypos + 1);
				matrix2downright = mImage.getRGB(xpos + 1, ypos + 1);
			}
		}
		if (ypos > 0){
			matrix2up = mImage.getRGB(xpos, ypos - 1);
		}
		if (ypos < imgheight - 1){
			matrix2down = mImage.getRGB(xpos, ypos + 1);
		}
		
		//work out the colour differences...
		int reddiff, greendiff, bluediff;
		reddiff = (int)Math.sqrt( Math.pow((((getRed(matrix1right) * 2) 
				+ getRed(matrix1rightup) 
				+ getRed(matrix1rightdown)) - 
				((getRed(matrix1left) * 2) +
						getRed(matrix1leftup) + 
						getRed(matrix1leftdown))), 2) +
						Math.pow((((getRed(matrix2up) * 2) 
								+ getRed(matrix2upleft) 
								+ getRed(matrix2upright)) - 
								((getRed(matrix2down) * 2) +
										getRed(matrix2downleft) + 
										getRed(matrix2downright))), 2));
		
		greendiff = (int)Math.sqrt( Math.pow((((getGreen(matrix1right) * 2) 
				+ getGreen(matrix1rightup) 
				+ getGreen(matrix1rightdown)) - 
				((getGreen(matrix1left) * 2) +
						getGreen(matrix1leftup) + 
						getGreen(matrix1leftdown))), 2) +
						Math.pow((((getGreen(matrix2up) * 2) 
								+ getGreen(matrix2upleft) 
								+ getGreen(matrix2upright)) - 
								((getGreen(matrix2down) * 2) +
										getGreen(matrix2downleft) + 
										getGreen(matrix2downright))), 2));
		
		bluediff = (int)Math.sqrt( Math.pow((((getBlue(matrix1right) * 2) 
				+ getBlue(matrix1rightup) 
				+ getBlue(matrix1rightdown)) - 
				((getBlue(matrix1left) * 2) +
						getBlue(matrix1leftup) + 
						getBlue(matrix1leftdown))), 2) +
						Math.pow((((getBlue(matrix2up) * 2) 
								+ getBlue(matrix2upleft) 
								+ getBlue(matrix2upright)) - 
								((getBlue(matrix2down) * 2) +
										getBlue(matrix2downleft) + 
										getBlue(matrix2downright))), 2));
		
		//now return the total of these values
		return Math.abs(reddiff)
		+ Math.abs(greendiff) + Math.abs(bluediff);
		
	}
	
	
	/**
	 * Gets the red content of a pixel.
	 *
	 * @param pixel The pixel to get the red content of.
	 * @return The red content of the pixel (which is masked).
	 */
	protected int getRed(int pixel){
		return ((pixel >> 16) & this.getByteMask());
	}
	
	/**
	 * Gets the green content of a pixel.
	 *
	 * @param pixel The pixel to get the green content of.
	 * @return The green content of the pixel (which is masked).
	 */
	protected int getGreen(int pixel){
		return ((pixel >> 8) & this.getByteMask());
	}
	
	/**
	 * Gets the blue content of a pixel.
	 *
	 * @param pixel The pixel to get the blue content of.
	 * @return The blue content of the pixel (which is masked).
	 */
	protected int getBlue(int pixel){
		return ((pixel) & this.getByteMask());
	}
	
	/**
	 * Gets the masking to perform on a pixel.
	 * <P>
	 * The mask is used to make sure that the same filter
	 * values that were used originally, get the same value
	 * after the lower portion of the pixel is changed.
	 *
	 * @return An integer that is a mask for the pixel.
	 */
	protected int getByteMask(){
		int abyte = 0, abyte2 = 0;
		for(int i = 0; i < 8; i++){
			byte bit;
			if(i <= mEndRange && i >= mStartRange)
				bit = 1;
			else
				bit = 0;
			abyte = (byte)(abyte << 1 | bit);
		}
		for(int i = 0; i < 8; i++){
			abyte2 = abyte2 << 1 | ((abyte >> i) & 0x1);
		}
		return abyte2;
	}
	
	
	/**
	 * Sets the image this filter will work on.
	 *
	 * @param image The image to work on.
	 */
	public void setImage(BufferedImage image){
		mImage = image;
	}
	
	/**
	 * Sets the start range of pixels to work on.
	 * 
	 * @param startrange The new start range of pixels.
	 */
	public void setStartRange(int startrange){
		mStartRange = startrange;
	}
	
	/**
	 * Sets the end range of pixels to work on.
	 *
	 * @param endrange The new end range of pixels.
	 */
	public void setEndRange(int endrange){
		mEndRange = endrange;
	}
	
	/**
	 * Gets the starting range of the pixel bits to use to calculate the
	 * filter value.
	 *
	 * @return The starting range of bits to filter.
	 */
	public int getStartRange(){
		return mStartRange;
	}
	
	/**
	 * Gets the end range of the pixel bits to use to calculate the
	 * filter value.
	 *
	 * @return The endrange of bits to filter.
	 */
	public int getEndRange(){
		return mEndRange;
	}
	
	//VARIABLES
	
	/**
	 * The image this filter will be applied to.
	 */
	private BufferedImage mImage;
	
	/**
	 * The starting range of pixels to use.
	 */
	private int mStartRange;
	
	/**
	 * The end range of pixels to use.
	 */
	private int mEndRange;
}
//end of class.

