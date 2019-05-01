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

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpinnerModel;
import javax.swing.border.TitledBorder;
import invisibleinktoolkit.algorithms.BattleSteg;
import java.awt.Dimension;


/**
 * A panel for getting all the battlesteg specific options.
 *
 * @author Kathryn Hempstalk.
 */
public class BattleStegOptionsPanel extends JPanel{
	
	//CONSTRUCTORS
	
	/**
	 * Sets up a panel to select which bits to write.
	 *
	 * @param algorithm The battlesteg algorithm being used.
	 */
	public BattleStegOptionsPanel(BattleSteg algorithm){
		super();
		
		//setup the layout
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBorder(new TitledBorder("Set the options for BattleSteg"));
		this.setPreferredSize(new Dimension(600,150));
		
		//setup the moveaway panel
		
		//setup the panel...
		JPanel mapanel = new JPanel();
		mapanel.setPreferredSize(new Dimension(600,50));
		mapanel.setLayout(new BoxLayout(mapanel, BoxLayout.X_AXIS));
		
		//setup the label
		JLabel label1 = new JLabel("Set the maximum number of shots before moving away:");
		label1.setPreferredSize(new Dimension(400,26));
		mapanel.add(label1);
		
		//setup the text box
		SpinnerModel model1 =
			new SpinnerNumberModel(algorithm.getMoveAway(), //initial value
					1, //min
					50, //max
					1); //increment
		mMoveAway = new JSpinner(model1);
		((JSpinner.DefaultEditor)mMoveAway.getEditor())
		.getTextField().setEditable(false);
		mMoveAway.setPreferredSize(new Dimension(100,26));
		mMoveAway.setToolTipText("The maximum number of shots before moving away");
		mapanel.add(mMoveAway);
		
		//setup the range panel
		
		//setup the panel...
		JPanel rangepanel = new JPanel();
		rangepanel.setPreferredSize(new Dimension(600,50));
		rangepanel.setLayout(new BoxLayout(rangepanel, BoxLayout.X_AXIS));
		
		//setup the label
		JLabel label2 = new JLabel("Set the range (radius) of the shots:");
		label2.setPreferredSize(new Dimension(400,26));
		rangepanel.add(label2);
		
		//setup the text box
		SpinnerModel model2 =
			new SpinnerNumberModel(algorithm.getRange(), //initial value
					1, //min
					50, //max
					1); //increment
		mRange = new JSpinner(model2);
		((JSpinner.DefaultEditor)mRange.getEditor())
		.getTextField().setEditable(false);
		mRange.setPreferredSize(new Dimension(100,26));
		mRange.setToolTipText("The range (radius) of the shots");
		rangepanel.add(mRange);
		
		//setup the initial shots panel
		
		//setup the panel...
		JPanel ispanel = new JPanel();
		ispanel.setPreferredSize(new Dimension(600,50));
		ispanel.setLayout(new BoxLayout(ispanel, BoxLayout.X_AXIS));
		
		//setup the label
		JLabel label3 = new JLabel("Set the number of initial shots to make:");
		label3.setPreferredSize(new Dimension(400,26));
		ispanel.add(label3);
		
		//setup the text box
		SpinnerModel model3 =
			new SpinnerNumberModel(algorithm.getInitialShots(), //initial value
					0, //min
					50, //max
					1); //increment
		mInitialShots = new JSpinner(model3);
		((JSpinner.DefaultEditor)mInitialShots.getEditor())
		.getTextField().setEditable(false);
		mInitialShots.setPreferredSize(new Dimension(100,26));
		mInitialShots.setToolTipText("The initial number of shots to make");
		ispanel.add(mInitialShots);
		
		//setup the increase shots number panel
		
		//setup the panel...
		JPanel inpanel = new JPanel();
		inpanel.setPreferredSize(new Dimension(600,50));
		inpanel.setLayout(new BoxLayout(inpanel, BoxLayout.X_AXIS));
		
		//setup the label
		JLabel label4 = new JLabel("Set the number of shots to increase by when a hit occurs:");
		label4.setPreferredSize(new Dimension(400,26));
		inpanel.add(label4);
		
		//setup the text box
		
		SpinnerModel model4 =
			new SpinnerNumberModel(algorithm.getIncreaseShots(), //initial value
					0, //min
					50, //max
					1); //increment
		mIncreaseNumber = new JSpinner(model4);
		((JSpinner.DefaultEditor)mIncreaseNumber.getEditor())
		.getTextField().setEditable(false);
		mIncreaseNumber.setPreferredSize(new Dimension(100,26));
		mIncreaseNumber.setToolTipText("The number of shots to increase by");
		inpanel.add(mIncreaseNumber);
		
		//and add them into this pane...
		this.add(rangepanel);
		JPanel spacer = new JPanel();
		spacer.setPreferredSize(new Dimension(10,5));
		this.add(spacer);
		this.add(mapanel);
		JPanel spacer2 = new JPanel();
		spacer2.setPreferredSize(new Dimension(10,5));
		this.add(spacer2);
		this.add(ispanel);
		JPanel spacer3 = new JPanel();
		spacer3.setPreferredSize(new Dimension(10,5));
		this.add(spacer3);
		this.add(inpanel);
		JPanel spacer4 = new JPanel();
		spacer4.setPreferredSize(new Dimension(10,5));
		this.add(spacer4);
		
	}
	
	
	//FUNCTIONS
	
	/**
	 * Gets the value currently residing in the move away spinner.
	 *
	 * @return The move away value.
	 */
	public int getMoveAway(){
		return ((Integer)mMoveAway.getValue()).intValue();
	}
	
	/**
	 * Gets the value currently residing in the range spinner.
	 *
	 * @return The range value.
	 */
	public int getRange(){
		return ((Integer)mRange.getValue()).intValue();
	}
	
	/**
	 * Gets the value currently residing in the initial shots spinner.
	 *
	 * @return The initial shots number.
	 */
	public int getInitialShots(){
		return ((Integer)mInitialShots.getValue()).intValue();
	}
	
	/**
	 * Gets the value currently in the increase number spinner.
	 *
	 * @return The increase shots by number.
	 */
	public int getIncreaseShots(){
		return ((Integer)mIncreaseNumber.getValue()).intValue();
	}
	
	
	//VARIABLES
	
	/**
	 * The field containing the move away value.
	 */
	private JSpinner mMoveAway;
	
	/**
	 * The field containing the initial number of ranged shots.
	 */
	private JSpinner mInitialShots;
	
	/**
	 * The field containing the number of shots to increase by on a hit.
	 */
	private JSpinner mIncreaseNumber;
	
	/**
	 * The field containing the range of the shots.
	 */
	private JSpinner mRange;
	
	/**
	 * The serialisation ID.
	 */
	private static final long serialVersionUID = 0;
	
}
//end of class.
