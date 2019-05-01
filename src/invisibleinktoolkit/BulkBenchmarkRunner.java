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

import invisibleinktoolkit.util.TestingUtils;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.imageio.ImageIO;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.swing.UIManager;
import java.io.BufferedWriter;
import java.io.FileWriter;
import invisibleinktoolkit.util.AFileFilter;
import java.awt.image.BufferedImage;
import invisibleinktoolkit.benchmark.LaplaceGraph;
import invisibleinktoolkit.benchmark.TraditionalLaplaceGraph;

/**
 * A class to bulk run benchmarks for steganography.
 * <P>
 * This allows for the following: creation of random messages,
 * combining two folders for stego output and outputting
 * a csv file for graphing an image laplace values.
 *
 * @author Kathryn Hempstalk
 */
public class BulkBenchmarkRunner{
	
	/**
	 * A main class to run the bulk benchmarker.
	 *
	 */
	public static void main(String[] args){
		
		//use system look & feel
		try { 
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {}
		
		//now on with it...
		try{
			stdin = new BufferedReader(new InputStreamReader(System.in));
			printOptions();
			int input = getUserInput();
			while(input != 6){
				if(input == 1){
					//user wants to create random messages...
					//get a directory
					System.out.println("Please select a message output folder\n");
					File directory = getFolder();
					if(directory == null){
						System.exit(1);
					}
					System.out.println("Please enter the starting number of KB:");
					int start = getUserInput();
					System.out.println("Please enter the amount of KB to increment by:");
					int inc = getUserInput();
					System.out.println("Please enter the number of files to write:");
					int numberfiles = getUserInput();
					TestingUtils.createRandomMessages
					(directory, start, inc, numberfiles);
				}
				if(input == 2){
					//user wants to create structured messages...
					//get a directory
					//get the file
					System.out.println("Please select a file to use for source\n");
					JFileChooser jfc = new JFileChooser();
					jfc.setDialogTitle("Pick a file to use for messages.");
					int choice = jfc.showSaveDialog(null);
					File tfile;
					if(choice == JFileChooser.APPROVE_OPTION){
						tfile = jfc.getSelectedFile();
						System.out.println("Please select a message output folder\n");
						File directory = getFolder();
						if(directory != null){
							System.out.println("Please enter the starting number of KB:");
							int start = getUserInput();
							System.out.println("Please enter the amount of KB to increment by:");
							int inc = getUserInput();
							System.out.println("Please enter the number of files to write:");
							int numberfiles = getUserInput();
							TestingUtils.cutTextIntoMessages
							(tfile, directory, start, inc, numberfiles);
						}else
							System.out.println("Cancelling...\n");
					}
					else{
						System.out.println("Cancelling...\n");
					}					
				}
				
				if(input == 3){
					//combine two folders 
					//get a directory
					System.out.println("Please select a image input folder\n");
					File imagedirectory = getFolder();
					System.out.println("Please select a message input folder\n");
					File messagedirectory = getFolder();
					System.out.println("Please select a stego output folder\n");
					File outdirectory = getFolder();
					String[] algorithms = {"invisibleinktoolkit.algorithms.BlindHide",
							"invisibleinktoolkit.algorithms.BattleSteg",
							"invisibleinktoolkit.algorithms.FilterFirst",
					"invisibleinktoolkit.algorithms.HideSeek"};
					String[] filters = {"invisibleinktoolkit.filters.Laplace",
					"invisibleinktoolkit.filters.Sobel"};
					System.out.println("Working, please wait....\n");
					TestingUtils.combineFolders(imagedirectory, messagedirectory,
							outdirectory, false, algorithms, filters);	    
				}
				if(input == 4){
					//laplace graph
					System.out.println("Pick the image file...\n");
					BufferedImage stego = (ImageIO.read(getInputStegoFile("Pick the stego image file")));
					String graph = LaplaceGraph.getCSVGraph(stego);
					
					JFileChooser jfc = new JFileChooser();
					jfc.setDialogTitle("Pick a file to write the results to.");
					System.out.println("Pick a file to write the results to...\n");
					int choice = jfc.showSaveDialog(null);
					if(choice == JFileChooser.APPROVE_OPTION){
						BufferedWriter bw = new BufferedWriter(new FileWriter(jfc.getSelectedFile()));
						bw.write(graph, 0, graph.length());
						bw.close();
					}
					else{
						System.out.println("Cancelling...\n");
					}
					
				}if(input == 5){
					//traditional laplace graph
					System.out.println("Pick the image file...\n");
					BufferedImage stego = (ImageIO.read(getInputStegoFile("Pick the stego image file")));
					String graph = TraditionalLaplaceGraph.getCSVGraph(stego);
					
					JFileChooser jfc = new JFileChooser();
					jfc.setDialogTitle("Pick a file to write the results to.");
					System.out.println("Pick a file to write the results to...\n");
					int choice = jfc.showSaveDialog(null);
					if(choice == JFileChooser.APPROVE_OPTION){
						BufferedWriter bw = new BufferedWriter(new FileWriter(jfc.getSelectedFile()));
						bw.write(graph, 0, graph.length());
						bw.close();
					}
					else{
						System.out.println("Cancelling...\n");
					}
					
				}
				
				//print options again...
				printOptions();
				input = getUserInput(); 
			}
			
			System.exit(0);
			
		}catch(Exception e){
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
		
	}
	
	
	//FUNCTIONS
	
	/**
	 * Prints all the options to be displayed at the command line.
	 *
	 */
	private static void printOptions(){
		System.out.println("\nDigital Invisible Ink Toolkit Bulk Benchmarker");
		System.out.println("==============================================\n");
		System.out.println("Please pick one of the following: \n");
		System.out.println(" 1. Generate random messages.");
		System.out.println(" 2. Generate messages from a given file.");
		System.out.println(" 3. Combine two folders of images/messages.");
		System.out.println(" 4. Output a CSV form laplace graph.");
		System.out.println(" 5. Output a traditional CSV form laplace graph.");
		System.out.println(" 6. Exit the program.");
		System.out.println();
		System.out.println();
	}	
	
	
	/**
	 * Gets input from the user as a number..
	 *
	 * @return The users input as a number.
	 */
	private static int getUserInput(){
		try{
			System.out.print(" > ");
			String info = stdin.readLine();
			int results = Integer.parseInt(info);
			//make an escape sequence...
			if(results <= -1) 
				System.exit(0);
			System.out.println();
			return results;
		}catch(Exception e){
			System.out.println();
			System.out.println("Error getting input, please enter a number.");
			System.out.println();
			return getUserInput();
		}
	}
	
	/**
	 * Lets the user choose a folder graphically.
	 *
	 * @return The path to the folder, or null if the user hit cancel.
	 */
	private static File getFolder(){
		JFileChooser jfc = new JFileChooser();
		jfc.setDialogTitle("Pick a directory to benchmark");
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int choice = jfc.showOpenDialog(null);
		
		if(choice == JFileChooser.APPROVE_OPTION)
			return jfc.getSelectedFile();
		else
			return null;
	}
	
		
	/**
	 * Gets the files from a user to benchmark.
	 *
	 * @param message The message to show in the dialog title.
	 * @return The selected file.
	 */
	private static File getInputStegoFile(String message){
		while(true){
			JFileChooser jfc = new JFileChooser();
			jfc.setDialogTitle(message);
			jfc.addChoosableFileFilter(new AFileFilter(".png", "PNG images (.png)"));
			jfc.addChoosableFileFilter(new AFileFilter(".bmp", "Bitmap images (.bmp)"));
			jfc.showOpenDialog(null);
			
			if(jfc.getSelectedFile().getPath().endsWith(".png")
					|| jfc.getSelectedFile().getPath().endsWith(".bmp"))
				return jfc.getSelectedFile();
			
			
			JOptionPane.showMessageDialog(null, "You must enter in a valid image type!",
					"Error! Wrong filetype!",
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	
	//VARIABLES
	
	/**
	 * Console input.
	 */
	private static BufferedReader stdin;
	
	
}
//end of class.
