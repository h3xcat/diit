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

import javax.swing.JDialog;
import java.awt.Frame;
import javax.swing.JPanel;
import invisibleinktoolkit.gui.InputImagePanel;
import invisibleinktoolkit.gui.WorkerThread;
import invisibleinktoolkit.benchmark.Benchmarker;
import invisibleinktoolkit.gui.WorkingPanel;
import javax.swing.BoxLayout;
import java.awt.Dimension;
import javax.swing.JButton;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import javax.swing.JOptionPane;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;


/**
 * A window for running benchmarking.
 * <P>
 * This window contains everything necessary for running a benchmark
 * on an image.  A benchmark requires an original image that must be
 * the same height and width as the original.  This window will perform
 * some consistency checking before running the benchmark to prove that
 * this is the case.  The benchmark results are all printed to the 
 * results window residing in the parent.  Any parameters for the 
 * benchmarks are fixed to avoid complication for the users - these
 * can be accessed by calling the methods directly in java (not via
 * this window).
 *
 * @author Kathryn Hempstalk.
 */
public class BenchmarkWindow extends JDialog implements ActionListener{
	
	//CONSTRUCTORS
	
	/**
	 * Sets up a new benchmark window.
	 *
	 * @param parent The parent frame calling this window.
	 * @param results The text frame to post the results to.
	 */
	public BenchmarkWindow(Frame parent, JTextArea results){
		//initial setup
		super(parent, "Run a Benchmark", true);
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
		displaypanel.setPreferredSize(new Dimension(750,340));
		
		//add in a panel to pick the picture this is for
		mIIPanel = new InputImagePanel("Pick the original image",
				"Get Image", false);
		c.weightx = 0;
		c.gridy = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(mIIPanel , c);
		displaypanel.add(mIIPanel);
		
		//add in a panel to pick the 2nd picture this is for
		mIIPanel2 = new InputImagePanel("Pick the stego image",
				"Get Image", false);
		c.weightx = 0;
		c.gridy = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(mIIPanel2 , c);
		displaypanel.add(mIIPanel2);
		
		//add in the analysis types to run
		mBTPanel = new BenchmarkTickPanel();
		c.weightx = 0;
		c.gridy = 2;
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(mBTPanel , c);
		displaypanel.add(mBTPanel);
		
		
		//add in the output options
		mOLPanel = new OutputLocationPanel();
		c.weightx = 0;
		c.gridy = 3;
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
		c.gridy = 4;
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(spacer, c);
		displaypanel.add(spacer);
		
		c.weightx = 0;
		c.gridy = 5;
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(buttonpanel, c);
		displaypanel.add(buttonpanel);
		this.setContentPane(displaypanel);
		
		//now add a panel to fill up the rest of the space
		JPanel fillerpanel = new JPanel();
		fillerpanel.setPreferredSize(new Dimension(500,10));
		c.weightx = 0;
		c.gridy = 6;
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
	 * If the user presses "cancel" then this window will simply
	 * be disposed of.  If the user presses "ok" then the window
	 * will check everything has been filled in correctly, then
	 * attempt to run the benchmarks and post back the results to 
	 * the text area on the parent window.
	 *
	 * @param e1 The action event (button press).
	 */
	public void actionPerformed(ActionEvent e1) {
		if(e1.getActionCommand().equalsIgnoreCase("cancel")){
			//close the window
			this.dispose();
		}else if (e1.getActionCommand().equalsIgnoreCase("ok")){
			final WorkerThread worker = new WorkerThread(){
				public void doWork(){
					//check everything's filled in right...
					mMe.setVisible(false);
					mParent.setVisible(false);
					WorkingPanel pane = new WorkingPanel();
					pane.show();
					
					BufferedImage orig, stego;
					
					//got an original image
					try{
						orig = 
							ImageIO.read(new File(mIIPanel.getPath()));
					}catch(Exception e){
						pane.hide();
						JOptionPane.showMessageDialog
						(null, "ERROR: Could not read original image file",
								"Error!",
								JOptionPane.ERROR_MESSAGE);
						mParent.setVisible(true);
						mMe.setVisible(true);
						return;
					}
					
					//got a stego image
					try{
						stego = 
							ImageIO.read(new File(mIIPanel2.getPath()));
					}catch(Exception e){
						pane.hide();
						JOptionPane.showMessageDialog
						(null, "ERROR: Could not read stego image file",
								"Error!",
								JOptionPane.ERROR_MESSAGE);
						mParent.setVisible(true);
						mMe.setVisible(true);
						return;
					}
					
					//check the images are the same size
					if(orig.getWidth() != stego.getWidth()
							|| orig.getHeight() != stego.getHeight()){
						pane.hide();
						JOptionPane.showMessageDialog
						(null, "ERROR: Images must be the same size!",
								"Error!",
								JOptionPane.ERROR_MESSAGE);
						mParent.setVisible(true);
						mMe.setVisible(true);
						return;
					}
					
					//check there are some check boxes ticked
					if(! (mBTPanel.isAverageAbsoluteDifferenceSelected() ||
							mBTPanel.isMeanSquaredErrorSelected() ||
							mBTPanel.isLpNormSelected() ||
							mBTPanel.isLaplacianMSErrorSelected() ||
							mBTPanel.isSNRSelected() ||
							mBTPanel.isPeakSNRSelected() ||
							mBTPanel.isNCCorrelationSelected() ||
							mBTPanel.isCorrelationQualitySelected())){
						pane.hide();
						JOptionPane.showMessageDialog
						(null, "ERROR: You must select some benchmarking types!",
								"Error!",
								JOptionPane.ERROR_MESSAGE);
						mParent.setVisible(true);
						mMe.setVisible(true);
						return;
					}
					
					//check one of the output options are selected
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
					
					
					//output the results and then close the window
					Benchmarker bmark = new Benchmarker
					(mBTPanel.isAverageAbsoluteDifferenceSelected(),
							mBTPanel.isMeanSquaredErrorSelected(),
							mBTPanel.isLpNormSelected(),
							mBTPanel.isLaplacianMSErrorSelected(),
							mBTPanel.isSNRSelected(),
							mBTPanel.isPeakSNRSelected(),
							mBTPanel.isNCCorrelationSelected(),
							mBTPanel.isCorrelationQualitySelected());
					
					//run it
					try{
						String results = bmark.run(orig, stego);
						
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
							"Success! Benchmarking information generated.",
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
	private BenchmarkWindow mMe = this;
	
	/**
	 * An ok button to change all the options.
	 */
	private JButton mOkButton;
	
	/**
	 * A cancel button to stop all the changes.
	 */
	private JButton mCancelButton;
	
	/**
	 * A panel for picking the original image to analyse.
	 */
	private InputImagePanel mIIPanel;
	
	/**
	 * A panel for picking the stego image to analyse.
	 */
	private InputImagePanel mIIPanel2;
	
	/**
	 * A panel full of benchmark tick boxes.
	 */
	private BenchmarkTickPanel mBTPanel;
	
	/**
	 * A panel to select the output locations.
	 */
	private OutputLocationPanel mOLPanel;
	
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
