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

package invisibleinktoolkit.algorithms.gui;

import javax.swing.JDialog;
import java.awt.Frame;
import javax.swing.JPanel;
import invisibleinktoolkit.stego.StegoAlgorithm;
import invisibleinktoolkit.filters.Filterable;
import javax.swing.BoxLayout;
import java.awt.Dimension;
import javax.swing.JButton;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


/**
 * A window for setting the start and end bits of a given algorithm.
 *
 * @author Kathryn Hempstalk.
 */
public class StartEndFilterWindow extends JDialog implements ActionListener{
	
	//CONSTRUCTORS
	
	/**
	 * Sets up a new start and end bit setting window.
	 *
	 */
	public StartEndFilterWindow(Frame parent, StegoAlgorithm algorithm){
		//initial setup
		super(parent, "Set the algorithm options", true);
		int start = algorithm.getStartBits();
		int end = algorithm.getEndBits();
		mBitsPanel = new WriteableBitsPanel(start, end);
		
		Filterable algo = (Filterable)algorithm;
		mFilterPanel = new FilterSelectionPanel(algo.getFilter(), mBitsPanel);
		
		mLSBMatch = new LSBMatchPanel(this, algorithm.getMatch());
		mBitsPanel.setEnabled(!algorithm.getMatch());
		
		mOkButton = new JButton("OK");
		mOkButton.setPreferredSize(new Dimension(150, 26));
		mCancelButton = new JButton("Cancel");
		mCancelButton.setPreferredSize(new Dimension(150, 26));
		
		
		//setup a panel for the display
		JPanel displaypanel = new JPanel();
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		displaypanel.setLayout(gridbag);
		displaypanel.setPreferredSize(new Dimension(620,300));
		
		//keep the buttons together
		JPanel buttonpanel = new JPanel();
		buttonpanel.setPreferredSize(new Dimension(300, 30));
		buttonpanel.setLayout(new BoxLayout(buttonpanel, BoxLayout.X_AXIS));
		buttonpanel.add(mOkButton);
		JPanel spacer1 = new JPanel();
		spacer1.setPreferredSize(new Dimension(20,10));
		buttonpanel.add(spacer1);
		buttonpanel.add(mCancelButton);
		
		//add in the panels together
		c.weightx = 0;
		c.gridy = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(mBitsPanel, c);
		displaypanel.add(mBitsPanel);
		
		c.weightx = 0;
		c.gridy = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(mFilterPanel, c);
		displaypanel.add(mFilterPanel);
		
		c.weightx = 0;
		c.gridy = 2;
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(mLSBMatch, c);
		displaypanel.add(mLSBMatch);
		
		JPanel spacer = new JPanel();
		spacer.setPreferredSize(new Dimension(500,30));
		c.weightx = 0;
		c.gridy = 3;
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(spacer, c);
		displaypanel.add(spacer);
		
		c.weightx = 0;
		c.gridy = 4;
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(buttonpanel, c);
		displaypanel.add(buttonpanel);
		this.setContentPane(displaypanel);
		
		//now add a panel to fill up the rest of the space
		JPanel fillerpanel = new JPanel();
		fillerpanel.setPreferredSize(new Dimension(500,10));
		c.weightx = 0;
		c.gridy = 5;
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(fillerpanel, c);
		displaypanel.add(fillerpanel);
		
		//do all the final setup
		mAlgorithm = algorithm;
		mOkButton.addActionListener(this);
		mCancelButton.addActionListener(this);
		this.pack();
		this.setResizable(false);
		this.setLocationRelativeTo(parent);
		this.setVisible(true);
	}
	
	//FUNCTIONS
	
	/**
	 * Sets an action that will fire when buttons are pressed.
	 *
	 * @param e The action event (button press).
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equalsIgnoreCase("cancel")){
			//close the window
			this.dispose();
		}else if (e.getActionCommand().equalsIgnoreCase("ok")){
			//we want to update the algorithm with the new options
			if(!mLSBMatch.shouldMatch()){
				mAlgorithm.setStartBits(mBitsPanel.getStartBit());
				mAlgorithm.setEndBits(mBitsPanel.getEndBit());
				mAlgorithm.setMatch(false);
				if(mAlgorithm instanceof Filterable){
					Filterable falgo = (Filterable)mAlgorithm;
					falgo.setFilter(mFilterPanel.getFilter());
				}
			}else{
				mAlgorithm.setStartBits(0);
				mAlgorithm.setEndBits(0);
				mAlgorithm.setMatch(true);
				if(mAlgorithm instanceof Filterable){
					Filterable falgo = (Filterable)mAlgorithm;
					falgo.setFilter(mFilterPanel.getFilter());
				}
			}
			this.dispose();
		}else{
			//check what's happened to the LSB matching
			if(mLSBMatch.shouldMatch()){
				//disable the other panels
				mBitsPanel.setEnabled(false);
				// change the filter panel to match up
				mFilterPanel.changeFilterBits(4);
			}else{
				//enable other panels.
				mBitsPanel.setEnabled(true);
			}
		}
	}
	
	
	//VARIABLES
	
	/**
	 * A panel holding how many bits to write.
	 */
	private WriteableBitsPanel mBitsPanel;
	
	/**
	 * The algorithm being configured.
	 */
	private StegoAlgorithm mAlgorithm;
	
	/**
	 * An ok button to change all the options.
	 */
	private JButton mOkButton;
	
	/**
	 * A cancel button to stop all the changes.
	 */
	private JButton mCancelButton;
	
	/**
	 * A filter panel for selecting the filter to use.
	 */
	private FilterSelectionPanel mFilterPanel;
	
	/**
	 * Panel holding whether we should LSB Match or not.
	 */
	private LSBMatchPanel mLSBMatch;
	
	/**
	 * The serialisation ID.
	 */
	private static final long serialVersionUID = 0;
	
}
//end of class.
