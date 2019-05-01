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

package invisibleinktoolkit.util;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * Filter for images to be read in.
 *
 * This filter is a filter for file dialog boxes.  It filters
 * just the given extension - so only files with these extensions
 * will show up in the file dialog boxes.
 *
 * @author Kathryn Hempstalk.
 */
public class AFileFilter extends FileFilter{
	
	//CONSTRUCTORS
	
	/**
	 * Creates a new file filter.
	 *
	 * @param extension The extension for this file filter.
	 * @param description The description of this file filter.
	 */
	public AFileFilter(String extension, String description){
		mDescription = description;
		mExtension = extension;
		
	}
	
	
	//FUNCTIONS
	
	/**
	 * Decides whether this file should show in the
	 * filter or not.
	 *
	 * @param afile The file to test.
	 * @return True if the file should show, false otherwise.
	 */
	public boolean accept(File afile){
		//check it isn't covered by the all files rule.
		if(mExtension == null) 
			return true;
		
		//now check if the file isn't null
		if(afile != null){
			//directories should always be shown.
			if(afile.isDirectory())
				return true;
			//if the extension matches, show it
			try{
				if(this.getFileExtension(afile).equalsIgnoreCase(mExtension))
					return true;
			}catch(Exception e){}
		}
		return false;
	}
	
	/**
	 * Returns the file extension of the given file.
	 *
	 * @param afile The file to get the extension of.
	 * @return The extension of the file.
	 */
	public String getFileExtension(File afile){
		return (afile != null)?((afile.getName().lastIndexOf('.')>0 
				&& afile.getName().lastIndexOf('.') <
				afile.getName().length()-1)?
						afile.getName().substring
						(afile.getName().lastIndexOf('.'))
						.toLowerCase():null):null;
	}
	
	/**
	 * Gets the extension described by this filter.
	 *
	 * @return The extension this filter filters.
	 */
	public String getExtension(){
		return mExtension;
	}
	
	/**
	 * Gets the description of this filter.
	 *
	 * @return The description of this filter.
	 */
	public String getDescription(){
		return (mExtension == null)? "All files": mDescription;
	}
	
	
	//VARIABLES
	
	/**
	 * The extension filtered by this filter.
	 */
	private String mExtension;
	
	/**
	 * The description of this filter.
	 */
	private String mDescription;
	
}
//end of class.
