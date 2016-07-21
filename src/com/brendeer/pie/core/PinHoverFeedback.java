package com.brendeer.pie.core;

/**
 * A handler that accepts pins as notifications. Used for example when
 * determining what pin is hovered atm.
 *
 * @author Erik
 */
public interface PinHoverFeedback {

	public void notification(Pin p);
}
