package com.brendeer.pie.gui;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/**
 * Some small useful operations for the GUI
 * @author Erik
 */
public class Util {

	/**
	 * draws a nice looking curve between the two given points
	 *
	 * @param g
	 * @param p1
	 * @param p2
	 */
	public static void drawCurve(Graphics2D g, Point p1, Point p2) {

		int count = (int) Math.pow(Math.hypot(p1.x - p2.x, p1.y - p2.y) * 1.3, 0.5);
		if (count < 1) {
			count = 1;
		}

		for (int i = 0; i < count; i++) {
			float factor = i / (float) count;
			float factor2 = (i + 1) / (float) count;
			Point d1 = interpol(p1, p2, factor);
			Point d2 = interpol(p1, p2, factor2);
			g.drawLine(d1.x, d1.y, d2.x, d2.y);
		}

		//g.drawLine(p1.x, p1.y, p2.x, p2.y);
	}

	/**
	 * makes a curvic interpolation between points
	 *
	 * @param p1
	 * @param p2
	 * @param factor
	 * @return
	 */
	private static Point interpol(Point p1, Point p2, float factor) {
		Point p = new Point((int) (p1.x * (1 - factor) + p2.x * factor), (int) (p1.y * (1 - factor) + p2.y * factor));

		int targety = factor <= 0.5 ? p1.y : p2.y;

		float facy = Math.abs(factor - 0.5f) * 2;

		p.y = (int) (targety * facy + p.y * (1 - facy));

		return p;
	}

	/*
	 *
	 * SCALING IMAGES
	 *
	 */
	public static final int MODE_FINE = AffineTransformOp.TYPE_BICUBIC;
	public static final int MODE_MEDIUM = AffineTransformOp.TYPE_BILINEAR;
	public static final int MODE_FAST = AffineTransformOp.TYPE_NEAREST_NEIGHBOR;

	public static BufferedImage getScaledImage(BufferedImage image, int width, int height, int mode) {
		return getScaledImage(image, width, height, mode, image.getType());
	}

	public static BufferedImage getScaledImage(BufferedImage image, int width, int height, int mode, int BufferedImageType) {
		if (width == 0 || height == 0) {
			return new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		}
		int imageWidth = image.getWidth();
		int imageHeight = image.getHeight();

		double scaleX = (double) width / imageWidth;
		double scaleY = (double) height / imageHeight;
		AffineTransform scaleTransform = AffineTransform.getScaleInstance(scaleX, scaleY);
		AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform, mode);

		return bilinearScaleOp.filter(image, new BufferedImage(width, height, BufferedImageType));
	}
}
