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

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import invisibleinktoolkit.stego.*;
import javax.swing.JOptionPane;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A decoding panel for a steganography program.
 * <P>
 * A decoding panel contains all that is needed to get the information
 * from the user in order to decode a message from an image.  This includes
 * something to get the stego-file, get the password, set an output message file,
 * and something to make it all go.
 *
 * @author Kathryn Hempstalk.
 */
public class Decoder extends JPanel implements ActionListener{
	
	
	//CONSTRUCTORS
	
	/**
	 * Creates a new decoder panel.
	 *
	 * @param parent The parent frame for this panel.
	 */
	public Decoder(Frame parent){
		super();
		mParent = parent;
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		this.setLayout(gridbag);
		this.setPreferredSize(new Dimension(750,420));
		
		//layout the image panel
		c.weightx = 0;
		c.gridy = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		mSPanel = new InputImagePanel("Pick an image to decode"
				, "Get Image", true);
		gridbag.setConstraints(mSPanel, c);
		this.add(mSPanel);
		
		//layout the password panel
		c.weightx = 0;
		c.gridy = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		mPPanel = new PasswordPanel("Enter the password");
		gridbag.setConstraints(mPPanel, c);
		this.add(mPPanel);
		
		//layout the algorithm panel
		c.weightx = 0;
		c.gridy = 2;
		c.gridwidth = GridBagConstraints.REMAINDER;
		mAPanel = new AlgorithmPanel(parent);
		gridbag.setConstraints(mAPanel, c);
		this.add(mAPanel);
		
		//layout the message output panel
		c.weightx = 0;
		c.gridy = 3;
		c.gridwidth = GridBagConstraints.REMAINDER;
		mMOPanel = new MessageOutputPanel();
		gridbag.setConstraints(mMOPanel, c);
		this.add(mMOPanel);
		
		//add the button to make it all go...
		mGoButton = new JButton("Go");
		mGoButton.setToolTipText("Decode message from stego image");
		mGoButton.setPreferredSize(new Dimension(150,40));
		mGoButton.addActionListener(this);
		
		//now add a panel to fill up the rest of the space
		JPanel spacer = new JPanel();
		spacer.setPreferredSize(new Dimension(500,10));
		c.weightx = 0;
		c.gridy = 4;
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(spacer, c);
		this.add(spacer);
		
		//layout button and add it...
		c.weightx = 0;
		c.gridy = 5;
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(mGoButton, c);
		this.add(mGoButton);
		
		//now add a panel to fill up the rest of the space
		JPanel fillerpanel = new JPanel();
		fillerpanel.setPreferredSize(new Dimension(500,165));
		c.weightx = 0;
		c.gridy = 6;
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(fillerpanel, c);
		this.add(fillerpanel);
		
	}
	
	
	/**
	 * Sets an action that will fire when the button is pressed.
	 *
	 * @param e The action event (button press).
	 */
	public void actionPerformed(ActionEvent e) {
		final WorkerThread worker = new WorkerThread(){
			public void doWork(){
				//recover a hidden message
				StegoImage stego;
				WorkingPanel pane = new WorkingPanel();
				mParent.setVisible(false);
				pane.show();		
				
				try{
					stego = new StegoImage(ImageIO.read(new File(mSPanel.getPath())));
				}catch(Exception e3){
					pane.hide();
					JOptionPane.showMessageDialog(null, 
							"ERROR: Stego image is not a valid file!",
							"Error!",
							JOptionPane.ERROR_MESSAGE);
					mParent.setVisible(true);
					return;
				}
				
				if(mMOPanel.getOutputMessageFile() == null){
					pane.hide();
					JOptionPane.showMessageDialog(null, 
							"ERROR: No message output file has been set!",
							"Error!",
							JOptionPane.ERROR_MESSAGE);
					mParent.setVisible(true);
					return;
				}
				
				try{
                                    
                                        FileOutputStream fstream = new FileOutputStream(mMOPanel.getOutputMessageFile());
					mAPanel.getAlgorithm().decode(stego, mPPanel.getPassword(), fstream);
                                        fstream.close();
				}catch(IOException e3){
					pane.hide();
					JOptionPane.showMessageDialog(null, 
							"ERROR: Could not decode message from image!",
							"Error!",
							JOptionPane.ERROR_MESSAGE);
					mParent.setVisible(true);
					return;
				}catch(NoMessageException nme){
					pane.hide();
					JOptionPane.showMessageDialog(null, 
							"ERROR: Could not find a message on image with given settings!",
							"Error - No Message!",
							JOptionPane.ERROR_MESSAGE);
					mParent.setVisible(true);
					return;
				}
				
				mParent.setVisible(true);
				pane.hide();
				JOptionPane.showMessageDialog(mParent, "Success! Message was retrieved.","Success", 
						JOptionPane.INFORMATION_MESSAGE);
			}
		};
		
		worker.start();
	}
	
	
	//VARIABLES
	
	/**
	 * The message output panel for the decoder.
	 */
	private MessageOutputPanel mMOPanel;
	
	/**
	 * The password panel for the decoder.
	 */
	private PasswordPanel mPPanel;
	
	/**
	 * A button to do the decoding from.
	 */
	private JButton mGoButton;
	
	/**
	 * The input stego image panel for the decoder.
	 */
	private InputImagePanel mSPanel;
	
	/**
	 * A panel to select the appropriate steganography algorithm.
	 */
	private AlgorithmPanel mAPanel;
	
	/**
	 * The parent frame.
	 */
	private Frame mParent;
	
	/**
	 * The serialisation ID.
	 */
	private static final long serialVersionUID = 0;
	
}//end of class.
