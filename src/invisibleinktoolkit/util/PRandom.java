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

package invisibleinktoolkit.util;

import java.util.Random;

/**
 * A psuedo-random shot generator.
 * <P>
 * This class generates shots and random numbers
 * for use with a Steganography algorithm.  
 *
 * @author Kathryn Hempstalk.
 */
public class PRandom{
	
	//CONSTRUCTORS
	
	/**
	 * Creates a new psuedo-random generator with a
	 * set seed.  If the seed is null, 0 will be used by
	 * default.
	 *
	 * @param seed The seed to initialise the generator.
	 * @param width The maximum breadth of the shots.
	 * @param height The maximum height of the shots.
	 * @param numlayers The number of layers for the shots.
	 * @param startrange The start range of bit positions.
	 * @param endrange The ending range of bit positions.
	 */
	public PRandom(long seed, int width, int height,
			int numlayers, int startrange, 
			int endrange) throws IllegalArgumentException{
		
		mHeight = height;
		mWidth = width;
		mNumLayers = numlayers;
		mStart = startrange;
		mEnd = endrange;
		mLastShot = null;
		mRandomGen = new Random(seed);
	}
	
	
	//FUNCTIONS
	
	/**
	 * Generates a new shot.
	 *
	 * A shot contains all the information needed to
	 * place where the bit should be encoded.
	 *
	 * @return A shot for a steganography algorithm.
	 */
	public Shot getShot(){
		//generate x and y shots
		int shotx = Math.abs(mRandomGen.nextInt(mWidth));
		int shoty = Math.abs(mRandomGen.nextInt(mHeight));
		
		//generate layer position
		int layer = Math.abs(mRandomGen.nextInt(mNumLayers));
		
		//generate bit position (start based)
		int bitpos = mStart + 
		Math.abs(mRandomGen.nextInt((mEnd - mStart) + 1));
		
		//return the next shot...
		Shot sh = new Shot(shotx, shoty, bitpos, layer);
		mLastShot = sh;
		return sh;
	}
	
	
	/**
	 * Generates a ranged shot.  
	 *
	 * A ranged shot is within a radius of the last shot,
	 * but will not update the last shot to be any 
	 * ranged shot.
	 *
	 * @param range The range to shoot within.
	 * @return The shot within the given range.
	 */
	public Shot getRangedShot(int range){
		range = Math.abs(range);
		
		//check the up/down ranges
		int rangeup, rangedown, rangeleft, rangeright;
		if(mLastShot.getX() == 0)
			rangeleft = 0;
		else
			rangeleft = range % mLastShot.getX();
		if( (mWidth - mLastShot.getX()) == 0)
			rangeright = 0;
		else
			rangeright = range % (mWidth - mLastShot.getX());
		if (mLastShot.getY() == 0)
			rangeup = 0;
		else
			rangeup = range % mLastShot.getY();
		if ( (mHeight - mLastShot.getY()) == 0)
			rangedown = 0;
		else
			rangedown = range % (mHeight - mLastShot.getY());
		
		int distright, distdown;
		distright = rangeleft + rangeright;
		distdown = rangeup + rangedown;
		
		//take a shot
		//generate x and y shots
		int shotx = (mLastShot.getX() - rangeleft) + mRandomGen.nextInt(distright);
		int shoty = (mLastShot.getY() - rangeup) + mRandomGen.nextInt(distdown);
		
		//generate layer position
		int layer = Math.abs(mRandomGen.nextInt(mNumLayers));
		
		//generate bit position (start based)
		int bitpos = mStart + 
		Math.abs(mRandomGen.nextInt((mEnd - mStart) + 1));
		
		return new Shot(shotx, shoty, bitpos, layer);
	}
	
	
	/**
	 * Allows the last shot to be set.
	 *
	 * @param sh The last shot.
	 */
	public void setLast(Shot sh){
		mLastShot = sh;
	}
	
	
	//VARIABLES
	/**
	 * A psuedorandom number generator.
	 */
	private Random mRandomGen;
	
	/**
	 * The number of layers this will generate shots for.
	 */
	private int mNumLayers;
	
	/**
	 * The height of the image this will generate shots for.
	 */
	private int mHeight;
	
	/**
	 * The width of the image this will generate shots for.
	 */
	private int mWidth;
	
	/**
	 * The start range of the bit positions.
	 */
	private int mStart;
	
	/**
	 * The end range of the bit positions.
	 */
	private int mEnd;
	
	/**
	 * The last shot that was made.
	 */
	private Shot mLastShot;
	
	
}
//end of class.
