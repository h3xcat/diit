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
import javax.swing.border.TitledBorder;

import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import java.lang.StringBuffer;
import java.security.MessageDigest;

/**
 * A panel for entering in a password.
 * <P>
 * A password panel consists of a label and a password field - to 
 * enter in a password for a steganography program.
 *
 * @author Kathryn Hempstalk.
 */
public class PasswordPanel extends JPanel{
	
	//CONSTRUCTORS
	
	/**
	 * Sets up a password panel with a given label text.
	 *
	 * @param text The text for the label.
	 *
	 */
	public PasswordPanel(String text){
		super();
		//setup the layout
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setBorder(new TitledBorder(text));
		this.setPreferredSize(new Dimension(740,50));
		
		//setup the label
		mLabel = new JLabel(text + ":  ");
		mLabel.setPreferredSize(new Dimension(150,26));
		this.add(mLabel);
		
		//setup the password field
		mPasswordField = new JPasswordField(50);
		mPasswordField.setToolTipText("The password for hiding");
		this.add(mPasswordField);
		
	}
	
	/**
	 * Gets the password currently residing in the password field.
	 * <P>
	 * This does some calculations to return the password as a long value.
	 * The function first gets the password value in the box, converts it
	 * to a byte string.  This byte string is then digested into a MD5 value.
	 * The byte string of the digest is converted back into a string, which
	 * is then reduced to 15 characters.  Then the MD5 string is finally
	 * converted to a long value.  This computation is done to make the 
	 * password as unique as possible, and not necessarily short.
	 *
	 * @return A numerical version of the password.
	 */
	public long getPassword(){
		String pass = new String(mPasswordField.getPassword());
                
		try {
			byte[] passbytes = pass.getBytes();
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(passbytes);
			byte[] md5sum = digest.digest();
			String smd5sum = toHexString(md5sum);
			smd5sum = smd5sum.substring(0,15);
			return Long.parseLong(smd5sum, 16);
		} catch (Exception e) {
			return 0;
		}
	}
	
	
	/**
	 * Converts a byte string to a String.
	 *
	 * @param b The bytestring string to convert.
	 * @return The string as a String.
	 */
	private static String toHexString ( byte[] bytestring )
	{
		StringBuffer sb = new StringBuffer( bytestring.length * 2 );
		for ( int i = 0; i < bytestring.length; i++ )
		{
			// look up high nibble character
			sb.append( hexChar [( bytestring[i] & 0xf0 ) >>> 4] );
			
			// look up low nibble character
			sb.append( hexChar [bytestring[i] & 0x0f] );
		}
		return sb.toString();
	}
	
	
	//VARIABLES
	
	/**
	 * An array of hexadecimal characters.
	 */
	private static char[] hexChar = {
		'0' , '1' , '2' , '3' ,
		'4' , '5' , '6' , '7' ,
		'8' , '9' , 'a' , 'b' ,
		'c' , 'd' , 'e' , 'f'};
	
	/**
	 * A button for the panel.
	 */
	private JLabel mLabel;
	
	/**
	 * A text area for the panel.
	 */
	private JPasswordField mPasswordField;
	
	/**
	 * The serialisation ID.
	 */
	private static final long serialVersionUID = 0;
	
}
//end of class
