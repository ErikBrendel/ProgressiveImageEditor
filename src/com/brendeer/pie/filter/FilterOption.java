package com.brendeer.pie.filter;

import java.io.File;

/**
 * This is one single option / variable a filter can have
 *
 * @author Erik
 */
public class FilterOption {

	private String name; //the name of this option, also the displaying string
	private FilterOptionType type; //the options data-type
	private Object defaultVal; //a default values this option can always get resetted to
	private Object[] params; //all possible parameters this option could need
	private Object val; //the current value of this option

	/**
	 * construct a new FilterOption from a set of raw input string, read form an
	 * Options-defining string
	 *
	 * @param data
	 */
	public FilterOption(String[] data) {
		name = data[0];
		type = FilterOptionType.valueOf(data[1].toUpperCase());
		if (type == FilterOptionType.TEXT) {
			return;
		}
		defaultVal = toMyType(data[2]);

		params = null;
		if (type == FilterOptionType.INT || type == FilterOptionType.FLOAT) {
			params = new Object[2];
			params[0] = toMyType(data[3]);
			params[1] = toMyType(data[4]);
		} else if (type == FilterOptionType.DROPDOWN) {
			params = new Object[data.length - 3];
			for (int i = 0; i < params.length; i++) {
				params[i] = toMyType(data[i + 3]);
			}
		} else if (type == FilterOptionType.FILE) {
			params = new Object[1];
			params[0] = data[2];
			defaultVal = null;
		}
		reset();
	}

	private Object toMyType(String data) {
		switch (type) {
			case TEXT:
				return null;
			case CHECKBOX:
				return Boolean.valueOf(data);
			case INT:
				return Integer.valueOf(data);
			case FLOAT:
				return Float.valueOf(data);
			case DROPDOWN:
				return Integer.valueOf(data);
			case FILE:
				return data;
			default:
				return null;
		}
	}

	private Object clamp(Object val) {
		if (type == FilterOptionType.INT) {
			int i = (Integer) val;
			if (i < (Integer) params[0]) {
				i = (Integer) params[0];
			}
			if (i > (Integer) params[1]) {
				i = (Integer) params[1];
			}
			return i;
		} else if (type == FilterOptionType.FLOAT) {
			float f = (Float) val;
			if (f < (Float) params[0]) {
				f = (Float) params[0];
			}
			if (f > (Float) params[1]) {
				f = (Float) params[1];
			}
			return f;
		}
		return val;
	}

	/**
	 * return this options current value as a boolean:
	 * <br>
	 * CHECKBOX: it's checking state,
	 * <br>
	 * NUMERIC: wether it is != 0,
	 * <br>
	 * DROPDOWN: wether it is "set" away from its default value,
	 * <br>
	 * FILE: wether it actually has a file selected
	 * <br>
	 *
	 * @return
	 */
	public boolean getBoolean() {
		if (type == FilterOptionType.CHECKBOX) {
			return (Boolean) val;
		} else if (type == FilterOptionType.INT) {
			return ((Integer) val) != 0;
		} else if (type == FilterOptionType.FLOAT) {
			return ((Float) val) != 0;
		} else if (type == FilterOptionType.DROPDOWN) {
			return val != defaultVal;
		} else if (type == FilterOptionType.FILE) {
			return val != null && !((String) val).equals("");
		}
		return false;
	}

	/**
	 * return this options current value as an integer
	 * <br>
	 * NUMERIC: the (maybe rounded) value of this option,
	 * <br>
	 * CHECKBOX: 1 for checked, 0 for not,
	 * <br>
	 * DROPDOWN: the index of the currently selected entity
	 *
	 * @return
	 */
	public int getInt() {
		if (type == FilterOptionType.INT) {
			return (Integer) val;
		} else if (type == FilterOptionType.FLOAT) {
			return (int) Math.round((Float) val);
		} else if (type == FilterOptionType.CHECKBOX) {
			return ((Boolean) val) ? 1 : 0;
		} else if (type == FilterOptionType.DROPDOWN) {
			return (Integer) val;
		}
		return 0;
	}

	/**
	 * return this options current value as a flot, basically the same output as
	 * in getInt(), but without the rounding for FLOAT-type
	 *
	 * @return
	 */
	public float getFloat() {
		if (type == FilterOptionType.FLOAT) {
			return (Float) val;
		}
		return getInt();
	}

	/**
	 * get this options current value formatted as a string:
	 * <br>
	 * DROPDOWN: The text of the currently selected entity,
	 * <br>
	 * FILE: The files path,
	 * <br>
	 * REST: Their specific value casted to a string
	 * @return 
	 */
	public String getString() {
		if (type == FilterOptionType.DROPDOWN) {
			return (String) params[(Integer) val];
		} else if (type == FilterOptionType.FILE) {
			return (String) val;
		} else if (type == FilterOptionType.CHECKBOX) {
			return ((Boolean) val).toString();
		} else if (type == FilterOptionType.FLOAT) {
			return ((Float) val).toString();
		} else if (type == FilterOptionType.INT) {
			return ((Integer) val).toString();
		}
		return "unknown type: " + type;
	}

	/**
	 * return this FILE-Options corresponding File-object
	 * @param context
	 * @return 
	 */
	public File getFile(File context) {
		if (this.type != FilterOptionType.FILE) {
			return null;
		}
		String path = getString();

		if (context != null && !path.matches("^[A-Z]:")) {
			return new File(context, path);
		}

		return new File(path);
	}

	public void setBoolean(boolean b) {
		if (type == FilterOptionType.CHECKBOX) {
			val = b;
		} else {
			setInt(b ? 1 : 0);
		}
	}

	public void setInt(int i) {
		if (type == FilterOptionType.INT) {
			val = clamp(i);
		} else if (type == FilterOptionType.FLOAT) {
			val = clamp(i);
		} else if (type == FilterOptionType.DROPDOWN) {
			val = clamp(i);
		} else if (type == FilterOptionType.CHECKBOX) {
			val = i == 0;
		}
	}

	public void setFloat(float f) {
		if (type == FilterOptionType.FLOAT) {
			val = clamp(f);
		} else {
			setInt((int) Math.round(f));
		}
	}

	public void setString(String s) {
		if (type == FilterOptionType.DROPDOWN) {
			int id = -1;
			for (int i = 0; i < params.length; i++) {
				if (params[i].equals(s)) {
					id = i;
					break;
				}
			}
			if (id != -1) {
				val = id;
			}
		} else if (type == FilterOptionType.FILE) {
			val = s;
		} else {
			val = toMyType(s);
		}
	}

	@Override
	public String toString() {
		return getString();
	}

	public String getName() {
		return name;
	}

	/**
	 * sets this options value back to the specified defaultValue
	 */
	private void reset() {
		val = defaultVal;
	}

	/**
	 * convert this options current value to a save-able string
	 * @return 
	 */
	public String serialize() {
		return getString();
	}

	/**
	 * read a former serialized value and store apply it.
	 * 
	 * deSerialize(serialize()) has in the end no visible effect
	 * 
	 * @param in 
	 */
	public void deSerialize(String in) {
		val = toMyType(in);
	}

	public FilterOptionType getType() {
		return type;
	}

}
