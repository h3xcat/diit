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
 * A panel for picking the message file.
 * <P>
 * A panel for a message file consists of a button and a text field.  The
 * button allows the user to bring up a file dialog, and from there,
 * choose a file to embed.  The text panel then shows the file that has 
 * been chosen by the user.
 *
 * @author Kathryn Hempstalk.
 */
public class MessagePanel extends JPanel implements ActionListener{
	
	//CONSTRUCTORS
	
	/**
	 * Sets up a message panel.
	 */
	public MessagePanel(){
		this(null);
	}
	
	/**
	 * Sets up a message panel in an encoder window.
	 *
	 * @param embed The embedder window, or null if there is none.
	 */
	public MessagePanel(Embedder embed){
		super();
		//setup the layout
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setBorder(new TitledBorder("Pick a message to embed"));
		this.setPreferredSize(new Dimension(740,50));
		
		//setup the button
		mButton = new JButton("Get Message");
		mButton.addActionListener(this);
		mButton.setPreferredSize(new Dimension(130,26));
		mButton.setToolTipText("Pick a message to embed");
		this.add(mButton);
		
		//now add a panel to fill up the rest of the space
		JPanel fillerpanel = new JPanel();
		fillerpanel.setPreferredSize(new Dimension(20,1));
		this.add(fillerpanel);
		
		//setup the text field
		mPath = new JTextField(50);
		mPath.setEditable(false);
		mPath.setToolTipText("The file currently selected for the message");
		this.add(mPath);
		
		//setup the message path
		mMessagePath = "";
		
		mEmbedder = embed;
	}
	
	//FUNCTIONS
	
	/**
	 * Sets an action that will fire when the button is pressed.
	 *
	 * @param e The action event (button press).
	 */
	public void actionPerformed(ActionEvent e) {
		String s = this.getMessageFile();
		if(s != null){
			//fill in the message path, and text field
			mMessagePath = s;
			s = s.substring(s.lastIndexOf(System.getProperty("file.separator")) + 1
					, s.length());
			mPath.setText(s);
			if(mEmbedder != null){
				mEmbedder.updateEmbeddingRate();
			}
		}
	}
	
	/**
	 * Brings up a file chooser dialog to help the user pick a message file.
	 *
	 * This particular file chooser will be an "open" dialog, and will accept all files.
	 * 
	 * @return The string path of the selected file, or null if no file
	 * was selected.
	 */
	private String getMessageFile(){
		//setup the file chooser
		JFileChooser jfc = new JFileChooser();
		jfc.setDialogTitle("Pick a message to embed");
		jfc.addChoosableFileFilter(jfc.getAcceptAllFileFilter());
		if(mMessagePath != null){
			if(!mMessagePath.equals("")){
				File fmessage = new File(mMessagePath);
				if(fmessage.exists())
					jfc.setSelectedFile(fmessage);
			}
		}
		
		//bring it up
		int returnval = jfc.showOpenDialog(null);
		
		//check the user pressed ok.
		if(returnval == JFileChooser.APPROVE_OPTION)
			return jfc.getSelectedFile().getPath();
		else return null;
	}
	
	/**
	 * Gets the path of the message currently selected in this
	 * message panel.
	 *
	 * @return The full path of the message.
	 */
	public String getPath(){
		return mMessagePath;
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
	 * The path to the message currently selected.
	 */
	private String mMessagePath;
	
	/**
	 * The encoder window.
	 */
	private Embedder mEmbedder;
	
	/**
	 * The serialisation ID.
	 */
	private static final long serialVersionUID = 0;
	
}
//end of class.
