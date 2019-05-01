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

import invisibleinktoolkit.util.InputImagesFileFilter;
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
 * A panel for picking an image file.
 * <P>
 * A panel for a image file consists of a button and a text field.  The
 * button allows the user to bring up a file dialog, and from there,
 * choose a file.  The text panel then shows the file that has 
 * been chosen by the user. This panel also includes a viewing button
 * that allows the user to view the image they have selected.
 *
 * @author Kathryn Hempstalk.
 */
public class InputImagePanel extends JPanel implements ActionListener{
	
	//CONSTRUCTORS
	
	/**
	 * Sets up an image panel.
	 *
	 * @param title The title for this panel.
	 * @param buttontext The text to display on the button.
	 * @param allowviewbutton Whether to show the view button or not.
	 */
	public InputImagePanel(String title, String buttontext, 
			       boolean allowviewbutton){
		this(title, buttontext, allowviewbutton, null);
	}
	
	/**
	 * Sets up an image panel.
	 *
	 * @param title The title for this panel.
	 * @param buttontext The text to display on the button.
	 * @param allowviewbutton Whether to show the view button or not.
	 * @param embed The embedder window (or null if there is not one).
	 */
	public InputImagePanel(String title, String buttontext, 
			       boolean allowviewbutton, Embedder embed){
		super();
		//setup the layout
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setBorder(new TitledBorder(title));
		this.setPreferredSize(new Dimension(740,50));
		
		//setup the button
		mButton = new JButton(buttontext);
		mButton.setToolTipText(title);
		mButton.setPreferredSize(new Dimension(130,26));
		mButton.addActionListener(this);
		this.add(mButton);
		
		
		//now add a panel to fill up the rest of the space
		JPanel fillerpanel = new JPanel();
		fillerpanel.setPreferredSize(new Dimension(20,1));
		this.add(fillerpanel);
		
		//setup the text field
		mTPath = new JTextField();
		mTPath.setToolTipText("The file currently selected");
		mTPath.setEditable(false);
		this.add(mTPath);
		
		//setup a button to view the image with
		mViewButton = new ViewButton("View");
		if(allowviewbutton){
		    this.add(mViewButton);
		}
		
		//setup the cover path
		mPath = "";
		
		mEmbedder = embed;
	}
	
	
	//FUNCTIONS
	
	/**
	 * Sets an action that will fire when the button is pressed.
	 *
	 * @param e The action event (button press).
	 */
	public void actionPerformed(ActionEvent e) {
		String s = this.getInputFile();
		if(s != null){
			//fill in the message path, and text field
			mPath = s;
			s = s.substring(s.lastIndexOf(System.getProperty("file.separator")) + 1
					, s.length());
			mTPath.setText(s);
			mViewButton.setImage(mPath);
			if(mEmbedder != null){
				mEmbedder.updateEmbeddingRate();
			}
		}
	}
	
	
	/**
	 * Brings up a file chooser dialog to help the user pick an image file.
	 *
	 * This particular file chooser will be an "open" dialog, and will allow
	 * either all files, jpg files, bmp files, and png files.
	 * 
	 * @return The string path of the selected file, or null if no file
	 * was selected.
	 */
	private String getInputFile(){
		//setup the file chooser
		JFileChooser jfc = new JFileChooser();
			
		jfc.setDialogTitle("Pick an image");
		jfc.setAcceptAllFileFilterUsed(true);
		jfc.addChoosableFileFilter(new InputImagesFileFilter());
		if(mPath != null){
			if(!mPath.equals("")){
				File afile = new File(getPath());
				if(afile.exists())
					jfc.setSelectedFile(afile);
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
	 * Gets the path of the image currently selected in this
	 * panel.
	 *
	 * @return The full path of the message.
	 */
	public String getPath(){
		return mPath;
	}
	
	
	//VARIABLES
	
	/**
	 * A button for the panel.
	 */
	private JButton mButton;
	
	/**
	 * A text area for the panel.
	 */
	private JTextField mTPath;
	
	/**
	 * The path to the image currently selected.
	 */
	private String mPath;
	
	/**
	 * The encoding window (or null if this isn't in one).
	 */
	private Embedder mEmbedder;
	
	/**
	 * A button to view the image.
	 */
	private ViewButton mViewButton;
	
	/**
	 * The serialisation ID.
	 */
	private static final long serialVersionUID = 0;
	
}
//end of class.
