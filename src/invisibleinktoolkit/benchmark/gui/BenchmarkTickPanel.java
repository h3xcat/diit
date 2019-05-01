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

package invisibleinktoolkit.benchmark.gui;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.border.TitledBorder;
import java.awt.Dimension;
import javax.swing.JCheckBox;

/**
 * A tick box panel for picking benchmark methods.
 * <P>
 * This panel contains 8 tick boxes - and depending on the system
 * they should all be evenly laid out on the screen.
 *
 * @author Kathryn Hempstalk.
 */
public class BenchmarkTickPanel extends JPanel{
	
	//CONSTRUCTORS
	
	/**
	 * Sets up a tick box panel for benchmarking.
	 *
	 */
	public BenchmarkTickPanel(){
		super();
		//setup the layout
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(new TitledBorder("Tick the analysis types to run"));
		this.setPreferredSize(new Dimension(740,100));
		
		JPanel firstline = new JPanel();
		firstline.setLayout(new BoxLayout(firstline, BoxLayout.X_AXIS));
		firstline.setPreferredSize(new Dimension(740, 30));
		
		JPanel secondline = new JPanel();
		secondline.setLayout(new BoxLayout(secondline, BoxLayout.X_AXIS));
		secondline.setPreferredSize(new Dimension(740, 30));
		
		//setup the checkboxes - first line
		mAADifference = new JCheckBox("Average Absolute Difference",
				true);
		mAADifference.setToolTipText("Average Absolute Difference");
		mAADifference.setPreferredSize(new Dimension(180, 26));
		firstline.add(mAADifference);
		
		mCorrelationQuality = new JCheckBox("Correlation Quality",
				true);
		mCorrelationQuality.setToolTipText("Correlation Quality");
		mCorrelationQuality.setPreferredSize(new Dimension(180,26));
		firstline.add(mCorrelationQuality);
		
		mLPMSError = new JCheckBox("Laplacian Mean Squared Error",
				true);
		mLPMSError.setToolTipText("Laplacian Mean Squared Error");
		mLPMSError.setPreferredSize(new Dimension(180, 26));
		firstline.add(mLPMSError);
		
		mLpNorm = new JCheckBox("Lp Norm", true);
		mLpNorm.setToolTipText("Lp Norm");
		mLpNorm.setPreferredSize(new Dimension(180,26));
		firstline.add(mLpNorm);
		
		JPanel spacer1 = new JPanel();
		firstline.add(spacer1);
		
		//checkboxes... second line
		mMSError = new JCheckBox("Mean Squared Error",
				true);
		mMSError.setToolTipText("Mean Squared Error");
		mMSError.setPreferredSize(new Dimension(180, 26));
		secondline.add(mMSError);
		
		mNCCorrelation = new JCheckBox("Normalised Cross-Correlation",
				true);
		mNCCorrelation.setToolTipText("Normalised Cross-Correlation");
		mNCCorrelation.setPreferredSize(new Dimension(180, 26));
		secondline.add(mNCCorrelation);
		
		mPSNRatio = new JCheckBox("Peak Signal to Noise Ratio",
				true);
		mPSNRatio.setToolTipText("Peak Signal to Noise Ratio");
		mPSNRatio.setPreferredSize(new Dimension(180,26));
		secondline.add(mPSNRatio);
		
		mSNRatio = new JCheckBox("Signal to Noise Ratio",
				true);
		mSNRatio.setToolTipText("Signal to Noise Ratio");
		mSNRatio.setPreferredSize(new Dimension(180,26));
		secondline.add(mSNRatio);
		
		JPanel spacer2 = new JPanel();
		secondline.add(spacer2);
		
		//now add the two panels to this one...
		this.add(firstline);
		this.add(secondline);
	}
	
	//FUNCTIONS
	
	/**
	 * Whether the average absolute difference is selected.
	 *
	 * @return Whether the average absolute difference is selected.
	 */
	public boolean isAverageAbsoluteDifferenceSelected(){
		return mAADifference.isSelected();
	}
	
	/**
	 * Whether the correlation quality is selected.
	 *
	 * @return Whether the correlation quality is selected.
	 */
	public boolean isCorrelationQualitySelected(){
		return mCorrelationQuality.isSelected();
	}
	
	/**
	 * Whether Peak Signal to Noise Ratio is selected.
	 *
	 * @return Whether Peak Signal to Noise Ratio is selected.
	 */
	public boolean isPeakSNRSelected(){
		return mPSNRatio.isSelected();
	}
	
	/**
	 * Whether Signal to Noise Ratio is selected.
	 *
	 * @return Whether Signal to Noise Ratio is selected.
	 */
	public boolean isSNRSelected(){
		return mSNRatio.isSelected();
	}
	
	/**
	 * Whether LpNorm is selected.
	 *
	 * @return Whether LpNorm is selected.
	 */
	public boolean isLpNormSelected(){
		return mLpNorm.isSelected();
	}
	
	/**
	 * Whether Normalised Cross-Correlation is selected.
	 *
	 * @return Whether Normalised Cross-Correlation is selected.
	 */
	public boolean isNCCorrelationSelected(){
		return mNCCorrelation.isSelected();
	}
	
	/**
	 * Whether Mean Squared Error is selected.
	 *
	 * @return Whether Mean Squared Error is selected.
	 */
	public boolean isMeanSquaredErrorSelected(){
		return mMSError.isSelected();
	}
	
	/**
	 * Whether Laplacian Mean Squared Error is selected.
	 *
	 * @return Whether Mean Squared Error is selected.
	 */
	public boolean isLaplacianMSErrorSelected(){
		return mLPMSError.isSelected();
	}
	
	
	//VARIABLES
	
	/**
	 * A tick box for average absolute difference.
	 */
	private JCheckBox mAADifference;
	
	/**
	 * A tick box for correlation quality.
	 */
	private JCheckBox mCorrelationQuality;
	
	/**
	 * A tick box for Laplacian mean squared error.
	 */
	private JCheckBox mLPMSError;
	
	/**
	 * A tick box for Lp Norm.
	 */
	private JCheckBox mLpNorm;
	
	/**
	 * A tick box for mean squared error.
	 */
	private JCheckBox mMSError;
	
	/**
	 * A tick box for normalised cross-correlation.
	 */
	private JCheckBox mNCCorrelation;
	
	/**
	 * A tick box for peak signal to noise ratio.
	 */
	private JCheckBox mPSNRatio;
	
	/**
	 * A tick box for signal to noise ratio.
	 */
	private JCheckBox mSNRatio;
	
	/**
	 * The serialisation ID.
	 */
	private static final long serialVersionUID = 0;
	
}
//end of class
