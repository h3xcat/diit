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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;


/**
 * A message to hide.
 * <P>
 * A message is a file on disk that will be embedded into
 * a cover object in order to hide it.  It can read out the
 * message in terms of 0's and 1's (boolean false and true) to
 * make life easier for the encoding process.
 *
 * @author Kathryn Hempstalk.
 */
public class InsertableMessage{
	
	//CONSTRUCTORS
	
	/**
	 * Creates a new message.
	 *
	 * @param path The path to the message on disk.
	 * @throws FileNotFoundException If the path is not a valid file.
	 * @throws SecurityException If the user doesn't have access to this file.
	 * @throws IOException If the input file is empty.
	 */
	public InsertableMessage(String path) throws
	FileNotFoundException, SecurityException, IOException{

		mPath = path;
		mMsgFile = new FileInputStream(path);
		mBuffer = new byte[1];
		
		mIsFileFinished = false;
		
		//get the first byte
		int status = mMsgFile.read(mBuffer);
		mCount = 8;
		if (status == -1)
			throw new IOException("File is empty!");
		
	}
	
	
	//FUNCTIONS

	/**
	 * Delivers the next bit from the stream.
	 *
	 * @return The next bit (as a boolean).
	 * @throws IOException If there are no more bits to read.
	 */
	public boolean nextBit() throws IOException{
		//first check if we need to get another byte.
		if(mIsFileFinished)
			throw new IOException("File reading has finished!");
		
		//have byte, must manipulate to get bits
		boolean bit = (((mBuffer[0] >> (mCount- 1)) &0x1) == 0x1);
		mCount--;
		
		if(mCount == 0){
			//get another byte
			int status = mMsgFile.read(mBuffer);
			mCount = 8;
			if( status == -1){
				mIsFileFinished = true;
				mMsgFile.close();
			}
		}
		
		return bit;
	}
	
	
	/**
	 * Indicates whether the file reading has finished or not.
	 *
	 * @return Whether the stream has finished.
	 */
	public boolean notFinished(){
		return !mIsFileFinished;
	}
	
	/**
	 * Gives the size of the message on disk.
	 *
	 * @return The size of the message (in bytes).
	 * @throws IOException If there was a problem getting the file size.
	 * @throws FileNotFoundException If the file can't be found on disk.
	 */
	public long getSize() throws IOException, FileNotFoundException{
		RandomAccessFile raf = new RandomAccessFile(mPath, "r");
		long filesize = raf.length();
		raf.close();
		return filesize;
	}

	
	//VARIABLES

	/**
	 * The path to the message (on disk).
	 */
	private String mPath;

	/**
	 * A count of the number of bits left in this byte that can 
	 * be streamed.
	 */
	private int mCount;
	
	/**
	 * The file being streamed.
	 */
	private FileInputStream mMsgFile;
	
	/**
	 * A buffer for the byte currently being streamed.
	 */
	private byte[] mBuffer;
	
	/**
	 * A indicator that the file reading is finished.
	 */
	private boolean mIsFileFinished;
	
}
//end of class.
