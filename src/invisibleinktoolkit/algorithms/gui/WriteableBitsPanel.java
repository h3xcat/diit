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

import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.TitledBorder;
import java.awt.Dimension;


/**
 * A panel for selecting which bits an algorithm should write to.
 *
 * @author Kathryn Hempstalk.
 */
public class WriteableBitsPanel extends JPanel implements ActionListener{
	
	/**
	 * Generated Serialization ID.
	 */
	private static final long serialVersionUID = -3025560889320558958L;

	//CONSTRUCTORS
	
	/**
	 * Sets up a panel to select which bits to write.
	 *
	 * @param start The initial position of the start bit.
	 * @param finish The initial position of the last bit.
	 */
	public WriteableBitsPanel(int start, int finish){
		super();
		
		mAllowedBits = 7;
		
		//setup the layout
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setBorder(new TitledBorder("Select the bits to write to"));
		this.setPreferredSize(new Dimension(600,50));
		
		//setup the label
		JLabel label1 = new JLabel("Write from bit number:");
		label1.setPreferredSize(new Dimension(250,26));
		this.add(label1);
		
		//setup the combo box
		Vector numbers = new Vector();
		for(int i = 1; i <= mAllowedBits; i++){
			numbers.add(new Integer(i));
		}
		
		mStartCombo = new JComboBox(numbers);
		mStartCombo.setToolTipText("Start bit");
		mStartCombo.setEditable(false);
		mStartCombo.setSelectedIndex(start);
		this.add(mStartCombo);
		
		//setup the second label
		JLabel label2 = new JLabel("  to:");
		label2.setPreferredSize(new Dimension(50,26));
		this.add(label2);
		
		numbers = new Vector();
		for(int i = start + 1; i <= mAllowedBits; i++){
			numbers.add(new Integer(i));
		}
		mEndCombo = new JComboBox(numbers);
		mEndCombo.setToolTipText("End bit");
		mEndCombo.setEditable(false);
		if(finish >= this.getSmallestEndBit() && finish <= mAllowedBits){
			mEndCombo.setSelectedIndex
			(finish - this.getSmallestEndBit());
		}else
			mEndCombo.setSelectedIndex(0);
		
		mStartCombo.addActionListener(this);
		this.add(mEndCombo);
		mAlteringBits = false;
		
	}
	
	//FUNCTIONS
	
	/**
	 * Sets an action that will fire when the button is pressed.
	 *
	 * @param e The action event (button press).
	 */
	public void actionPerformed(ActionEvent e) {
		if(!mAlteringBits){
			//reset the end bits combo box...
			int currentendbit = this.getEndBit();
			mEndCombo.removeAllItems();
			for(int i = this.getStartBit() + 1; i <= mAllowedBits; i++){
				mEndCombo.addItem(new Integer(i));
			}
			
			//reselect the right item
			if(currentendbit >= this.getSmallestEndBit()){
				//check we aren't over the limit
				if(currentendbit > 
				(this.getSmallestEndBit() + mEndCombo.getItemCount()))
					mEndCombo.setSelectedIndex(mEndCombo.getItemCount() - 1);
				else
					mEndCombo.setSelectedIndex
					(currentendbit - this.getSmallestEndBit());
			}else
				mEndCombo.setSelectedIndex(0);	    
		}
	}
	
	
	/**
	 * Sets the number of allowed bits.
	 *
	 * @param newAllowed The new allowed number of bits.
	 */
	public void setAllowedBits(int newAllowed){
		if(newAllowed < 0) newAllowed = 1;
		if(newAllowed > 8) newAllowed = 8;
		
		if(newAllowed == mAllowedBits) return;
		
		//else we need to set up the combo boxes again.
		mAllowedBits = newAllowed;
		
		mAlteringBits = true;
		
		//first the start bits...
		int currentbit = getStartBit();
		
		mStartCombo.setSelectedIndex(0);
		
		mStartCombo.removeAllItems();
		for(int i = 1; i <= mAllowedBits; i++){
			mStartCombo.addItem(new Integer(i));
		}
		
		//reselect the right item
		if(currentbit >= mAllowedBits){
			mStartCombo.setSelectedIndex(mStartCombo.getItemCount() - 1);
		}else{
			mStartCombo.setSelectedIndex(currentbit);
		}
		
		//reset the end bits combo box...
		int currentendbit = this.getEndBit();
		mEndCombo.removeAllItems();
		for(int i = this.getStartBit() + 1; i <= mAllowedBits; i++){
			mEndCombo.addItem(new Integer(i));
		}
		
		//reselect the right item
		if(currentendbit >= this.getSmallestEndBit()){
			//check we aren't over the limit
			if(currentendbit > 
			(this.getSmallestEndBit() + (mEndCombo.getItemCount() - 1)))
				mEndCombo.setSelectedIndex(mEndCombo.getItemCount() - 1);
			else
				mEndCombo.setSelectedIndex
				(currentendbit - this.getSmallestEndBit());
		}else
			mEndCombo.setSelectedIndex(0);	
		
		mAlteringBits = false;
	}
	
	/**
	 * Gets the smallest value in the end bits combo box.
	 *
	 * @return The smallest value in the end bits combo box (0 based).
	 */
	private int getSmallestEndBit(){
		return ((Integer)mEndCombo.getItemAt(0)).intValue() - 1;
	}
	
	/**
	 * Gets the start bit that is currently set.
	 *
	 * @return The start bit currently set (0 based).
	 */
	public int getStartBit(){
		return ((Integer)mStartCombo.getSelectedItem()).intValue() - 1;
	}
	
	
	/**
	 * Gets the end bit that is currently set.
	 *
	 * @return The end bit currently set (0 based).
	 */
	public int getEndBit(){
		return ((Integer)mEndCombo.getSelectedItem()).intValue() - 1;
	}
	
	/**
	 * Whether this panel is enabled or not.
	 */
	public void setEnabled(boolean enabled){
		if(enabled){
			mStartCombo.setEnabled(true);
			mEndCombo.setEnabled(true);			
		}
		else{
			mStartCombo.setEnabled(false);
			mEndCombo.setEnabled(false);	
		}
		super.setEnabled(enabled);
	}
	
	//VARIABLES
	
	/** 
	 * A startbits combo box for the panel.
	 */
	private JComboBox mStartCombo;
	
	/** 
	 * A startbits combo box for the panel.
	 */
	private JComboBox mEndCombo;
	
	/**
	 * The number of bits allowed to be in the boxes.
	 */
	private int mAllowedBits;
	
	/**
	 * Tells whether the bits allowed are being changed (no action listener
	 * should be fired in this time.
	 */
	private boolean mAlteringBits;	
	
}
//end of class.
