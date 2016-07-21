package com.brendeer.pie.basePlugins;

import com.brendeer.pie.core.PluginContainer;
import com.brendeer.pie.filter.Filter;
import com.brendeer.pie.filter.FilterDataFormat;
import com.brendeer.pie.filter.FilterFactory;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Here are some simple image reading / writing plugins
 *
 * @author Erik
 */
public class IOPlugins {

	public static void load() {

		PluginContainer.registerPlugin("imageio.simpleInput", new FilterFactory() {

			@Override
			public Filter createInstance() {
				return new Filter("", "image:RGBA", "Select your file:file:image") {

					@Override
					public String getDisplayName() {
						return "Image input";
					}

					@Override
					protected void render() {
						File f = getOption(0).getFile(getFileContext());
						if (!f.exists()) {
							setOutputImage("image", null);
							return;
						}
						try {
							BufferedImage raw = ImageIO.read(f);

							BufferedImage img = new BufferedImage(raw.getWidth(), raw.getHeight(), FilterDataFormat.RGBA.getBIType());
							Graphics2D g = img.createGraphics();
							g.drawImage(raw, 0, 0, null);
							g.dispose();
							setOutputImage("image", img);
						} catch (Exception ex) {
							ex.printStackTrace();
							setOutputImage("image", null);
						}
					}
				};
			}
		});

		PluginContainer.registerPlugin("imageio.simpleOutput", new FilterFactory() {

			@Override
			public Filter createInstance() {
				return new Filter("image:RGBA", "", "Select your file:file:image") {

					private boolean doNotDisturb = false;

					@Override
					public String getDisplayName() {
						return "Image output";
					}

					@Override
					protected void render() {
						String outPath = getOption(0).getString();
						if (outPath == null || outPath.equals("")) {
							return;
						}
						BufferedImage img = getInputImage("image");
						if (img == null) {
							return;
						}

						File f = getOption(0).getFile(getFileContext());
						if (f.exists() && !f.delete()) {
							//System.err.println("Unable to save to \"" + outPath + "\", cannot delete file that already exists!");
						}
						try {
							String format = "png";
							if (outPath.endsWith("jpg") || outPath.endsWith("jpeg")) {
								format = "jpg";
							}
							doNotDisturb = true;
							ImageIO.write(img, format, f);
						} catch (Exception ex) {
						}
						doNotDisturb = false;
					}

					@Override
					public void destroy() {
						/**
						 * This waiting here is needed. Otherwise the program
						 * could terminate while the node is writing, and this
						 * would create corrupted images
						 */
						while (doNotDisturb) {
							try {
								Thread.sleep(10);
							} catch (Exception ex) {
							}
						}
					}

				};
			}
		});
	}
}
