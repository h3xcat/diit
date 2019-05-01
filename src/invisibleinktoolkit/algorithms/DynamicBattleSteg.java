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
import java.awt.image.BufferedImage;
import invisibleinktoolkit.filters.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;
import java.util.TreeSet;


/**
 * A dynamic programming battlesteg way of hiding data.
 * <P>
 * Battlesteg is short for battleship steganography - an attempt
 * at getting the best of both worlds.  Both adaptive steganography
 * and a random element are used, in order to make the data hidden
 * securely, and mostly in a place where it is difficult to detect.
 * <P>
 * The algorithm first filters the image, and sorts the filter results.
 * Then the top ten percent of the results are used to generate "engines" -
 * pixels to drive off to find clusters.  Currently, just a good neighbour
 * is needed for this and the neighbour to become a "ship".  Once a list
 * of ships is established, the algorithm begins to hide the data.  It
 * randomly generates positions, until it hits a "ship".  Then the following
 * hits are ranged close to the hit, until the algorithm decides to move away.
 * <P>
 * What ships are determined is reliant on the filter being used - different
 * filters produce different results. The hitting is entirely controlled
 * by an internal random number generator that tracks hits and shots.
 * <P>
 * This version of BattleSteg is based on dynamic programming.  It is not able decode
 * images encoded with the traditional BattleSteg algorithm (this is also true for BattleSteg
 * and this algorithm).  It does not have the same memory issues because it does
 * not need to hold as much information in memory when it runs.
 *
 * @author Kathryn Hempstalk.
 */
public class DynamicBattleSteg extends BattleSteg implements StegoAlgorithm, Filterable{
	
	//INNER CLASS
	/**
	 * An internal representation of a battlesteg 
	 * psuedorandom shot generator.
	 *
	 * @author Kathryn Hempstalk.
	 */
	private class BPRandom extends PRandom{
		
		//CONSTRUCTORS
		
		/**
		 * Creates a new psuedo-random generator with a
		 * set seed.  If the seed is null, 0 will be used by
		 * default.
		 *
		 * @param seed The seed to initialise the generator.
		 * @param numlayers The number of layers for the shots.
		 * @param startrange The start range of bit positions.
		 * @param endrange The ending range of bit positions.
		 * @param image The image to figure out the ships for.
		 * @param moveaway The time to start moving away from the hits.
		 * @param initshots The number of shots to make after an initial hit.
		 * @param shotsincrease The number of shots to increase by after a hit.
		 * @param shotrange The range for the shots.
		 * @throws IllegalArgumentException if something bad was passed.
		 */
		public BPRandom(long seed, 
				int numlayers, int startrange, 
				int endrange,
				BufferedImage image, int moveaway,
				int initshots,
				int shotsincrease, 
				int shotrange,
				Filter filter) throws IllegalArgumentException{
			
			//call the super constructor
			super(seed, image.getWidth(), image.getHeight(), 
					numlayers, startrange, endrange);
			
			//sort out local variables
			mMoveAwayFixed = moveaway;
			mInitShots = initshots;
			mShotsIncrease = shotsincrease;
			mShotRange = shotrange;
			
			//set the filter ranges
			filter.setStartRange(endrange + 1);
			filter.setEndRange(8);
			
			//set up the matrix of "shots"
			beenShot = new boolean[image.getWidth()][image.getHeight()][3][8];
			for(int i = 0; i < image.getWidth(); i++){
				for(int j = 0; j < image.getHeight(); j++){
					for(int k = 0; k < 8; k++){
						beenShot[i][j][0][k] = false;
						beenShot[i][j][1][k] = false;
						beenShot[i][j][2][k] = false;
					}
				}
			}
			
			//all the ships
			mShips = new boolean[image.getWidth()][image.getHeight()];
			for(int i = 0; i < image.getWidth(); i++){
				for(int j = 0; j < image.getHeight(); j++){
					mShips[i][j] = false;
				}
			}
			
			try{
				this.generateShips(image, filter);
			}catch(Exception e){
				e.printStackTrace();
				System.exit(0);
			}
		}
		
		
		/**
		 * Generates all the ships to use.
		 *
		 * @param image The image to filter for ships
		 * @throws Exception If the image for the filter has not been correctly set.
		 */
		private void generateShips(BufferedImage image, 
				Filter filter) throws Exception{
			
			//set up the image for the filter
			filter.setImage(image);			
			int size = (image.getWidth() * image.getHeight()) / 10;
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
			Object[] engines = sortedlist.toArray();
			int halfway = ((FilteredPixel)sortedlist.last()).getFilterValue() / 2;
			
	
			//now do a quick search to check if any of the engines are
			//ships
			int x, y, acount;
			
			for(int i = 0; i < engines.length; i++){
				x = ((FilteredPixel)(engines[i])).getX();
				y = ((FilteredPixel)(engines[i])).getY();
				acount = 0;
				if(x > 0){
					if(Math.abs(filter.getValue(x - 1, y)) >= halfway){
						mShips[x - 1][y] = true;
						acount++;
					}
				}
				if(x < (image.getWidth() - 1)){
					if(Math.abs(filter.getValue(x + 1, y)) >= halfway){
						mShips[x + 1][y] = true;
						acount++;
					}
				}
				if(y < (image.getHeight() - 1)){
					if(Math.abs(filter.getValue(x, y + 1)) >= halfway){
						mShips[x][y + 1] = true;
						acount++;
					}
				}
				if(y > 0){
					if(Math.abs(filter.getValue(x, y - 1)) >= halfway){
						mShips[x][y - 1] = true;
						acount++;
					}
				}

				mShips[x][y] = true;
				
			} 
			//now all the ships are picked and set
		}
		
				
		
		/**
		 * Generates a new shot.
		 *
		 * A shot contains all the information needed to
		 * place where the bit should be encoded.
		 *
		 * @return A shot on the board.
		 */
		public Shot getShot(){
			//first check if we should make a ranged shot (hit previous)
			if(mNumShots <= 0 || mMoveAway <= 0){
				//normal shot
				//reset ranged variables...
				mMoveAway = mMoveAwayFixed;
				mNumShots = 0;
				//get a shot (until a non-hit square is found)
				Shot sh = super.getShot();
				while(beenShot[sh.getX()][sh.getY()][sh.getLayer()][sh.getBitPosition()]){
					sh = super.getShot();
				}
				
				//save it
				beenShot[sh.getX()][sh.getY()][sh.getLayer()][sh.getBitPosition()] = true;
				
				//check if it is a ship (next hit will be ranged)
				if (mShips[sh.getX()][sh.getY()]){
					mNumShots = mInitShots;
				}
				return sh;
			}else{
				//ranged shot
				
				//subtract variables to make sure it does moveaway
				mNumShots--;
				mMoveAway--;
				Shot sh = super.getRangedShot(mShotRange);
				//get ranged shots till we run out, then just make normal shots
				while(beenShot[sh.getX()][sh.getY()][sh.getLayer()][sh.getBitPosition()]){
					if(mNumShots <= 0 || mMoveAway <= 0)
						sh = super.getShot();
					else
						sh = super.getRangedShot(mShotRange);
					mNumShots--;
					mMoveAway--;
				}
				//save it
				beenShot[sh.getX()][sh.getY()][sh.getLayer()][sh.getBitPosition()] = true;
				//if it was a hit, recenter shots on newest hit
				if(mShips[sh.getX()][sh.getY()]){
					mNumShots = mNumShots + mShotsIncrease;
					super.setLast(sh);
				}
				return sh;
			}
		}
		
		//VARIABLES
		
		/**
		 * Contains a list of all the positions already used.
		 */
		private boolean beenShot[][][][];
		
		/**
		 * Contains a map of all the ships.
		 */
		private boolean mShips[][];
		
		/**
		 * Contains the count of the number of ranged shots.
		 */
		private int mNumShots;
		
		/**
		 * The value to reset the moveaway count to.
		 */
		private int mMoveAwayFixed;
		
		/**
		 * The value to reset the shot count to.
		 */
		private int mInitShots;
		
		/**
		 * The range shots will be made in.
		 */
		private int mShotRange;
		
		/**
		 * The number of shots to increase by each time we hit.
		 */
		private int mShotsIncrease;
		
		/**
		 * The count of shots till it moves away.
		 */
		private int mMoveAway;
		
		
	}
	//end of inner class.
	
	//=============
	//ACTUAL CLASS
	//=============
	
	//CONSTRUCTORS
	
	/**
	 * Creates a new BattleSteg algorithm.
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
	public DynamicBattleSteg(int startbits, int endbits, int moveaway,
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
		
		//set up local variables
		mMoveAway = moveaway;
		mInitShots = initshots;
		mShotsIncrease = shotsincrease;
		mShotsRange = shotrange;
		mFilter = filter;
	}
	
	/**
	 * Creates a new BattleSteg algorithm using
	 * default bit ranges of 0 and 0 (1 bits), and a Laplace filter
	 * on the rest of the bits.
	 *
	 * @throws IllegalArgumentException This will actually not be thrown.
	 */
	public DynamicBattleSteg() throws IllegalArgumentException{
		this(0, 0, 10, 5, 2, 1, new Laplace(1, 8));
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
		BPRandom rgen = new BPRandom(seed, 
				cimage.getLayerCount(),
				mStartBits,
				mEndBits,
				cimage.getImage(),
				mMoveAway, mInitShots, mShotsIncrease, mShotsRange,
				mFilter);
		
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
		BPRandom rgen = new BPRandom(seed,
				simage.getLayerCount(),
				mStartBits,
				mEndBits,
				simage.getImage(),
				mMoveAway, mInitShots, mShotsIncrease, mShotsRange,
				mFilter);
		
		
		
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
		
		//start retrieving and writing out the message
		for(int k = 0; k < size2; k++){
			sh = rgen.getShot();
			
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
			long seed) throws IOException, IllegalArgumentException{
		
		//check the message fill actually fit
		if(!this.willMessageFit(message, simage)){
			throw new IllegalArgumentException
			("Message is too big for this image!");
		}
		
		//setup the random number generator
		BPRandom rgen = new BPRandom(seed, 
				simage.getLayerCount(),
				mStartBits,
				mEndBits,
				simage.getImage(),
				mMoveAway, mInitShots, mShotsIncrease, mShotsRange,
				mFilter);
		
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
			
			//put in the next size bit...
			image.setRGB(sh.getX(), sh.getY(), 
					decreaseDarkness(image.getRGB(sh.getX(), sh.getY())));
			
		}
		
		//now we can start embedding the message into the cover
		while(message.notFinished()){
			
			sh = rgen.getShot();
			
			image.setRGB(sh.getX(), sh.getY(), 
					decreaseDarkness(image.getRGB(sh.getX(), sh.getY())));
			
			message.nextBit();
						
		}
		
		//now the message is hidden inside the image.
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
	 * Returns text explaining what this algorithm does.
	 * 
	 * @return What this algorithm does.
	 */
	public String explainMe(){
		return "Works like BattleSteg only uses dynamic programming\n"
				+ "to prevent lots of memory from being used.  This is\n" + 
				"NOT compatible with normal BattleSteg.";
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
	 * The value to reset the moveaway count to.
	 */
	private int mMoveAway;
	
	/**
	 * The value to reset the shot count to.
	 */
	private int mInitShots;
	
	/**
	 * The range shots will be made in.
	 */
	private int mShotsRange;
	
	/**
	 * The number of shots to increase by each time we hit.
	 */
	private int mShotsIncrease;
	
	/**
	 * The filter to use for the algorithm.
	 */
	private Filter mFilter;
	
		
}
//end of class.
