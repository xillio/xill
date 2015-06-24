package nl.xillio.xill.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Simple implementation of an LRU Cache that will automatically remove entries that are in disuse.
 */
public class LRUCache<K, V> extends LinkedHashMap<K, V> {
	final int maxEntries;

	public LRUCache(final int maxEntries) {
		super(maxEntries + 1, 0.75f, true);
		this.maxEntries = maxEntries;
	}

	@Override
	public boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
		return super.size() > maxEntries;
	}
}
