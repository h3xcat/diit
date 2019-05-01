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
import javax.swing.JTextArea;

import java.awt.Frame;
import javax.swing.JPanel;
import invisibleinktoolkit.gui.AlgorithmPanel;
import invisibleinktoolkit.gui.WorkerThread;
import invisibleinktoolkit.gui.WorkingPanel;
import javax.swing.BoxLayout;
import java.awt.Dimension;
import javax.swing.JButton;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import java.io.File;
import invisibleinktoolkit.benchmark.StegAnalyser;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Calendar;
import java.text.DateFormat;
import java.util.Date;


/**
 * A window for running bulk steganalysis.
 * <P>
 * Bulk steganalysis in this case is fixed to combining a folder
 * of messages and a folder of images together in a uniform way,
 * using a single algorithm.  The folder the results is output to
 * is then steg-analysed, and the results output to the correct folder.
 * The combined files are not deleted from the temporary folder.  Only
 * .txt message files will be combined with .jpg, .png and .bmp image files.
 * Any other sort of file in the given folders will be ignored.
 *
 * @author Kathryn Hempstalk.
 */
public class BulkSteganalysisWindow extends JDialog implements ActionListener{
	
	//CONSTRUCTORS
	
	/**
	 * Sets up a new bulk steganalysis window.
	 *
	 * @param parent The parent calling window.
	 * @param results The area to post any errors/results to.
	 */
	public BulkSteganalysisWindow(Frame parent, JTextArea results){
		//initial setup
		super(parent, 
				"Run Steganalysis across multiple images and messages",
				true);
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
		displaypanel.setPreferredSize(new Dimension(750, 440));
		
		//add in the message folder panel
		mMFPanel = new FolderPanel("Pick a folder containing the messages",
				true);
		c.weightx = 0;
		c.gridy = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(mMFPanel , c);
		displaypanel.add(mMFPanel);
		
		//add in the source image folder panel
		mSourceFPanel = new FolderPanel("Pick a folder containing the images",
				true);
		c.weightx = 0;
		c.gridy = 2;
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(mSourceFPanel , c);
		displaypanel.add(mSourceFPanel);
		
		//add in a panel to pick the algorithm
		mAPanel = new AlgorithmPanel(parent);
		c.weightx = 0;
		c.gridy = 3;
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(mAPanel , c);
		displaypanel.add(mAPanel);
		
		//add in the temporary output folder panel
		mTFPanel = new FolderPanel("Pick a folder to use for temporary files",
				false);
		c.weightx = 0;
		c.gridy = 4;
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(mTFPanel , c);
		displaypanel.add(mTFPanel);
		
		//add in the steg tick boxes
		mSTPanel = new StegTickPanel();
		c.weightx = 0;
		c.gridy = 5;
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(mSTPanel , c);
		displaypanel.add(mSTPanel);
		
		//now pick the format to write out in
		mFormatPanel = new FormatPanel();
		c.weightx = 0;
		c.gridy = 6;
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(mFormatPanel , c);
		displaypanel.add(mFormatPanel);
		
		//save panel
		mSavePanel = new FolderPanel
		("Pick a folder to output the file to",
				true);
		c.weightx = 0;
		c.gridy = 7;
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(mSavePanel , c);
		displaypanel.add(mSavePanel);
		
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
		c.gridy = 8;
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(spacer, c);
		displaypanel.add(spacer);
		
		c.weightx = 0;
		c.gridy = 9;
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(buttonpanel, c);
		displaypanel.add(buttonpanel);
		
		//now add a panel to fill up the rest of the space
		JPanel fillerpanel = new JPanel();
		fillerpanel.setPreferredSize(new Dimension(500,10));
		c.weightx = 0;
		c.gridy = 10;
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(fillerpanel, c);
		displaypanel.add(fillerpanel);
		this.setContentPane(displaypanel);
		
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
	 * If the "cancel" button is pressed this window will simply dispose of
	 * itself.  If it is the "ok" button, then the window will do some
	 * consistency checking around the contents of the window.  If it is
	 * all ok then the appropriate file will be generated, a success message
	 * printed to the screen, and the window is disposed of.
	 *
	 * @param e The action event (button press).
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equalsIgnoreCase("cancel")){
			//close the window
			this.dispose();
		}else if (e.getActionCommand().equalsIgnoreCase("ok")){
			
			final WorkerThread worker = new WorkerThread(){
				public void doWork(){
					//run bulk steganalysis
					mMe.setVisible(false);
					mParent.setVisible(false);
					WorkingPanel pane = new WorkingPanel();
					pane.show();
					
					//check all the fields are filled in correctly...
					/*
					 //is there a message folder?
					  try{
					  if(mMFPanel.getOutputFolder() == null){
					  throw new Exception("No message folder is set!");
					  }else{
					  if(mMFPanel.getOutputFolder() == ""){
					  throw new Exception("No message folder is set!");
					  }
					  }
					  }catch(Exception e1){
					  pane.hide();
					  JOptionPane.showMessageDialog
					  (this, "ERROR: No message folder is set!",
					  "Error!",
					  JOptionPane.ERROR_MESSAGE);
					  mParent.setVisible(true);
					  this.setVisible(true);
					  return;
					  }
					  
					  //is there an image folder?
					   try{
					   if(mSourceFPanel.getOutputFolder() == null){
					   throw new Exception("No source image folder is set!");
					   }else{
					   if(mSourceFPanel.getOutputFolder() == ""){
					   throw new Exception("No source image folder is set!");
					   }
					   }
					   }catch(Exception e1){
					   pane.hide();
					   JOptionPane.showMessageDialog
					   (this, "ERROR: No source image folder is set!",
					   "Error!",
					   JOptionPane.ERROR_MESSAGE);
					   mParent.setVisible(true);
					   this.setVisible(true);
					   return;
					   }
					   
					   //is there a temporary working folder?
					    try{
					    if(mTFPanel.getOutputFolder() == null){
					    throw new Exception("No temporary output folder is set!");
					    }else{
					    if(mTFPanel.getOutputFolder() == ""){
					    throw new Exception("No temporary output folder is set!");
					    }
					    }
					    }catch(Exception e1){
					    pane.hide();
					    JOptionPane.showMessageDialog
					    (this, "ERROR: No temporary output folder is set!",
					    "Error!",
					    JOptionPane.ERROR_MESSAGE);
					    mParent.setVisible(true);
					    this.setVisible(true);
					    return;
					    }
					    
					    //check at least one of the steganalysis options 
					     //is filled in.
					      if(!(mSTPanel.isLaplaceSelected() || mSTPanel.isRSAnalysisSelected() 
					      || mSTPanel.isSamplePairsSelected())){
					      pane.hide();
					      JOptionPane.showMessageDialog
					      (this, "ERROR: You must select at least one steganalysis type!",
					      "Error!",
					      JOptionPane.ERROR_MESSAGE);
					      mParent.setVisible(true);
					      this.setVisible(true);
					      return;				
					      }
					      */
					//is there a folder to save results to?
					try{
						if(mSavePanel.getOutputFolder() == null){
							throw new Exception("No output folder is set!");
						}else{
							if(mSavePanel.getOutputFolder() == ""){
								throw new Exception("No output folder is set!");
							}
						}
					}catch(Exception e1){
						pane.hide();
						JOptionPane.showMessageDialog
						(null, "ERROR: No output folder is set!",
								"Error!",
								JOptionPane.ERROR_MESSAGE);
						mParent.setVisible(true);
						mMe.setVisible(true);
						return;
					}
					
					//safe to continue...
					String errors = "";
					StegAnalyser sa = new StegAnalyser
					(mSTPanel.isRSAnalysisSelected(), 
							mSTPanel.isSamplePairsSelected(),
							mSTPanel.isLaplaceSelected());
					
					if(mMFPanel.getOutputFolder() != null){
						errors = sa.createCombineDirectories
						(new File(mMFPanel.getOutputFolder()),
								new File(mSourceFPanel.getOutputFolder()),
								new File(mTFPanel.getOutputFolder()),
								mAPanel.getAlgorithm());
					}
					
					
					File filetowrite;
					String filename1 = "";
					try{
						//grab the date - to make the file names unique
						Calendar rightnow = Calendar.getInstance();
						Date timenow = rightnow.getTime();
						String datenow = DateFormat.getDateTimeInstance().format(timenow);
						//remove all the illegal characters for filenames
						datenow = datenow.replace(' ','-');
						datenow = datenow.replace(',','-');
						datenow = datenow.replace('.','-');
						datenow = datenow.replace('\\','-');
						datenow = datenow.replace('/','-');
						datenow = datenow.replaceAll(":","");
						
						//CSV file
						if(mFormatPanel.isCSVSelected()){
							//now write to csv file...
							String csvfile = sa.getCSV
							(new File(mTFPanel.getOutputFolder()), 700);
							
							filename1 = "steganalysis-" + datenow + ".csv";
							filetowrite = new File(mSavePanel.getOutputFolder(),
									filename1);
							BufferedWriter bw = new BufferedWriter(new FileWriter
									(filetowrite));
							bw.write(csvfile, 0, csvfile.length());
							bw.close();
						}else{
							//ARFF file
							File temp = new File(mMFPanel.getOutputFolder());
							String relation = temp.getName();
							temp = new File(mSourceFPanel.getOutputFolder());
							relation = relation + "_" + temp.getName() 
							+ "_" + mAPanel.getAlgorithmName() + "_" + datenow;
							
							String arfffile = sa.getARFF
							(new File(mTFPanel.getOutputFolder()), 700, relation);
							filename1 = relation + ".arff";
							filetowrite = new File(mSavePanel.getOutputFolder(),
									filename1);
							BufferedWriter bw = new BufferedWriter(new FileWriter
									(filetowrite));
							bw.write(arfffile, 0, arfffile.length());
							bw.close();
						}
					}catch(Exception e2){
						pane.hide();
						JOptionPane.showMessageDialog
						(null, "ERROR: Could not produce output file " + filename1,
								"Error!",
								JOptionPane.ERROR_MESSAGE);
						mParent.setVisible(true);
						mMe.setVisible(true);
						mResults.setText(errors);
						mResults.setCaretPosition(0);
						return;				
					}		
					
					//clean up and exit
					mResults.setText(errors + "\n\n Success! File " 
							+ filename1 + " was successfully created.\n\n");
					mResults.setCaretPosition(0);
					mParent.setVisible(true);
					pane.hide();
					JOptionPane.showMessageDialog
					(mParent, 
							"Success! Bulk analysis information generated.",
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
	 * Global reference to this window.
	 */
	private BulkSteganalysisWindow mMe = this;
	
	/**
	 * An ok button to change all the options.
	 */
	private JButton mOkButton;
	
	/**
	 * A cancel button to stop all the changes.
	 */
	private JButton mCancelButton;
	
	/**
	 * A panel full of steganalysis tick boxes.
	 */
	private StegTickPanel mSTPanel;
	
	/**
	 * A panel for picking the folder full of messages.
	 */
	private FolderPanel mMFPanel;
	
	/**
	 * A panel for picking the folder full of source images.
	 */
	private FolderPanel mSourceFPanel;
	
	/**
	 * A panel for picking the folder to temporarily output to.
	 */
	private FolderPanel mTFPanel;
	
	/**
	 * A panel for picking the folder to save to.
	 */
	private FolderPanel mSavePanel;
	
	/**
	 * A panel for picking the algorithm.
	 */
	private AlgorithmPanel mAPanel;
	
	/**
	 * A panel for picking the output format.
	 */
	private FormatPanel mFormatPanel;
	
	/**
	 * The parent frame to this frame.
	 */
	private Frame mParent;
	
	/**
	 * The results pane to output to.
	 */
	private JTextArea mResults;
	
	/**
	 * The serialisation ID.
	 */
	private static final long serialVersionUID = 0;
	
}
//end of class.
