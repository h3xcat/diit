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

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.border.LineBorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * A non-blocking window to state "Working, please wait".
 *
 * @author Kathryn Hempstalk.
 */
public class WorkingPanel{
	
	//VARIABLES
	
	/**
	 * The dialog being hidden/shown.
	 */
	private JWindow mDialog;
	
	/**
	 * Button to cancel the current operation.
	 * 
	 */
	private JButton mCancelButton;
	
		
	//CONSTRUCTORS	
	
	
	/**
	 * Sets up a working panel that displays "Working, please wait".
	 *
	 * @param wt The worker thread that pressing the cancel button will stop.
	 */
	public WorkingPanel(){		
		Frame parent = null;
		mDialog = new JWindow(parent);
		JPanel contentPanel = new JPanel();
		contentPanel.setBorder(new LineBorder(Color.BLACK));
		contentPanel.setPreferredSize(new Dimension(200, 85));
		contentPanel.setLayout(new BorderLayout());
		
		JLabel label = new JLabel("    WORKING, PLEASE WAIT...  ");
		label.setPreferredSize(new Dimension(200,40));
				
		mCancelButton = new JButton("Exit DIIT");
		mCancelButton.setPreferredSize(new Dimension(200, 30));
		
		contentPanel.add(label, BorderLayout.NORTH);
		
		JProgressBar movingThing = new JProgressBar();
		movingThing.setIndeterminate(true);
		movingThing.setPreferredSize(new Dimension(200,15));
		contentPanel.add(movingThing, BorderLayout.CENTER);
		
		contentPanel.add(mCancelButton, BorderLayout.SOUTH);
		
		mCancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				System.exit(1);
				
			}
		});
				
		mDialog.getContentPane().add(contentPanel);
		mDialog.pack();
		mDialog.repaint();
		mDialog.setLocationRelativeTo(parent);
	}
	
	//FUNCTIONS
	
	/**
	 * Shows this dialog centered on the parent on screen.
	 */
	public void show(){
		mDialog.setVisible(true);
	}
	
	/**
	 * Hides this dialog.
	 */
	public void hide(){
		mDialog.setVisible(false);
	}
	
	
	
}
//end of class.
