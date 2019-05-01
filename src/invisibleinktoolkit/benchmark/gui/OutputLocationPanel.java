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
 * A tick box panel for picking the output options for steganalysis methods.
 * <P>
 * This panel contains two tick boxes.  One for to file, the other for to
 * the results window in the calling window.
 *
 * @author Kathryn Hempstalk.
 */
public class OutputLocationPanel extends JPanel{
	
	//CONSTRUCTORS
	
	/**
	 * Sets up a tick box panel for steganalysis output options.  
	 *
	 */
	public OutputLocationPanel(){
		super();
		//setup the layout
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setBorder(new TitledBorder("Tick places to output results to"));
		this.setPreferredSize(new Dimension(740,50));
		
		//setup the checkboxes
		mToResultsWindow = new JCheckBox("Print to results window", true);
		mToResultsWindow.setPreferredSize(new Dimension(180,26));
		this.add(mToResultsWindow);
		
		mToFile = new JCheckBox("Print to file", false);
		mToFile.setPreferredSize(new Dimension(180,26));
		this.add(mToFile);
		
		//put in a spacer panel
		JPanel spacer1 = new JPanel();
		this.add(spacer1);			
	}
	
	//FUNCTIONS
	
	/**
	 * Whether to print to a file or not.
	 *
	 * @return Whether to print to a file.
	 */
	public boolean isToFile(){
		return mToFile.isSelected();
	}
	
	/**
	 * Whether to print to the results window or not.
	 *
	 * @return Whether to print to the results window.
	 */
	public boolean isToResultsWindow(){
		return mToResultsWindow.isSelected();
	}
	
	
	//VARIABLES
	
	/**
	 * A tick box for printing to a file.
	 */
	private JCheckBox mToFile;
	
	/**
	 * A tick box for printing to the results window.
	 */
	private JCheckBox mToResultsWindow;
	
	/**
	 * The serialisation ID.
	 */
	private static final long serialVersionUID = 0;
	
}
//end of class
