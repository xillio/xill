package nl.xillio.xill.api.components;

import java.util.List;
import java.util.function.Consumer;

/**
 * This class represents an {@link EventEx} with raised permissions.
 * This version is thread-safe.
 *
 * @param <T> The type of the parameter for this event
 */
public class EventHostEx<T> {
    private final EventEx<T> event;

    /**
     * Creates a new {@link EventHostEx}.
     */
    public EventHostEx() {
        this.event = new EventEx<>();
    }

    /**
     * Invokes this event with a given argument.
     *
     * @param argument an argument
     */
    public void invoke(final T argument) {
        event.invoke(argument);
    }

    /**
     * Gets the list of listeners.
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
