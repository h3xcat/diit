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

package invisibleinktoolkit.gui;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.border.TitledBorder;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import java.util.Vector;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import invisibleinktoolkit.stego.StegoAlgorithm;
import invisibleinktoolkit.algorithms.*;
import invisibleinktoolkit.filters.Filterable;
import invisibleinktoolkit.filters.Filter;


/**
 * A panel for picking a steganography algorithm.
 *
 * @author Kathryn Hempstalk.
 */
public class AlgorithmPanel extends JPanel implements ActionListener{
	
	//CONSTRUCTORS
	
	/**
	 * Sets up an algorithm panel.
	 *
	 * @param parent The parent frame.
	 */
	public AlgorithmPanel(Frame parent){
		this(parent, null);
	}
	
	/**
	 * Sets up an algorithm panel on an encoder panel.
	 *
	 * @param parent The parent frame.
	 * @param embed The embedder panel (if this is on one).
	 */
	public AlgorithmPanel(Frame parent, Embedder embed){
		super();
		//setup the layout
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setBorder(new TitledBorder("Select an algorithm to use"));
		this.setPreferredSize(new Dimension(740,50));
		
		//setup the label
		mLabel = new JLabel("Select an algorithm:   ");
		mLabel.setPreferredSize(new Dimension(150,26));
		this.add(mLabel);
		
		//setup the combo box
		Vector algorithms = new Vector();
		algorithms.add("BattleSteg");
		algorithms.add("BlindHide");
		algorithms.add("DynamicBattleSteg");
		algorithms.add("DynamicFilterFirst");
		algorithms.add("FilterFirst");
		algorithms.add("HideSeek");
		//TO ADD A NEW ALGORITHM PUT algorithms.add("algorithmname"); HERE
		//AND MAKE SURE IT IS IN THE PACKAGE invisibleinktoolkit.algorithms
		
		
		mComboBox = new JComboBox(algorithms);
		mComboBox.setEditable(false);
		mComboBox.setToolTipText("The algorithm to be used for hiding");
		mComboBox.addActionListener(this);
		this.add(mComboBox);
		
		mHelpButton = new JButton("?");
		mHelpButton.setActionCommand("Explain");
		mHelpButton.setToolTipText("What does this algorithm do?");
		mHelpButton.setPreferredSize(new Dimension(40,26));
		mHelpButton.addActionListener(this);
		this.add(mHelpButton);
		
		//setup the options box
		mButton = new JButton("Options");
		mButton.setToolTipText("Set the options for the algorithm");
		mButton.setPreferredSize(new Dimension(100,26));
		mButton.addActionListener(this);
		this.add(mButton);
		
		
		mAlgorithm = (StegoAlgorithm)new BattleSteg();
		mParent = parent;
		mEmbedder = embed;
	}
	
	/**
	 * Sets an action that will fire when the combobox changes.
	 *
	 * @param e The action event (button press).
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equalsIgnoreCase("comboBoxChanged")){
			String algo = "invisibleinktoolkit.algorithms." + 
			(String) mComboBox.getSelectedItem();
			try{
				mAlgorithm = (StegoAlgorithm)Class.forName(algo).newInstance();
			}catch(Exception exp){
				exp.printStackTrace();
				System.exit(1);
			}
		}else if (e.getActionCommand().equalsIgnoreCase("options")){
			//set up the options for this algorithm...
			mAlgorithm.openConfigurationWindow(mParent);
		}else if (e.getActionCommand().equalsIgnoreCase("explain")){
			JOptionPane.showMessageDialog(this, mAlgorithm.explainMe(),
					mAlgorithm.getClass().getSimpleName(),
					JOptionPane.INFORMATION_MESSAGE);
		}
		if(mEmbedder != null){
			mEmbedder.updateEmbeddingRate();
		}
	}
	
	
	/**
	 * Gets the algorithm currently selected.
	 *
	 * @return The current algorithm.
	 */
	public StegoAlgorithm getAlgorithm(){
		return mAlgorithm;
	}
	
	/**
	 * Gets the name of the algorithm/filter that is currently selected.
	 * 
	 * @return The name of the algorithm/filter.
	 */
	public String getAlgorithmName(){
		String name = mAlgorithm.getClass().getName();
		name = name.substring(name.lastIndexOf('.') + 1);
				
		if(mAlgorithm instanceof Filterable){
			Filter filt = ((Filterable)mAlgorithm).getFilter();
			String filtername = filt.getClass().getName();
			filtername = filtername.substring(filtername.lastIndexOf('.') + 1);
			name = name + "-" + filtername;
		}
		
		return name;
	}
	
	//VARIABLES
	
	/**
	 * The parent frame this window lives in.
	 */
	private Frame mParent;
	
	/**
	 * A button for the panel.
	 */
	private JLabel mLabel;
	
	/**
	 * A combo box to select algorithms from.
	 */
	private JComboBox mComboBox;
	
	/**
	 * A button to get all the options to set for this algorithm.
	 */
	private JButton mButton;
	
	/**
	 * A button to get help about for this algorithm.
	 */
	private JButton mHelpButton;
	
	/**
	 * A steganography algorithm.
	 */
	private StegoAlgorithm mAlgorithm;
	
	/**
	 * The encoder window if this panel is on one.
	 */
	private Embedder mEmbedder;
	
	/**
	 * The serialisation ID.
	 */
	private static final long serialVersionUID = 0;
	
}
