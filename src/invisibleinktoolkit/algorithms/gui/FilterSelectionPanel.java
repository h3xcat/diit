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
import invisibleinktoolkit.filters.*;


/**
 * A panel for selecting which filter should be used.
 *
 * @author Kathryn Hempstalk.
 */
public class FilterSelectionPanel extends JPanel implements ActionListener{
	
	//CONSTRUCTORS
	
	/**
	 * Sets up a panel to select which bits to write.
	 *
	 * @param filter The filter currently being used.
	 */
	public FilterSelectionPanel(Filter filter, WriteableBitsPanel wbpanel){
		super();
		
		//setup the layout
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(new TitledBorder("Select the filter to use"));
		this.setPreferredSize(new Dimension(600,115));
		
		//setup a panel to hold the combo box and label
		JPanel toppanel = new JPanel();
		toppanel.setPreferredSize(new Dimension(600, 26));
		toppanel.setLayout(new BoxLayout(toppanel, BoxLayout.X_AXIS));
		
		//setup the label
		JLabel label1 = new JLabel("Select the filter to use:");
		label1.setPreferredSize(new Dimension(400,26));
		toppanel.add(label1);
		
		//setup the combobox
		Vector filters = new Vector();
		filters.add("Laplace");
		filters.add("Sobel");
		filters.add("Prewitt");
		//TO ADD A FILTER - add filters.add("filtername"); here
		//MAKE SURE filtername IS IN invisibleinktoolkit.filters
		//AND MODIFY THE CURRENT DISPLAY VARIABLE BELOW
		//add more filter names here if necessary...
		
		mFilterBox = new JComboBox(filters);
		
		//default to the currently displayed one...
		if(filter instanceof Laplace)
			mFilterBox.setSelectedIndex(0);
		else if (filter instanceof Sobel)
			mFilterBox.setSelectedIndex(1);
		else if (filter instanceof Prewitt)
			mFilterBox.setSelectedIndex(2);
		//add more indexes here if necessary
		else
			mFilterBox.setSelectedIndex(0);
		
		toppanel.add(mFilterBox);
		mFilterBox.setToolTipText("The filter that will be used by the algorithm");
		
		//setup a panel to hold the slider bar and label
		JPanel midpanel = new JPanel();
		midpanel.setPreferredSize(new Dimension(600,26));
		midpanel.setLayout(new BoxLayout(midpanel, BoxLayout.X_AXIS));
		
		//setup the label
		JLabel label2 = new JLabel("Set how many bits the filter should use:");
		label2.setPreferredSize(new Dimension(400,26));
		midpanel.add(label2);
		
		//setup the bit box
		Vector numbers = new Vector();
		for(int i = 2; i <= 7; i++){
			numbers.add(new Integer(i));
		}
		
		mBitBox = new JComboBox(numbers);
		mBitBox.setPreferredSize(new Dimension(50,26));
		int index = (filter.getEndRange() - filter.getStartRange());
		index = index - 2;
		mBitBox.setSelectedIndex(index);
		mBitBox.setToolTipText("The number of most significant bits the filter will use");
		midpanel.add(mBitBox);
		mBitBox.addActionListener(this);
		
		//add in the panels to the program.
		this.add(toppanel);
		JPanel spacer = new JPanel();
		spacer.setPreferredSize(new Dimension(10,5));
		this.add(spacer);
		this.add(midpanel);
		JPanel spacer2 = new JPanel();
		spacer2.setPreferredSize(new Dimension(10,5));
		this.add(spacer2);
		JPanel warningpanel = new JPanel();
		warningpanel.setPreferredSize(new Dimension(600,32));
		warningpanel.setLayout(new BoxLayout(warningpanel, BoxLayout.X_AXIS));
		JLabel warning = new JLabel("Please note: changing the number of "
				+ "filter bits may affect your write-to "
				+ "bit selection above.");
		warning.setPreferredSize(new Dimension(530,26));
		warningpanel.add(warning);
		this.add(warningpanel);
		
		
		mWBPanel = wbpanel;		
		this.actionPerformed(null);
	}
	
	
	//FUNCTIONS
	
	/**
	 * Sets an action that will fire when combo bit box is changed.
	 *
	 * @param e The action event (button press).
	 */
	public void actionPerformed(ActionEvent e) {
		//bit box has changed... change the values in the writeable bits to
		//match...
		int bitstaken = mBitBox.getSelectedIndex() + 2;
		int bitsleft = 8 - bitstaken;
		mWBPanel.setAllowedBits(bitsleft);		
	}
	
	public void changeFilterBits(int change){
		mBitBox.setSelectedIndex(change - 2);
		int bitstaken = mBitBox.getSelectedIndex() + 2;
		int bitsleft = 8 - bitstaken;
		mWBPanel.setAllowedBits(bitsleft);
	}
	
	
	/**
	 * Gets the current filter described in this panel.
	 *
	 * @return The current filter described in this panel.
	 */
	public Filter getFilter(){
		Filter filter;
		try{
			filter = (Filter)Class.forName
			("invisibleinktoolkit.filters." + 
					((String)mFilterBox.getSelectedItem())).newInstance();
			filter.setStartRange(8 - (mBitBox.getSelectedIndex() + 2));
			filter.setEndRange(8);
			return filter;
		}catch(Exception exp){
			exp.printStackTrace();
			System.exit(1);
		}
		return new Laplace(1,8);
	}
	
	public void setEnabled(boolean enabled){
		mFilterBox.setEnabled(enabled);
		mBitBox.setEnabled(enabled);
		mWBPanel.setEnabled(enabled);		
		super.setEnabled(enabled);
	}
	
	//VARIABLES
	
	/**
	 * The box displaying all the filters.
	 */
	private JComboBox mFilterBox;
	
	/**
	 * The box displaying how many bits to use.
	 */
	private JComboBox mBitBox;
	
	/**
	 * The writeable bits panel that is in the same window as this panel.
	 */
	private WriteableBitsPanel mWBPanel;
	
	/**
	 * The serialisation ID.
	 */
	private static final long serialVersionUID = 0;
	
}
//end of class.
