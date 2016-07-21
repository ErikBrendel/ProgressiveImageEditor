package com.brendeer.pie.gui;

import com.brendeer.pie.filter.FilterOption;
import com.brendeer.pie.filter.FilterOptions;
import static com.brendeer.pie.filter.FilterOptions.FILTER_OPTION_HEIGHT_UNITS;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

/**
 * A wrapper class around a FilterOptions-object to display it and make the
 * interaction with it possible
 *
 * @author Erik
 */
public class FilterOptionsDisplay {

	private FilterOptions options;

	public FilterOptionsDisplay(FilterOptions options) {
		this.options = options;
	}

	public void render(Graphics2D g, Point pos, Point size, float scale) {
		float rowSize = FILTER_OPTION_HEIGHT_UNITS * scale;

		Point optSize = new Point(size.x, (int)Math.round(rowSize));

		synchronized (options.getOptionsLock()) {
			int i = 1; //to leave one blank row for the title
			for (FilterOption opt : options.getOptions()) {
				Point optPos = new Point(pos.x, pos.y + (int)Math.round(i * rowSize));
				switch (opt.getType()) {
					case CHECKBOX:
						renderCheckbox(g, optPos, new Point(optSize), opt);
						//break;
					case TEXT:
					default:
						renderText(g, optPos, new Point(optSize), opt);
						break;
				}
				i++;
			}
		}
	}

	/**
	 * render a filter-option from the type of a checkbox all render*-methods
	 * take the same parameters, as described here:
	 *
	 * @param g the graphics object to draw with
	 * @param rectStart the upper left pixel coordinate of the rect of this
	 * filterOption
	 * @param rectSize the size of the rect of this option
	 */
	private void renderCheckbox(Graphics2D g, Point rectStart, Point rectSize, FilterOption o) {
		g.setColor(Color.red);
		g.fillRect(rectStart.x, rectStart.y, rectSize.x, rectSize.y);
	}

	private void renderText(Graphics2D g, Point optPos, Point point, FilterOption opt) {
		g.setColor(Color.WHITE);
		int size = 1;
		boolean fits;
		
		do {
			size++;
			fits = true;
		} while(size < 200 && fits);
		size--;
		
	}
}
