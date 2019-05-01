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
import invisibleinktoolkit.util.AFileFilter;

/**
 * A panel for picking stego file to be output.
 *
 * @author Kathryn Hempstalk.
 */
public class StegoImagePanel extends JPanel implements ActionListener{
	
	//CONSTRUCTORS
	
	/**
	 * Sets up a stego-image panel.
	 */
	public StegoImagePanel(){
		this("Set the stego image to write to", "Set Image");
	}
	
	/**
	 * Sets up a stego-image panel.
	 *
	 */
	public StegoImagePanel(String heading, String buttontext){
		super();
		//setup the layout
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setBorder(new TitledBorder(heading));
		this.setPreferredSize(new Dimension(740,50));
		
		//setup the button
		mButton = new JButton(buttontext);
		mButton.addActionListener(this);
		mButton.setPreferredSize(new Dimension(130,26));
		mButton.setToolTipText(heading);
		this.add(mButton);
		
		//now add a panel to fill up the rest of the space
		JPanel fillerpanel = new JPanel();
		fillerpanel.setPreferredSize(new Dimension(20,1));
		this.add(fillerpanel);
		
		//setup the text field
		mPath = new JTextField(50);
		mPath.setEditable(false);
		mPath.setPreferredSize(new Dimension(540,26));
		mPath.setToolTipText("The file currently selected for the image");
		this.add(mPath);
	}
	
	//FUNCTIONS
	
	/**
	 * Sets an action that will fire when the button is pressed.
	 *
	 * @param e The action event (button press).
	 */
	public void actionPerformed(ActionEvent e) {
		File s = this.getOutputStegoFile();
		if(s != null){
			//fill in the message path, and text field
			mPath.setText(s.getPath());
			mStegoFile = s;
		}
	}
	
	
	/**
	 * Brings up a file chooser dialog to help the user pick a stego image.
	 *
	 * This particular file chooser will be an "save" dialog, and will only select
	 * bitmap and png images.  It will automatically append the extension if the user
	 * has failed to do so themselves.
	 * 
	 * @return The string path of the selected file, or null if no file
	 * was selected.
	 */
	private File getOutputStegoFile(){
		JFileChooser jfc = new JFileChooser();
		jfc.setAcceptAllFileFilterUsed(false);
		jfc.setDialogTitle("Pick a stego image file");
		AFileFilter pngfilter = new AFileFilter(".png", "PNG images (.png)");
		AFileFilter bmpfilter = new AFileFilter(".bmp", "Bitmap images (.bmp)");
		jfc.addChoosableFileFilter(bmpfilter);
		jfc.addChoosableFileFilter(pngfilter);
		if(mStegoFile != null){
			if(mStegoFile.exists()){
				jfc.setSelectedFile(mStegoFile);
				String aformat = getFormat();
				if(aformat.equalsIgnoreCase("png"))
					jfc.setFileFilter(pngfilter);
				else
					jfc.setFileFilter(bmpfilter);					
			}
		}
		
		//bring it up
		int returnval = jfc.showSaveDialog(null);
		
		String path;
		
		//check the user pressed ok.
		if(returnval == JFileChooser.APPROVE_OPTION){
			AFileFilter filter = (AFileFilter)jfc.getFileFilter();
			if(!jfc.getSelectedFile().getPath().endsWith(".png") &&
					filter.getExtension() == ".png"){
				path = jfc.getSelectedFile().getPath() + ".png";
			}else if(!jfc.getSelectedFile().getPath().endsWith(".bmp") &&
					filter.getExtension() == ".bmp"){
				path = jfc.getSelectedFile().getPath() + ".bmp";
			}else{
				path = jfc.getSelectedFile().getPath();
			}
			return new File(path);
		}
		else return null;	    		
	}
	
	
	/**
	 * Gets the file currently selected in this stego image pane.
	 *
	 * @return The file currently selected.
	 */
	public File getOutputFile(){
		return mStegoFile;
	}
	
	/**
	 * Gets the format of the output image file.
	 *
	 * @return The format of the output file.
	 */
	public String getFormat(){
		String filepath = mStegoFile.getPath();
		filepath = filepath.substring(filepath.lastIndexOf(".") + 1,
				filepath.length());
		return filepath;
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
	private File mStegoFile;
	
	/**
	 * The serialisation ID.
	 */
	private static final long serialVersionUID = 0;
}
//end of class.
