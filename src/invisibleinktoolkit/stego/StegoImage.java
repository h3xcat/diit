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

package invisibleinktoolkit.stego;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;


/**
 * An image that has had steganography applied.
 * <P>
 * The image will be a 24 bit colour format, and if it is read from
 * disk will be in a lossless format.  If it is passed over as a 
 * buffered image, it is just a generic image, in 24 bit colour.
 *
 *
 * @author Kathryn Hempstalk.
 */
public class StegoImage{
	
	//CONSTRUCTORS
	
	/**
	 * Creates a new stego image from a given buffered
	 * image.
	 *
	 * @param image The image that has had steganography applied.
	 * @throws NullPointerException If image is null.
	 * @throws IllegalArgumentException If the file is not a 
	 * recognisable type, or if the incorrect number of 
	 * start/end bits is passed.
	 */
	public StegoImage(BufferedImage image)
	throws NullPointerException, IllegalArgumentException{
		
		if(image == null)
			throw new NullPointerException
			("Image must be set to a non-null value");
		else
			mStego = image;
	}
	
	
	//FUNCTIONS
	
	/**
	 * Writes an image out to disk.
	 *
	 * @param formatname The format type to output.
	 * @param output The file to write to.
	 * @return True if image write successful, false if no appropriate
	 * writers could be found.
	 * @throws IllegalArgumentException If the argurments are null.
	 * @throws IOException If an error occurs during writing.
	 */
	public boolean write(String formatname, File output)throws
			IllegalArgumentException, IOException{
		return ImageIO.write(mStego, formatname, output);
	}

	public boolean write(String formatname, OutputStream output)throws
			IllegalArgumentException, IOException{

		return ImageIO.write(mStego, formatname, output);
	}

	/**
	 * Gets the number of layers the image has.
	 * <P>
	 * The number of layers is determined by the colour depth of
	 * the image.  If a colour has a 24 bit colour depth, then the
	 * number of layers is 3, one for each of red, green and blue. In
	 * contrast, if the colour depth is 8, 1 layer is returned.
	 * <P>
	 * To summarise, 24 bits = 3 layers, 16 bit = 3 layers, 8 bit = 1.
	 * Images which do not have a deep enough set of colours return 0.
	 *
	 * @return The number of "layers" an image has.
	 */
	public int getLayerCount(){
		int type = mStego.getType();
		
		if (type == BufferedImage.TYPE_BYTE_BINARY)
			//1, 2 and 4 bit images
			return 0;
		else if (type == BufferedImage.TYPE_BYTE_INDEXED 
				|| type == BufferedImage.TYPE_BYTE_GRAY
				|| type == BufferedImage.TYPE_USHORT_GRAY)
			//8 bit images
			return 1;
		else
			//all other image types
			return 3;
	}
	
	/**
	 * Gets the image.
	 *
	 * @return The image.
	 */
	public BufferedImage getImage(){
		return mStego;
	}
	
	/**
	 * Gets a particular bit in the image, and puts
	 * it into the LSB of an integer.
	 *
	 * @param xpos The x position of the pixel on the image.
	 * @param ypos The y position of the pixel on the image.
	 * @param layer The layer (R,G,B) containing the bit.
	 * @param bitpos The bit position (0 - LSB -> 7 - MSB).
	 * @return The bit at the given position, as the LSB of an integer.
	 */
	public int getPixelBit(int xpos, int ypos, int layer, int bitpos){
		int pixel = mStego.getRGB(xpos, ypos);
		int layerpos = (layer * 8) + bitpos;
		return ((pixel >> layerpos) & 0x1);
	}
	
	
	//VARIABLES
	
	/**
	 * The image that has had steganography applied.
	 */
	private BufferedImage mStego;
	
}
//end of class.
