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
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.TitledBorder;
import javax.swing.JTextField;
import javax.swing.JFileChooser;
import java.awt.Dimension;
import java.io.File;

/**
 * A panel for picking the message file to be output.
 *
 * @author Kathryn Hempstalk.
 */
public class MessageOutputPanel extends JPanel implements ActionListener{
	
	//CONSTRUCTORS
	
	/**
	 * Sets up a message panel.
	 *
	 */
	public MessageOutputPanel(){
		super();
		//setup the layout
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setBorder(new TitledBorder("Pick a message file to write to"));
		this.setPreferredSize(new Dimension(740,50));
		
		//setup the button
		mButton = new JButton("Set Message");
		mButton.addActionListener(this);
		mButton.setPreferredSize(new Dimension(130,26));
		mButton.setToolTipText("Pick a message file to write to");
		this.add(mButton);
		
		//now add a panel to fill up the rest of the space
		JPanel fillerpanel = new JPanel();
		fillerpanel.setPreferredSize(new Dimension(20,1));
		this.add(fillerpanel);
		
		//setup the text field
		mPath = new JTextField(50);
		mPath.setEditable(false);
		mPath.setToolTipText("The file currently selected for the message output");
		this.add(mPath);
	}
	
	//FUNCTIONS
	
	/**
	 * Sets an action that will fire when the button is pressed.
	 *
	 * @param e The action event (button press).
	 */
	public void actionPerformed(ActionEvent e) {
		String s = this.getOutputFile();
		if(s != null){
			//fill in the message path, and text field
			mPath.setText(s);
			mFilePath = s;
		}	   
	}
	
	
	/**
	 * Brings up a file chooser dialog to help the user pick an output message.
	 *
	 * This particular file chooser will be an "save" dialog
	 * 
	 * 
	 * @return The string path of the selected file, or null if no file
	 * was selected.
	 */
	private String getOutputFile(){
		JFileChooser jfc = new JFileChooser();
		jfc.setDialogTitle("Pick an output message file");
		jfc.addChoosableFileFilter(jfc.getAcceptAllFileFilter());
		if(mFilePath != null){
			if(!mFilePath.equals("")){
				File fmessage = new File(mFilePath);
				if(fmessage.exists())
					jfc.setSelectedFile(fmessage);
			}
		}
		
		//bring it up
		int returnval = jfc.showSaveDialog(null);
		
		//check the user pressed ok.
		if(returnval == JFileChooser.APPROVE_OPTION){
			return jfc.getSelectedFile().getPath();
		}
		else return null;	    
		
	}
	
	
	/**
	 * Gets the file currently selected in this pane.
	 *
	 * @return The file currently selected.
	 */
	public String getOutputMessageFile(){
		return mFilePath;
	}
	
	
	//VARIABLES
	
	/**
	 * A button for the panel.
	 */
	private JButton mButton;
	
	/**
	 * A text area for the panel.
	 */
	private JTextField mPath;
	
	/**
	 * The stego file currently selected.
	 */
	private String mFilePath;
	
	/**
	 * The serialisation ID.
	 */
	private static final long serialVersionUID = 0;
	
}
//end of class.
