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
import invisibleinktoolkit.filters.*;

import java.io.IOException;
import java.awt.Frame;
import invisibleinktoolkit.algorithms.gui.StartEndFilterWindow;
import java.io.OutputStream;

import java.util.Random;
import java.util.TreeSet;


/**
 * A filter first way of hiding data.
 * <P>
 * This algorithm is the second extreme of BattleSteg (the first being
 * Hide and Seek).  It filters the image first, and hides in the filter
 * first (hence the name).  In this way the message is always hidden in the part
 * of the image deemed "best to hide".
 * <P>
 * This algorithm differs from the traditional FilterFirst in that it uses
 * dynamic programming to keep memory usage to a minimum.  Because of this,
 * it is not compatible with the traditional FilterFirst.
 *
 * @author Kathryn Hempstalk.
 */
public class DynamicFilterFirst implements StegoAlgorithm, Filterable{
	
	//INNER CLASS
	/**
	 * An internal representation of a shot generator
	 * which produces the next filter value in the chain
	 * to use.
	 *
	 * @author Kathryn Hempstalk.
	 */
	private class ShotPicker{
		
		//CONSTRUCTORS
		
		/**
		 * Creates a new shot picker, for a given image.
		 * The random seed is not used at this stage - but may
		 * be used in the future to help shuffle the array.
		 *
		 * @param seed The seed to initialise the generator.
		 * @param startrange The start range of bit positions.
		 * @param endrange The ending range of bit positions.
		 * @param image The image to figure out the ships for.
		 * @param filter The filter to use on the image.
		 * @param messagesize The size of the message (in bits).
		 * @param ignore The number of initial bits to ignore when providing a response.
		 */
		public ShotPicker(long seed, 
				int startrange, 
				int endrange,
				BufferedImage image,
				Filter filter,
				int messagesize, int ignore) throws IllegalArgumentException{
			
			//setup all the local variables
			mStartRange = startrange;
			mEndRange = endrange;
			filter.setStartRange(endrange + 1);
			filter.setEndRange(8);
			
			int numpix = (int) (messagesize / ((mEndRange + 1 - mStartRange) * 3));
			mBitCount = 0; 
			mColour = 0;
			mArrayPos = 0;
			
			for(int i = 0; i < ignore; i++){
				mBitCount++;
				if(mBitCount >= (mEndRange - mStartRange) + 1){
					mBitCount = 0;
					mColour++;
					if(mColour >= 3){
						mColour = 0;
						mArrayPos++;
					}
				}
			}
						
			
			try{
				//organise the filter (similar to picking ships)
				this.generateList(image, filter, numpix + 1);
			}catch(Exception e){
				e.printStackTrace();
				System.exit(0);
			}
		}
		
		
		/**
		 * Generates a list of all the filter values to use.
		 *
		 * @param image The image to filter.
		 * @param filter The filter to use.
		 * @param size The size of the message being hidden.
		 * @throws Exception If the image for the filter 
		 * has not been correctly set.
		 */
		private void generateList(BufferedImage image, 
				Filter filter, int size) throws Exception{
			
			filter.setImage(image);
			System.gc();
			
			//filter the image
			TreeSet sortedlist = new TreeSet(new FPComparator());
			for(int i = 0; i < image.getWidth(); i++){
				for(int j = 0; j < image.getHeight(); j++){
					if((i * image.getHeight()) + j < size)
						sortedlist.add(new FilteredPixel(i, j, 
								Math.abs(filter.getValue(i, j))));
					else if (Math.abs(filter.getValue(i, j)) 
						> ((FilteredPixel)sortedlist.first()).getFilterValue()){
						sortedlist.remove(sortedlist.first());
						sortedlist.add(new FilteredPixel(i, j, 
								Math.abs(filter.getValue(i, j))));
					}

				}				
			} 
				
			//turn into an array
			fparray = sortedlist.toArray();		
			
		}
		
		
		
		/**
		 * Generates a new shot.
		 *
		 * A shot contains all the information needed to
		 * place where the bit should be encoded.
		 *
		 * @return A "shot" on the image where the next bit of information
		 * will be hidden.
		 */
		public Shot getShot(){

			//work out where we are up to...
			FilteredPixel fp = (FilteredPixel)fparray[(fparray.length - mArrayPos) - 1];
			
			//make the next shot
			Shot sh = new Shot(fp.getX(), fp.getY(), mBitCount, mColour);
			
			mBitCount++;
			if(mBitCount >= (mEndRange - mStartRange) + 1){
				mBitCount = 0;
				mColour++;
				if(mColour >= 3){
					mColour = 0;
					mArrayPos++;
				}
			}
			
			return sh;
		}
		
		
		//VARIABLES
		
		/**
		 * The set of filtered pixels.
		 */
		private Object[] fparray;
		
		/**
		 * The start range to hide data.
		 */
		private int mStartRange;
		
		/**
		 * The end range to hide data.
		 */
		private int mEndRange;
		
		
		/**
		 * The bit position we are up to.
		 */
		private int mBitCount;
		
		/**
		 * The colour we are working on.
		 */
		private int mColour;
		
		/**
		 * The array position we are looking at.
		 */
		private int mArrayPos;	
		
		
	}
	//end of inner class.
	
	//=============
	//ACTUAL CLASS
	//=============
	
	//CONSTRUCTORS
	
	/**
	 * Creates a new FilterFirst algorithm.
	 *
	 * The start and end positions for possible hits should
	 * be zero based - i.e. the first bit is 0.  This is counting
	 * from LSB to MSB (0 is least significant, 7 most).  555 and
	 * 565 image formats should be in the range 0-4, other images
	 * should have 0-7.
	 *
	 * @param startbits The start bit position for possible hits.
	 * @param endbits The end bit position for possible bits.
	 * @param moveaway The time to start moving away from the hits.
	 * @param initshots The number of shots to make after an initial hit.
	 * @param shotsincrease The number of shots to increase by after a hit.
	 * @param shotrange The range for the shots.
	 * @param filter The filter to use for the shots.
	 * @throws IllegalArgumentException If the incorrect number of 
	 * start/end bits is passed.
	 */
	public DynamicFilterFirst(int startbits, int endbits, int moveaway,
			int initshots,
			int shotsincrease, 
			int shotrange, Filter filter)
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
		
		mFilter = filter;
	}
	
	/**
	 * Creates a new FilterFirst algorithm using
	 * default bit ranges of 0 and 0 (4 bits), and a default
	 * Laplace filter on the remainder of the bits.
	 *
	 * @throws IllegalArgumentException This will actually not be thrown.
	 */
	public DynamicFilterFirst() throws IllegalArgumentException{
		this(0, 0, 10, 5, 2, 5, new Laplace(1, 8));
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
		
		//check the message fill actually fit
		if(!this.willMessageFit(message, cimage)){
			throw new IllegalArgumentException
			("Message is too big for this image!");
		}
		
		//setup the random number generator
		ShotPicker rgen = new ShotPicker(seed, 
				mStartBits,
				mEndBits,
				cimage.getImage(),
				mFilter,
				(int)((message.getSize() * 8) + 32), 0);
		
		Shot sh;
		int messagesize = (int) message.getSize();
		
		Random aran = new Random(seed);
		
		//put the size in the first 32 bits
		for(int i = 0; i < 32; i++){
			//generate a valid shot
			sh = rgen.getShot();
						
			//put in the next size bit...
			boolean bit = ((messagesize >> i) & 0x1) == 0x1;
			if(!mLSBMatch){
				cimage.setPixelBit(sh.getX(),
						sh.getY(),
						sh.getLayer(),
						sh.getBitPosition(),
						bit);
			}else{
				//match!
				cimage.matchPixelBit(sh.getX(),
						sh.getY(),
						sh.getLayer(),
						mFilter.getStartRange(),
						bit,
						aran.nextBoolean());
			}
		}
		
		//now we can start embedding the message into the cover
		while(message.notFinished()){
			
			sh = rgen.getShot();
			
			boolean bit = message.nextBit();
			if(!mLSBMatch){
				cimage.setPixelBit(sh.getX(),
						sh.getY(),
						sh.getLayer(),
						sh.getBitPosition(),
						bit);
			}else{
				//match!
				cimage.matchPixelBit(sh.getX(),
						sh.getY(),
						sh.getLayer(),
						mFilter.getStartRange(),
						bit,
						aran.nextBoolean());
			}
			
		}

		//now the message is hidden inside the image.
		return new StegoImage(cimage.getImage());
		
	}
	
	
	/**
	 * Retrieves a message from a steganographic image.
	 *
	 * @param simage The stego image to retreive the message from.
	 * @param seed The seed to the message.
	 * @param stream The stream for output message.
	 * @return The message retrieved.
	 * @throws IOException If an I/O error occurred.
	 */
	public RetrievedMessage decode(StegoImage simage, long seed, OutputStream stream)
	throws IOException, NoMessageException{
		
		//setup the random number generator
		ShotPicker rgen = new ShotPicker(seed, 
				mStartBits,
				mEndBits,
				simage.getImage(),
				mFilter,
				50, 0);
		
		Shot sh;
		int size = 0;
		
		//get the size - in the first 32 hidden bits
		for(int i = 0; i < 32; i++){
			
			sh = rgen.getShot();
			
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
				* simage.getImage().getHeight())
				* simage.getLayerCount())
				* ((mEndBits - mStartBits) + 1));
		if(size2 >= imagespace || size2 < 0)
			throw new NoMessageException();
		
		RetrievedMessage rmess = new RetrievedMessage(stream);
		
		rgen = new ShotPicker(seed, 
				mStartBits,
				mEndBits,
				simage.getImage(),
				mFilter,
				size2 + 50, 32);
		try{
		//start retrieving and writing out the message
		for(int k = 0; k < size2; k++){
			sh = rgen.getShot();
			
			rmess.setNext( (simage.getPixelBit(sh.getX(), 
					sh.getY(), 
					sh.getLayer(),
					sh.getBitPosition())
			) == 0x1);
						
		}
		}catch(Exception e){
			e.printStackTrace();
			rmess.close();
			System.exit(0);
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
			long seed) throws IOException, IllegalArgumentException{
		
		//check the message fill actually fit
		if(!this.willMessageFit(message, simage)){
			throw new IllegalArgumentException
			("Message is too big for this image!");
		}
		
		//setup the random number generator
		ShotPicker rgen = new ShotPicker(seed, 
				mStartBits,
				mEndBits,
				simage.getImage(),
				mFilter,
				(int)(message.getSize() * 8 + 50), 0);
		
		Shot sh;
		
		//make the whole image black...
		BufferedImage image = simage.getImage();
		int black = 0x0;
		for(int i = 0; i < image.getHeight(); i++){
			for(int j = 0; j < image.getWidth(); j++){
				image.setRGB(j, i, black);
			}
		}
		
		//put the size in the first 32 bits
		for(int i = 0; i < 32; i++){
			//generate a valid shot
			sh = rgen.getShot();
			
			image.setRGB(sh.getX(), sh.getY(), 
					decreaseDarkness(image.getRGB(sh.getX(), sh.getY())));
			
		}
		
		//now we can start "embedding" the message into the cover
		while(message.notFinished()){
			
			sh = rgen.getShot();
			
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
	 * Sets the filter for the algorithm.
	 *
	 * @param filter The new filter to use.
	 */
	public void setFilter(Filter filter){
		mFilter = filter; 
	}
	
	
	/**
	 * Gets the filter that is being used for the algorithm.
	 *
	 * @return The filter currently used.
	 */
	public Filter getFilter(){
		return mFilter;
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
		new StartEndFilterWindow(parent, this);
		parent.repaint();
	}
	
	/**
	 * Returns text explaining what this algorithm does.
	 * 
	 * @return What this algorithm does.
	 */
	public String explainMe(){
		return "Works like FilterFirst only uses dynamic programming\n"
				+ "to prevent lots of memory from being used.  This is\n" + 
				"NOT compatible with normal FilterFirst.";
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
	 * The filter to use for the algorithm.
	 */
	private Filter mFilter;
	
	
}
//end of class.
