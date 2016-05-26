package nl.xillio.events;

import java.util.List;
import java.util.function.Consumer;

/**
 * This class represents an {@link Event} with raised permissions
 *
 * @param <T>
 */
public class EventHost<T> {
	private final Event<T> event;

	/**
	 * Create a new {@link EventHost}
	 */
	public EventHost() {
		this.event = new Event<>();
	}

	/**
	 * Invoke this event with a given argument.
	 *
	 * @param argument
	 *        an argument
	 */
	public void invoke(final T argument) {
		event.listeners.forEach(listener -> listener.accept(argument));
	}

	/**
	 * Gets a list of listeners.
	 *
	 * @return listeners
	 */
	public List<Consumer<T>> getListeners() {
		return event.listeners;
	}

	/**
	 * Returns the event.
	 *
	 * @return the event
	 */
	public Event<T> getEvent() {
		return event;
	}
}
