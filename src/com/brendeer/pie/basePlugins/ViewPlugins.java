package com.brendeer.pie.basePlugins;

import com.brendeer.pie.core.PluginContainer;
import com.brendeer.pie.filter.Filter;
import com.brendeer.pie.filter.FilterFactory;
import com.brendeer.pie.gui.BackdropStack;
import java.awt.image.BufferedImage;

/**
 *
 * @author Erik
 */
public class ViewPlugins {

	public static void load() {

		/**
		 * This one adds itself to the static backdrop stack, thus making its
		 * input permanently visible to the user
		 */
		PluginContainer.registerPlugin("view.backdrop", new FilterFactory() {

			@Override
			public Filter createInstance() {
				return new Filter("image:RGB", "", "Enabled:checkbox:true") {

					private final long id = System.nanoTime();

					@Override
					public String getDisplayName() {
						return "Backdrop";
					}

					@Override
					protected void render() {
						BufferedImage in = getInputImage("image");
						if (in == null || !getOption("Enabled").getBoolean()) {
							BackdropStack.update(id, null);
						} else {
							BackdropStack.update(id, in);
						}
					}

					@Override
					public void destroy() {
						BackdropStack.update(id, null);
					}
				};
			}
		});
	}
}
