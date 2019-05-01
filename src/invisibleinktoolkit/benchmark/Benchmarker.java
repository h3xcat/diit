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


/**
 * Runs a series of benchmarks on images.
 * <P>
 * This class will benchmark original vs stego images as well as evaluating
 * stego images by themselves.  The tests that are run depends on the
 * options set in the constructor.  It can also output benchmarks as plain text.
 *
 * @author Kathryn Hempstalk.
 */  
public class Benchmarker{
	
	//CONSTRUCTORS
	
	/**
	 * Sets up the benchmarker ready for use.  All tests are set as
	 * per the entered values.
	 * 
	 * @param runaadiff Whether to run Average Absolute Difference.
	 * @param runmserror Whether to run Mean Squared Error.
	 * @param runlpnorm Whether to run Lp Norm.
	 * @param runlpmserror Whether to run Laplacian Mean Squared Error.
	 * @param runsnr Whether to run Signal to Noise Ratio.
	 * @param runpeaksnr Whether to run Peak Signal to Noise Ratio.
	 * @param runncc Whether to run Normalised Cross Correlation.
	 * @param runcquality Whether to run Correlation Quality.
	 */
	public Benchmarker(boolean runaadiff, boolean runmserror,
			boolean runlpnorm, boolean runlpmserror,
			boolean runsnr, boolean runpeaksnr,
			boolean runncc, boolean runcquality){
		mResultsString = "";
		mRunAverageAbsoluteDifference = runaadiff;
		mRunMeanSquaredError = runmserror;
		mRunLpNorm = runlpnorm;	
		mRunLaplacianMeanSquaredError = runlpmserror;
		mRunSignalToNoiseRatio = runsnr;
		mRunPeakSignalToNoiseRatio = runpeaksnr;
		mRunNormalisedCrossCorrelation = runncc;
		mRunCorrelationQuality = runcquality;
	}
	
	//FUNCTIONS
	
	/**
	 * Runs all the benchmarking tests.
	 *
	 * @param original The original image to compare against. 
	 * @param stego The stego image to test.  
	 * @return All the results as text.
	 * @throws IllegalArgumentException If the stego image is null.
	 * @throws Exception If it has problems reading the images.
	 */
	public String run(BufferedImage original, BufferedImage stego) 
	throws IllegalArgumentException, Exception{
		if(original == null)
			throw new IllegalArgumentException("Original image must not be null!");
		if(stego == null)
			throw new IllegalArgumentException("Stego image must not be null!");
		
		//setup temp variables
		Benchmark bench;
		mResultsString = "Results of benchmark tests\n"
			+ "==========================\n\n";
		
		
		
		//run all the tests...
		if(mRunAverageAbsoluteDifference){
			bench = new AverageAbsoluteDifference();
			mResultsString = mResultsString
			+ bench.toString()
			+ ": " 
			+ bench.calculate(original,stego)
			+ "\n";	    
		}
		if(mRunMeanSquaredError){
			bench = new MeanSquaredError();
			mResultsString = mResultsString
			+ bench.toString()
			+ ": " 
			+ bench.calculate(original,stego)
			+ "\n";	    
		}
		if(mRunLpNorm){
			bench = new LpNorm();
			mResultsString = mResultsString
			+ bench.toString()
			+ ": " 
			+ bench.calculate(original,stego)
			+ "\n";	    
		}
		if(mRunLaplacianMeanSquaredError){
			bench = new LaplacianMeanSquaredError();
			mResultsString = mResultsString
			+ bench.toString()
			+ ": " 
			+ bench.calculate(original,stego)
			+ "\n";	    
		}
		if(mRunSignalToNoiseRatio){
			bench = new SignalToNoiseRatio();
			mResultsString = mResultsString
			+ bench.toString()
			+ ": " 
			+ bench.calculate(original,stego)
			+ "\n";	    
		}
		if(mRunPeakSignalToNoiseRatio){
			bench = new PeakSignalToNoiseRatio();
			mResultsString = mResultsString
			+ bench.toString()
			+ ": " 
			+ bench.calculate(original,stego)
			+ "\n";	    
		}
		if(mRunNormalisedCrossCorrelation){
			bench = new NormalisedCrossCorrelation();
			mResultsString = mResultsString
			+ bench.toString()
			+ ": " 
			+ bench.calculate(original,stego)
			+ "\n";	    
		}
		if(mRunCorrelationQuality){
			bench = new CorrelationQuality();
			mResultsString = mResultsString
			+ bench.toString()
			+ ": " 
			+ bench.calculate(original,stego)
			+ "\n";	    
		}
		
		
		return mResultsString;
	}
		
	
	/**
	 * Returns the last results of this benchmarker.
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
	 * A string holding the results of all the benchmarking tests.
	 */
	private String mResultsString;
	
	/**
	 * Whether to run an Average Absolute Difference benchmark.
	 */
	private boolean mRunAverageAbsoluteDifference;
	
	/**
	 * Whether to run a Mean Squared Error benchmark.
	 */
	private boolean mRunMeanSquaredError;
	
	/**
	 * Whether to run a LpNorm benchmark.
	 */
	private boolean mRunLpNorm;
	
	/**
	 * Whether to run a Laplacian Mean Squared Error benchmark.
	 */
	private boolean mRunLaplacianMeanSquaredError;
	
	/**
	 * Whether to run a Signal-to-Noise Ratio benchmark.
	 */
	private boolean mRunSignalToNoiseRatio;
	
	/**
	 * Whether to run a Peak Signal-to-Noise Ratio benchmark.
	 */
	private boolean mRunPeakSignalToNoiseRatio;
	
	/**
	 * Whether to run a Normalised Cross-Correlation benchmark.
	 */
	private boolean mRunNormalisedCrossCorrelation;
	
	/**
	 * Whether to run a Correlation Quality benchmark.
	 */
	private boolean mRunCorrelationQuality;
	
	
}
//end of class.
