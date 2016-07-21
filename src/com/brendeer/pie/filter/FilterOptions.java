package com.brendeer.pie.filter;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * A collection of multiple FilterOption-instances
 * 
 * This object describes the current variable-state of a filter
 * 
 * @author Erik
 */
public class FilterOptions {

	private static final String SERIALIZATION_SEPERATOR = ":::";

	private List<FilterOption> options; //all the options this filter has

	/**
	 * parse a config-string
	 * @param config 
	 */
	public FilterOptions(String config) {
		options = new ArrayList<>();

		if (!config.contains(":")) {
			return;
		}

		String[] optionData = config.split(",");
		for (String o : optionData) {
			String[] data = o.split(":");
			options.add(new FilterOption(data));
		}

	}

	/**
	 * return the option that has the given name
	 * @param name
	 * @return 
	 */
	public FilterOption getOptionByName(String name) {
		for (FilterOption o : options) {
			if (o.getName().equals(name)) {
				return o;
			}
		}
		return null;
	}

	public FilterOption getOptionByID(int id) {
		return options.get(id);
	}

	/**
	 * converts all the filters options to one string
	 * @return 
	 */
	public String serialize() {
		String s = "";
		for (FilterOption o : options) {
			s += o.serialize();
			s += SERIALIZATION_SEPERATOR;
		}
		if (s.length() > 0) {
			s = s.substring(0, s.length() - SERIALIZATION_SEPERATOR.length());
		}
		return s;
	}

	/**
	 * reads back in a former serialized filterOptions-object
	 * 
	 * deSerialize(serialize()) has in the end no visible changes
	 * 
	 * @param data 
	 */
	public void deSerialize(String data) {
		String[] split = data.split(SERIALIZATION_SEPERATOR);
		for (int i = 0; i < split.length && i < options.size(); i++) {
			options.get(i).deSerialize(split[i]);
		}
	}

	/**
	 * examine the size this set of options needs to get displayed correctly
	 *
	 * @return
	 */
	public Point2D.Float getDisplaySize() {
		float width;
		float height;

		height = (options.size() + 1) * 35;

		width = 150;

		return new Point2D.Float(width, height);
	}
}
