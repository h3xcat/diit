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
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;

/**
 * A panel to pick the formats for file output.
 *
 * @author Kathryn Hempstalk.
 */
public class FormatPanel extends JPanel{
	
	//CONSTRUCTORS
	
	/**
	 * Sets up the panel with all the formats.
	 */
	public FormatPanel(){
		super();
		//setup the layout
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setBorder(new TitledBorder("Choose the output format"));
		this.setPreferredSize(new Dimension(740,50));
		
		//setup the radio buttons
		mCSVButton = new JRadioButton("CSV file");
		mCSVButton.setToolTipText("CSV File - Comma Separated Values");
		mCSVButton.setPreferredSize(new Dimension(180, 26));
		mCSVButton.setSelected(true);
		
		mARFFButton = new JRadioButton("ARFF (WEKA) file");
		mARFFButton.setToolTipText("ARFF file - WEKA machine learning file format");
		mARFFButton.setPreferredSize(new Dimension(180, 26));
		mARFFButton.setSelected(false);
		
		//group them
		ButtonGroup bgroup = new ButtonGroup();
		bgroup.add(mCSVButton);
		bgroup.add(mARFFButton);
		
		//add them to the panel
		this.add(mCSVButton);
		this.add(mARFFButton);
		
		//put in a spacer panel
		JPanel spacer1 = new JPanel();
		this.add(spacer1);			
	}
	
	//FUNCTIONS
	
	/**
	 * Whether CSV is selected.
	 *
	 * @return Whether CSV is selected.
	 */
	public boolean isCSVSelected(){
		return mCSVButton.isSelected();
	}
	
	//VARIABLES
	
	/**
	 * A radio button for CSV files.
	 */
	private JRadioButton mCSVButton;
	
	/**
	 * A radio button for ARFF files.
	 */
	private JRadioButton mARFFButton;
	
	/**
	 * The serialisation ID.
	 */
	private static final long serialVersionUID = 0;
	
}
//end of class
