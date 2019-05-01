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

import javax.swing.JTabbedPane;
import javax.swing.JFrame;
import java.awt.BorderLayout;


/**
 * A GUI for a steganography program.
 * <P>
 * This provides all the functionality needed to graphically get all the
 * information needed for steganographic operations such as encoding,decoding,
 * simulating and benchmarking.
 *
 * @author Kathryn Hempstalk.
 */
public class GUI extends JFrame{
	
	/**
	 * Creates a new GUI with default title.
	 *
	 */
	public GUI(){
		this("Digital Invisible Ink Toolkit");
	}
	
	/**
	 * Creates a new GUI with a set title.
	 *
	 * @param title The title for the window.
	 */
	public GUI(String title){
		super(title);
		
		//Make sure we have nice window decorations.
		JFrame.setDefaultLookAndFeelDecorated(true);
		
		//Create and set up the window.
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//create a tabbed pane window...
		JTabbedPane program = new JTabbedPane();
		//add encoder...
		program.addTab("    Encode    ", null, new Encoder(this),
		"Encode a message onto an image");
		
		//add decoder...
		program.addTab("    Decode   ", null, new Decoder(this),
		"Decode a message from an image");
		
		//add simulator...
		program.addTab("   Simulate   ", null, new Simulator(this),
		"Simulate hiding a message on an image");

		//add analysis...
		program.addTab("   Analysis   ", null, new StegAnalyser(this),
			       "Analyse a stego-image");
		
		//Create and set up the content pane.
		program.setOpaque(true);
		
		this.getContentPane().add(program, BorderLayout.CENTER);
		
		
		//Display the window.
		this.pack();
		this.setResizable(false);
		this.setLocation(25,25);
	}    
	
	//VARIABLES
	
	/**
	 * The serialisation ID.
	 */
	private static final long serialVersionUID = 0;
	
	
}
//end of class.
