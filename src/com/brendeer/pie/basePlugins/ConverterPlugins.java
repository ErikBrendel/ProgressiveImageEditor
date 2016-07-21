package com.brendeer.pie.basePlugins;

import com.brendeer.pie.core.PluginContainer;
import com.brendeer.pie.filter.Filter;
import com.brendeer.pie.filter.FilterDataFormat;
import com.brendeer.pie.filter.FilterFactory;
import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * These are just some basic channel splitting / combining plugins
 * @author Erik
 */
public class ConverterPlugins {

	public static void load() {
		PluginContainer.registerPlugin("converter.rgbasplit", new FilterFactory() {

			@Override
			public Filter createInstance() {
				return new Filter("image:RGBA", "color:RGB,alpha:VALUE", "") {

					@Override
					public String getDisplayName() {
						return "RGBA to RGB/A Split";
					}

					@Override
					protected void render() {
						BufferedImage in = getInputImage("image");
						if (in == null) {
							setOutputImage("color", null);
							setOutputImage("alpha", null);
							return;
						}
						BufferedImage colorImg = new BufferedImage(in.getWidth(), in.getHeight(), FilterDataFormat.RGB.getBIType());
						BufferedImage alphaImg = new BufferedImage(in.getWidth(), in.getHeight(), FilterDataFormat.VALUE.getBIType());

						for (int x = 0; x < in.getWidth(); x++) {
							for (int y = 0; y < in.getHeight(); y++) {
								Color combined = new Color(in.getRGB(x, y), true);
								Color chroma = new Color(combined.getRed(), combined.getGreen(), combined.getBlue());
								Color alpha = new Color(combined.getAlpha(), combined.getAlpha(), combined.getAlpha());
								colorImg.setRGB(x, y, chroma.getRGB());
								alphaImg.setRGB(x, y, alpha.getRGB());
							}
						}
						setOutputImage("color", colorImg);
						setOutputImage("alpha", alphaImg);
					}
				};
			}
		});

		PluginContainer.registerPlugin("converter.rgbacombine", new FilterFactory() {

			@Override
			public Filter createInstance() {
				return new Filter("color:RGB,alpha:VALUE", "combined:RGBA", "") {

					@Override
					public String getDisplayName() {
						return "RGB/A to RGBA Combine";
					}

					@Override
					protected void render() {
						BufferedImage colorImg = getInputImage("color");
						BufferedImage alphaImg = getInputImage("alpha");
						if (colorImg == null || alphaImg == null) {
							setOutputImage("combined", null);
							return;
						}
						BufferedImage combinedImg = new BufferedImage(colorImg.getWidth(), colorImg.getHeight(), FilterDataFormat.RGBA.getBIType());
						for (int x = 0; x < combinedImg.getWidth(); x++) {
							for (int y = 0; y < combinedImg.getHeight(); y++) {
								Color chroma = new Color(colorImg.getRGB(x, y), false);
								Color alpha = new Color(alphaImg.getRGB(x, y), false);
								Color combined = new Color(chroma.getRed(), chroma.getGreen(), chroma.getBlue(), alpha.getRed());
								combinedImg.setRGB(x, y, combined.getRGB());
							}
						}
						setOutputImage("combined", combinedImg);
					}
				};
			}
		});

		PluginContainer.registerPlugin("converter.rgbsplit", new FilterFactory() {

			@Override
			public Filter createInstance() {
				return new Filter("image:RGB", "red:VALUE,green:VALUE,blue:VALUE", "") {

					@Override
					public String getDisplayName() {
						return "RGB channel split";
					}

					@Override
					protected void render() {
						BufferedImage in = getInputImage("image");
						if (in == null) {
							setOutputImage("red", null);
							setOutputImage("green", null);
							setOutputImage("blue", null);
							return;
						}
						BufferedImage redImg = new BufferedImage(in.getWidth(), in.getHeight(), FilterDataFormat.VALUE.getBIType());
						BufferedImage greenImg = new BufferedImage(in.getWidth(), in.getHeight(), FilterDataFormat.VALUE.getBIType());
						BufferedImage blueImg = new BufferedImage(in.getWidth(), in.getHeight(), FilterDataFormat.VALUE.getBIType());

						for (int x = 0; x < in.getWidth(); x++) {
							for (int y = 0; y < in.getHeight(); y++) {
								Color combined = new Color(in.getRGB(x, y), true);
								Color red = new Color(combined.getRed(), combined.getRed(), combined.getRed());
								Color green = new Color(combined.getGreen(), combined.getGreen(), combined.getGreen());
								Color blue = new Color(combined.getBlue(), combined.getBlue(), combined.getBlue());
								redImg.setRGB(x, y, red.getRGB());
								greenImg.setRGB(x, y, green.getRGB());
								blueImg.setRGB(x, y, blue.getRGB());
							}
						}
						setOutputImage("red", redImg);
						setOutputImage("green", greenImg);
						setOutputImage("blue", blueImg);
					}
				};
			}
		});

		PluginContainer.registerPlugin("converter.rgbcombine", new FilterFactory() {

			@Override
			public Filter createInstance() {
				return new Filter("red:VALUE,green:VALUE,blue:VALUE", "image:RGB", "") {

					@Override
					public String getDisplayName() {
						return "RGB channel combine";
					}

					@Override
					protected void render() {
						BufferedImage redImg = getInputImage("red");
						BufferedImage greenImg = getInputImage("green");
						BufferedImage blueImg = getInputImage("blue");
						if (redImg == null && greenImg == null && blueImg == null) {
							setOutputImage("image", null);
							return;
						}
						int w = 0;
						int h = 0;
						if (redImg != null) {
							w = Math.max(w, redImg.getWidth());
							h = Math.max(h, redImg.getHeight());
						}
						if (greenImg != null) {
							w = Math.max(w, greenImg.getWidth());
							h = Math.max(h, greenImg.getHeight());
						}
						if (blueImg != null) {
							w = Math.max(w, blueImg.getWidth());
							h = Math.max(h, blueImg.getHeight());
						}

						if (w < 1 || h < 1) {
							setOutputImage("image", null);
							return;
						}

						BufferedImage out = new BufferedImage(w, h, FilterDataFormat.RGB.getBIType());

						for (int x = 0; x < w; x++) {
							for (int y = 0; y < h; y++) {
								int r = 0;
								int g = 0;
								int b = 0;
								if (redImg != null) {
									r = (new Color(redImg.getRGB(x, y))).getRed();
								}
								if (greenImg != null) {
									g = (new Color(greenImg.getRGB(x, y))).getRed();
								}
								if (blueImg != null) {
									b = (new Color(blueImg.getRGB(x, y))).getRed();
								}
								Color res = new Color(r, g, b);
								out.setRGB(x, y, res.getRGB());
							}
						}
						setOutputImage("image", out);
					}
				};
			}
		});
	}
}
