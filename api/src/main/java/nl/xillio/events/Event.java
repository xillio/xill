package nl.xillio.events;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * This class allows other classes to subscribe to an event that can be fired by the {@link EventHost}.
 *
 * @param <T>
 */
public class Event<T> {

	/**
	 * This is a list of listeners for this event.
	 */
	protected List<Consumer<T>> listeners = new ArrayList<>();

	/**
	 * Adds a listener to the event.
	 *
	 * @param listener
	 *        the listener to add
	 */
	public void addListener(final Consumer<T> listener) {
		if (listener == null) {
			throw new NullPointerException("Cannot add null listeners.");
		}

		listeners.add(listener);
	}

	/**
	 * Removes a listener from the event.
	 *
	 * @param listener
	 *        the listener to remove
	 */
	public void removeListener(final Consumer<T> listener) {
		listeners.remove(listener);
	}

}
