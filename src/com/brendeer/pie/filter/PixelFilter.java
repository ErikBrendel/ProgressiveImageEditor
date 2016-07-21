package com.brendeer.pie.filter;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * A half-implementation of the abstract Filter-class, that only still needs a
 * function to modify one single pixel color at a time.
 *
 * Filters that only modify the colors of each pixel independently are easier to
 * write with this class
 *
 * @author Erik
 */
public abstract class PixelFilter extends Filter {

	public PixelFilter(String optionsConfig) {
		super("image:RGBA", "image:RGBA", optionsConfig);
	}

	@Override
	public void render() {
		BufferedImage in = getInputImage("image");
		if (in == null) {
			setOutputImage("image", null);
			return;
		}
		BufferedImage out = new BufferedImage(in.getWidth(), in.getHeight(), FilterDataFormat.RGBA.getBIType());
		for (int x = 0; x < in.getWidth(); x++) {
			for (int y = 0; y < in.getHeight(); y++) {
				Color inColor = new Color(in.getRGB(x, y), true);
				Color outColor = filter(inColor);
				out.setRGB(x, y, outColor.getRGB());
			}
		}
		setOutputImage("image", out);
	}

	/**
	 * generate the output-color by a given input-color
	 * @param in
	 * @return 
	 */
	public abstract Color filter(Color in);

}
