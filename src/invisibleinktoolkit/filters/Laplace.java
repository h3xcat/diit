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
 * A laplace filter.
 * <P>
 * A laplace filter is an image filter that can tell how
 * much an image changes.  On a single pixel this filter can
 * give a value of how close it is in colour to it's neighbours.
 * On an entire image it can evaluate areas where pixels are unlike
 * their neighbours.
 *
 * @author Kathryn Hempstalk.
 */
public class Laplace implements Filter{
	
	//CONSTRUCTORS
	/**
	 * Creates a new laplace filter, with default
	 * options of no image, and pixels to filter being 4 -> 8.
	 *
	 */
	public Laplace(){
		this(null,1,8);
	}
	
	/**
	 * Creates a new laplace filter, with no image, but
	 * given starting and end filter positions.
	 *
	 * @param startbits The start range of bits to use to calculate out 
	 * filter values.
	 * @param endbits The end range of bits to use to calculate out
	 * filter values.
	 *
	 */
	public Laplace(int startbits, int endbits){
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
	public Laplace(BufferedImage image, int startbits, int endbits){
		mStartRange = startbits;
		mEndRange = endbits;
		mImage = image;
	}
	
	
	//FUNCTIONS
	
	/**
	 * Produces a laplace filter value on a given pixel.
	 * <P>
	 * A laplace filter (in this case) subtracts the pixel
	 * value above, below and to the left and right of a given
	 * pixel from 4 times the pixels value.  This is a good estimation
	 * of both the edges and noise in the image.  In this case, each
	 * colour is worked out separately, and their absolute values are
	 * added together in the final step. 
	 *
	 * @param xpos The x position of the pixel to filter.
	 * @param ypos The y position of the pixel to filter.
	 * @return The filter value for the given pixel.
	 */
	public int getValue(int xpos, int ypos) throws Exception{
		
		if(mImage == null)
			throw new Exception("No image has been set!");
		
		//initialise all the local variables
		int pixelval = mImage.getRGB(xpos, ypos);
		int imgheight = mImage.getHeight();
		int imgwidth = mImage.getWidth();
		int leftpix = 0, uppix = 0, rightpix = 0, downpix = 0;
		int pixcount = 4;
		
		//work out what the pixel values are
		//(only add pixels that exist)
		if (xpos <= 0)
			pixcount--;
		else leftpix = mImage.getRGB(xpos-1, ypos);
		if(xpos >= imgwidth-1)
			pixcount--;
		else rightpix = mImage.getRGB(xpos + 1, ypos);
		if (ypos <= 0)
			pixcount--;
		else uppix = mImage.getRGB(xpos, ypos - 1);
		if (ypos >= imgheight - 1)
			pixcount--;
		else downpix = mImage.getRGB(xpos, ypos + 1);
		
		//work out the colours individually
		int reddiff, greendiff, bluediff;
		reddiff = (getRed(pixelval) * pixcount) -
		(getRed(leftpix) + getRed(rightpix) 
				+ getRed(uppix) + getRed(downpix));
		greendiff = (getGreen(pixelval) * pixcount) - 
		(getGreen(leftpix) + getGreen(rightpix)
				+ getGreen(uppix) + getGreen(downpix));
		bluediff = (getBlue(pixelval) * pixcount) - 
		(getBlue(leftpix) + getBlue(rightpix)
				+ getBlue(uppix) + getBlue(downpix));
		
		//return the results...
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
	
	
	// VARIABLES
	
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
