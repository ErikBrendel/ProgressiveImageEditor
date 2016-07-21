package com.brendeer.pie.filter;

import java.awt.image.BufferedImage;

/**
 * All the possible formats that a pin can have
 * @author Erik
 */
public enum FilterDataFormat {

	/**
	 * A full RGBA-Data bufferedImage
	 */
	RGBA(BufferedImage.TYPE_INT_ARGB),
	/**
	 * Just RGB-Data needed, but no alpha
	 */
	RGB(BufferedImage.TYPE_INT_RGB),
	/**
	 * A float array, stored inside the RED-Channel (maybe also in the others, but not to rely on)
	 */
	VALUE(BufferedImage.TYPE_INT_RGB),
	/**
	 * one single numeric value, passed as a VALUE-type-DataFormat with
	 * dimensions of 1x1
	 */
	CONSTANT(BufferedImage.TYPE_INT_RGB);

	private FilterDataFormat(int type) {
		this.type = type;
	}

	private int type;

	/**
	 * get the BufferedImage-Type this format needs
	 * @return 
	 */
	public int getBIType() {
		return type;
	}
}
