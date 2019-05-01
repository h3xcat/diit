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

package invisibleinktoolkit.benchmark;

import java.awt.image.BufferedImage;

import invisibleinktoolkit.stego.*;
import invisibleinktoolkit.util.TestingUtils;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import javax.imageio.ImageIO;
import java.util.Enumeration;


/**
 * Allows for steganalysis to be run for images.
 * <P>
 * This steganalyser can do RS Analysis, Sample Pairs Analysis and
 * Laplace graphs.  The laplace graphs are printed in CSV format unless
 * it is run for an arff file. 
 *
 * @author Kathryn Hempstalk.
 */  
public class StegAnalyser{
	
    //CONSTRUCTORS
	
    /**
     * Sets up steganalysis types that will be run.
     * 
     * @param runrsanalysis Whether to run RS analysis.
     * @param runsamplepairs Whether to run Sample Pairs.
     * @param runlaplacegraph Whether to run a Laplace Graph.
     */
    public StegAnalyser(boolean runrsanalysis,
			boolean runsamplepairs, boolean runlaplacegraph){
	mResultsString = "";
	mRunRSAnalysis = runrsanalysis;
	mRunSamplePairs = runsamplepairs;
	mRunLaplaceGraph = runlaplacegraph;
    }
	
    //FUNCTIONS
	
    /**
     * Runs all the steganalysis.
     *
     * @param stego The stego image to test.  
     * @return All the results as text.
     * @throws IllegalArgumentException If the stego image is null.
     * @throws Exception If it has problems reading the images.
     */
    public String run(BufferedImage stego) 
	throws IllegalArgumentException, Exception{
		
	if(stego == null)
	    throw new IllegalArgumentException("Stego image must not be null!");
		
		
	StringBuffer results = new StringBuffer("Results of steganalysis\n"
						+ "==========================\n\n");
		
	String colour;
	double averageresults = 0, averagelength = 0;
		
	//RS Analysis
	if(mRunRSAnalysis){
	    results.append("RS ANALYSIS\n" + "============\n\n");
	    results.append("RS Analysis (Non-overlapping groups)\n");
	    for(int j = 0; j < 3; j++){
		RSAnalysis rsa = new RSAnalysis(2,2);
		double[] testresults = 
		    rsa.doAnalysis(stego, j, false);
				
		//get the right colour
		if(j == 0)
		    colour = "red";
		else if(j == 1)
		    colour = "green";
		else
		    colour = "blue";
				
		//append the results
		results.append("Percentage in " + colour + ": ");
				
		//round and append results
		results.append(round(testresults[26] * 100, 5) + "\n");
				
		//and the approximate length (in bytes)
		results.append("Approximate length (in bytes) from " + colour + ": "
			       + round(testresults[27], 5) + "\n");
				
		averageresults += testresults[26];
		averagelength += testresults[27];
	    }
			
	    //now do again for overlapping groups
	    results.append("\nRS Analysis (Overlapping groups)\n");
	    for(int j = 0; j < 3; j++){
		RSAnalysis rsa = new RSAnalysis(2,2);
		double[] testresults = 
		    rsa.doAnalysis(stego, j, true);
				
		//get the right colour
		if(j == 0)
		    colour = "red";
		else if(j == 1)
		    colour = "green";
		else
		    colour = "blue";
				
		//append the results
		results.append("Percentage in " + colour + ": ");
				
		//round and append results
		results.append(round(testresults[26] * 100, 5) + "\n");
				
		//and the approximate length (in bytes)
		results.append("Approximate length (in bytes) from " + colour + ": "
			       + round(testresults[27], 5) + "\n");
				
		averageresults += testresults[26];
		averagelength += testresults[27];
	    }
			
	    results.append("\nAverage across all groups/colours: " +
			   round(averageresults/6 * 100,5) );
	    results.append("\nAverage approximate length across all groups/colours: " + 
			   round(averagelength/6, 5) );
	    results.append("\n\n\n");
	}
		
		
	//Sample Pairs
	averageresults = 0;
	averagelength = 0;
	if(mRunSamplePairs){
	    results.append("SAMPLE PAIRS\n" + "=============\n");
	    for(int j = 0; j < 3; j++){
		SamplePairs sp = new SamplePairs();
		double estimatedlength = sp.doAnalysis(stego, j);
		double numbytes = ((stego.getHeight() * stego.getWidth() * 3)/8)
		    * estimatedlength;
				
		//get the right colour
		if(j == 0)
		    colour = "red";
		else if(j == 1)
		    colour = "green";
		else
		    colour = "blue";
				
		//append the results
		results.append("Percentage in " + colour + ": ");
				
		//round and append results
		results.append(round(estimatedlength * 100, 5) + "\n");
				
		//and the approximate length (in bytes)
		results.append("Approximate length (in bytes) from " + colour + ": "
			       + round(numbytes, 5) + "\n");
				
		averageresults += estimatedlength;
		averagelength += numbytes;
	    }
				   
		//average results
	    results.append("\nAverage across all groups/colours: " +
			   round(averageresults/3 * 100,5) );
	    results.append("\nAverage approximate length across all groups/colours: " + 
			   round(averagelength/3, 5) );
	    results.append("\n\n\n");
	}
		
		
	//Laplace graph
	if(mRunLaplaceGraph){
	    results.append("LAPLACE GRAPH (CSV formatted)\n" 
			   + "==============================\n\n");
	    results.append(LaplaceGraph.getCSVGraph(stego));			
	}
		
	//append some new lines to make it look nice
	results.append("\n\n\n\n");
		
	mResultsString = results.toString();
	return mResultsString;
    }
	
	
    /**
     * Combines two directories into an output directory.
     * <P>
     * The output directory (tempdir) will have all the original
     * images plus all the stegoimages copied into it.
     * 
     * @param messagedir The directory full of messages in (txt files).
     * @param imagedir The directory full of images (png, jpg, bmp).
     * @param tempdir The directory to write the results to.
     * @param algorithm The algorithm to use to combine the messages and
     * images with.
     * @throws IllegalArgumentException If any directories are null.
     * @return A string full of skipped files.
     */
    public String createCombineDirectories(File messagedir, File imagedir,
					   File tempdir,
					   StegoAlgorithm algorithm)
	throws IllegalArgumentException{
		
	if(!messagedir.isDirectory() || !imagedir.isDirectory() ||
	   !tempdir.isDirectory()){
	    throw new IllegalArgumentException
		("All passed files must be directories!");
	}
		
	StringBuffer errors = new StringBuffer("Errors: \n========\n\n");
	String[] messagelist = messagedir.list();
	String[] imagelist = imagedir.list();
	CoverImage cimage;
	InsertableMessage imess;
	String coverfilepath, messagefilepath, outputpath, originalname;
	String fileseparator = System.getProperty("file.separator");
		
	//for each file in the image folder...
	for(int i = 0; i < imagelist.length; i++){
	    if(imagelist[i].endsWith(".bmp") ||
	       imagelist[i].endsWith(".jpg") ||
	       imagelist[i].endsWith(".png")){
		//ok to combine it...
		coverfilepath = imagedir.getPath() + fileseparator
		    + imagelist[i];
		originalname = imagelist[i].substring(0, imagelist[i].indexOf("."));
				
		//for each message in the message folder...
		for(int j = 0; j < messagelist.length; j++){
		    try{
			if(messagelist[j].endsWith(".txt")){
			    //ok to combine...
			    messagefilepath = messagedir.getPath() + 
				fileseparator + messagelist[j];
							
			    //setup the two files...
			    cimage = new CoverImage(new FileInputStream(coverfilepath));
			    imess = new InsertableMessage(messagefilepath);
							
			    //setup the filename...
			    outputpath = originalname + "~"
				+ messagelist[j].substring(0,messagelist[j].lastIndexOf("."))
				+ "-" 
				+ algorithm.getClass().getName().toLowerCase().substring
				(algorithm.getClass().getName().lastIndexOf(".") + 1,
				 algorithm.getClass().getName().length())
				+ "." + "png";
							
			    //write it
			    StegoImage stego = algorithm.encode(imess, cimage, 0);
			    stego.write("png", new File(tempdir, outputpath));			
			}
		    }catch(Exception e){
			//just go onto the next one...
			errors.append("Error: Could not process: " + imagelist[i]
				      + " with " + messagelist[j] + "\n");
		    }
		    //end image loop
		    //cleanup to speed up memory
		    System.gc();
		}
	    }
			
	}
	//now we have a folder full of images - copy in the source files...
	try{
	    TestingUtils.copyIntoTempFolder(imagedir, tempdir, tempdir);
	}catch(Exception e2){
	    //do nothing
	}		
		
	return errors.toString();
    }
	
	
    /**
     * Generates a CSV formatted string of steganalysis information.
     * <P>
     * The directory passed has all it's image files steganalysed and
     * the results are returned in a comma separated file format. No spaces
     * are used in column headings, and all bar the steganography type are
     * numerical values.
     * 
     * @param directory The directory to steganalyse.
     * @param laplacelimit The number of laplace values to write out in total.
     * @return A string containing a csv file of results.
     */
    public String getCSV(File directory, int laplacelimit){
		
	//output progress to console
	System.out.print("\n\nCSV Progress: {");
	String[] files = directory.list();
	int fivepercent = (int)Math.floor(files.length / 20);
		
	StringBuffer csv = new StringBuffer();
		
	//add all the headings
	if(mRunRSAnalysis){
	    RSAnalysis rsa = new RSAnalysis(2,2);
	    String rflag = "(rs overlapping)";
	    String colour;
			
	    //overlapping
	    for(int i = 0; i < 3; i++){
		Enumeration rnames = rsa.getResultNames();
		//get the right colour
		if(i == 0)
		    colour = " red ";
		else if(i == 1)
		    colour = " green ";
		else
		    colour = " blue ";
		while(rnames.hasMoreElements()){
		    String aname = (String) rnames.nextElement();
		    String towrite = aname + colour + rflag + ",";
		    towrite = towrite.replace(' ', '-');
		    csv.append(towrite);
		}
	    }
			
	    //non overlapping
	    rflag = "(rs non-overlapping)";
	    for(int i = 0; i < 3; i++){
		Enumeration rnames = rsa.getResultNames();
		//get the right colour
		if(i == 0)
		    colour = " red ";
		else if(i == 1)
		    colour = " green ";
		else
		    colour = " blue ";
		while(rnames.hasMoreElements()){
		    String aname = (String) rnames.nextElement();
		    String towrite = aname + colour + rflag + ",";
		    towrite = towrite.replace(' ', '-');
		    csv.append(towrite);
		}
	    }
			
	}
	if(mRunSamplePairs){
	    String colour;
			
	    //overlapping
	    for(int i = 0; i < 3; i++){
		//get the right colour
		if(i == 0)
		    colour = "-red-";
		else if(i == 1)
		    colour = "-green-";
		else
		    colour = "-blue-";
		csv.append("SP-Percentage" + colour + ",");
		csv.append("SP-Approximate-Bytes" + colour + ",");
	    }			
	}
	if(mRunLaplaceGraph){
	    for(int i = 0; i < laplacelimit; i++){
		csv.append("Laplace-value-" + i + ",");
	    }
	}
	csv.append("Steganography-Type,Image-Name\n");
		
		
		
	//check all the files
	for (int i = 0; i < files.length; i++) {
			
	    //print progress
		if(i > 0 && fivepercent > 0){
			if(i % fivepercent == 0){
				System.out.print("#");
			}
		}
			
	    if (files[i].endsWith(".bmp") || files[i].endsWith(".png")
		|| files[i].endsWith(".jpg")) {
		//file can be worked on.
				
				
		BufferedImage image;
		String flag;
				
		try{
		    image = ImageIO.read(new File(directory, files[i]));
					
		    //run RS analysis
		    if(mRunRSAnalysis){
			//overlapping
			for(int j = 0; j < 3; j++){
			    RSAnalysis rsa = new RSAnalysis(2,2);
			    double[] testresults = 
				rsa.doAnalysis(image, j, true);
							
			    for(int k = 0; k < testresults.length; k++){
				csv.append(testresults[k] + ",");
			    }
			}
			//non-overlapping
			for(int j = 0; j < 3; j++){
			    RSAnalysis rsa = new RSAnalysis(2,2);
			    double[] testresults = 
				rsa.doAnalysis(image, j, false);
							
			    for(int k = 0; k < testresults.length; k++){
				csv.append(testresults[k] + ",");
			    }
			}
		    }
					
		    //run Sample Pairs
		    if(mRunSamplePairs){
			//overlapping
			for(int j = 0; j < 3; j++){
			    SamplePairs sp = new SamplePairs();
			    double estimatedlength = sp.doAnalysis(image, j);
			    double numbytes = ((image.getHeight() * image.getWidth() * 3)/8)
				* estimatedlength;
			    csv.append(estimatedlength + "," + numbytes + ",");
			}
			}
					
		    //run LaplaceGraph
		    if(mRunLaplaceGraph){
			double[][] lgres = LaplaceGraph.getGraph(image);
						
			for(int j = 0; j < laplacelimit; j++){
			    if(lgres.length <= laplacelimit && j >= lgres.length){
				csv.append("0,");
			    }else{
				if(lgres[j][0] != j)
				    csv.append("0,");
				else
				    csv.append(lgres[j][1] + ",");
			    }
			}
		    }
					
		    if(files[i].indexOf("_") >= 0 || files[i].indexOf("-") >= 0){
			if(files[i].indexOf("_") >= 0)
			    flag = files[i].substring(files[i].indexOf("_") + 1, files[i].lastIndexOf("."));
			else
			    flag = files[i].substring(files[i].indexOf("-") + 1, files[i].lastIndexOf("."));
		    }else
			flag = "none";
					
		    csv.append(flag);
		    //append in the file name
		    csv.append("," + files[i]);
					
		    if(csv.charAt(csv.length() - 1) == ',')
			csv.deleteCharAt(csv.length() - 1);
					
		    csv.append("\n");
					
		}catch(Exception e1){
		    //skip the file...
		}
		//cleanup to speed up memory
		System.gc();
	    }
	}
		
	//all done
	System.out.println("} Complete!");
		
	csv.append("\n");
	return csv.toString();
    }
	
	
	
	
    /**
     * Creates an ARFF file of steganography information.
     * <P>
     * An ARFF file is the natural internal format for WEKA - Waikato
     * Environment for Knowledge Analysis.  WEKA can also handle CSV
     * files but it is much nicer to be able to produce the natural format.
     * The same information as per the CSV generator is produced here, just
     * in a different format.
     * 
     * @param directory The directory to steganalyse.
     * @param laplacelimit The maximum number of laplace values to output.
     * @param relationname The internal name of the relation as it will be
     * seen in WEKA.
     * @return An ARFF formatted file full of the steganalysis information.
     * @see www.cs.waikato.ac.nz/ml/weka
     * 
     */
    public String getARFF(File directory, int laplacelimit, String relationname){
	StringBuffer arff = new StringBuffer();
		
	//output progress to console
	System.out.print("\n\nARFF Progress: {");
	String[] files = directory.list();
	int fivepercent = (int)Math.floor(files.length / 20);
		
		
	arff.append("% Steganography Benchmarking Data\n%\n");
	arff.append("% Sourced from automatic generation in Digital Invisible Ink Toolkit\n");
	arff.append("% Generator created by Kathryn Hempstalk.\n");
	arff.append("% Generator copyright under the Gnu General Public License, 2005\n");
	arff.append("\n");
		
	arff.append("\n@relation '" + relationname + "'\n\n");
		
		
	//add all the headings
	if(mRunRSAnalysis){
	    RSAnalysis rsa = new RSAnalysis(2,2);
	    String rflag = "(rs overlapping)";
	    String colour;
			
	    //overlapping
	    for(int i = 0; i < 3; i++){
		Enumeration rnames = rsa.getResultNames();
		//get the right colour
		if(i == 0)
		    colour = " red ";
		else if(i == 1)
		    colour = " green ";
		else
		    colour = " blue ";
		while(rnames.hasMoreElements()){
		    String aname = (String) rnames.nextElement();
		    String towrite = aname + colour + rflag;
		    arff.append("@attribute '" + towrite + "' numeric\n");
		}
	    }
			
	    //non overlapping
	    rflag = "(rs non-overlapping)";
	    for(int i = 0; i < 3; i++){
		Enumeration rnames = rsa.getResultNames();
		//get the right colour
		if(i == 0)
		    colour = " red ";
		else if(i == 1)
		    colour = " green ";
		else
		    colour = " blue ";
		while(rnames.hasMoreElements()){
		    String aname = (String) rnames.nextElement();
		    String towrite = aname + colour + rflag;
		    arff.append("@attribute '" + towrite + "' numeric\n");
		}
	    }
			
	}
	if(mRunSamplePairs){
	    String colour;
			
	    //overlapping
	    for(int i = 0; i < 3; i++){
		//get the right colour
		if(i == 0)
		    colour = " red ";
		else if(i == 1)
		    colour = " green ";
		else
		    colour = " blue ";
		arff.append("@attribute 'SP Percentage" + colour + "' numeric\n");
		arff.append("@attribute 'SP Approximate Bytes" + colour + "' numeric\n");
	    }
	}
	if(mRunLaplaceGraph){
	    for(int i = 0; i < laplacelimit; i++){
		arff.append("@attribute 'Laplace value " + i + "' numeric\n");
	    }
	}
		
		
	arff.append("@attribute 'Steganography Type' {");
	//iterate through all the hashmap values...
	HashMap stegotypes = getStegTypes(directory.list());
	Object[] valuesarray = stegotypes.values().toArray();
	arff.append( (String) valuesarray[0]);
	for(int i = 1; i < valuesarray.length; i++){
	    arff.append("," + (String)valuesarray[i]);
	}
	arff.append("}\n");
	arff.append("@attribute 'Image Name' string\n");


	arff.append("\n@data\n");
		
		
	//check all the files
	for (int i = 0; i < files.length; i++) {
			
	    //print progress
		if(i > 0 && fivepercent > 0){
			if(i % fivepercent == 0 && i != 0){
				System.out.print("#");
			}
		}
			
	    if (files[i].endsWith(".bmp") || files[i].endsWith(".png")
		|| files[i].endsWith(".jpg")) {
		//file can be worked on.
				
				
		BufferedImage image;
		String flag;
				
		try{
		    image = ImageIO.read(new File(directory, files[i]));
					
		    //run RS analysis
		    if(mRunRSAnalysis){
			//overlapping
			for(int j = 0; j < 3; j++){
			    RSAnalysis rsa = new RSAnalysis(2,2);
			    double[] testresults = 
				rsa.doAnalysis(image, j, true);
							
			    for(int k = 0; k < testresults.length; k++){
				arff.append(testresults[k] + ",");
			    }
			}
			//non-overlapping
			for(int j = 0; j < 3; j++){
			    RSAnalysis rsa = new RSAnalysis(2,2);
			    double[] testresults = 
				rsa.doAnalysis(image, j, false);
							
			    for(int k = 0; k < testresults.length; k++){
				arff.append(testresults[k] + ",");
			    }
			}
		    }
					
		    //run Sample Pairs
		    if(mRunSamplePairs){
			//overlapping
			for(int j = 0; j < 3; j++){
			    SamplePairs sp = new SamplePairs();
			    double estimatedlength = sp.doAnalysis(image, j);
			    double numbytes = ((image.getHeight() * image.getWidth() * 3)/8)
				* (estimatedlength / 100);
			    arff.append(estimatedlength + "," + numbytes + ",");
			}
			}
					
		    //run LaplaceGraph
		    if(mRunLaplaceGraph){
			double[][] lgres = LaplaceGraph.getGraph(image);
						
			for(int j = 0; j < laplacelimit; j++){
			    if(lgres.length <= laplacelimit && j >= lgres.length){
				arff.append("0,");
			    }else{
				if(lgres[j][0] != j)
				    arff.append("0,");
				else
				    arff.append(lgres[j][1] + ",");
			    }
			}
		    }
					
		    if(files[i].indexOf("_") >= 0 || files[i].indexOf("-") >= 0){
			if(files[i].indexOf("_") >= 0)
			    flag = files[i].substring(files[i].indexOf("_") + 1, files[i].lastIndexOf("."));
			else
			    flag = files[i].substring(files[i].indexOf("-") + 1, files[i].lastIndexOf("."));
		    }else
			flag = "none";
					
		    arff.append(flag);
		    arff.append("," + files[i]);
					
		    if(arff.charAt(arff.length() - 1) == ',')
			arff.deleteCharAt(arff.length() - 1);
					
		    arff.append("\n");
					
		}catch(Exception e1){
		    //skip the file...
		}
		//cleanup to speed up memory
		System.gc();
	    }
	}
		
	//all done
	System.out.println("} Complete!");
		
	return arff.toString();
    }
	
	
	
    /**
     * Gets a map of all the steganography types in a 
     * given list of files.
     * <P>
     * A steganography type is the word between an underscore or hyphen and
     * the final dot in the file. None is added by default.  If there is no
     * hyphen/underscore then no steg type will be added.
     *
     * @param filelist The list of files.
     * @return A hashmap full of steganography names.
     */
    private HashMap getStegTypes(String[] files){
	HashMap stegotypes = new HashMap();
	stegotypes.put("none","none");
	String stegtype = "";
	for (int i = 0; i < files.length; i++) {
	    if (files[i].endsWith(".bmp") || files[i].endsWith(".png")) {
		//also add in the stego type
		if(files[i].indexOf("_") >= 0){
		    //steg type detected...
		    stegtype = files[i].substring
			(files[i].indexOf("_") + 1, files[i].lastIndexOf("."));
		    stegotypes.put(stegtype, stegtype);
		}
		else if (files[i].indexOf("-") >= 0){
		    //steg type detected...
		    stegtype = files[i].substring
			(files[i].indexOf("-") + 1, files[i].lastIndexOf("."));
		    stegotypes.put(stegtype, stegtype);
		}
	    }			
	}
		
	return stegotypes;
    }
	
	
	
    /**
     * Returns the last results of this steg analyser.
     *
     * @return The last results.
     */
    public String toString(){
	return mResultsString;
    }
	
	
    /**
     * Rounds to the specified number of decimal places.
     *
     * @param number The number to round.
     * @param places The number of decimal places to round to.
     * @return The rounded number.
     */
    public double round(double number, int places){
	double multiple = Math.pow(10, places);
	number = number * multiple;
	long num2 = Math.round(number);
	number = num2 / multiple;
		
	if(number < 0.000000000001)
	    return 0;
	return number;
    }
	
	
    //VARIABLES
	
    /**
     * A string holding the results of all the steganalysis.
     */
    private String mResultsString;
	
    /**
     * Whether to run RS Analysis.
     */
    private boolean mRunRSAnalysis;
	
    /**
     * Whether to run Sample Pairs.
     */
    private boolean mRunSamplePairs;
	
    /**
     * Whether to run a Laplace Graph.
     */
    private boolean mRunLaplaceGraph;
	
}
//end of class.
