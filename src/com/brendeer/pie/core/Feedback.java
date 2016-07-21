package com.brendeer.pie.core;

/**
 * a little cute handler, passed to functions that need to "speak back" at
 * certain events.
 *
 * Example usage: The tree-traversing filter-refreshing passes an instance of
 * this interface wich, on being called, repaints the editPanel
 *
 * @author Erik
 */
public interface Feedback {

	public void notification();
}
