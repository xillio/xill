package nl.xillio.xill.api.components;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * This class represents an object that is able to
 *
 * @param <T>
 *        The base type of the stored objects
 */
public class MetadataExpressionPool<T> implements AutoCloseable {
	private static final Logger log = Logger.getLogger(MetadataExpressionPool.class);
	private final Map<Class<? extends T>, T> metadataMap = new HashMap<>();

	/**
	 * Get a value from the pool
	 * 
	 * @param <C>
	 *        the type to fetch
	 *
	 * @param clazz
	 *        the type of object to get from the pool
	 * @return The requested value or null is none was found
	 * @throws ClassCastException
	 *         When the requested value is of a wrong type
	 */
	@SuppressWarnings("unchecked")
	public <C extends T> C get(final Class<C> clazz) {
		return (C) metadataMap.get(clazz);
	}

	/**
	 * Store a value in the pool
	 * 
	 * @param <C>
	 *        The type key to store the value as
	 *
	 * @param object
	 *        object to store. The class key will be defaulted to the result of {@link Object#getClass()}
	 * @return The previously stored value or null when no value was stored.
	 */
	@SuppressWarnings("unchecked")
	public <C extends T> C put(final C object) {
		return (C) metadataMap.put((Class<? extends T>) object.getClass(), object);
	}

	/**
	 * Store a value in the pool
	 * 
	 * @param <C>
	 *        the type key to store the value as
	 * 
	 * @param clazz
	 *        the class key
	 * @param object
	 *        the object to store. This object must implement the class key
	 * @return The previously stored value or null when no value was stored.
	 */
	@SuppressWarnings("unchecked")
	public <C extends T> C put(final Class<C> clazz, final C object) {
		return (C) metadataMap.put(clazz, object);
	}

	/**
	 * Returns true if the pool contains a mapping for this type
	 *
	 * @param clazz
	 *        the class to check the value for
	 * @return true if an object was mapped else false
	 */
	public boolean hasValue(final Class<? extends T> clazz) {
		return metadataMap.containsKey(clazz);
	}

	/**
	 * @return The number of objects stored in this pool
	 */
	public int size() {
		return metadataMap.size();
	}

	/**
	 * Close all objects implementing the {@link AutoCloseable} interface and
	 * clear the map
	 *
	 */
	@Override
	public void close() throws Exception {
		for (T closable : metadataMap.values()) {
			if (closable instanceof AutoCloseable) {
				try {
					((AutoCloseable) closable).close();
				} catch (Exception e) {
					log.error("Exception while closing " + closable, e);
				}
			}
		}
		metadataMap.clear();
	}
}
