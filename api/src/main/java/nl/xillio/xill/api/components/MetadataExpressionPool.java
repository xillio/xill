package nl.xillio.xill.api.components;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * This class represents an object that is able to
 *
 * @param <T> The base type of the stored objects
 */
public class MetadataExpressionPool<T> implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger(MetadataExpressionPool.class);
    private final List<T> data = new ArrayList<>();

    /**
     * Get a value from the pool
     *
     * @param <C>   the type to fetch
     * @param clazz the type of object to get from the pool
     * @return The requested value or null is none was found
     * @throws ClassCastException     When the requested value is of a wrong type
     * @throws NoSuchElementException when the requested interface or class does not exist in this pool.
     */
    @SuppressWarnings("unchecked")
    public <C extends T> C get(final Class<C> clazz) {
        return (C) data.stream()
                .filter(element -> clazz.isAssignableFrom(element.getClass()))
                .findAny()
                .orElse(null);
    }

    /**
     * Store a value in the pool
     *
     * @param object object to store.
     */
    @SuppressWarnings("unchecked")
    public void put(final T object) {
        data.add(object);
    }

    /**
     * Returns true if the pool contains a mapping for this type
     *
     * @param clazz the class to check the value for
     * @return true if an object was mapped else false
     */
    public boolean hasValue(final Class<? extends T> clazz) {
        return data.stream().anyMatch(element -> clazz.isAssignableFrom(element.getClass()));
    }

    /**
     * @return The number of objects stored in this pool
     */
    public int size() {
        return data.size();
    }

    /**
     * Close all objects implementing the {@link AutoCloseable} interface and
     * clear the map
     */
    @Override
    public void close() {
        for (T closable : data) {
            if (closable instanceof AutoCloseable) {
                try {
                    ((AutoCloseable) closable).close();
                } catch (Exception e) {
                    LOGGER.error("Exception while closing " + closable, e);
                }
            }
        }
        data.clear();
    }
}
