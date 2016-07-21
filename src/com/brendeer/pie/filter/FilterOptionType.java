package com.brendeer.pie.filter;

/**
 * All the possible types of options a filter can have
 * @author Erik
 */
public enum FilterOptionType {

	/**
	 * A simple boolean yes/no-option
	 */
	CHECKBOX,
	/**
	 * an integer input, with a given range of pissible values
	 */
	INT,
	/**
	 * a floating sliding input, also with a range
	 */
	FLOAT,
	/**
	 * A set of options to select from
	 */
	DROPDOWN,
	/**
	 * Just some text to display, nothing much
	 */
	TEXT,
	/**
	 * A file chooser. The FilterOption-Object returns the files absolute path
	 * as string. As a parameter behind file, a type filter has to be passed (from a set of "image", "text", "all")
	 */
	FILE
	;
}
