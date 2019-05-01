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

import java.io.OutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

/**
 * A retrieved message.
 * <P>
 * A retrieved message is any sort of file, to be written
 * to disk as it is retrieved from a steganographic object.
 *
 * @author Kathryn Hempstalk.
 */
public class RetrievedMessage{
	
	//CONSTRUCTORS
	
	/**
	 * Creates a new retrieved message.
	 *
	 * @param outstream The stream to write the message to.
	 * @throws FileNotFoundException If the file exists but is a directory
	 *  rather than a regular file, does not exist but cannot be created, 
	 * or cannot be opened for any other reason.
	 * @throws SecurityException If write access to this file is denied.
	 */
	public RetrievedMessage(OutputStream outstream)
	throws FileNotFoundException, SecurityException{
		//set up all the variables
		mRetrievedMessage = outstream;
		mIsFinished = false;
		mBitCount = 0;
		mBuffer = 0;
	}
	
	
	//FUNCTIONS
	
	/**
	 * Sets the next bit of the output stream.
	 *
	 * The bit will be set to 0 if bit is false, 1 if
	 * it is true.
	 *
	 * @param bit The value to set.
	 * @throws IOException If the file is finished writing already,
	 * or there was an I/O Error.
	 */
	public void setNext(boolean bit) throws IOException{
		
		//check file hasn't been closed
		if(mIsFinished)
			throw new IOException
			("File has finished writing!");
		//set the new bit
		int newbit = 0x0;
		if(bit) newbit = 0x1;
		//change the buffer and increment count
		mBuffer = mBuffer << 1 | newbit;
		mBitCount++;
		
		//if the buffer is full, write it out
		if(mBitCount == 8){
			this.writeBuffer();
		}	    
	}
	
	/**
	 * Writes the buffer out to the stream.
	 *
	 * This method also re-zeroes the bitcount and buffer
	 * ready for the next byte.
	 *
	 * @throws IOException If there was an I/O error.
	 */
	private void writeBuffer() throws IOException{
		mRetrievedMessage.write(mBuffer);
		mBitCount = 0;
		mBuffer = 0;
	}
	
	/**
	 * Closes off and releases resources for this message.
	 *
	 * If there is half a buffer, it will be padded to the correct
	 * position, and written before the file is completely closed.
	 *
	 * @throws IOException If an I/O error occurs.
	 */
	public void close() throws IOException{
		//check there isn't something left to write
		if(mBitCount > 0){
			//write it
			int left = 8 - mBitCount;
			mBuffer = mBuffer << left;
			this.writeBuffer();
		}
		//close it all off
		mRetrievedMessage.flush();
		mIsFinished = true;
	}	
	
	
	//VARIABLES
	
	/**
	 * The file the message is being written to.
	 */
	private OutputStream mRetrievedMessage;
	
	/**
	 * Whether this file has finished writing or not.
	 */
	private boolean mIsFinished;
	
	/**
	 * The count of bits in the buffer.
	 */
	private int mBitCount;
	
	/**
	 * A buffer that will be the next byte output to the file.
	 */
	private int mBuffer;
	
	
}
//end of class.

