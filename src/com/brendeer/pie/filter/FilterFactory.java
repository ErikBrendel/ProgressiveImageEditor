package com.brendeer.pie.filter;

/**
 * FilterFactory == Plugin
 *
 * An instance of this class should be able to create new filter-objects and, to
 * be accessible by the user, needs to be registered to the PluginManager
 *
 * @author Erik
 */
public interface FilterFactory {

	public Filter createInstance();

}
