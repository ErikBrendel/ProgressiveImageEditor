package com.brendeer.pie.gui;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 * A static class consisting of several image previews, wich should be shown in
 * the editor
 *
 * @author Erik
 */
public class BackdropStack {

	private static HashMap<Long, ScalableBufferedImage> backdrops = new HashMap<>();

	private static BufferedImage combined = null;

	/**
	 * in the case that a backdropper (identified by its id as long) has new
	 * data to display (or null if none)
	 *
	 * @param id
	 * @param img
	 */
	public static void update(long id, BufferedImage img) {
		if (img == null) {
			backdrops.put(id, null);
		} else {
			backdrops.put(id, new ScalableBufferedImage(img));
		}
		combined = null;
	}

	/**
	 * create the full backdrop-image, combining all backdrops together, and
	 * fitting a given size
	 *
	 * @param size
	 * @return
	 */
	public static BufferedImage getCombined(Point size) {
		if (combined == null || combined.getWidth() != size.x || combined.getHeight() != size.y) {
			combined = new BufferedImage(size.x, size.y, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = combined.createGraphics();

			int numOfImages = 0;
			for (ScalableBufferedImage img : backdrops.values()) {
				if (img != null) {
					numOfImages++;
				}
			}

			//todo make some intelligent code to arrange multiple backdrop images next to each other
			if (numOfImages == 2) {
				//make both images next to each other
				Point inSize = new Point(size.x / 2, size.y);
				int pos = 0;
				for (ScalableBufferedImage img : backdrops.values()) {
					if (img == null) {
						continue;
					}
					BufferedImage draw = img.getInBounds(inSize);
					int dx = (size.x - draw.getWidth()) / 2;
					dx += pos * (size.x / 2) - size.x / 4;
					int dy = (size.y - draw.getHeight()) / 2;
					g.drawImage(draw, dx, dy, null);
					pos++;
				}
			} else {
				//since we have no idea to display this amount of images, just put them all over each other
				for (ScalableBufferedImage img : backdrops.values()) {
					if (img == null) {
						continue;
					}
					BufferedImage draw = img.getInBounds(size);
					int dx = (size.x - draw.getWidth()) / 2;
					int dy = (size.y - draw.getHeight()) / 2;
					g.drawImage(draw, dx, dy, null);
				}
			}
			g.dispose();
		}

		return combined;
	}

	/**
	 * a useful Image-boxing class that generates and caches scald versions of a
	 * given raw image
	 */
	private static class ScalableBufferedImage {

		private final BufferedImage orig;
		private BufferedImage scaled;

		public ScalableBufferedImage(BufferedImage orig) {
			this.orig = orig;
			this.scaled = null;
		}

		/**
		 * get the image scaled to exactly the given size, probably distorting
		 * its aspect ratio
		 *
		 * @param size
		 * @return
		 */
		public BufferedImage get(Point size) {
			if (scaled == null || scaled.getWidth() != size.x || scaled.getHeight() != size.y) {
				scaled = Util.getScaledImage(orig, size.x, size.y, Util.MODE_FINE);
			}
			return scaled;
		}

		/**
		 * scales the image down so that it fits inside the given rect-size, but
		 * keeps its aspect ratio
		 *
		 * @param maxSize
		 * @return
		 */
		public BufferedImage getInBounds(Point maxSize) {
			float fac = 0.00001f; //i just don't like to divide by zero

			fac = Math.max(fac, orig.getWidth() / (float) maxSize.x);
			fac = Math.max(fac, orig.getHeight() / (float) maxSize.y);

			int x = (int) Math.round(orig.getWidth() / fac);
			int y = (int) Math.round(orig.getHeight() / fac);

			return get(new Point(x, y));
		}

	}

}
