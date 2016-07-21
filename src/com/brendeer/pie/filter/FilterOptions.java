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
	public static final int FILTER_OPTION_HEIGHT_UNITS = 35;

	private List<FilterOption> options; //all the options this filter has
	private final Object optionsLock; //a list accessing lock

	/**
	 * parse a config-string
	 *
	 * @param config
	 */
	public FilterOptions(String config) {
		options = new ArrayList<>();
		optionsLock = new Object();

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
	 *
	 * @param name
	 * @return
	 */
	public FilterOption getOptionByName(String name) {
		FilterOption opt = null;
		synchronized (optionsLock) {
			for (FilterOption o : options) {
				if (o.getName().equals(name)) {
					opt = o;
					break;
				}
			}
		}
		return opt;
	}

	public FilterOption getOptionByID(int id) {
		FilterOption opt;
		synchronized (optionsLock) {
			opt = options.get(id);
		}
		return opt;
	}

	/**
	 * converts all the filters options to one string
	 *
	 * @return
	 */
	public String serialize() {
		String s = "";
		synchronized (optionsLock) {
			for (FilterOption o : options) {
				s += o.serialize();
				s += SERIALIZATION_SEPERATOR;
			}
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
		synchronized (optionsLock) {
			for (int i = 0; i < split.length && i < options.size(); i++) {
				options.get(i).deSerialize(split[i]);
			}
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

		synchronized (optionsLock) {
			height = (options.size() + 1) * FILTER_OPTION_HEIGHT_UNITS;
		}

		width = 150;

		return new Point2D.Float(width, height);
	}

	public List<FilterOption> getOptions() {
		return options;
	}

	public Object getOptionsLock() {
		return optionsLock;
	}

}
