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

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.awt.Frame;
import javax.swing.JPanel;
import invisibleinktoolkit.benchmark.StegAnalyser;
import invisibleinktoolkit.gui.InputImagePanel;
import invisibleinktoolkit.gui.WorkerThread;
import invisibleinktoolkit.gui.WorkingPanel;
import javax.swing.BoxLayout;
import java.awt.Dimension;
import javax.swing.JButton;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import javax.swing.JTextArea;

/**
 * A window for running steg analysis.
 * <P>
 * This window contains everything necessary to run steganalysis
 * on an image. Running steganalysis just requires the stego-image, so
 * this is the only real prerequisite for this window.
 *
 * @author Kathryn Hempstalk.
 */
public class SteganalysisWindow extends JDialog implements ActionListener{
	
	//CONSTRUCTORS
	
	/**
	 * Sets up a new steganalysis window.
	 *
	 * @param parent The parent window that called this one.
	 * @param results The text area to print the results to.
	 */
	public SteganalysisWindow(Frame parent, JTextArea results){
		//initial setup
		super(parent, "Run Steganalysis", true);
		mParent = parent;
		mResults = results;
		
		mOkButton = new JButton("OK");
		mOkButton.setPreferredSize(new Dimension(150, 26));
		mCancelButton = new JButton("Cancel");
		mCancelButton.setPreferredSize(new Dimension(150, 26));
		
		
		//setup a panel for the display
		JPanel displaypanel = new JPanel();
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		displaypanel.setLayout(gridbag);
		displaypanel.setPreferredSize(new Dimension(750,240));
		
		//add in a panel to pick the picture this is for
		mIIPanel = new InputImagePanel("Pick the image to analyse",
				"Pick Image", false);
		c.weightx = 0;
		c.gridy = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(mIIPanel , c);
		displaypanel.add(mIIPanel);
		
		//add in the steg tick boxes
		mSTPanel = new StegTickPanel();
		c.weightx = 0;
		c.gridy = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(mSTPanel , c);
		displaypanel.add(mSTPanel);
		
		//add in a output location
		mOLPanel = new OutputLocationPanel();
		c.weightx = 0;
		c.gridy = 2;
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(mOLPanel , c);
		displaypanel.add(mOLPanel);
		
		
		//keep the buttons together
		JPanel buttonpanel = new JPanel();
		buttonpanel.setPreferredSize(new Dimension(300, 30));
		buttonpanel.setLayout(new BoxLayout(buttonpanel, BoxLayout.X_AXIS));
		buttonpanel.add(mOkButton);
		JPanel spacer1 = new JPanel();
		spacer1.setPreferredSize(new Dimension(20,10));
		buttonpanel.add(spacer1);
		buttonpanel.add(mCancelButton);
		
		
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
		
		//add action listeners to buttons
		mOkButton.addActionListener(this);
		mCancelButton.addActionListener(this);
		
		//do all the final setup
		this.pack();
		this.setResizable(false);
		this.setLocationRelativeTo(parent);
		this.setVisible(true);
		
	}
	
	//FUNCTIONS
	
	/**
	 * Sets an action that will fire when buttons are pressed.
	 * <P>
	 * If the user presses the "cancel" button this window will
	 * simply dispose of itself.  If the user presses "ok" then the window
	 * will do some consistency checking on it's contents, then attempt to
	 * produce the necessary information, print it to the parent's text box
	 * and close.
	 *
	 * @param e1 The action event (button press).
	 */
	public void actionPerformed(ActionEvent e1) {
		if(e1.getActionCommand().equalsIgnoreCase("cancel")){
			//close the window
			this.dispose();
		}else if (e1.getActionCommand().equalsIgnoreCase("ok")){
			//run the steganalysis :)
			final WorkerThread worker = new WorkerThread(){
				public void doWork(){
			mMe.setVisible(false);
			mParent.setVisible(false);
			WorkingPanel pane = new WorkingPanel();
			pane.show();
			
			BufferedImage stego;
			
			//got an image
			try{
				stego = ImageIO.read(new File(mIIPanel.getPath()));
				if(stego == null){
					throw new Exception("Could not read file.");
				}
			}catch(Exception e){
				pane.hide();
				JOptionPane.showMessageDialog
				(null, "ERROR: Could not read image file",
						"Error!",
						JOptionPane.ERROR_MESSAGE);
				mParent.setVisible(true);
				mMe.setVisible(true);
				return;
			}
			
			//check at least one of the steganalysis options 
			//is filled in.
			if(!(mSTPanel.isLaplaceSelected() || mSTPanel.isRSAnalysisSelected() 
					||	mSTPanel.isSamplePairsSelected())){
				pane.hide();
				JOptionPane.showMessageDialog
				(null, "ERROR: You must select at least one steganalysis type!",
						"Error!",
						JOptionPane.ERROR_MESSAGE);
				mParent.setVisible(true);
				mMe.setVisible(true);
				return;				
			}
			
			//check one of the output option is filled in
			if(! (mOLPanel.isToFile() || mOLPanel.isToResultsWindow())){
				pane.hide();
				JOptionPane.showMessageDialog
				(null, "ERROR: You must select an output option!",
						"Error!",
						JOptionPane.ERROR_MESSAGE);
				mParent.setVisible(true);
				mMe.setVisible(true);
				return;
			}
			
			//now we are going to try and run all the different 
			//analysis options...
			StegAnalyser sa = new StegAnalyser(mSTPanel.isRSAnalysisSelected(),
					mSTPanel.isSamplePairsSelected(), mSTPanel.isLaplaceSelected());
			try{
				String results = sa.run(stego);
				
				//now if the option is set to save to file, open a save dialog...
				if(mOLPanel.isToFile()){
					try{
						pane.hide();
						JFileChooser jfc = new JFileChooser();
						jfc.setDialogTitle("Pick a file to save results to");
						jfc.setAcceptAllFileFilterUsed(true);
						int choice = jfc.showSaveDialog(null);
						
						if(choice == JFileChooser.APPROVE_OPTION){
							if(jfc.getSelectedFile() != null){
								BufferedWriter bw = new BufferedWriter(new FileWriter
										(jfc.getSelectedFile()));
								bw.write(results, 0, results.length());
								bw.close();
							}
						}		
						pane.show();
					}catch(Exception e2){
						pane.hide();
						JOptionPane.showMessageDialog
						(null, "ERROR: Error writing results file, " +
								"check permissions and try again.",
								"Error!",
								JOptionPane.ERROR_MESSAGE);
						mParent.setVisible(true);
						mMe.setVisible(true);
						return;
					}
				}
				
				//show it in the window (if option is set)
				if(mOLPanel.isToResultsWindow()){
					mResults.setText(results);
					//set the cursor to the start of the window
					mResults.setCaretPosition(0);
				}
				
			}catch(Exception e){
				pane.hide();
				JOptionPane.showMessageDialog
				(null, "ERROR: Error running benchmarks!",
						"Error!",
						JOptionPane.ERROR_MESSAGE);
				mParent.setVisible(true);
				mMe.setVisible(true);
				return;
			}
			
			//clean up and report success			       
			mParent.setVisible(true);
			pane.hide();
			JOptionPane.showMessageDialog(mParent, 
					"Success! Steganalysis information generated.",
					"Success!",
					JOptionPane.INFORMATION_MESSAGE);
			mMe.dispose();
				}
			};
			worker.start();
		}
	}	
	
	
	//VARIABLES
	
	/**
	 * Global reference to this object.
	 */
	private SteganalysisWindow mMe = this;
		
	/**
	 * An ok button to change all the options.
	 */
	private JButton mOkButton;
	
	/**
	 * A cancel button to stop all the changes.
	 */
	private JButton mCancelButton;
	
	/**
	 * A panel with the output options.
	 */
	private OutputLocationPanel mOLPanel;
	
	/**
	 * A panel with the input image file.
	 */
	private InputImagePanel mIIPanel;
	
	/**
	 * A panel full of steganalysis tick boxes.
	 */
	private StegTickPanel mSTPanel;
	
	/**
	 * The results pane to output to.
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
	
}
//end of class.
