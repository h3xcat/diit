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


package invisibleinktoolkit;

import javax.imageio.ImageIO;
import java.util.Iterator;
import javax.swing.JOptionPane;
import invisibleinktoolkit.gui.GUI;
import javax.swing.UIManager;

/**
 * Starts up the program, and checks constantly to make sure
 * there is enough memory to keep it going.
 *
 * @author Kathryn Hempstalk.
 */
public class Run{
	
	/**
	 * Main class used to run the program.
	 *
	 */
	public static void main(String[] args){
		
		try { //use system look & feel
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {}
		
		//First things first, let's check the user has the right 
		//things to be able to run this...
		Iterator bmpwriter = ImageIO.getImageWritersByFormatName("bmp");
		Iterator pngwriter = ImageIO.getImageWritersByFormatName("png");
		
		//if both are empty, then we aren't ok
		if(!pngwriter.hasNext() && !bmpwriter.hasNext()){
			//show a window saying this program is useless
			JOptionPane.showMessageDialog
			(null, "This program requires Java 1.4 with the advanced "
					+ "imaging toolkit or Java 1.5 to run.\n  There appears to "
					+ "be no appropriate writers on your pc, suggesting you do not"
					+ " have the right version of Java.\n  The program will now exit."
					, "No LossLess Image Writers!", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
			
		}else if (!pngwriter.hasNext()){
			//no png format writer... make note but carry on
			JOptionPane.showMessageDialog
			(null, "This program requires Java 1.4 with the advanced "
					+ "imaging toolkit or Java 1.5 to run.\n  It appears that you "
					+ "do not have the ability to write png files, with your "
					+ "Java installation.\n The program will continue but please "
					+ "use bitmaps (bmp) as the output format."
					, "No PNG Image Writers!", JOptionPane.ERROR_MESSAGE);
			
		}else if (!bmpwriter.hasNext()){
			//no bmp format writer... make note but carry on
			JOptionPane.showMessageDialog
			(null, "This program requires Java 1.4 with the advanced "
					+ "imaging toolkit or Java 1.5 to run.\n  It appears that you "
					+ "do not have the ability to write bmp files, with your "
					+ "Java installation.\n The program will continue but please "
					+ "use png as the output format."
					, "No Bitmap Image Writers!", JOptionPane.ERROR_MESSAGE);
		}//else we are ok... continue.
		
		//start up a gui window and we're away!
		try{
			mGUI = new GUI("Digital Invisible Ink Toolkit 1.5 (cli)");
			mGUI.setVisible(true);
			
			//monitor the memory so we can exit gracefully...
			Thread memMonitor = new Thread(){
				public void run() {
					while(true) {
						try {
							Thread.sleep(3000);
							
							System.gc();
							if((Runtime.getRuntime().maxMemory() - 
									Runtime.getRuntime().totalMemory())  < 
									m_initialJVMSize+300000) {
								
								mGUI.dispose();
								
								System.gc();
								
								//gp through all the java threads and kill them off...
								Thread [] theGroup = new Thread[Thread.activeCount()];
								Thread.enumerate(theGroup);
								for(int i = 0; i < theGroup.length; i++) {
									Thread t = theGroup[i];
									if(t!=null) {
										if(t!=Thread.currentThread()) {
											if(t.getName().startsWith("Thread")) {
												t.interrupt();
											}
											else if(t.getName().startsWith("AWT-EventQueue")) {
												t.interrupt();
											}
										}
									}
								}
								theGroup=null;
								
								JOptionPane.showMessageDialog(null,
										"Not enough memory. \nPlease load "+
										"a smaller image or use "+
										"larger heap size by setting the " +
										"-Xmx flag on the JVM." +
										"\nThe program will now exit.", 
										"OutOfMemory",
										JOptionPane.WARNING_MESSAGE);
								System.err.println("Not enough memory. Please load a smaller "+
								"image or use larger heap size.");
								System.err.println("Now exiting...");
								System.exit(-1);
							}
						} catch(InterruptedException ex) { 
							ex.printStackTrace();
						}
					}//end while block
				}//end run function
			};//end thread function
			memMonitor.setPriority(Thread.NORM_PRIORITY);
			memMonitor.start();    
		} catch (Exception ex) {
			ex.printStackTrace();
			System.err.println(ex.getMessage());
		}
		
	}
	
	
	//VARIABLES
	
	/**
	 * The gui for this project.
	 */
	private static GUI mGUI;
	
	/** 
	 * The size in bytes of the java virtual machine at the start. This 
	 * represents the critical amount of memory that is necessary for the  
	 * program to run. If our free memory falls below (or is very close) to this 
	 * critical amount then the virtual machine throws an OutOfMemoryError.
	 */
	private static long m_initialJVMSize;  
	
}
//end of class.
