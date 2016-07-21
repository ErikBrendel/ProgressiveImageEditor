package com.brendeer.pie.gui;

import com.brendeer.pie.core.Feedback;
import com.brendeer.pie.core.FilterNode;
import com.brendeer.pie.core.FilterProgram;
import com.brendeer.pie.core.Pin;
import com.brendeer.pie.core.PinHoverFeedback;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 * the main screen of the application, for editing filter programs
 *
 * This monster draws a FilterProgram and has multiple nice functions to modify
 * it, wich could get outsourced some time
 *
 * @author Erik
 */
public class EditPanel extends JPanel {
	//todo outsource all FilterProgram-editing methods und just keep the displaying methods

	public static final Color BG_BRIGHT = new Color(150, 150, 150);
	public static final Color BG_LIGHT = new Color(0x38, 0x38, 0x38);
	public static final Color BG_DARK = new Color(0x2e, 0x2e, 0x2e);
	public static final int BG_LINE_DISTANCE = 32;

	private float scaleFactor = 1;
	private float posX = 0;
	private float posY = 0;
	private FilterProgram program = new FilterProgram(null);
	private BufferedImage previewImage;
	private Pin dragPin = null;
	private FilterNode dragNode = null;
	private Pin hoveringPin = null;

	private final Feedback repaintFeedback = new Feedback() {

		@Override
		public void notification() {
			repaint();
		}
	};

	@Override
	public void paint(Graphics _g) {
		Graphics2D g = (Graphics2D) _g;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		Point mousePos = getMousePosition();
		if (mousePos == null) {
			mousePos = MouseInfo.getPointerInfo().getLocation();
			Point correction = getLocationOnScreen();
			mousePos.x -= correction.x;
			mousePos.y -= correction.y;
		}

		drawBackground(g);

		if (program != null) {
			program.render(g, scaleFactor, posX, posY);
		}
		if (previewImage != null) {
			if (previewImage.getWidth() > 640 || previewImage.getHeight() > 480) {
				float factx = 640f / previewImage.getWidth();
				float facty = 480f / previewImage.getHeight();
				float fact = factx < facty ? factx : facty;
				previewImage = Util.getScaledImage(previewImage,
						(int) (previewImage.getWidth() * fact),
						(int) (previewImage.getHeight() * fact), Util.MODE_FINE);
			}
			g.setColor(BG_DARK);
			g.fillRect(mousePos.x - 2, mousePos.y - 2, previewImage.getWidth() + 4, previewImage.getHeight() + 4);
			g.setColor(BG_BRIGHT);
			for (int x = 0; x < previewImage.getWidth() / 16; x++) {
				for (int y = 0; y < previewImage.getHeight() / 16; y++) {
					if ((x + y) % 2 == 0) {
						g.fillRect(mousePos.x + (x * 16), mousePos.y + (y * 16), 16, 16);
					}
				}
			}
			g.drawImage(previewImage, mousePos.x, mousePos.y, null);
		}
		if (dragPin != null && dragStart != null) {
			//draw dragging connection
			Point mouse = mousePos;
			g.setColor(Color.WHITE);
			Util.drawCurve(g, mouse, dragStart);
		}
	}

	/**
	 * draws the whole background of this thing, with lines and backdrop
	 *
	 * @param g
	 */
	private void drawBackground(Graphics2D g) {
		Point center = new Point(getWidth() / 2, getHeight() / 2);
		center.x += posX * scaleFactor;
		center.y += posY * scaleFactor;
		float dist = BG_LINE_DISTANCE * scaleFactor;

		//draw fill color
		g.setColor(BG_LIGHT);
		g.fillRect(0, 0, getWidth(), getHeight());

		//draw lines
		g.setColor(BG_DARK);
		drawBGLines(g, center, dist);

		//draw the backdrop
		BufferedImage backdrop = BackdropStack.getCombined(new Point(getWidth(), getHeight()));
		g.drawImage(backdrop, 0, 0, null);

		//draw some more, half transparent lines
		g.setColor(new Color(BG_DARK.getRed(), BG_DARK.getGreen(), BG_DARK.getBlue(), 50));
		drawBGLines(g, center, dist);
	}

	/**
	 * draw some nice raster-kind-of lines
	 *
	 * @param g
	 * @param center
	 * @param dist
	 */
	private void drawBGLines(Graphics2D g, Point center, float dist) {
		drawVLine(g, center.x);
		drawHLine(g, center.y);

		float current = center.x;
		while (current > 0) {
			current -= dist;
			drawVLine(g, (int) Math.round(current));
		}
		current = center.x;
		while (current < getWidth()) {
			current += dist;
			drawVLine(g, (int) Math.round(current));
		}

		current = center.y;
		while (current > 0) {
			current -= dist;
			drawHLine(g, (int) Math.round(current));
		}
		current = center.y;
		while (current < getHeight()) {
			current += dist;
			drawHLine(g, (int) Math.round(current));
		}
	}

	private void drawVLine(Graphics g, int x) {
		g.fillRect(x, 0, 1, getHeight());
	}

	private void drawHLine(Graphics g, int y) {
		g.fillRect(0, y, getWidth(), 1);
	}

	/**
	 * when someone zoomed the view
	 *
	 * @param evt
	 */
	void onMouseWheel(MouseWheelEvent evt) {
		if (program == null) {
			return;
		}
		double rot = evt.getPreciseWheelRotation();
		float scale = 1.13f;
		if (rot > 0) {
			scaleFactor /= scale;
			if (scaleFactor < 0.1) {
				scaleFactor = 0.1f;
			}
			repaint();
		} else if (rot < 0) {
			scaleFactor *= scale;
			if (scaleFactor > 5) {
				scaleFactor = 5;
			}
			repaint();
		}
	}

	private Point dragStart = null;

	/**
	 * a drag can mean multiple things: move of view, move of filters or even
	 * connecting pins
	 *
	 * @param evt
	 */
	void onMouseDrag(MouseEvent evt) {
		onMouseMove(evt);
		if (program == null) {
			return;
		}
		if (dragStart == null) {
			dragStart = evt.getPoint();
			return;
		}
		int dX = evt.getPoint().x - dragStart.x;
		int dY = evt.getPoint().y - dragStart.y;
		if ((evt.getModifiers() & MouseEvent.BUTTON2_MASK) != 0) { //move the view
			posX += dX / scaleFactor;
			posY += dY / scaleFactor;
			dragStart = evt.getPoint();
			repaint();
		} else if ((evt.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) { //move selected things or drag some connection
			if (hoveringPin == null && dragPin == null) {
				program.moveSelected(dX / scaleFactor, dY / scaleFactor);
				dragStart = evt.getPoint();
				repaint();
			} else {
				if (dragPin == null) {
					dragPin = hoveringPin;
					//calculate dragStart to the exact position of the 
					if (dragNode == null) {
						dragNode = program.getNodeByPin(dragPin);
					}
					if (dragNode != null) {
						Point[] data = dragNode.getDimensions(getSize(), scaleFactor, posX, posY);
						dragStart = dragPin.getDrawingLocation(data[0], data[1], scaleFactor);
					}
				}
				repaint();
			}
		}
	}

	/**
	 * finished dragging
	 *
	 * @param evt
	 */
	void onMouseUp(MouseEvent evt) {
		if (dragPin != null && hoveringPin != null) {
			program.connect(dragPin, hoveringPin, repaintFeedback);
		} else if (dragPin != null && dragPin.isIngoing()) {
			dragPin.connectTo(null, repaintFeedback);
		}
		dragStart = null;
		dragPin = null;
		dragNode = null;
		repaint();
	}

	void setProgram(FilterProgram program) {
		this.program = program;
		repaint();
	}

	/**
	 * probably change some isHovered-properties of nodes/pins and thus mabe
	 * re-render the panel
	 *
	 * @param evt
	 */
	void onMouseMove(MouseEvent evt) {
		if (program == null) {
			return;
		}
		Point2D.Float cursor = traceBack(evt.getPoint());
		hoveringPin = null;
		if (program.updateCursorLocation(cursor, new PinHoverFeedback() {

			@Override
			public void notification(Pin p) {
				previewImage = p.getData();
				hoveringPin = p;
				repaint();
			}
		})) {
			repaint();
		} else {
			if (previewImage != null) {
				previewImage = null;
				repaint();
			}
		}
	}

	/**
	 * convert a curser location to the coordinate space of the FilterProgram
	 * @param locOnComponent
	 * @return 
	 */
	private Point2D.Float traceBack(Point locOnComponent) {
		int w = getWidth() / 2;
		int h = getHeight() / 2;

		float x = locOnComponent.x - w;
		float y = locOnComponent.y - h;

		x /= scaleFactor;
		y /= scaleFactor;

		x -= posX;
		y -= posY;

		return new Point2D.Float(x, y);
	}

	void onMouseClick(MouseEvent evt) {
		if (program == null) {
			return;
		}
		Point2D.Float cursor = traceBack(evt.getPoint());
		if (program.onClick(cursor)) {
			repaint();
		}
	}

	/**
	 * refreshes all the filterNodes
	 */
	void refreshProgram() {
		if (program != null) {
			program.refresh(repaintFeedback);
			repaint();
		}
	}

	/**
	 * refreshes only all nodes which type name contains the word input
	 */
	void refreshInputs() {
		if (program != null) {
			synchronized (program.getNodeLock()) {
				for (FilterNode node : program.getNodes()) {
					if (node.getPluginName().toLowerCase().contains("input")) {
						node.getFilter().refresh(repaintFeedback);
					}
				}
			}
		}
	}

	public FilterProgram getProgram() {
		return program;
	}

	public Point2D.Float getPos() {
		return new Point2D.Float(posX, posY);
	}

	/**
	 * (de)select all of the FilterNodes
	 */
	void de_selectAll() {
		//first, check if we want to select all (if none are selected)
		synchronized (program.getNodeLock()) {
			boolean noneSelected = true;
			for (FilterNode node : program.getNodes()) {
				if (node.isSelected()) {
					noneSelected = false;
					break;
				}
			}
			//then (de)select all
			for (FilterNode node : program.getNodes()) {
				node.setSelected(noneSelected);
			}
		}
		repaint();
	}

	/**
	 * delete all FilterNodes wich are selected atm
	 */
	void deleteSelected() {
		synchronized (program.getNodeLock()) {
			for (int nid = 0; nid < program.getNodes().size(); nid++) {
				FilterNode node = program.getNodes().get(nid);
				if (node.isSelected()) {
					for (Pin p : node.getFilter().getInPins()) {
						System.err.println("p = " + p);
						p.connectTo(null, null);
					}
					for (Pin p : node.getFilter().getOutPins()) {
						synchronized (p.getDestinationsLock()) {
							while (p.getDestinations().size() > 0) {
								p.getDestinations().get(0).connectTo(null, null);
							}
						}
					}

					program.getNodes().remove(node);
					nid--;
				}
			}
		}
		repaint();
	}
}
