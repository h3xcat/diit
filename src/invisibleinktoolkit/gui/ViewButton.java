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
import java.awt.BorderLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import java.io.File;
import javax.swing.JFrame;
import java.awt.Graphics;
import javax.swing.JScrollPane;


/**
 * A button that causes an image to be displayed in a window.
 * <P>
 * This class represents a button and a window, the window is called
 * up when the button is pressed.  Only one instance of the window
 * exists for each view button (i.e. the image is replaced in the window
 * if the window is left open and the button pressed multiple times.
 *
 * @author Kathryn Hempstalk.
 */
public class ViewButton extends JButton implements ActionListener{
	
	//INNER CLASS
	
	/**
	 * A panel for displaying images.
	 *
	 * @author Kathryn Hempstalk.
	 */
	private class ImagePanel extends JPanel{
		
		
		/**
		 * Sets up the image panel with a given image.
		 *
		 * @param image The image to display.
		 */
		public ImagePanel(BufferedImage image){
			this.image = image;
			setOpaque(true);
			size = new Dimension(image.getWidth(), image.getHeight());
		}
		
		/**
		 * Repaints the frame with the current image.
		 *
		 * @param g The graphics settings to use.
		 */
		protected void paintComponent(Graphics g){
			super.paintComponent(g);
			int w = getWidth();
			int h = getHeight();
			int x = (w - size.width)/2;
			int y = (h - size.height)/2;
			g.drawImage(image, x, y, this);
		}
		
		/**
		 * Gets the preferred size of the panel.
		 *
		 * @return The preferred panel size.
		 */
		public Dimension getPreferredSize(){
			return size;
		}
		
		/**
		 * An image to display.
		 */
		private BufferedImage image;
		
		/**
		 * The size of the image to display.
		 */
		private Dimension size;
		
		/**
		 * The serialisation ID.
		 */
		private static final long serialVersionUID = 0;
		
	}
	//end of inner class
	
	//CONSTRUCTORS
	
	/**
	 * Sets up the view button with a given title.
	 *
	 * @param title The text to display on the button.
	 */
	public ViewButton(String title){
		super(title);
		this.setToolTipText("View selected image file");
		this.setPreferredSize(new Dimension(100,26));
		this.addActionListener(this);
	}
	
	/**
	 * Sets up the image to be displayed in the window for this button.
	 *
	 * @param path The path to the image on disk.
	 */
	public void setImage(String path){
		mPath = path;
	}
	
	
	/**
	 * Sets an action that will fire when the button is pressed. In this case,
	 * a window will be brought up displaying the image.  If the image is 
	 * bigger than the window, the window will have scroll bars so that the
	 * image can be scrolled.  The image will not be resized.
	 *
	 * @param e The action event (button press).
	 */
	public void actionPerformed(ActionEvent e) {
		if(mPath != null){
			try{
				BufferedImage img = ImageIO.read(new File(mPath));
				ImagePanel panel = new ImagePanel(img);
				if(mImageViewer == null){
					mImageViewer = new JFrame("Image Viewer");
					mImageViewer.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					
					mImageViewer.setResizable(true);
					JScrollPane scrollpane = new JScrollPane(panel);
					
					mImageViewer.getContentPane().add(scrollpane,
							BorderLayout.CENTER);
					mImageViewer.setSize(620,460);
					mImageViewer.setLocation(20,20);
					mImageViewer.setVisible(true);
				}else{
					//just update the current frame
					mImageViewer.getContentPane().removeAll();
					JScrollPane scrollpane = new JScrollPane(panel);
					mImageViewer.getContentPane().add(scrollpane,
							BorderLayout.CENTER);
					mImageViewer.setSize(620,460);
					mImageViewer.setLocation(20,20);
					mImageViewer.setVisible(true);
				}
			}catch(Exception exp){
				JOptionPane.showMessageDialog(null, "Image file is not a valid type!",
						"Error! Wrong image filetype!",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	
	//VARIABLES
	
	/**
	 * The frame to display the image within.
	 */
	private JFrame mImageViewer;
	
	/**
	 * The path to the image on disk.
	 */
	private String mPath;
	
	/**
	 * The serialisation ID.
	 */
	private static final long serialVersionUID = 0;
}
//end of  class.
