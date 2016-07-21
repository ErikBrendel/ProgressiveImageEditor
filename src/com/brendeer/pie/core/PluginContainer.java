package com.brendeer.pie.core;

import com.brendeer.pie.filter.FilterFactory;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * The static collection of all Plugins(FilterFacory)
 *
 * A map from the plugins name to a FilterFactory-object that can create new
 * instances of this plugin's filters
 *
 * @author Erik
 */
public class PluginContainer {

	static {
		plugins = new HashMap<>();
		log = "PluginContainer INIT "
				+ new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date())
				+ "\n";
	}

	private static HashMap<String, FilterFactory> plugins;
	public static String log; //some nice plugin-loading log-data

	/**
	 * get the FilterFactory that was registered under a given name
	 *
	 * @param name
	 * @return
	 */
	public static FilterFactory getPlugin(String name) {
		FilterFactory f = plugins.get(name);
		return f;
	}

	/**
	 * register new filterFactory
	 *
	 * @param name
	 * @param plugin
	 */
	public static void registerPlugin(String name, FilterFactory plugin) {
		log("Loaded Plugin: " + name);
		plugins.put(name, plugin);
	}

	/**
	 * perform a simple test for plugins to see if they roduce no errors if used
	 * ithout connection
	 */
	public static void testAllPlugins() {
		log("Starting of complete plugin test.");
		Iterator<Entry<String, FilterFactory>> it = plugins.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, FilterFactory> f = it.next();
			try {
				f.getValue().createInstance().refresh(null);
			} catch (Throwable t) {
				log("Error detected: " + t.getMessage());
				log("(In Plugin \"" + f.getKey() + "\")");
			}
		}
		log("Test finished!");
	}

	private static void log(String msg) {
		log += msg + "\n";
	}

	public static List<String> getAllNames() {
		List<String> l = new ArrayList<>();
		for (String s : plugins.keySet()) {
			l.add(s);
		}
		l.sort(null);
		return l;
	}

	/**
	 * load all the plugins that are stored under
	 * %appdata%/brendeer.pie/plugins/ as jar-files
	 */
	public static void loadExternalPlugins() {
		//todo load external plugins
	}
}
