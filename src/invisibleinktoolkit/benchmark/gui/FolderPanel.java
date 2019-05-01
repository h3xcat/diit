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
 * A panel for picking a folder.
 *
 * @author Kathryn Hempstalk.
 */
public class FolderPanel extends JPanel implements ActionListener{
	
	//CONSTRUCTORS
	
	/**
	 * Sets up a folder panel.
	 *
	 * @param text The text to display in the panel border.
	 * @param showopen Whether to show open file dialog 
	 * (if false will show save).
	 */
	public FolderPanel(String text, boolean showopen){
		super();
		//setup the layout
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setBorder(new TitledBorder(text));
		this.setPreferredSize(new Dimension(740,50));
		
		//setup the button
		mButton = new JButton("Set Folder");
		mButton.addActionListener(this);
		mButton.setPreferredSize(new Dimension(130,26));
		mButton.setToolTipText(text);
		this.add(mButton);
		
		//now add a panel to fill up the rest of the space
		JPanel fillerpanel = new JPanel();
		fillerpanel.setPreferredSize(new Dimension(20,1));
		this.add(fillerpanel);
		
		//setup the text field
		mPath = new JTextField(50);
		mPath.setEditable(false);
		mPath.setToolTipText("The folder currently selected");
		this.add(mPath);
		
		mText = text;
		mShowOpen = showopen;
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
	 * @return The string path of the selected file, or null if no file
	 * was selected.
	 */
	private String getOutputFile(){
		JFileChooser jfc = new JFileChooser();
		jfc.setDialogTitle(mText);
		jfc.addChoosableFileFilter(jfc.getAcceptAllFileFilter());
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if(mFilePath != null){
			if(!mFilePath.equals("")){
				File fmessage = new File(mFilePath);
				if(fmessage.exists())
					jfc.setSelectedFile(fmessage);
			}
		}
		
		//bring it up
		int returnval;
		if(mShowOpen)
			returnval = jfc.showOpenDialog(null);
		else
			returnval = jfc.showSaveDialog(null);
		
		//check the user pressed ok.
		if(returnval == JFileChooser.APPROVE_OPTION){
			return jfc.getSelectedFile().getPath();
		}
		else return null;	    
		
	}
	
	
	/**
	 * Gets the folder currently selected in this pane.
	 *
	 * @return The folder currently selected.
	 */
	public String getOutputFolder(){
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
	 * The heading for the file chooser.
	 */
	private String mText;
	
	/**
	 * Whether to show a save (or open) file dialog.
	 */
	private boolean mShowOpen;
	
	/**
	 * The serialisation ID.
	 */
	private static final long serialVersionUID = 0;
	
}
//end of class.
