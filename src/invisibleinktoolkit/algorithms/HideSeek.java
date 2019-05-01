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
import invisibleinktoolkit.util.PRandom;
import java.io.IOException;
import java.util.Random;
import java.awt.image.BufferedImage;
import java.awt.Frame;

import invisibleinktoolkit.algorithms.gui.StartEndWindow;
import java.io.OutputStream;

/**
 * A hide and seek way of hiding data.
 * <P>
 * This is based on the tool "Hide and Seek 5.0" which uses a random
 * number generator to hide data inside an image.  However, HS 5.0 hides
 * the seed to the random number generator in the header of the file, here 
 * it is not hidden at all.  The header is not changed in any way.  Instead,
 * the first 32 bits that are written contain the size of the message that is 
 * hidden.  This algorithm can also write to a range of bits, instead of just
 * the most least significant bits.
 * <P>
 * Essentially, random noise is embedded in the picture in the form of 
 * the message.
 *
 * @author Kathryn Hempstalk.
 */
public class HideSeek implements StegoAlgorithm{
	
	//CONSTRUCTORS
	
	/**
	 * Creates a new HideSeek algorithm using
	 * default bit ranges of 0 and 0 (1 bits).
	 *
	 * @throws IllegalArgumentException This will actually not be thrown.
	 */
	public HideSeek() throws IllegalArgumentException{
		this(0,0);
	}
	
	/**
	 * Creates a new HideSeek algorithm.
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
	public HideSeek(int startbits, int endbits)
	throws IllegalArgumentException{
		
		//check the bit ranges.
		if(startbits > 7 || startbits < 0){
			throw new IllegalArgumentException
			("Start bit range not in range 0-7!");
		}
		if(endbits > 7 || endbits < 0){
			throw new IllegalArgumentException
			("End bit range not in range 1-7!");
		}
		if(startbits > endbits){
			throw new IllegalArgumentException
			("End bit range must be higher than start range!");
		}
		
		
		//assign in the start and end bits.
		mStartBits = startbits;
		mEndBits = endbits;
		
	}
	
	
	//FUNCTIONS
	
	/**
	 * Encodes an image with the given message, using the seed
	 * to initialise the random number generator.
	 *
	 * @param message The message to embed.
	 * @param cimage The image to hide the message in.
	 * @param seed The seed to the random number generator.
	 * @return An image containing the embedded message.
	 * @throws IOException When the message is not properly finished.
	 * @throws IllegalArgumentException When the message is too big.
	 */
	public StegoImage encode(InsertableMessage message, CoverImage cimage,
			long seed) throws 
			IOException 
			,IllegalArgumentException{
		
		//get the size of the image
		int imgX = cimage.getImage().getWidth(); 
		int imgY = cimage.getImage().getHeight();
		
		//hold using an array - initialise it to be false.
		boolean haveWritten[][][][] = new boolean[imgX][imgY][3][8];
		for(int i = 0; i < imgX; i++){
			for(int j = 0; j < imgY; j++){
				for(int k = 0; k < 8; k++){
					haveWritten[i][j][0][k] = false;
					haveWritten[i][j][1][k] = false;
					haveWritten[i][j][2][k] = false;
				}					
			}
		}
		
		//check it will fit...
		if(!this.willMessageFit(message, cimage)){
			throw new IllegalArgumentException
			("Message is too big for this image!");
		}
		
		//initialise some variables
		PRandom rgen = new PRandom(seed, imgX, imgY,
				cimage.getLayerCount(),
				mStartBits,
				mEndBits);
		
		Shot sh;
		
		int messagesize = (int) message.getSize();
		
		//put the size in the first 32 bits
		Random aran = new Random(seed);
		
		for(int i = 0; i < 32; i++){
			//generate a valid shot
			sh = rgen.getShot();
			
			while(haveWritten[sh.getX()][sh.getY()][sh.getLayer()][sh.getBitPosition()]){
				sh = rgen.getShot();
			}
			//save it
			haveWritten[sh.getX()][sh.getY()][sh.getLayer()][sh.getBitPosition()] = true;
			
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
						aran.nextBoolean());
			}
		}
		
		//now we can start embedding the message into the cover
		while(message.notFinished()){
			
			sh = rgen.getShot();
			while(haveWritten[sh.getX()][sh.getY()][sh.getLayer()][sh.getBitPosition()]){
				sh = rgen.getShot();
				//while(mShotsMade.containsKey(sh.toString())){
			}
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
						aran.nextBoolean());
			}
			
			haveWritten[sh.getX()][sh.getY()][sh.getLayer()][sh.getBitPosition()] = true;
			
		}
		
		//now the message is hidden inside the image.
		return new StegoImage(cimage.getImage());
		
	}
	
	
	/**
	 * Retrieves a message from a steganographic image.
	 *
	 * @param simage The stego image to retreive the message from.
	 * @param seed The seed to the message.
	 * @param stream The stream for message output.
	 * @return The message retrieved.
	 * @throws IOException If an I/O error occurred.
	 */
	public RetrievedMessage decode(StegoImage simage, long seed, OutputStream stream)
	throws IOException, NoMessageException{
		
		//get the size of the image
		int imgX = simage.getImage().getWidth(); 
		int imgY = simage.getImage().getHeight();
		
		//hold using an array - initialise it to be false.
		boolean haveWritten[][][][] = new boolean[imgX][imgY][3][8];
		for(int i = 0; i < imgX; i++){
			for(int j = 0; j < imgY; j++){
				for(int k = 0; k < 8; k++){
					haveWritten[i][j][0][k] = false;
					haveWritten[i][j][1][k] = false;
					haveWritten[i][j][2][k] = false;
				}
			}
		}
		
		//initialise some variables
		PRandom rgen = new PRandom(seed, imgX, imgY,
				simage.getLayerCount(),
				mStartBits,
				mEndBits);
		
		
		Shot sh;
		int size = 0;
		
		//get the size - in the first 32 hidden bits
		for(int i = 0; i < 32; i++){
			
			sh = rgen.getShot();
			
			while(haveWritten[sh.getX()][sh.getY()][sh.getLayer()][sh.getBitPosition()]){
				sh = rgen.getShot();
			}
			
			haveWritten[sh.getX()][sh.getY()][sh.getLayer()][sh.getBitPosition()] = true;
			
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
		
		size2 = size2 * 8;
		
		//make sure that the message isn't bigger than it's supposed to be
		long imagespace = (((simage.getImage().getWidth()
				* simage.getImage().getHeight())
				* simage.getLayerCount())
				* ((mEndBits - mStartBits) + 1));
		if(size2 >= imagespace || size2 < 0)
			throw new NoMessageException();
		
		RetrievedMessage rmess = new RetrievedMessage(stream);
		
		//begin to recover the message
		for(int k = 0; k < size2; k++){
			sh = rgen.getShot();
			
			
			while(haveWritten[sh.getX()][sh.getY()][sh.getLayer()][sh.getBitPosition()]){
				sh = rgen.getShot();
			}
			
			haveWritten[sh.getX()][sh.getY()][sh.getLayer()][sh.getBitPosition()] = true;
			
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
		
		//intialise some variables
		Shot sh;
		int height = simage.getImage().getHeight();
		int width = simage.getImage().getWidth();
		
		//make sure the shot list is empty
		//hold using an array - initialise it to be false.
		boolean haveWritten[][][][] = new boolean[width][height][3][8];
		for(int i = 0; i < width; i++){
			for(int j = 0; j < height; j++){
				for(int k = 0; k < 8; k++){
					haveWritten[i][j][0][k] = false;
					haveWritten[i][j][1][k] = false;
					haveWritten[i][j][2][k] = false;
				}
			}
		}
		
		
		PRandom rgen = new PRandom(seed, width, height,
				simage.getLayerCount(),
				mStartBits,
				mEndBits);
		
		
		//make the whole image black...
		BufferedImage image = simage.getImage();
		int black = 0x0;
		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++){
				image.setRGB(j, i, black);
			}
		}
		
		
		
		//pretent to put the size in the first 32 bits
		for(int i = 0; i < 32; i++){
			//generate a valid shot
			
			sh = rgen.getShot();
			while(haveWritten[sh.getX()][sh.getY()][sh.getLayer()][sh.getBitPosition()]){
				sh = rgen.getShot();
			}
			
			haveWritten[sh.getX()][sh.getY()][sh.getLayer()][sh.getBitPosition()] = true;
			
			//put in the next size bit...
			image.setRGB(sh.getX(), sh.getY(), 
					decreaseDarkness(image.getRGB(sh.getX(), sh.getY())));
		}
		
		//now we can start "embedding" the message into the cover
		while(message.notFinished()){
			sh = rgen.getShot();
			
			while(haveWritten[sh.getX()][sh.getY()][sh.getLayer()][sh.getBitPosition()]){
				sh = rgen.getShot();
			}
			
			haveWritten[sh.getX()][sh.getY()][sh.getLayer()][sh.getBitPosition()] = true;
			
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
		return "HideSeek randomly picks a pixel/colour/bit and hides there.\n" +
				"If it picks a bit it's written to before, it will skip over it\n" +
				"and go onto to the next randomly selected bit.";
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
	
}
//end of class.
