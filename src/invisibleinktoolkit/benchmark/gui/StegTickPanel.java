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
 * A tick box panel for picking steganalysis methods.
 * <P>
 * This panel contains three tick boxes.  One for RS analysis,
 * one for Sample Pairs, and one for Laplace Graphs.
 *
 * @author Kathryn Hempstalk.
 */
public class StegTickPanel extends JPanel{
	
	//CONSTRUCTORS
	
	/**
	 * Sets up a tick box panel for steganalysis.  Three check boxes
	 * are set up for RS Analysis, Sample Pairs and Laplace Graphs.
	 *
	 */
	public StegTickPanel(){
		super();
		//setup the layout
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setBorder(new TitledBorder("Tick the analysis types to run"));
		this.setPreferredSize(new Dimension(740,50));
		
		//setup the checkboxes
		mRSAnalysisTick = new JCheckBox("RS Analysis", true);
		mRSAnalysisTick.setToolTipText("RS Analysis");
		mRSAnalysisTick.setPreferredSize(new Dimension(180,26));
		this.add(mRSAnalysisTick);
		
		mSamplePairsTick = new JCheckBox("Sample Pairs", false);
		mSamplePairsTick.setToolTipText("Sample Pairs");
		mSamplePairsTick.setPreferredSize(new Dimension(180,26));
		this.add(mSamplePairsTick);
		
		mLaplaceTick = new JCheckBox("Laplace Graph", false);
		mLaplaceTick.setToolTipText("Laplace Graph");
		mLaplaceTick.setPreferredSize(new Dimension(180,26));
		this.add(mLaplaceTick);
		
		//put in a spacer panel
		JPanel spacer1 = new JPanel();
		this.add(spacer1);			
	}
	
	//FUNCTIONS
	
	/**
	 * Whether the Laplace tick box is selected.
	 *
	 * @return Whether the Laplace tick box is selected.
	 */
	public boolean isLaplaceSelected(){
		return mLaplaceTick.isSelected();
	}
	
	/**
	 * Whether the RS Analysis tick box is selected.
	 *
	 * @return Whether the RS Analysis tick box is selected.
	 */
	public boolean isRSAnalysisSelected(){
		return mRSAnalysisTick.isSelected();
	}
	
	/**
	 * Whether the Sample Pairs tick box is selected.
	 *
	 * @return Whether the Sample Pairs tick box is selected.
	 */
	public boolean isSamplePairsSelected(){
		return mSamplePairsTick.isSelected();
	}
	
	
	//VARIABLES
	
	/**
	 * A tick box for Laplace graphs.
	 */
	private JCheckBox mLaplaceTick;
	
	/**
	 * A tick box for RS Analysis.
	 */
	private JCheckBox mRSAnalysisTick;
	
	/**
	 * A tick box for Sample Pairs Analysis.
	 */
	private JCheckBox mSamplePairsTick;
	
	/**
	 * The serialisation ID.
	 */
	private static final long serialVersionUID = 0;
	
}
//end of class
