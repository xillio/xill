package nl.xillio.xill.api.components;

import nl.xillio.xill.api.components.EventEx;

import java.util.List;
import java.util.function.Consumer;

/**
 * This class represents an {@link EventEx} with raised permissions
 *
 * @param <T>
 */
public class EventHostEx<T> {
	private final EventEx<T> event;

	/**
	 * Create a new {@link EventHostEx}
	 */
	public EventHostEx() {
		this.event = new EventEx<>();
	}

	/**
	 * Invoke this event with a given argument.
	 *
	 * @param argument
	 *        an argument
	 */
	public void invoke(final T argument) {
		event.invoke(argument);
	}

	/**
	 * Gets a list of listeners.
	 *
	 * @return listeners
	 */
	public List<Consumer<T>> getListeners() {
		return event.getListeners();
	}

	/**
	 * Returns the event.
	 *
	 * @return the event
	 */
	public EventEx<T> getEvent() {
		return event;
	}
}
