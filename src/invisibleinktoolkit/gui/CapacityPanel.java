/*
 *    Digital Invisible Ink Toolkit
 *    Copyright (C) 2005, 2006  K. Hempstalk	
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
import javax.swing.border.TitledBorder;
import java.awt.Dimension;
import javax.swing.JProgressBar;
import invisibleinktoolkit.stego.*;
import java.awt.Color;
import javax.swing.UIManager;



/**
 * A panel for showing the current embedding rate.
 *
 * @author Kathryn Hempstalk.
 */
public class CapacityPanel extends JPanel{
	
	//CONSTRUCTORS
	
	/**
	 * Sets up an embedding rate panel.
	 *
	 */
	public CapacityPanel(){
		super();
		//setup the layout
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setBorder(new TitledBorder("Current Embedding Rate"));
		this.setPreferredSize(new Dimension(740,50));
		
		//setup the capacity bar
		UIManager.put("ProgressBar.selectionForeground", Color.BLACK);
		UIManager.put("ProgressBar.selectionBackground", Color.BLACK);
		mCapacityBar = new JProgressBar(0,100);
		mCapacityBar.setValue(0);
		mCapacityBar.setStringPainted(true);
		mCapacityBar.setToolTipText("Aim for less than 10%");
		this.add(mCapacityBar);
	}
	
	/**
	 * Sets the current value of the capacity panel.
	 * <P>
	 * The rate is set by obtaining the writable size from the coverimage,
	 * the size of the message, and the algorithm's writing settings.
	 * 
	 * @param im The message to be inserted.
	 * @param ci The image being written onto.
	 * @param sa The steganography algorithm being used.
	 */
	public void setValue(InsertableMessage im, CoverImage ci, StegoAlgorithm sa){
		try{
			//work out the sizes
			long size = im.getSize();			
			int imgx = ci.getImage().getWidth();
			int imgy = ci.getImage().getHeight();
			int layercount = ci.getLayerCount();
			int bitspace = (sa.getEndBits() - sa.getStartBits()) + 1;
			float space = (imgx * imgy * layercount * bitspace);
			//calculate the percentage
			float percent;
			if((size * 8) > space)
				percent = 100;
			else
				percent = ((size * 8) / space) * 100;
			
			//draw the new progress bar
			if(percent <= 90){
				this.remove(mCapacityBar);
				UIManager.put("ProgressBar.foreground", Color.GREEN);
				mCapacityBar = new JProgressBar(0,100);
				mCapacityBar.setStringPainted(true);
				mCapacityBar.setToolTipText("Aim for less than 10%");
				mCapacityBar.setValue(Math.round(percent));
				this.add(mCapacityBar);
			}					
			else{
				this.remove(mCapacityBar);
				UIManager.put("ProgressBar.foreground", Color.RED);
				mCapacityBar = new JProgressBar(0,100);
				mCapacityBar.setStringPainted(true);
				mCapacityBar.setValue(100);
				mCapacityBar.setString("Not enough space to hide selected message in selected cover.");
				mCapacityBar.setToolTipText("Try changing the message, cover image, algorithm or algorithm options");
				this.add(mCapacityBar);
			}
		}catch(Exception e){
			return;
		}		
	}
	
	//VARIABLES
	
	/**
	 * A bar showing the current embedding rate.
	 */
	private JProgressBar mCapacityBar;
		
	/**
	 * The serialisation ID.
	 */
	private static final long serialVersionUID = 0;
	
}
