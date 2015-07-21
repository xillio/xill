package nl.xillio.migrationtool.ElasticConsole;

import java.util.HashMap;

/**
 * This class maps a value to an {@link Integer}. This can be used to count things.
 * 
 * @param <T>
 */
public class Counter<T> extends HashMap<T, Integer> {
	private static final long serialVersionUID = -9154933899974709707L;

	/**
	 * @return the value to which the specified key is mapped, or 0 if this map contains no mapping for the key.
	 */
	@Override
	public Integer get(final Object key) {
		Integer value = super.get(key);

		if (value == null) {
			value = 0;
		}

		return value;
	}
}
