package com.brendeer.pie.filter;

import com.brendeer.pie.core.Feedback;
import com.brendeer.pie.core.Pin;
import com.brendeer.pie.core.PinList;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * This class represents a processing node in the node editor.
 *
 * @author Erik
 */
public abstract class Filter {

	private PinList inPins; //all the pins that lead input into the filter
	private PinList outPins; //all the outputs of this filter

	private boolean isRendering; //wether this filter is currently busy with rendering
	private boolean refreshAgain; //to mark that, after the current refresh, it should refresh again

	private FilterOptions myOptions; //the set of options and their values for this particular filter-instance
	private File fileContext; //the directory, in wich context the filter should execute (for IO-processes)

	private boolean disabled; //in disabled mode, the filter redirects input data between pins
	//todo implement the disabled-mode for filters

	/**
	 * The Pin-Config Strings look like this: "myInImageVar:ARGB,myMask:VALUE"
	 * The names are case sensitive, and the types must represent a
	 * FilterDataFormat. The OptionsConfig is roughly the same:
	 * "myVarNameAndDisplyString:Type:type-params, <...>". Option-types can be:
	 * "checkbox:default", "int:default:min:max", "float:default:min:max",
	 * "dropdown:defaultIndex:option1:option2:...", or even "text" or "button"
	 * or "file:image:default"
	 *
	 * each in-pin and out-pin with the same name and type will be linked when
	 * the filter is in "disabled" mode
	 *
	 * @param inPinConfig
	 * @param outPinConfig
	 * @param optionsConfig the variables the user can adjust
	 */
	public Filter(String inPinConfig, String outPinConfig, String optionsConfig) {
		inPins = new PinList();
		outPins = new PinList();
		try {
			parseConfig(inPinConfig, inPins, true);
			parseConfig(outPinConfig, outPins, false);
		} catch (Exception ex) {
			System.err.println("Wrong formatted FilterConfig Strings: ");
			ex.printStackTrace();
		}
		isRendering = false;
		refreshAgain = false;
		myOptions = new FilterOptions(optionsConfig);
	}

	/**
	 * parse a pin-config string
	 *
	 * @param data
	 * @param pinList
	 * @param incoming
	 */
	private void parseConfig(String data, PinList pinList, boolean incoming) {
		if (!data.contains(":")) {
			return;
		}
		String[] pins = data.split(",");
		for (String p : pins) {
			String[] pinData = p.split(":");
			Pin pin = new Pin(this, pinList.size(), incoming, pinData[0], FilterDataFormat.valueOf(pinData[1]));
			pinList.add(pin);
		}
	}

	public PinList getInPins() {
		return inPins;
	}

	public PinList getOutPins() {
		return outPins;
	}

	private BufferedImage getOutput(String pinName) {
		return getOutput(outPins.getIndexByName(pinName));
	}

	private BufferedImage getOutput(int pinIndex) {
		return outPins.get(pinIndex).getData();
	}

	public FilterOptions getOptions() {
		return myOptions;
	}

	public void setFileContext(File fileContext) {
		this.fileContext = fileContext;
	}

	/**
	 * This method is heavy: It starts a re-render of this Node and notifies all
	 * fathers, so that they re-render themselves
	 *
	 * @param fb a feedback to call whenever one filter finished rendering
	 */
	public void refresh(Feedback fb) {
		if (isRendering()) {
			//if we are rendering atm: do nothing now, but indicate that a 
			//new re-render should happen after this render
			refreshAgain = true;
			return;
		}
		isRendering = true;
		refreshAgain = false;
		if (fb != null) {
			fb.notification();
		}
		Thread t = new Thread() {
			public void run() {
				render();
				//re-render all following nodes
				for (Pin out : outPins) {
					synchronized (out.getDestinationsLock()) {
						for (Pin other : out.getDestinations()) {
							other.getFilter().refresh(fb);
						}
					}
				}
				isRendering = false;
				if (refreshAgain) {
					//refresh again immediately if requested
					refresh(fb);
				} else {
					if (fb != null) {
						fb.notification();
					}
				}
			}
		};
		t.setName("FilterNode refreshing");
		t.start();
	}

	/**
	 * resets all the cached outputs
	 */
	public void resetData() {
		for (Pin p : outPins) {
			p.setData(null);
		}
	}

	public boolean isRendering() {
		return isRendering;
	}

	/**
	 * get the size this filter needs to get displayed correctly
	 *
	 * @return
	 */
	public Point2D.Float getDisplaySize() {
		Point2D.Float size = myOptions.getDisplaySize();
		int pinCount = Math.max(inPins.size(), outPins.size()) + 1;
		float pinSize = pinCount * 20f;
		if (size.y < pinSize) {
			size.y = pinSize;
		}
		return size;
	}

	/////////
	///////// Below here are the parts for subclasses
	/////////
	public final FilterOption getOption(String name) {
		return myOptions.getOptionByName(name);
	}

	public final FilterOption getOption(int id) {
		return myOptions.getOptionByID(id);
	}

	/**
	 * gives the input data at a given port name
	 *
	 * @param name
	 * @return
	 */
	public final BufferedImage getInputImage(String name) {
		return getInputImage(inPins.getIndexByName(name));
	}

	/**
	 * gives the input data at a given port id
	 *
	 * @return
	 */
	public final BufferedImage getInputImage(int id) {
		return inPins.get(id).getData();
	}

	/**
	 * get the cached output at a given port id
	 *
	 * @param i
	 * @return
	 */
	public BufferedImage getOutputImage(int i) {
		return outPins.get(i).getData();
	}

	/**
	 * gives the input data at a given port name
	 *
	 * @param name
	 * @return
	 */
	public final float getInputValue(String name) {
		BufferedImage img = getInputImage(name);
		if (img == null) {
			return 0;
		}
		return new Color(img.getRGB(0, 0), false).getRed() / 255f;
	}

	/**
	 * sets the output data at a given port name
	 *
	 * @param name
	 * @param value
	 */
	public final void setOutputValue(String name, float value) {
		int r = (int) (value * 255);
		if (r < 0) {
			r = 0;
		}
		if (r > 255) {
			r = 255;
		}
		Color c = new Color(r, 0, 0);
		BufferedImage img = new BufferedImage(1, 1, FilterDataFormat.VALUE.getBIType());
		img.setRGB(0, 0, c.getRGB());
		setOutputImage(name, img);
	}

	/**
	 * sets the output image at a given port name
	 *
	 * @param name
	 * @param img
	 */
	public final void setOutputImage(String name, BufferedImage img) {
		outPins.getPinByName(name).setData(img);
	}

	/**
	 * return the current directory this filter has as execution-context
	 *
	 * @return
	 */
	protected File getFileContext() {
		return fileContext;
	}

	/**
	 * The name of the filter to display
	 *
	 * @return
	 */
	public abstract String getDisplayName();

	/**
	 * re-calculates the output data using the getInput* and setOutput* methods
	 */
	protected abstract void render();

	/**
	 * when the filter gets deleted / the program unloaded or such things.
	 * Override when needed
	 *
	 * fter this method returns, the application might get stopped. So make sure
	 * to finish all IO-tasks and close open connections.
	 *
	 */
	public void destroy() {
		//nothing by default
	}
}
