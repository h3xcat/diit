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
import java.awt.image.BufferedImage;
import java.io.FileInputStream;


/**
 * A simulation panel for a steganography program.
 *
 * A simulation panel is similar to a encoding panel, only instead of
 * writing the message, it just simulates doing so.  The output image
 * is a black and white image showing where the information is been hidden.
 * Lighter areas denote higher concentrations of hidden information.
 *
 * @author Kathryn Hempstalk.
 */
public class Simulator extends JPanel implements ActionListener, Embedder{
	
	
	//CONSTRUCTORS
	
	/**
	 * Creates a new simulator panel.
	 *
	 * @param parent The parent frame this panel lives in.
	 */
	public Simulator(Frame parent){
		//do initial setup
		super();
		mParent = parent;
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		this.setLayout(gridbag);
		this.setPreferredSize(new Dimension(750,420));
		
		//layout message getter
		c.weightx = 0;
		c.anchor = GridBagConstraints.PAGE_START;
		c.gridy = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		mMPanel = new MessagePanel(this);
		gridbag.setConstraints(mMPanel, c);
		this.add(mMPanel);
		
		//layout cover getter
		c.weightx = 0;
		c.gridy = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		mCPanel = new InputImagePanel("Pick a cover image", 
				"Get Cover", true, this);
		gridbag.setConstraints(mCPanel, c);
		this.add(mCPanel);
		
		//layout algorithm chooser
		c.weightx = 0;
		c.gridy = 2;
		c.gridwidth = GridBagConstraints.REMAINDER;
		mAPanel = new AlgorithmPanel(parent, this);
		gridbag.setConstraints(mAPanel, c);
		this.add(mAPanel);
		
		c.weightx = 0; 
		c.gridy = 3;
		c.gridwidth = GridBagConstraints.REMAINDER;
		mERPanel = new CapacityPanel();
		gridbag.setConstraints(mERPanel, c);
		this.add(mERPanel);
		
		//layout stego image (map) panel
		c.weightx = 0;
		c.gridy = 4;
		c.gridwidth = GridBagConstraints.REMAINDER;
		mSPanel = new StegoImagePanel("Set an output map image file", "Set Map");
		gridbag.setConstraints(mSPanel, c);
		this.add(mSPanel);
		
		//now add a panel to fill up the rest of the space
		JPanel spacer = new JPanel();
		spacer.setPreferredSize(new Dimension(500,10));
		c.weightx = 0;
		c.gridy = 5;
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(spacer, c);
		this.add(spacer);
		
		//add the button to make it all go...
		mGoButton = new JButton("Go");
		mGoButton.setToolTipText("Simulate encoding");
		mGoButton.setPreferredSize(new Dimension(150,40));
		mGoButton.addActionListener(this);
		c.weightx = 0;
		c.gridy = 6;
		c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(mGoButton, c);
		this.add(mGoButton);
		
		//now add a panel to fill up the rest of the space
		JPanel fillerpanel = new JPanel();
		fillerpanel.setPreferredSize(new Dimension(500,115));
		c.weightx = 0;
		c.gridy = 7;
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
				//first we check the two files are valid.
				InsertableMessage mess;
				CoverImage img;
				BufferedImage stego;
				WorkingPanel pane = new WorkingPanel();
				mParent.setVisible(false);
				pane.show();
				
				try{
					mess = new InsertableMessage(mMPanel.getPath());
				}catch(Exception e1){
					pane.hide();
					JOptionPane.showMessageDialog(null, "ERROR: Could not read message file",
							"Error!",
							JOptionPane.ERROR_MESSAGE);
					mParent.setVisible(true);
					return;
				}
				
				try{
					img = new CoverImage(new FileInputStream(mCPanel.getPath()));
				}catch(Exception e2){
					pane.hide();
					JOptionPane.showMessageDialog(null, "ERROR: Could not read cover image file",
							"Error!",
							JOptionPane.ERROR_MESSAGE);
					mParent.setVisible(true);
					return;
				}
				
				if(mSPanel.getOutputFile() == null){					
					pane.hide();
					JOptionPane.showMessageDialog(null, 
							"ERROR: No output simulation file has been set!",
							"Error!",
							JOptionPane.ERROR_MESSAGE);
					mParent.setVisible(true);
					return;
				}
				
				long password = System.currentTimeMillis();
				
				try{
					stego = mAPanel.getAlgorithm().outputSimulation(mess, img, password);
				}catch(Exception e3){
					e3.printStackTrace();
					pane.hide();
					JOptionPane.showMessageDialog(null, 
							"ERROR: Message does not fit on image!",
							"Error!",
							JOptionPane.ERROR_MESSAGE);
					mParent.setVisible(true);
					return;
				}
				
				try{
					ImageIO.write(stego, mSPanel.getFormat(), mSPanel.getOutputFile());
				}catch(Exception e4){
					pane.hide();
					JOptionPane.showMessageDialog(null, 
							"ERROR: Could not output stego image file!",
							"Error!",
							JOptionPane.ERROR_MESSAGE);
					mParent.setVisible(true);
					return;
				}
				
				mParent.setVisible(true);
				pane.hide();
				
				Object[] oarray = new Object[2];
				oarray[0] = "OK";
				oarray[1] = "View Results";
				int results = 
					JOptionPane.showOptionDialog(null, "Success! Simulation was successful.",
							"Success!",
							JOptionPane.INFORMATION_MESSAGE,
							JOptionPane.DEFAULT_OPTION, null,
							oarray, oarray[0]);
				
				//check the selection
				if(results == 1){
					ViewButton vb = new ViewButton("View Results");
					vb.setImage(mSPanel.getOutputFile().getPath());
					vb.doClick();
				}
				
			}
		};
		
		worker.start();		
	}
	
	
	public void updateEmbeddingRate(){
		InsertableMessage mess;
		CoverImage img;
		StegoAlgorithm stego;
		
		try{
			mess = new InsertableMessage(mMPanel.getPath());
		}catch(Exception e1){
			return;
		}
		
		try{
			img = new CoverImage(new FileInputStream(mCPanel.getPath()));
		}catch(Exception e2){
			return;
		}
		
		stego = mAPanel.getAlgorithm();
		
		mERPanel.setValue(mess, img, stego);		
	}
	
	//VARIABLES
	
	/**
	 * The message panel for the encoder.
	 */
	private MessagePanel mMPanel;
	
	/**
	 * The cover image panel for the encoder.
	 */
	private InputImagePanel mCPanel;
	
	
	/**
	 * A button to do the encoding from.
	 */
	private JButton mGoButton;
	
	/**
	 * The stego image panel for the encoder.
	 */
	private StegoImagePanel mSPanel;
	
	/**
	 * A panel to select the appropriate steganography algorithm.
	 */
	private AlgorithmPanel mAPanel;
	
	/**
	 * The parent frame.
	 */
	private Frame mParent;
	
	/**
	 * The panel showing the embedding rate.
	 */
	private CapacityPanel mERPanel;
	
	/**
	 * The serialisation ID.
	 */
	private static final long serialVersionUID = 0;
	
}//end of class.
