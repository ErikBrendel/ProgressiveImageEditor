package com.brendeer.pie.basePlugins;

import com.brendeer.pie.core.PluginContainer;
import com.brendeer.pie.filter.Filter;
import com.brendeer.pie.filter.FilterDataFormat;
import com.brendeer.pie.filter.FilterFactory;
import com.brendeer.pie.filter.PixelFilter;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Here are some nonsense / testing / really simple plugins
 * @author Erik
 */
public class BasePlugins {

	public static void load() {
		
		/**
		 * this filter simply inverts color channels if set in the options
		 */
		PluginContainer.registerPlugin("default.invertColors", new FilterFactory() {

			@Override
			public Filter createInstance() {
				return new PixelFilter(""
						+ "Invert red:checkbox:true,Invert green:checkbox:true,"
						+ "Invert blue:checkbox:true,Invert alpha:checkbox:false") {
							@Override
							public String getDisplayName() {
								return "Color inverter";
							}

							@Override
							public Color filter(Color in) {
								int r = in.getRed();
								if (getOption("Invert red").getBoolean()) {
									r = 255 - r;
								}
								int g = in.getGreen();
								if (getOption("Invert green").getBoolean()) {
									g = 255 - g;
								}
								int b = in.getBlue();
								if (getOption("Invert blue").getBoolean()) {
									b = 255 - b;
								}
								int a = in.getAlpha();
								if (getOption("Invert alpha").getBoolean()) {
									a = 255 - a;
								}
								return new Color(r, g, b, a);
							}
						};
			}
		});
		
		/**
		 * here we have some simple overlaying of two images
		 */
		PluginContainer.registerPlugin("default.alphaOver", new FilterFactory() {

			@Override
			public Filter createInstance() {
				return new Filter("image1:RGBA,image2:RGBA", "combined:RGBA", "") {

					@Override
					public String getDisplayName() {
						return "Alpha combine";
					}

					@Override
					protected void render() {
						BufferedImage img1 = getInputImage("image1");
						BufferedImage img2 = getInputImage("image2");
						if (img1 == null && img2 == null) {
							setOutputImage("combined", null);
							return;
						}
						if (img1 == null) {
							setOutputImage("combined", img2);
							return;
						}
						if (img2 == null) {
							setOutputImage("combined", img1);
							return;
						}
						int w = 0;
						int h = 0;
						w = Math.max(w, img1.getWidth());
						w = Math.max(w, img2.getWidth());
						h = Math.max(h, img1.getHeight());
						h = Math.max(h, img2.getHeight());
						BufferedImage comb = new BufferedImage(w, h, FilterDataFormat.RGBA.getBIType());
						
						Graphics2D g = comb.createGraphics();
						g.drawImage(img2, 0, 0, null);
						g.drawImage(img1, 0, 0, null);
						g.dispose();
						
						setOutputImage("combined", comb);
					}
					
				};
			}
		});

		/**
		 * This one does some channel switching
		 */
		PluginContainer.registerPlugin("default.lol", new FilterFactory() {
			@Override
			public Filter createInstance() {
				return new PixelFilter(""
						+ "Simple Settings:text,Brightness:float:1:0:3,"
						+ "Contrast:float:1:0:3,Saturation:float:1:0:3") {

							@Override
							public String getDisplayName() {
								return "LOL";
							}

							@Override
							public Color filter(Color in) {
								return new Color(in.getGreen(), in.getRed(), in.getBlue(), in.getAlpha());
							}

						};
			}

		});

		/**
		 * This is actually a nothing-doing filter
		 */
		PluginContainer.registerPlugin("default.empty", new FilterFactory() {
			@Override
			public Filter createInstance() {
				return new Filter("", "", "") {
					@Override
					public String getDisplayName() {
						return "<<Empty>>";
					}

					@Override
					protected void render() {
					}

				};
			}

		});
		
		/**
		 * This filter redirects every input it gets to the output
		 */
		PluginContainer.registerPlugin("default.redirector", new FilterFactory() {
			@Override
			public Filter createInstance() {
				return new Filter("rgba:RGBA,rgb:RGB,value:VALUE,constant:CONSTANT", "rgba:RGBA,rgb:RGB,value:VALUE,constant:CONSTANT", "") {
					@Override
					public String getDisplayName() {
						return "Redirect things";
					}

					@Override
					protected void render() {
						setOutputImage("rgba", getInputImage("rgba"));
						setOutputImage("rgb", getInputImage("rgb"));
						setOutputImage("value", getInputImage("value"));
						setOutputImage("constant", getInputImage("constant"));
					}

				};
			}

		});
	}
}
