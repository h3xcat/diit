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
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import invisibleinktoolkit.benchmark.gui.*;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import javax.swing.BoxLayout;


/**
 * A steganalysis tab.
 * <P>
 * This tab contains two buttons to launch windows to select the
 * benchmark/steganalysis to run, as well as a text pane to display
 * the results.  The results are also able to be saved to a text file,
 * csv, or arff file format (for steganalysis only).
 *
 * @author Kathryn Hempstalk.
 */
public class StegAnalyser extends JPanel implements ActionListener{
	
	
	//CONSTRUCTORS
	
	/**
	 * Creates a new steganalysis panel.
	 *
	 * @param parent The parent frame for this panel.
	 */
	public StegAnalyser(Frame parent){
		super();
		mParent = parent;
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		this.setLayout(gridbag);
		this.setPreferredSize(new Dimension(750,420));
		
		//BUTTON PANE
		
		//setup a panel to hold the three buttons
		JPanel buttonpanel = new JPanel();
		buttonpanel.setLayout(new BoxLayout(buttonpanel, BoxLayout.X_AXIS));
		buttonpanel.setBorder(new TitledBorder("Pick an analysis method"));
		buttonpanel.setPreferredSize(new Dimension(740,60));
		
		//add the first spacer
		JPanel spacer1 = new JPanel();
		spacer1.setPreferredSize(new Dimension(60,10));
		buttonpanel.add(spacer1);
		
		//setup the steganalysis button
		mStegButton = new JButton("Steganalysis");
		mStegButton.setToolTipText("Analyse just the stego-image");
		mStegButton.setPreferredSize(new Dimension(180,40));
		buttonpanel.add(mStegButton);
		
		//add the second spacer
		JPanel spacer2 = new JPanel();
		spacer2.setPreferredSize(new Dimension(30,10));
		buttonpanel.add(spacer2);
		
		//setup the benchmark button
		mBenchmarkButton = new JButton("Benchmark");
		mBenchmarkButton.setToolTipText
		("Analyse the stego-image versus the original");
		mBenchmarkButton.setPreferredSize(new Dimension(180,40));
		buttonpanel.add(mBenchmarkButton);
		
		//add the third spacer
		JPanel spacer3 = new JPanel();
		spacer3.setPreferredSize(new Dimension(30,10));
		buttonpanel.add(spacer3);
		
		//setup the bulk steganalysis button
		mBulkButton = new JButton("Bulk Steganalysis");
		mBulkButton.setToolTipText
		("Analyse methods using several pictures and messages");
		mBulkButton.setPreferredSize(new Dimension(180,40));
		buttonpanel.add(mBulkButton);
		
		//add the third spacer
		JPanel spacer4 = new JPanel();
		spacer4.setPreferredSize(new Dimension(60,10));
		buttonpanel.add(spacer4);
		
		//add the button panel to the panel
		c.weightx = 0;
		c.gridy = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(buttonpanel, c);
		this.add(buttonpanel);
		
		//RESULTS PANE
		
		//setup a panel to hold the two buttons
		JPanel textpanel = new JPanel();
		textpanel.setLayout(new BorderLayout());
		textpanel.setBorder(new TitledBorder("Results from last analysis"));
		textpanel.setPreferredSize(new Dimension(740, 355));
		
		//setup/add the text panel itself
		mResults = new JTextArea();
		mResults.setEditable(false);
		mResults.setBackground(Color.white);
		mResults.setSize(580,300);
		mResults.setToolTipText("The last analysis results");
		mResults.setLineWrap(true);
		
		//make it scrollable and add it
		JScrollPane scrollPane = new JScrollPane(mResults);
		scrollPane.setPreferredSize(new Dimension(600, 300));
		textpanel.add(scrollPane, BorderLayout.CENTER);
		
		//add in the text pane
		c.weightx = 0;
		c.gridy = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(textpanel, c);
		this.add(textpanel);		
				
		mBenchmarkButton.addActionListener(this);
		mStegButton.addActionListener(this);	
		mBulkButton.addActionListener(this);
	}
	
	
	/**
	 * Sets an action that will fire when a button is pressed.
	 * Only "steganalysis", "benchmark" and "bulk steganalysis"
	 * buttons will be affected, and the appropriate window 
	 * will be popped up when they are pressed.
	 *
	 * @param e The action event (button press).
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equalsIgnoreCase("steganalysis")){
			//run steganalysis
			new SteganalysisWindow(mParent, 	mResults);
			
			
		}else if (e.getActionCommand().equalsIgnoreCase("benchmark")){
			//run benchmarking
			new BenchmarkWindow(mParent, mResults);
			
		}else if (e.getActionCommand().equalsIgnoreCase("bulk steganalysis")){
			//run benchmarking
			new BulkSteganalysisWindow(mParent, mResults);
			
		}
		return;
	}
	
	
	//VARIABLES	
	
	/**
	 * A button to select steganalysis.
	 */
	private JButton mStegButton;
	
	/**
	 * A button to select benchmarking.
	 */
	private JButton mBenchmarkButton;
	
	/**
	 * A button to select bulk steganalysis.
	 */
	private JButton mBulkButton;
	
	/**
	 * A text frame to display the results.
	 */
	private JTextArea mResults;
	
	/**
	 * The parent frame.
	 */
	private Frame mParent;
	
	/**
	 * The serialisation ID.
	 */
	private static final long serialVersionUID = 0;
	
}//end of class.
