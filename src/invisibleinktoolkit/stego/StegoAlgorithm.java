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

package invisibleinktoolkit.stego;

import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.Frame;
import java.io.OutputStream;


/**
 * A stego algorithm.
 * <P>
 * A stego algorithm is a algorithm that can be used to both
 * encode and decode a message to/from a graphical image. A stego
 * algorithm must implement at least the functionality described here.
 *
 * @author Kathryn Hempstalk.
 */
public interface StegoAlgorithm{
	
	/**
	 * Encodes a message into a picture.
	 * <P> 
	 * This method encodes a message, read as a stream of bits,
	 * into a picture, and returns the resulting image with the 
	 * message embedded.  The algorithm to do this may vary,
	 * and different algorithms are not expected to encode/decode
	 * each other.
	 *
	 * @param message The message to encode.
	 * @param cimage The cover image to encode into.
	 * @param seed The seed for any random number generation.
	 * @return An image containing the encoded message.
	 * @throws IOException When the message doesn't finish properly.
	 */
	public StegoImage encode(InsertableMessage message, CoverImage cimage,
			long seed) throws IOException;
	
	
	/**
	 * Retrieves a message from a steganographic image.
	 * <p>
	 * This method decodes a message from a steganographic image,
	 * and writes the message to disk.  The different implementations 
	 * are not expected to decode each other.
	 *
	 * @param simage The stego image to retreive the message from.
	 * @param seed The seed to the message.
	 * @param stream The stream for output message.
	 * @return The message retrieved.
	 * @throws IOException If an I/O error occurred.
	 */
	public RetrievedMessage decode(StegoImage simage, long seed, OutputStream stream)
	throws IOException, NoMessageException;
	
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
			long seed) throws IOException;
	
	/**
	 * Gets the start position of the hiding.
	 *
	 * @return The start position of the hiding.
	 */
	public int getStartBits();
	
	/**
	 * Gets the end position of the hiding.
	 *
	 * @return The end position of the hiding.
	 */
	public int getEndBits();
	
	/**
	 * Sets the end position for hiding.
	 *
	 *@param newend The new end position for hiding.
	 */
	public void setEndBits(int newend);
	
	/**
	 * Sets the start position for hiding.
	 *
	 * @param newstart The new start position for hiding.
	 */
	public void setStartBits(int newstart);
	
	/**
	 * Pops up a window to alter the configuration.
	 *
	 * @param parent The parent frame to display this within.
	 */
	public void openConfigurationWindow(Frame parent);
	
	/**
	 * Returns text explaining what this algorithm does.
	 * 
	 * @return What this algorithm does.
	 */
	public String explainMe();	
	
	/**
	 * Whether LSB Matching should be used.
	 * 
	 * @param shouldMatch Whether to use LSB or not.
	 */
	public void setMatch(boolean shouldMatch);
	
	/**
	 * Gets whether this algorithm is using LSB matching or not.
	 * 
	 * @return Whether this algorithm will use LSB matching.
	 */
	public boolean getMatch();
	
}
//end of interface.
