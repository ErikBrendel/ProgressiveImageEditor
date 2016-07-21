package com.brendeer.pie.core;

import com.brendeer.pie.file.FileSaver;
import com.brendeer.pie.filter.Filter;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a collection of filter instances (and thus, their settings) packed
 * inside a FilterNode It's basically the object that gets to be edited / saved
 *
 * @author Erik
 */
public class FilterProgram {

	private List<FilterNode> nodes; //a list of all the filterNodes in this program, in no particular order
	private final Object nodeLock; //a list accessing lock
	private File origin; //the .pieml file this program was loaded from
	

	public FilterProgram(File origin) {
		nodes = new ArrayList<>();
		nodeLock = new Object();
		this.origin = origin;
	}

	public void addNode(FilterNode n) {
		n.getFilter().setFileContext(origin);
		synchronized (nodeLock) {
			nodes.add(n);
		}
	}

	/**
	 * renders all the filter nodes (and their connections)
	 * @param g
	 * @param scale
	 * @param marginX
	 * @param marginY 
	 */
	public void render(Graphics2D g, float scale, float marginX, float marginY) {
		synchronized(nodeLock) {
			for (FilterNode n: nodes) {
				n.render(g, scale, marginX, marginY, this);
			}
		}
	}
	
	/**
	 * pass an updated mouse pointer location (inprogram coordinates) to this program
	 * @param loc
	 * @param img
	 * @return 
	 */
	public boolean updateCursorLocation(Point2D.Float loc, PinHoverFeedback img) {
		boolean changed = false;
		synchronized(nodeLock) {
			for (FilterNode n: nodes) {
				changed |= n.updateCursorLocation(loc, img);
			}
		}
		
		return changed;
	}

	/**
	 * handle a click event
	 * @param cursor
	 * @return 
	 */
	public boolean onClick(Point2D.Float cursor) {
		boolean changed = false;
		
		synchronized(nodeLock) {
			for (FilterNode n: nodes) {
				changed |= n.onClick(cursor);
			}
		}
		
		return changed;
	}

	/**
	 * get the filterNode that holds a specific filter-object
	 * @param filter
	 * @return 
	 */
	FilterNode getFilterNode(Filter filter) {
		synchronized(nodeLock) {
			for (FilterNode n: nodes) {
				if (n.getFilter() == filter) {
					return n;
				}
			}
		}
		return null;
	}

	/**
	 * move all the selected filterNodes by some delta
	 * @param dx
	 * @param dy 
	 */
	public void moveSelected(float dx, float dy) {
		synchronized(nodeLock) {
			for (FilterNode n: nodes) {
				if (n.isSelected()) {
					n.move(dx, dy);
				}
			}
		}
	}

	/**
	 * refresh all the nodes
	 * @param f 
	 */
	public void refresh(Feedback f) {
		synchronized(nodeLock) {
			for (FilterNode n: nodes) {
				n.getFilter().resetData();
			}
			for (FilterNode n: nodes) {
				n.getFilter().refresh(f);
			}
		}
	}

	/**
	 * connect two pins together
	 * @param p1
	 * @param p2 
	 */
	public void connect(Pin p1, Pin p2, Feedback fb) {
		if (p1.isIngoing()) {
			assert !p2.isIngoing();
			p1.connectTo(p2, fb);
		} else {
			assert p2.isIngoing();
			p2.connectTo(p1, fb);
		}
	}
	
	/**
	 * get the filterNode that holds a given pin
	 * @param p
	 * @return 
	 */
	public FilterNode getNodeByPin(Pin p) {
		synchronized(nodeLock) {
			for (FilterNode node: nodes) {
				if (node.getFilter().getInPins().contains(p) ||
						node.getFilter().getOutPins().contains(p)) {
					return node;
				}
			}
		}
		return null;
	}

	public List<FilterNode> getNodes() {
		return nodes;
	}

	public Object getNodeLock() {
		return nodeLock;
	}

	/**
	 * calls the destroy-function on all the filters
	 */
	public void destroy() {
		synchronized(getNodeLock()) {
			for (FilterNode n: nodes) {
				n.getFilter().destroy();
			}
		}
	}
	
	/**
	 * try to save this program back to its origin
	 * @return wether the saving was successful
	 */
	public boolean save() {
		if (origin == null) {
			return false;
		} else {
			return FileSaver.saveXML(this, origin);
		}
	}

	/**
	 * return the .pieml file this program was loaded from
	 * @return 
	 */
	public File getOrigin() {
		return origin;
	}
	
}
