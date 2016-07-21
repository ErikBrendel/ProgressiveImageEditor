package com.brendeer.pie.core;

import com.brendeer.pie.filter.Filter;
import com.brendeer.pie.filter.FilterDataFormat;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This thing represents a connection point on a filter, thus consisting of a
 * filter object and an index, and possible connection data
 *
 * @author Erik
 */
public class Pin {

	private Pin source; //the pin this ingoing pin is connected to, if any
	private List<Pin> destinations; //all the pins this outgoing pin is connected to
	private final Object destLock; //a lock for that list

	private Filter f; //the filter this pin belongs to
	private int index; //the index in the filters pinList
	private boolean ingoing; //wether this is an ingoing or an outgoing pin
	private String name; //the filter-internal name of this pin (to call getImageInput(name))
	private FilterDataFormat format; //the format of data this pin offers / accepts
	private BufferedImage data; //the cached data

	//display things, dont know if they belog here
	private boolean hovered;

	public Pin(Filter f, int index, boolean ingoing, String name, FilterDataFormat format) {
		this.f = f;
		this.index = index;
		this.source = null;
		this.ingoing = ingoing;
		this.name = name;
		this.format = format;
		data = null;
		destinations = new ArrayList<>();
		this.hovered = false;
		destLock = new Object();
	}

	public boolean isIngoing() {
		return ingoing;
	}

	public String getName() {
		return name;
	}

	public FilterDataFormat getFormat() {
		return format;
	}

	public Pin getSource() {
		return source;
	}

	/**
	 * xonnects this ingoing pin to some new source, also properly disconnects a
	 * possible former connection
	 *
	 * @param newSource a new outgoing pin to connect to, or null just to remove a former connection
	 * @param fb a feedback to pass to the filters refresh-function
	 */
	public void connectTo(Pin newSource, Feedback fb) {
		assert isIngoing();
		if (newSource == this) {
			return;
		}
		if (this.source != null) {
			synchronized (this.source.getDestinationsLock()) {
				this.source.getDestinations().remove(this);
			}
		}
		this.source = newSource;
		if (this.source != null) {
			synchronized (this.source.getDestinationsLock()) {
				this.source.getDestinations().add(this);
			}
		}
		if (f != null) {
			f.refresh(fb);
		}
	}

	/**
	 * cache some data to this ingoing pin
	 * @param data 
	 */
	public void setData(BufferedImage data) {
		assert !isIngoing();
		this.data = data;
	}

	/**
	 * get the data that lies on this pin
	 * @return 
	 */
	public BufferedImage getData() {
		if (isIngoing()) {
			return source == null ? null : source.getData();
		} else {
			return data;
		}
	}

	public List<Pin> getDestinations() {
		return destinations;
	}

	public Object getDestinationsLock() {
		return destLock;
	}

	public Filter getFilter() {
		return f;
	}

	public int getIndex() {
		return index;
	}

	public void setHovered(boolean hovered) {
		this.hovered = hovered;
	}

	public boolean isHovered() {
		return hovered;
	}

	/**
	 * calculate the point where this pin has to be drawn
	 *
	 * @param filterNodePos the view-position of the surrounding filterNode
	 * @param filterNodeSize the filterNodes size
	 * @param scale the current scaleFactor
	 * @return
	 */
	public Point getDrawingLocation(Point filterNodePos, Point filterNodeSize, float scale) {
		int dx = filterNodePos.x;
		if (!isIngoing()) {
			dx += filterNodeSize.x;
		}
		int dy = filterNodePos.y + (int) ((getIndex() + 1) * 20 * scale);
		return new Point(dx, dy);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Pin other = (Pin) obj;
		if (!Objects.equals(this.f, other.f)) {
			return false;
		}
		return this.index == other.index;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 71 * hash + Objects.hashCode(this.f);
		hash = 71 * hash + this.index;
		return hash;
	}

}
