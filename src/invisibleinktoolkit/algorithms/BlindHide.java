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

package invisibleinktoolkit.algorithms;

import invisibleinktoolkit.stego.*;
import invisibleinktoolkit.util.Shot;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import java.awt.Frame;


import invisibleinktoolkit.algorithms.gui.StartEndWindow;
import java.io.OutputStream;


/**
 * A blindhide way of hiding data.
 * <P>
 * Blindly hiding the data is the simplest way to hide data
 * in a cover image.  The algorithm simply starts at point (0,0)
 * and continues to write data to the image until it runs out of
 * data to write.  This algorithm allows for a range of bits to be
 * selected, instead of just writing the least significant bits.
 *
 * @author Kathryn Hempstalk.
 */
public class BlindHide implements StegoAlgorithm{
	
	//CONSTRUCTORS
	
	/**
	 * Creates a new BlindHide algorithm.
	 *
	 * The start and end positions for possible hits should
	 * be zero based - i.e. the first bit is 0.  This is counting
	 * from LSB to MSB (0 is least significant, 7 most).  555 and
	 * 565 image formats should be in the range 0-4, other images
	 * should have 0-7.
	 *
	 * @param startbits The start bit position for possible hits.
	 * @param endbits The end bit position for possible bits.
	 * @throws IllegalArgumentException If the incorrect number of 
	 * start/end bits is passed.
	 */
	public BlindHide(int startbits, int endbits)
	throws IllegalArgumentException{
		
		//check the bit ranges.
		if(startbits > 6 || startbits < 0){
			throw new IllegalArgumentException
			("Start bit range not in range 0-6!");
		}
		if(endbits > 6 || endbits < 0){
			throw new IllegalArgumentException
			("End bit range not in range 1-6!");
		}
		if(startbits > endbits){
			throw new IllegalArgumentException
			("End bit range must be higher than start range!");
		}
		
		
		//assign in the start and end bits.
		mStartBits = startbits;
		mEndBits = endbits;
		mCountBits = 0;
		
	}
	
	/**
	 * Creates a new blindhide algorithm with default bit ranges
	 * of 0 and 0 (1 bits).
	 *
	 * @throws IllegalArgumentException Actually won't be thrown.
	 */
	public BlindHide() throws IllegalArgumentException{
		this(0, 0);
	}
	
	
	//FUNCTIONS
	
	/**
	 * Encodes an image with the given message.  The random seed
	 * can be anything - it is not used in this algorithm.
	 *
	 * @param message The message to embed.
	 * @param cimage The image to hide the message in.
	 * @param seed The seed to the random number generator (not used).
	 * @return An image containing the embedded message.
	 * @throws IOException When the message is not properly finished.
	 * @throws IllegalArgumentException When the message is too big.
	 */
	public StegoImage encode(InsertableMessage message, CoverImage cimage,
			long seed) throws 
			IOException 
			,IllegalArgumentException{
		
		//check the message fill actually fit
		if(!this.willMessageFit(message, cimage)){
			throw new IllegalArgumentException
			("Message is too big for this image!");
		}
		
		
		Shot sh;
		mCountBits = 0;
		int messagesize = (int) message.getSize();
		
		Random rgen = new Random(seed);
		
		//put the size in the first 32 bits
		for(int i = 0; i < 32; i++){
			//generate a valid shot
			sh = getShot(cimage.getImage().getHeight(), 
					cimage.getImage().getWidth());
			
			//put in the next size bit...
			boolean bit = ((messagesize >> i) & 0x1) == 0x1;
			
			if(!mLSBMatch){
				cimage.setPixelBit(sh.getX(),
					sh.getY(),
					sh.getLayer(),
					sh.getBitPosition(),
					bit);
			}else{
				//need to get the value of the colour
				//subtract 1, and update pixel.
				cimage.matchPixelBit(sh.getX(),
						sh.getY(),
						sh.getLayer(),
						8,
						bit,
						rgen.nextBoolean());
			}
		}
		
		//now we can start embedding the message into the cover
		while(message.notFinished()){
			
			sh = getShot(cimage.getImage().getHeight(), 
					cimage.getImage().getWidth());
			
			boolean bit = message.nextBit();
			if(!mLSBMatch){
				cimage.setPixelBit(sh.getX(),
					sh.getY(),
					sh.getLayer(),
					sh.getBitPosition(),
					bit);
			}else{
				//need to get the value of the colour
				//subtract 1, and update pixel.
				cimage.matchPixelBit(sh.getX(),
						sh.getY(),
						sh.getLayer(),
						8,
						bit,
						rgen.nextBoolean());
			}			
		}
		
		//now the message is hidden inside the image.
		return new StegoImage(cimage.getImage());
		
	}
	
	
	/**
	 * Retrieves a message from a steganographic image.
	 *
	 * @param simage The stego image to retreive the message from.
	 * @param seed The seed to the image. (null)
	 * @param stream The stream for output message
	 * @return The message retrieved.
	 * @throws IOException If an I/O error occurred.
	 */
	public RetrievedMessage decode(StegoImage simage, long seed, OutputStream stream)
	throws IOException, NoMessageException{
		
		Shot sh;
		int size = 0;
		mCountBits = 0;
		
		//get the size - in the first 32 hidden bits
		for(int i = 0; i < 32; i++){
			
			sh = getShot(simage.getImage().getHeight(), 
					simage.getImage().getWidth());
			
			int bit =  simage.getPixelBit(sh.getX(),
					sh.getY(),
					sh.getLayer(),
					sh.getBitPosition());
			size = size << 1 | bit;
			
		}
		
		//reverse it as it was retrieved backwards
		int size2 = 0;
		for(int j = 0; j < 32; j++){
			size2 = size2 << 1 | ((size >> j) & 0x1);
		}
		
		//multiply by 8 to get then number of bits
		size2 = size2 * 8;
		
		//make sure that the message isn't bigger than it's supposed to be
		long imagespace = (((simage.getImage().getWidth()
				* simage.getImage().getHeight()) * simage.getLayerCount())
				* ((mEndBits - mStartBits) + 1));
		if(size2 >= imagespace || size2 < 0)
			throw new NoMessageException();
		
		RetrievedMessage rmess = new RetrievedMessage(stream);
		
		//start retrieving and writing out the message
		for(int k = 0; k < size2; k++){
			
			sh = getShot(simage.getImage().getHeight(), 
					simage.getImage().getWidth());
			
			rmess.setNext( (simage.getPixelBit(sh.getX(), 
					sh.getY(), 
					sh.getLayer(),
					sh.getBitPosition())
			) == 0x1);			
			
		}
		
		rmess.close();
		return rmess;
	}
	
	
	/**
	 * Gets the next shot on the image.
	 *
	 * @param height The height of the image.
	 * @param width The width of the image.
	 * @return The next shot to make.
	 */
	private Shot getShot(int height, int width){
		//get the number of rows written so far...
		int bitsperpixel = (mEndBits - mStartBits) + 1;
		if(height * width * bitsperpixel * 3 < mCountBits)
			return null;
		int rangeupto = (int)(mCountBits % (bitsperpixel * 3));
		int xrow = (int)(((mCountBits - rangeupto)/(bitsperpixel * 3)) % width);
		int yrow = (int)((((mCountBits - rangeupto)/(bitsperpixel * 3)) - xrow)
				/ width);			 
		Shot sh = new Shot(xrow, yrow, rangeupto % bitsperpixel, 
				((rangeupto - (rangeupto % bitsperpixel)) / bitsperpixel));
		mCountBits++;
		return sh;
	}
	
	
	/**
	 * Gets whether a message will fit inside a given cover image
	 *
	 * @param message The message to hide.
	 * @param image The cover image to hide in.
	 * @return True if the message will fit, false otherwise.
	 * @throws IOException If an I/O error occurred.
	 */
	public boolean willMessageFit(InsertableMessage message,
			CoverImage image) throws IOException{
		
		//get the size of the image
		int imgX = image.getImage().getWidth(); 
		int imgY = image.getImage().getHeight();
		
		//check the size of the image on disk
		long imagespace = (((imgX * imgY) * image.getLayerCount())
				* ((mEndBits - mStartBits) + 1));
		
		int messagesize = ((int) message.getSize() * 8) + 50;
		
		return (messagesize <= imagespace);
	}
	
	
	/**
	 * Outputs a simulation of where it is writing to.
	 *
	 * @param message The message to simulate writing of.
	 * @param simage The cover image to simulate on.
	 * @param seed The seed to the algorithm.
	 * @return A black and white map of where the message will be hidden.
	 * @throws IOException If there was an I/O error with the message.
	 */
	public BufferedImage outputSimulation(InsertableMessage message, 
			CoverImage simage, 
			long seed) 
	throws IOException, IllegalArgumentException{
		
		//check the message fill actually fit
		if(!this.willMessageFit(message, simage)){
			throw new IllegalArgumentException
			("Message is too big for this image!");
		}
		
		
		Shot sh;
		mCountBits = 0;
		
		BufferedImage image = simage.getImage();
//		make the whole image black...
		int height = image.getHeight();
		int width = image.getWidth();
		int black = 0x0;
		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				image.setRGB(j, i, black);
			}
		}
		
		//"put" the size in the first 32 bits
		for(int i = 0; i < 32; i++){
			//generate a valid shot
			sh = getShot(height, width);
			
			//put in the next size bit...
			image.setRGB(sh.getX(), sh.getY(), 
					decreaseDarkness(image.getRGB(sh.getX(), sh.getY())));
		}
		
		//now we can start "embedding" the message into the cover
		while(message.notFinished()){
			sh = getShot(height, width); 
			image.setRGB(sh.getX(), sh.getY(), 
					decreaseDarkness(image.getRGB(sh.getX(), sh.getY())));
			message.nextBit();			
			
		}
		
		//now the message is "hidden" inside the image.
		return image;
	}
	
	
	/**
	 * Increases the darkness of a pixel.
	 * 
	 * @param colour The pixel's original colour
	 * @return The (whiter) colour of the pixel.
	 */
	private int decreaseDarkness(int colour){
		return (int)(colour << 1 | 0x0f0f0f0f);	
	}
	
	/**
	 * Gets the start position of the hiding.
	 *
	 * @return The start position for hiding.
	 */
	public int getStartBits(){
		return mStartBits;
	}
	
	/**
	 * Gets the end position of the hiding.
	 *
	 * @return The end position for hiding.
	 */
	public int getEndBits(){
		return mEndBits;
	}
	
	/**
	 * Sets the end position for hiding.
	 *
	 *@param newend The new end position for hiding.
	 */
	public void setEndBits(int newend){
		mEndBits = newend;
	}
	
	/**
	 * Sets the start position for hiding.
	 *
	 * @param newstart The new start position for hiding.
	 */
	public void setStartBits(int newstart){
		mStartBits = newstart;
	}
	
	/**
	 * Pops up a window to alter the configuration.
	 *
	 * @param parent The parent frame to display this within.
	 */
	public void openConfigurationWindow(Frame parent){
		new StartEndWindow(parent, this);
		parent.repaint();
	}
	
	/**
	 * Returns text explaining what this algorithm does.
	 * 
	 * @return What this algorithm does.
	 */
	public String explainMe(){
		return "Starts writing at (0,0) and moves along each pixel,\n"
				+ "colour and bit in scan lines across the image.  Uses\n" +
				"pure steganography.";
	}
	/**
	 * Whether LSB Matching should be used.
	 * 
	 * @param shouldMatch Whether to use LSB or not.
	 */
	public void setMatch(boolean shouldMatch){
		mLSBMatch = shouldMatch;
	}	
	
	/**
	 * Gets whether this algorithm is using LSB matching or not.
	 * 
	 * @return Whether this algorithm will use LSB matching.
	 */
	public boolean getMatch(){
		return mLSBMatch;
	}
		
	//VARIABLES
	
	/**
	 * Whether to use LSB Matching or not.
	 */
	private boolean mLSBMatch;
	
	/**
	 * The start range for writable bits.
	 */
	private int mStartBits;
	
	/**
	 * The end range for writable bits.
	 */
	private int mEndBits;
	
	/**
	 * Counts the number of bits that have been written.
	 */
	private long mCountBits;	

}
//end of class.
