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
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.IOException;
import invisibleinktoolkit.stego.*;
import invisibleinktoolkit.filters.Filterable;
import invisibleinktoolkit.filters.Filter;

/**
 * Utilities for testing out benchmarks.
 * <P>
 * This class is full of utility functions to do such things as
 * create random messages into a folder on disk, put together
 * two folders of messages and images, and other utilities that
 * may be useful for benchmarking images.
 *
 * @author Kathryn Hempstalk.
 */
public final class TestingUtils{
	
	/**
	 * Creates random output to disk.
	 * <P>
	 * The files will be created as message<x>K.txt, where <x> is
	 * replaced by the size of this message in kilobytes.
	 * 
	 * @param folder The output folder for the messages.
	 * @param startsize The starting size for message size.
	 * @param increment The amount of kilobytes to increment
	 * the message size by on each output.
	 * @param nummessages The number of messages to output before
	 * stopping.
	 * @throws IllegalArgumentException If the folder is not a folder, or
	 * the numbers passed are <= 0.
	 * @throws IOException If there was a problem writing the files.
	 */
	public static final void createRandomMessages(File folder, int startsize, 
			int increment, int nummessages)
	throws IllegalArgumentException, IOException{
		
		//check the file is a folder
		if(!folder.isDirectory())
			throw new IllegalArgumentException("Passed file is not a folder!");
		
		if(startsize <= 0)
			throw new IllegalArgumentException
			("Start size must be greater than zero!");
		if(increment <= 0)
			throw new IllegalArgumentException
			("Increment must be greater than zero!");
		if(nummessages <= 0)
			throw new IllegalArgumentException
			("Number of message must be greater than zero");
		
		
		//continue on...
		Random rangen = new Random(System.currentTimeMillis());
		File output = new File(folder, "message" + startsize + "K.txt");
		BufferedWriter message = new BufferedWriter(new FileWriter(output));
		byte[] randomtext;
		
		//write out each message.
		for(int i = 0; i < nummessages; i++){
			output = new File(folder, "message" + 
					(startsize + increment*i) + "K.txt");
			message = new BufferedWriter(new FileWriter(output));
			randomtext = new byte[(startsize + increment*i) * 1024];
			rangen.nextBytes(randomtext);
			for(int j = 0; j < randomtext.length; j++){
				message.write((char)randomtext[j]);
			}
			message.close();
		}
		
		//all done :)
	}
	
	
	/**
	 * Combines two folders - one of images, the other of messages.
	 * <P>
	 * The resulting output is placed in the specified folder, named
	 * using the message name, followed by a '-' and the steganography
	 * type.  The algorithms will be written out in full.
	 * <P>
	 * It is expected that the images are in one of jpg, png or bmp 
	 * formats and have their filenames as such.  Files without this
	 * extension will be ignored.  Likewise, the messages are expected
	 * to be txt files for this function.  Random txt files can be created
	 * using createRandomMessages() , provided by this class also.
	 *
	 * This is intended for use for benchmarking, so the options like
	 * password (seed), filter options, algorithm write places are fixed.
	 * The algorithms will use their default settings. The seed will always be 0.
	 * The random number generator is within each algorithm, so it will
	 * be reseeded with each image/message combination.
	 *
	 * @param imagefolder The folder containing the original images.
	 * @param messagefolder The folder containing the messages.
	 * @param outputfolder The folder to output the results to.
	 * @param bmpformat The format to output the stego-files (if true,
	 * uses bmp, if false uses png).
	 * @param algorithms The list of algorithms to use.
	 * @param filters The list of filters to use.
	 * @throws IllegalArgumentException If the files aren't directories,
	 * or the list of algorithms has nothing in it.
	 */
	public static final void combineFolders(File imagefolder, File messagefolder,
			File outputfolder, boolean bmpformat,
			String[] algorithms, String[] filters)
	throws IllegalArgumentException{
		
		//set the output file format
		String outformat = "png";
		if(bmpformat)
			outformat = "bmp";
		
		//check each of the folders is indeed a folder...
		if(!imagefolder.isDirectory() || !messagefolder.isDirectory()
				|| !outputfolder.isDirectory())
			throw new IllegalArgumentException
			("Not all passed files are folders!");
		
		if(algorithms.length == 0)
			throw new IllegalArgumentException
			("You must pass some algorithms to use!");
		
		
		//now we're good to go...
		
		String[] messagelist = messagefolder.list();
		String[] imagelist = imagefolder.list();
		CoverImage cimage;
		InsertableMessage imess;
		StegoAlgorithm alg;
		String coverfilepath, messagefilepath, outputpath, originalname;
		String fileseparator = System.getProperty("file.separator");
		
		//for each file in the image folder...
		for(int i = 0; i < imagelist.length; i++){
			if(imagelist[i].endsWith(".bmp") ||
					imagelist[i].endsWith(".jpg") ||
					imagelist[i].endsWith(".png")){
				//ok to combine it...
				coverfilepath = imagefolder.getPath() + fileseparator
				+ imagelist[i];
				originalname = imagelist[i].substring(0, imagelist[i].indexOf("."));
				
				//for each message in the message folder...
				for(int j = 0; j < messagelist.length; j++){
					if(messagelist[j].endsWith(".txt")){
						//ok to combine...
						messagefilepath = messagefolder.getPath() + 
						fileseparator + messagelist[j];
						
						//now write it out using each algorithm...
						
						for(int k = 0; k < algorithms.length; k++){
							try{
								//setup the algorithm...
								alg =  (StegoAlgorithm)Class.forName(algorithms[k]).newInstance();
								
								if(alg instanceof Filterable){
									for(int l = 0; l < filters.length; l++){
										//setup the filter...
										((Filterable)alg).setFilter
										((Filter)Class.forName(filters[l]).newInstance());
										
										//setup the two files...
										cimage = new CoverImage(new FileInputStream(coverfilepath));
										imess = new InsertableMessage(messagefilepath);
										
										//setup the filename...
										outputpath = originalname + "~"
										+ messagelist[j].substring(0,messagelist[j].lastIndexOf("."))
										+ "-" 
										+ algorithms[k].toLowerCase().substring(algorithms[k].lastIndexOf(".") + 1,
												algorithms[k].length())
												+ "-"
												+ filters[l].toLowerCase().substring(filters[l].lastIndexOf(".") + 1,
														filters[l].length())
														+ "." + outformat;
										
										//encode it
										System.out.println("Outputting... " + outputpath);
										StegoImage stego = alg.encode(imess, cimage, 0);
										stego.write(outformat, new File(outputfolder, outputpath));	
									}
								}else{
									//setup the two files...
									cimage = new CoverImage(new FileInputStream(coverfilepath));
									imess = new InsertableMessage(messagefilepath);
									
									//setup the filename...
									outputpath = originalname + "~"
									+ messagelist[j].substring(0,messagelist[j].lastIndexOf("."))
									+ "-" 
									+ algorithms[k].toLowerCase().substring(algorithms[k].lastIndexOf(".") + 1,
											algorithms[k].length())
											+ "." + outformat;
									//encode it
									System.out.println("Outputting... " + outputpath);
									StegoImage stego = alg.encode(imess, cimage, 0);
									stego.write(outformat, new File(outputfolder, outputpath));	
								}	
							}catch(Exception e){
								//just go on to the next one...
								System.out.println("Error processing image. Skipping...");
							}
						}
					}
				}
			}
		}
		
		//it is done.
	}
	
	
	
	/**
	 * Copies all the files in two given directories into a temporary directory.
	 *
	 * @param sourcedir The source image directory.
	 * @param stegdir The stego image directory.
	 * @param outputdir The directory to output the files to.
	 * @return true if the operation succeeded, false otherwise.
	 * @throws IOException If there was a problem writing a file.
	 * @throws SecurityException If it can't create the directory because of security
	 * reasons.
	 */
	public static boolean copyIntoTempFolder(File sourcedir, File stegdir, File outputdir)
	throws IOException, SecurityException{
		
		//make the output directory exist.
		if(!outputdir.exists())
			outputdir.mkdir();
		
		//check the other directories exist
		if(!sourcedir.exists() || !stegdir.exists())
			return false;
		
		//setup temp variables
		String[] sourcelist = sourcedir.list();
		String[] steglist = stegdir.list();
		BufferedInputStream fis;
		BufferedOutputStream fos;
		byte[] abyte = new byte[10000];
		int read = 0;
		
		//copy each file into the output directory
		if(sourcedir != outputdir){
		for(int i = 0; i < sourcelist.length; i++){
			fis = new BufferedInputStream(new FileInputStream(new File(sourcedir, sourcelist[i])));
			fos = new BufferedOutputStream(new FileOutputStream(new File(outputdir, sourcelist[i])));
			read = fis.read(abyte, 0, abyte.length);
			while(read != -1){
				fos.write(abyte, 0, read);
				read = fis.read(abyte, 0, abyte.length);
			}
			fis.close();
			fos.close();		
		}
		}
		
		//do the same for the stego files
		if(stegdir != outputdir){
		for(int i = 0; i < steglist.length; i++){
			fis = new BufferedInputStream(new FileInputStream(new File(stegdir, steglist[i])));
			fos = new BufferedOutputStream(new FileOutputStream(new File(outputdir, steglist[i])));
			read = fis.read(abyte, 0, abyte.length);
			while(read != -1){
				fos.write(abyte, 0, read);
				read = fis.read(abyte, 0, abyte.length);
			}
			fis.close();
			fos.close();
			
		}
		}
		
		return true;
	}
	
	/**
	 * Cuts up a given text file into messages of given size and outputs them
	 * to disk.
	 * 
	 * @param textfile A text file to use for the messages.
	 * @param folder The output folder for the messages.
	 * @param startsize The starting size for message size.
	 * @param increment The amount of kilobytes to increment
	 * the message size by on each output.
	 * @param nummessages The number of messages to output before
	 * stopping.
	 * @throws IllegalArgumentException If the folder is not a folder, or
	 * the numbers passed are <= 0.
	 * @throws IOException If there was a problem writing the files.
	 */
	public static final void cutTextIntoMessages(File textfile, File folder,
			int startsize, int increment, int nummessages)
	throws IllegalArgumentException, IOException{
		
		//check the text file exists
		if(!textfile.exists())
			throw new IllegalArgumentException("Text file does not exist!");
		
		//check the file is a folder
		if(!folder.isDirectory())
			throw new IllegalArgumentException("Passed file is not a folder!");
		
		//check other input
		if(startsize <= 0)
			throw new IllegalArgumentException
			("Start size must be greater than zero!");
		if(increment <= 0)
			throw new IllegalArgumentException
			("Increment must be greater than zero!");
		if(nummessages <= 0)
			throw new IllegalArgumentException
			("Number of message must be greater than zero");
		
		
		//continue on...
		File output;
		BufferedReader tfile;
		BufferedWriter message;
		char[] structuredtext;
		
		//write out each message.
		for(int i = 0; i < nummessages; i++){
			output = new File(folder, "smessage" + 
					(startsize + increment * i) + "K.txt");
			message = new BufferedWriter(new FileWriter(output));
			tfile = new BufferedReader(new FileReader(textfile));
			structuredtext = new char[(startsize + increment * i) * 1024];
			int read = tfile.read(structuredtext,0,structuredtext.length);
			for(int j = 0; j < read; j++){
				message.write((char)structuredtext[j]);
			}
			tfile.close();
			message.close();
		}
		
		//all done :)
	}
	
	
	
}
//end of class.
