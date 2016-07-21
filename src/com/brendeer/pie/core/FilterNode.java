package com.brendeer.pie.core;

import com.brendeer.pie.filter.Filter;
import com.brendeer.pie.filter.FilterDataFormat;
import com.brendeer.pie.gui.EditPanel;
import com.brendeer.pie.gui.FilterOptionsDisplay;
import com.brendeer.pie.gui.Util;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * Consists of a filter object and some displaying data, like position and size
 *
 * This is the reactangle you can see on the editPanel.
 *
 * @author Erik
 */
public class FilterNode {

	private static final Color NODE_BORDER = EditPanel.BG_DARK.darker();
	private static final Color NODE_FILL = EditPanel.BG_LIGHT.darker();
	private static final Color NODE_BUSY = Color.GRAY;
	private static final Color SELECTED_BORDER = Color.YELLOW.darker().darker();
	private static final Color HOVER_COLOR = new Color(255, 100, 0).darker();

	private Filter filter; //the internal filter object to render things
	private FilterOptionsDisplay optionsDisplay;
	private String pluginName; //the plugin name this filter was created from
	private float posX, posY; //position and size of the filterNode. Position is the center point of the rect
	private float sizeX, sizeY; //the size of the node. both, pos&size are in the programs inner coordinate system

	private boolean hover;
	private boolean selected;

	/**
	 *
	 * @param pluginName the name of the plugin, this filter was created from
	 * @param filter the newly generated filter object
	 * @param posX
	 * @param posY
	 * @param sizeX
	 * @param sizeY
	 */
	public FilterNode(String pluginName, Filter filter, float posX, float posY, float sizeX, float sizeY) {
		this.pluginName = pluginName;
		this.filter = filter;
		this.posX = posX;
		this.posY = posY;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		selected = false;
		hover = false;
		optionsDisplay = new FilterOptionsDisplay(filter.getOptions());
	}

	/**
	 * Renders the FilterNode
	 *
	 * @param g
	 * @param scale
	 * @param marginX
	 * @param marginY
	 * @param program
	 */
	public void render(Graphics2D g, float scale, float marginX, float marginY, FilterProgram program) {
		Point[] data = getDimensions(
				new Dimension((int) g.getClipBounds().getWidth(), (int) g.getClipBounds().getHeight()),
				scale, marginX, marginY);
		Point drawPos = data[0];
		Point drawSize = data[1];

		drawBackground(g, new Point(drawPos), drawSize);
		drawPins(g, new Point(drawPos), drawSize, scale);
		drawTitle(g, new Point(drawPos), drawSize);
		drawConnections(g, new Point(drawPos), drawSize, scale, program);
		optionsDisplay.render(g, drawPos, drawSize, scale);
	}

	/**
	 * gets the current draw dimensions for this node
	 *
	 * @param d
	 * @param scale
	 * @param marginX
	 * @param marginY
	 * @return {Position, size}
	 */
	public Point[] getDimensions(Dimension d, float scale, float marginX, float marginY) {
		int w = (int) d.getWidth();
		int h = (int) d.getHeight();

		float sx = sizeX * scale;
		float sy = sizeY * scale;

		float px = w / 2 + (marginX + posX) * scale;
		float py = h / 2 + (marginY + posY) * scale;
		Point drawPos = new Point((int) px, (int) py);
		Point drawSize = new Point((int) sx, (int) sy);
		drawPos.x -= drawSize.x / 2;
		drawPos.y -= drawSize.y / 2;
		return new Point[]{drawPos, drawSize};
	}

	private void drawBackground(Graphics2D g, Point drawPos, Point drawSize) {
		if (filter.isRendering()) {
			g.setColor(NODE_BUSY);
		} else {
			g.setColor(NODE_FILL);
		}
		g.fillRect(drawPos.x, drawPos.y, drawSize.x, drawSize.y);
		if (selected) {
			g.setColor(SELECTED_BORDER);
		} else {
			g.setColor(NODE_BORDER);
		}
		drawBorder(g, drawPos, drawSize);
		if (hover) {
			g.setColor(HOVER_COLOR);
			drawPos = new Point(drawPos);
			drawSize = new Point(drawSize);
			drawPos.x += 2;
			drawPos.y += 2;
			drawSize.x -= 4;
			drawSize.y -= 4;
			drawBorder(g, drawPos, drawSize);
		}
	}

	/**
	 * draws all the connection pins this object has
	 *
	 * @param g
	 * @param drawPos
	 * @param drawSize
	 * @param scale
	 */
	private void drawPins(Graphics2D g, Point drawPos, Point drawSize, float scale) {
		PinList in = filter.getInPins();
		for (int i = 0; i < in.size(); i++) {
			Point pinPos = in.get(i).getDrawingLocation(drawPos, drawSize, scale);
			drawPin(g, pinPos, in.get(i).getFormat(), scale, in.get(i).isHovered());
		}
		PinList out = filter.getOutPins();
		for (int i = 0; i < out.size(); i++) {
			Point pinPos = out.get(i).getDrawingLocation(drawPos, drawSize, scale);
			drawPin(g, pinPos, out.get(i).getFormat(), scale, out.get(i).isHovered());
		}
	}

	/**
	 * draws one pin
	 *
	 * @param g
	 * @param location
	 * @param format
	 * @param scale
	 * @param hover
	 */
	private void drawPin(Graphics2D g, Point location, FilterDataFormat format, float scale, boolean hover) {
		float size = 7f * scale;
		if (hover) {
			g.setColor(HOVER_COLOR);
		} else {
			g.setColor(NODE_BORDER);
		}
		g.fillOval((int) Math.round(location.x - size), (int) Math.round(location.y - size), (int) Math.round(2 * size), (int) Math.round(2 * size));

		switch (format) {
			case RGBA:
				g.setColor(Color.GREEN);
				break;
			case RGB:
				g.setColor(Color.YELLOW);
				break;
			case VALUE:
				g.setColor(Color.DARK_GRAY);
				break;
			default:
				g.setColor(Color.WHITE);
		}
		size *= 0.6f;
		g.fillOval((int) Math.round(location.x - size), (int) Math.round(location.y - size), (int) Math.round(2 * size), (int) Math.round(2 * size));

	}

	/**
	 * draws the displayName of the consisting filter. The font size is choosed
	 * to make the title fit horizontally inside the box
	 *
	 * @param g
	 * @param drawPos
	 * @param drawSize
	 */
	private void drawTitle(Graphics2D g, Point drawPos, Point drawSize) {
		String title = filter.getDisplayName();
		int size = 0;

		int width;

		do {
			size++;
			g.setFont(new Font(g.getFont().getName(), g.getFont().getStyle(), size));
			width = (int) g.getFontMetrics().getStringBounds(title, g).getWidth();
		} while (width < drawSize.x * 0.8f);
		size--;
		g.setColor(Color.LIGHT_GRAY);
		g.drawString(title, drawPos.x + (drawSize.x - width) / 2, drawPos.y + size);
	}

	/**
	 * draws all ingoing connections of this filter (since all outgoing ones are
	 * ingoing ones of another filterNode they can be ignored here)
	 *
	 * @param g
	 * @param drawPos
	 * @param drawSize
	 * @param scale
	 * @param program
	 */
	private void drawConnections(Graphics2D g, Point drawPos, Point drawSize, float scale, FilterProgram program) {
		PinList inList = filter.getInPins();
		for (int i = 0; i < inList.size(); i++) {
			int dX = drawPos.x;
			int dY = drawPos.y + (int) ((i + 1) * 20 * scale);
			Point p1 = new Point(dX, dY);

			Pin sourcePin = inList.get(i).getSource();
			Filter child = sourcePin == null ? null : sourcePin.getFilter();
			if (child != null && sourcePin != null) {
				FilterNode cNode = program.getFilterNode(child);
				dX = drawPos.x + (int) ((cNode.posX - posX) * scale);
				dY = drawPos.y + (int) ((cNode.posY - posY) * scale) - (int) ((cNode.sizeY - sizeY) * scale * 0.5f);
				dY += (int) ((sourcePin.getIndex() + 1) * 20 * scale);
				dX += drawSize.x;
				Point p2 = new Point(dX, dY);
				g.setColor(Color.WHITE);
				Util.drawCurve(g, p1, p2);
			}
		}
	}

	private void drawBorder(Graphics2D g, Point drawPos, Point drawSize) {
		int borderWidth = 2;
		g.fillRect(drawPos.x, drawPos.y, drawSize.x, borderWidth);
		g.fillRect(drawPos.x, drawPos.y, borderWidth, drawSize.y);

		g.fillRect(drawPos.x + drawSize.x - borderWidth, drawPos.y, borderWidth, drawSize.y);
		g.fillRect(drawPos.x, drawPos.y + drawSize.y - borderWidth, drawSize.x, borderWidth);
	}

	public boolean isSelected() {
		return selected;
	}

	public void setHovered(boolean hover) {
		this.hover = hover;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * tell this component the new cursor position
	 *
	 * @param pos
	 * @param img a handler to notify when an image should be shown thanks to
	 * the hovering cursor
	 * @return wether it now has to be repainted
	 */
	public boolean updateCursorLocation(Point2D.Float pos, PinHoverFeedback img) {
		boolean inside = isInside(pos, true);
		boolean changed = inside != hover;
		hover = inside;
		changed |= isOverPin(pos, img);
		return changed;
	}

	/**
	 * pass a click event through here
	 * @param cursor the relativ cursor position inide the programs coordinates
	 * @return wether this filterNode needs to be repainted now
	 */
	boolean onClick(Point2D.Float cursor) {
		boolean inside = isInside(cursor, false);
		if (!inside) {
			return false;
		}

		selected = !selected;

		return true;
	}

	private boolean isInside(Point2D.Float pos, boolean larger) {
		int e = larger ? 10 : 0;
		return pos.x + e >= posX - sizeX / 2 && pos.y + e >= posY - sizeY / 2 && pos.x - e <= posX + sizeX / 2 && pos.y - e <= posY + sizeY / 2;
	}

	/**
	 * determine wether the cursor is over a pin of this FilterNode
	 * @param pos
	 * @param feedback a handler to call, when there is image data to show
	 * @return 
	 */
	private boolean isOverPin(Point2D.Float pos, PinHoverFeedback feedback) {
		PinList in = filter.getInPins();
		boolean ret = false;
		for (int i = 0; i < in.size(); i++) {
			float x = posX - sizeX / 2;
			float y = posY - sizeY / 2 + ((i + 1) * 20);
			if (pos.x > x - 10 && pos.y > y - 10 && pos.x < x + 10 && pos.y < y + 10) {
				in.get(i).setHovered(true);
				if (feedback != null) {
					feedback.notification(in.get(i));
				}
				ret = true;
			} else {
				ret |= in.get(i).isHovered();
				in.get(i).setHovered(false);
			}
		}
		PinList out = filter.getOutPins();
		for (int i = 0; i < out.size(); i++) {
			float x = posX + sizeX / 2;
			float y = posY - sizeY / 2 + ((i + 1) * 20);
			if (pos.x > x - 10 && pos.y > y - 10 && pos.x < x + 10 && pos.y < y + 10) {
				out.get(i).setHovered(true);
				if (feedback != null) {
					feedback.notification(out.get(i));
				}
				ret = true;
			} else {
				ret |= out.get(i).isHovered();
				out.get(i).setHovered(false);
			}
		}
		return ret;
	}

	public Filter getFilter() {
		return filter;
	}

	/**
	 * moves the filterNode by some delta
	 * @param dx
	 * @param dy 
	 */
	void move(float dx, float dy) {
		posX += dx;
		posY += dy;
	}

	public String getPluginName() {
		return pluginName;
	}

	public Point2D.Float getPos() {
		return new Point2D.Float(posX, posY);
	}

}
