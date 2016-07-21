package com.brendeer.pie.core;

import java.util.ArrayList;

/**
 * A list of Pin-objects, enhanced with some more useful options
 * @author Erik
 */
public class PinList extends ArrayList<Pin> {

	public PinList() {
		super();
	}
	
	public int getIndexByName(String name) {
		for (int p = 0; p < size(); p++) {
			if (get(p).getName().equals(name)) {
				return p;
			}
		}
		return -1;
	}
	
	public Pin getPinByName(String name) {
		return get(getIndexByName(name));
	}
}
